package com.jeetx.service.member.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jeetx.service.system.SystemConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryDailyOrderTotal;
import com.jeetx.bean.lottery.LotteryPeriods;
import com.jeetx.bean.lottery.LotteryRoom;
import com.jeetx.bean.lottery.LotteryRule;
import com.jeetx.bean.member.BankCard;
import com.jeetx.bean.member.TransRecord;
import com.jeetx.bean.member.User;
import com.jeetx.bean.member.Withdraw;
import com.jeetx.bean.system.Station;
import com.jeetx.common.constant.Globals;
import com.jeetx.common.exception.BusinessException;
import com.jeetx.common.redis.JedisClient;
import com.jeetx.common.swagger.model.lottery.LotteryOrderItemDTO;
import com.jeetx.controller.api.ApiUtil;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryDailyOrderTotalService;
import com.jeetx.service.lottery.LotteryOrderService;
import com.jeetx.service.lottery.LotteryPeriodsService;
import com.jeetx.service.lottery.LotteryRoomService;
import com.jeetx.service.lottery.LotteryRuleService;
import com.jeetx.service.member.BankCardService;
import com.jeetx.service.member.MemberLogService;
import com.jeetx.service.member.PointsLevelService;
import com.jeetx.service.member.RechargeService;
import com.jeetx.service.member.TransRecordService;
import com.jeetx.service.member.UserService;
import com.jeetx.service.member.WithdrawService;
import com.jeetx.service.system.StationService;
import com.jeetx.util.DateTimeTool;
import com.jeetx.util.MD5Util;
import com.jeetx.util.RandomUtil;

@Service
@Transactional
public class UserServiceImpl extends DaoSupport<User> implements UserService {
    @Autowired JedisClient jedisClient;
	@Autowired PointsLevelService pointsLevelService;
	@Autowired TransRecordService transRecordService;
	@Autowired MemberLogService memberLogService;
	@Autowired LotteryDailyOrderTotalService lotteryDailyOrderTotalService;
	@Autowired StationService stationService;
	@Autowired LotteryOrderService lotteryOrderService;
	@Autowired RechargeService rechargeService;
	@Autowired LotteryPeriodsService lotteryPeriodsService;
	@Autowired LotteryRoomService lotteryRoomService;
	@Autowired LotteryRuleService lotteryRuleService;
	@Autowired WithdrawService withdrawService;
	@Autowired BankCardService bankCardService;
	@Autowired
	SystemConfigService systemConfigService;

	public void validateUser(User user,Boolean validateDisable,Boolean validateDreeze) {
		if(user == null) {
			throw new BusinessException(ApiUtil.getErrorCode("117"));
		}
		
		if(validateDisable && user.getStatus()==0) {
			throw new BusinessException(ApiUtil.getErrorCode("130"));
		}
		
		if(validateDreeze && user.getStatus()==2) {
			throw new BusinessException(ApiUtil.getErrorCode("162"));
		}
	}
	
	@SuppressWarnings("unchecked")
	public User findUserByToken(String loginToken) {
		List<User> list = this.getSession().createQuery("from User o where o.loginToken = ?").setParameter(0, loginToken).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (User) list.get(0);
		}
	}
	
	public Long getExpandUserNum(Integer parentId) {
		Object counts = this.getSession().createQuery("select count(o.id) from User o where o.userType = 1 and o.parent.id = ? ").setParameter(0, parentId).uniqueResult();
		if(counts != null) {
			return (Long)counts;
		}
		return 0l;
	}
	
	@SuppressWarnings("unchecked")
	public User findUserByNickName(String nickName,Integer stationId) {
		List<User> list = this.getSession().createQuery("from User o where o.nickName = ? and o.station.id = ?").setParameter(0, nickName).setParameter(1, stationId).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (User) list.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public User findUser(String username,Integer stationId) {
		List<User> list = this.getSession().createQuery("from User o where o.username = ? and o.station.id = ? ").setParameter(0, username).setParameter(1, stationId).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (User) list.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public User checkUser(String username,String password,Integer stationId) {
		List<User> list = this.getSession().createQuery("from User o where o.username = ? and o.password = ? and o.station.id = ?")
				.setParameter(0, username).setParameter(1, password).setParameter(2, stationId).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (User) list.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public User checkUser(String wechatOpenId,Integer stationId) {
		List<User> list = this.getSession().createQuery("from User o where o.wechatOpenId = ? and o.station.id = ?")
				.setParameter(0, wechatOpenId).setParameter(1, stationId).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (User) list.get(0);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public User checkUserByToken(String username,String loginToken,Integer stationId) {
		List<User> list = this.getSession().createQuery("from User o where o.username = ? and o.loginToken = ? and o.station.id = ?")
				.setParameter(0, username).setParameter(1, loginToken).setParameter(2, stationId).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (User) list.get(0);
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public User register(Integer pid,String username,String phone,String referee,String wechat,
			Integer userType,String password,String device,Integer stationId,String pUsername) throws Exception {
		User parent = null; 
		Station station = stationService.find(stationId);
		if(station == null || station.getStatus() !=1) {
			throw new BusinessException(ApiUtil.getErrorCode("108"));
		}
		
		if(StringUtils.isNoneBlank(referee)) {
			parent = this.findUser(referee, stationId);
			if(parent == null || parent.getStatus()!=1) {
				throw new BusinessException(ApiUtil.getErrorCode("164"));
			}
		}
		
		if(pid != null) {
			parent = this.find(pid);
			if(parent == null || parent.getStatus()==0 || parent.getStation().getId() != station.getId() || 
					(parent.getUserType() != 2 && parent.getUserType() != 3)) {
				throw new BusinessException(ApiUtil.getErrorCode("119"));
			}
		}else {
			if(userType == 1) {
				if(stationId == 17 || stationId == 18) {
					if(StringUtils.isBlank(pUsername)) {
						throw new BusinessException(ApiUtil.getErrorCode("166")); 
					}
					
					User p = this.findUser(pUsername, stationId);
					if(p!=null && p.getUserType() == 4 && p.getParent() !=null && p.getParent().getUserType() == 3) {
						parent = p.getParent();	
					}else {
						throw new BusinessException(ApiUtil.getErrorCode("167"));
					}
				}else {
					parent = this.findUser("默认拓展", stationId);
					if(parent == null) {
						//初始化默认代理
						String pwd = MD5Util.MD5Encode(MD5Util.MD5Encode("123456", "utf-8").toLowerCase(),"utf-8").toLowerCase();
						User agent = this.register(null, "默认代理",null,null,null,2, pwd, null, station.getId(),null);	
						if(agent != null) {
							//初始化默认拓展
							parent = this.register(agent.getId(), "默认拓展",null,null,null,3, pwd, null, station.getId(),null);
						}
					}
				}
			}
		}
		
		User user = this.findUser(username,stationId);
		if(user!=null) {
			throw new BusinessException(ApiUtil.getErrorCode("110"));
		}
		
		user = new User();
		user.setParent(parent);
		user.setHeadImg("/upload/faces/".concat(RandomUtil.getRangeRandom(1, 100)+".jpg"));
		user.setUsername(username);
		user.setPassword(password);
		user.setNickName(username);//游戏昵称
		user.setPhone(phone);
		user.setWechat(wechat);
		user.setReferee(referee);
		user.setUserType(userType);//0游客、1玩家、2代理、3推广员、4虚拟号
		user.setCreateTime(new Date());
		user.setStatus(1);
		user.setBalance(new BigDecimal(0));//余额
		user.setLotteryBalance(new BigDecimal(0));//彩金
		user.setFreezeBalance(new BigDecimal(0));//冻结金额
		user.setRegSource(device);//注册来源
		user.setIsGag(1);//是否被禁言（1是 0否）
		user.setIsBet(1);//是否允许下注
		user.setPoints(0);
		user.setPointsLevel(pointsLevelService.findPointsLevel(0));
		user.setStation(station);
		user.setFlowRequire(new BigDecimal(0));
		this.save(user);
		
		if(parent != null && user.getUserType() == 1) {
			LotteryDailyOrderTotal lotteryDailyOrderTotal = lotteryDailyOrderTotalService.getLotteryDailyOrderTotal(DateTimeTool.dateFormat("yyyy-MM-dd", new Date()), parent.getId());
			if(lotteryDailyOrderTotal==null) {
				lotteryDailyOrderTotal = new LotteryDailyOrderTotal();
				
				lotteryDailyOrderTotal.setUser(parent);//玩家帐号
				lotteryDailyOrderTotal.setTotalDate(DateTimeTool.dateFormat("yyyy-MM-dd", DateTimeTool.dateFormat("yyyy-MM-dd", new Date())));//统计日期
				lotteryDailyOrderTotal.setBetMoney(new BigDecimal(0));//流水金额
				lotteryDailyOrderTotal.setProfitMoney(new BigDecimal(0));//盈亏金额
				lotteryDailyOrderTotal.setBackWaterMoney(new BigDecimal(0));// //回水金额
				lotteryDailyOrderTotal.setRechargeMoney(new BigDecimal(0));//充值金额
				lotteryDailyOrderTotal.setWithdrawMoney(new BigDecimal(0));//提现金额
				lotteryDailyOrderTotal.setWinMoney(new BigDecimal(0));//中奖金额
				lotteryDailyOrderTotal.setExpandUserNum(1);//拓展玩家数
				lotteryDailyOrderTotal.setBalance(parent.getBalance());
				lotteryDailyOrderTotal.setLotteryBalance(user.getLotteryBalance());//赠送彩金统计
				lotteryDailyOrderTotalService.save(lotteryDailyOrderTotal);
			}else{
				lotteryDailyOrderTotal.setBalance(parent.getBalance());
				lotteryDailyOrderTotal.setLotteryBalance(user.getLotteryBalance());//赠送彩金统计
				lotteryDailyOrderTotal.setExpandUserNum(lotteryDailyOrderTotal.getExpandUserNum()+1);//拓展玩家数
				lotteryDailyOrderTotalService.update(lotteryDailyOrderTotal);
			}
		}
		
		return user;
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public User login(String username,String password,String ip,String device,Integer stationId) {
		User user = this.checkUser(username,password,stationId);
		this.validateUser(user, true, false);//验证用户
		
		user.setLoginNum(user.getLoginNum()!=null?user.getLoginNum()+1:1);
		user.setLoginLastDate(user.getLoginDate());
		user.setLoginDate(new Date());
		this.update(user);
		this.resetToken(user);//token重置

		memberLogService.saveLog(user, null, "玩家登陆", ip, device);
		return user;
	}
	
	/**
	 * token认证
	 * @param username
	 * @param password
	 * @param device
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public User checkToken(String username,String token,Integer stationId) {
		User user = this.checkUserByToken(username,token,stationId);
		if(user==null) {
			throw new BusinessException(ApiUtil.getErrorCode("112"));
		}
		
		this.validateUser(user, true, false);//验证用户
		return user;
	}
	
	/*** token重置*/
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public String resetToken(User user) {
		user.setLoginToken(RandomUtil.generateString(32));
		this.update(user);

		jedisClient.set(user.getLoginToken(), RandomUtil.generateString(64));
		jedisClient.expire(user.getLoginToken(), Globals.REIDS_USER_TOKEN_EXPIRE); //设置会话过期时间180秒=3分钟
		
		return user.getLoginToken();
	}
	
	/*** 
	 * 修改昵称
	 * @param username
	 * @param password
	 * @param device
	 * */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void updateNickName(User user,String ip,String device,String nickName,Integer stationId) {
		User u = this.findUserByNickName(nickName,stationId);
		if(u != null && u.getId() != user.getId()) {
			throw new BusinessException(ApiUtil.getErrorCode("157"));
		}
		
		user.setNickName(nickName);
		this.update(user);
		
		memberLogService.saveLog(user, null, "修改昵称", ip, device);
	}
	
	
	/*** 
	 * 修改密码
	 * @param username
	 * @param password
	 * @param device
	 * */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void updatePwd(User user,String ip,String device,Integer type,String password,String newPassword,Integer stationId) {
		this.validateUser(user, true, true);//验证用户
		
		Station station = stationService.find(stationId);
		if(station == null || station.getStatus() !=1) {
			throw new BusinessException(ApiUtil.getErrorCode("108"));
		}
		
		switch (type) {
		case 1:
			if(!user.getPassword().equals(password)) {
				throw new BusinessException(ApiUtil.getErrorCode("116"));
			}
			user.setPassword(newPassword);
			this.update(user);
			
			this.resetToken(user);//token重置
			
			memberLogService.saveLog(user, null, "修改登陆密码", ip, device);
			break;
		case 2:
			if(StringUtils.isNoneBlank(user.getPayPassword()) && (StringUtils.isBlank(password) || !user.getPayPassword().equals(password))) {
				throw new BusinessException(ApiUtil.getErrorCode("116"));
			}
			
			user.setPayPassword(newPassword);
			this.update(user);
			
			memberLogService.saveLog(user, null, "修改安全码", ip, device);
			break;

		default:
			throw new BusinessException(ApiUtil.getErrorCode("102")+",type必须是1或者2");
		}

	}
	
	
	/*** 
	 * 修改头像
	 * @param username
	 * @param password
	 * @param device
	 * */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void updateHeadImg(User user,String ip,String device,String headImg,Integer stationId) {
		user.setHeadImg(headImg);
		this.update(user);
		
		memberLogService.saveLog(user, null, "修改昵称", ip, device);
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public User getUserInfo(String username,String token,String ip,String device,Integer stationId) {
		User user = this.checkToken(username, token,stationId);
		
		memberLogService.saveLog(user, null, "获取玩家信息", ip, device);
		return user;
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void logout(String username,String token,String ip,String device,Integer stationId) {
		User user = this.checkUserByToken(username,token,stationId);
		if(user==null) {
			throw new BusinessException(ApiUtil.getErrorCode("112"));
		}
	
		this.resetToken(user);//token重置
		
		memberLogService.saveLog(user, null, "玩家退出", ip, device);
	}
	
	/**
	 * 游客登陆
	 * @param username
	 * @param password
	 * @param device
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public User touristLogin(String device,Integer stationId) {
		Station station = stationService.find(stationId);
		if(station == null || station.getStatus() !=1) {
			throw new BusinessException(ApiUtil.getErrorCode("108"));
		}
		
		String username = "游客".concat(DateTimeTool.dateFormat("yyMMddHHmmss", new Date())).concat(RandomUtil.generateInt(2));
		User user = this.findUser(username,station.getId());
		if(user!=null) {
			throw new BusinessException(ApiUtil.getErrorCode("113"));
		}
		
		BigDecimal lotteryAmount = new BigDecimal(2000);
		user = new User();
		user.setHeadImg("/upload/faces/".concat(RandomUtil.getRangeRandom(1, 100)+".jpg"));
		user.setUsername(username);
		user.setNickName(username);//游戏昵称
		user.setUserType(0);//0游客、1玩家、2代理、3推广员、4虚拟号
		user.setCreateTime(new Date());
		user.setStatus(1);
		user.setBalance(new BigDecimal(0));//余额
		user.setLotteryBalance(lotteryAmount);//彩金
		user.setFreezeBalance(new BigDecimal(0));//冻结金额
		user.setRegSource(device);//注册来源
		user.setIsGag(1);//是否被禁言（1是 0否）
		user.setIsBet(1);//是否允许下注
		user.setLoginNum(1);
		user.setLoginDate(new Date());
		user.setPoints(0);
		user.setPointsLevel(pointsLevelService.findPointsLevel(0));
		user.setPassword(MD5Util.MD5Encode(MD5Util.MD5Encode("123456", "utf-8").toLowerCase(),"utf-8").toLowerCase());
		user.setStation(station);
		user.setFlowRequire(new BigDecimal(0)); 
		this.save(user);
		
		TransRecord transRecord = new TransRecord();
		transRecord.setUser(user);//用户
		transRecord.setCreateTime(new Date());//交易时间
		transRecord.setTransCategory(6);//交易类型（1充值、2提现、3抽奖、4投注、5撤单、6赠送、7中奖、8回水）
		transRecord.setEndBalance(new BigDecimal(0));//剩余账户金额
		transRecord.setTransAmount(new BigDecimal(0));//交易账户金额
		transRecord.setTransLotteryAmount(lotteryAmount);//交易彩金
		transRecord.setEndLotteryBalance(lotteryAmount);//剩余彩金
		transRecord.setRemark("游客送2000体验金");
		transRecord.setFlag(1);
		transRecordService.save(transRecord);
		
		this.resetToken(user);//token重置
		
		return user;
	}
	
	/**
	 * 代理新增推广号
	 * @param username
	 * @param password
	 * @param ip
	 * @param device
	 * @return
	 * @throws Exception 
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public User addSalesman(User user,String username,String password,String ip,String device,Integer stationId) throws Exception {
		this.validateUser(user, true, true);//验证用户
		
		if(user.getUserType()!=2) {
			throw new BusinessException(ApiUtil.getErrorCode("150"));
		}
		this.register(user.getId(), username,null,null,null,3, password, device,stationId,null);
		
		memberLogService.saveLog(user, username, "新增推广号", ip, device);
		return user;
	}
	
	/**
	 * 推广员新增虚拟号
	 * @param username
	 * @param password
	 * @param ip
	 * @param device
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public User addVirtualUser(User user,String username,String password,String isInitOrder,String cardJson,String ip,
			String device,Integer stationId) throws Exception {
		this.validateUser(user, true, true);//验证用户
		
		if(user.getUserType()!=3) {
			throw new BusinessException(ApiUtil.getErrorCode("151"));
		}
		User virtualUser = this.register(user.getId(), username,null,null,null,4, password, device,stationId,null);
		
		//初始虚拟交易记录
		if(virtualUser != null && StringUtils.isNoneBlank(isInitOrder) && isInitOrder.equals("1")) {
			virtualUser.setPayPassword(MD5Util.MD5Encode(MD5Util.MD5Encode("147258", "utf-8").toLowerCase(),"utf-8").toLowerCase());
			this.update(virtualUser);

			bankCardService.addBankCard(virtualUser, ip, device, cardJson);//新增银行卡

			String stationId18VirtualReportType = systemConfigService.getValueByName("stationId18_virtual_report_type");
			String stationId17VirtualReportType = systemConfigService.getValueByName("stationId17_virtual_report_type");
			if(stationId == 18 && StringUtils.isNotBlank(stationId18VirtualReportType)&&"幸运飞艇".equals(stationId18VirtualReportType)) {//幸运飞艇
				//第一天 充值3000 送彩金150+150 余额3300   【投注幸运飞艇 初级房 冠军大1650 冠军小1650】 提现3250.5
				Date beginDate = DateTimeTool.getDaysByDate2Days(12, new Date());
				this.initVirtualDataV2(virtualUser, beginDate, true, new BigDecimal(3000), true, new BigDecimal(150), true, ip, device, stationId, true);

				//第二天 充值3000 送彩金150 余额3150       【投注幸运飞艇 初级房 冠军大1575 冠军小1575】 提现3103
				beginDate = DateTimeTool.getDaysByDate2Days(11, new Date());
				this.initVirtualDataV2(virtualUser, beginDate, true, new BigDecimal(3000), false, new BigDecimal(0), true, ip, device, stationId, true);

				//第三天 充值5000 送彩金300+300 余额5600   【投注幸运飞艇 初级房 冠军大2800 冠军小2800】 提现5516
				beginDate = DateTimeTool.getDaysByDate2Days(10, new Date());
				this.initVirtualDataV2(virtualUser, beginDate, true, new BigDecimal(5000), true, new BigDecimal(300), true, ip, device, stationId, true);

				//第四天 充值5500 送彩金330 余额5830       【投注幸运飞艇 初级房 冠军大2915 冠军小2915】 提现5742.5
				beginDate = DateTimeTool.getDaysByDate2Days(9, new Date());
				this.initVirtualDataV2(virtualUser, beginDate, true, new BigDecimal(5500), false, new BigDecimal(0), true, ip, device, stationId, true);

				//第五天 充值10000 送彩金700+700 余额11400 【投注幸运飞艇 初级房 冠军大5700 冠军小5700】 提现11319
				beginDate = DateTimeTool.getDaysByDate2Days(8, new Date());
				this.initVirtualDataV2(virtualUser, beginDate, true, new BigDecimal(10000), true, new BigDecimal(700), true, ip, device, stationId, true);

				//第六天 充值12000 送彩金840 余额12840     【投注幸运飞艇 初级房 冠军大6420 冠军小6420】 提现12647
				beginDate = DateTimeTool.getDaysByDate2Days(7, new Date());
				this.initVirtualDataV2(virtualUser, beginDate, true, new BigDecimal(12000), false, new BigDecimal(0), true, ip, device, stationId, true);

				//第七天 充值15000 送彩金1050 余额16050    【投注幸运飞艇 初级房 冠军大8025 冠军小8025】 提现15809
				beginDate = DateTimeTool.getDaysByDate2Days(6, new Date());
				this.initVirtualDataV2(virtualUser, beginDate, true, new BigDecimal(15000), false, new BigDecimal(0), true, ip, device, stationId, true);

				//第八天 充值15000 送彩金1050 余额16050    【投注幸运飞艇 初级房 冠军大8025 冠军小8025】 提现15809
				beginDate = DateTimeTool.getDaysByDate2Days(5, new Date());
				this.initVirtualDataV2(virtualUser, beginDate, true, new BigDecimal(15000), false, new BigDecimal(0), true, ip, device, stationId, true);

				//第九天 充值 18000 送彩金1260 余额19260   【投注幸运飞艇 初级房 冠军大9630 冠军小9630】 提现18971
				beginDate = DateTimeTool.getDaysByDate2Days(4, new Date());
				this.initVirtualDataV2(virtualUser, beginDate, true, new BigDecimal(18000), false, new BigDecimal(0), true, ip, device, stationId, true);

				//第十天 充值 20000 送彩金1400 余额21400   【投注幸运飞艇 初级房 冠军大10700 冠军小10700】 提现21079
				beginDate = DateTimeTool.getDaysByDate2Days(3, new Date());
				this.initVirtualDataV2(virtualUser, beginDate, true, new BigDecimal(20000), false, new BigDecimal(0), true, ip, device, stationId, true);

				//第十一天 充值 25000 送彩金 1750 余额26750【投注幸运飞艇 初级房 冠军大13375 冠军小13375】 提现26349
				beginDate = DateTimeTool.getDaysByDate2Days(2, new Date());
				this.initVirtualDataV2(virtualUser, beginDate, true, new BigDecimal(25000), false, new BigDecimal(0), true, ip, device, stationId, true);

				//第十二天 充值 20000 送彩金 1400 余额21400【投注幸运飞艇 初级房 冠军大10700 冠军小10700】 提现21079
				beginDate = DateTimeTool.getDaysByDate2Days(1, new Date());
				this.initVirtualDataV2(virtualUser, beginDate, true, new BigDecimal(20000), false, new BigDecimal(0), true, ip, device, stationId, true);
			}else if(stationId == 18 && StringUtils.isNotBlank(stationId18VirtualReportType)&&"北京赛车".equals(stationId18VirtualReportType)) {//北京赛车
				//第一天 充值3000 送彩金150+150 余额3300   【投注幸运飞艇 初级房 冠军大1650 冠军小1650】 提现3250.5
				Date beginDate = DateTimeTool.getDaysByDate2Days(12, new Date());
				this.initVirtualDataV4(virtualUser, beginDate, true, new BigDecimal(3000), true, new BigDecimal(150), true, ip, device, stationId, true);

				//第二天 充值3000 送彩金150 余额3150       【投注北京赛车 初级房 冠军大1575 冠军小1575】 提现3103
				beginDate = DateTimeTool.getDaysByDate2Days(11, new Date());
				this.initVirtualDataV4(virtualUser, beginDate, true, new BigDecimal(3000), false, new BigDecimal(0), true, ip, device, stationId, true);

				//第三天 充值5000 送彩金300+300 余额5600   【投注北京赛车 初级房 冠军大2800 冠军小2800】 提现5516
				beginDate = DateTimeTool.getDaysByDate2Days(10, new Date());
				this.initVirtualDataV4(virtualUser, beginDate, true, new BigDecimal(5000), true, new BigDecimal(300), true, ip, device, stationId, true);

				//第四天 充值5500 送彩金330 余额5830       【投注北京赛车 初级房 冠军大2915 冠军小2915】 提现5742.5
				beginDate = DateTimeTool.getDaysByDate2Days(9, new Date());
				this.initVirtualDataV4(virtualUser, beginDate, true, new BigDecimal(5500), false, new BigDecimal(0), true, ip, device, stationId, true);

				//第五天 充值10000 送彩金700+700 余额11400 【投注北京赛车 初级房 冠军大5700 冠军小5700】 提现11319
				beginDate = DateTimeTool.getDaysByDate2Days(8, new Date());
				this.initVirtualDataV4(virtualUser, beginDate, true, new BigDecimal(10000), true, new BigDecimal(700), true, ip, device, stationId, true);

				//第六天 充值12000 送彩金840 余额12840     【投注北京赛车 初级房 冠军大6420 冠军小6420】 提现12647
				beginDate = DateTimeTool.getDaysByDate2Days(7, new Date());
				this.initVirtualDataV4(virtualUser, beginDate, true, new BigDecimal(12000), false, new BigDecimal(0), true, ip, device, stationId, true);

				//第七天 充值15000 送彩金1050 余额16050    【投注北京赛车 初级房 冠军大8025 冠军小8025】 提现15809
				beginDate = DateTimeTool.getDaysByDate2Days(6, new Date());
				this.initVirtualDataV4(virtualUser, beginDate, true, new BigDecimal(15000), false, new BigDecimal(0), true, ip, device, stationId, true);

				//第八天 充值15000 送彩金1050 余额16050    【投注北京赛车 初级房 冠军大8025 冠军小8025】 提现15809
				beginDate = DateTimeTool.getDaysByDate2Days(5, new Date());
				this.initVirtualDataV4(virtualUser, beginDate, true, new BigDecimal(15000), false, new BigDecimal(0), true, ip, device, stationId, true);

				//第九天 充值 18000 送彩金1260 余额19260   【投注北京赛车 初级房 冠军大9630 冠军小9630】 提现18971
				beginDate = DateTimeTool.getDaysByDate2Days(4, new Date());
				this.initVirtualDataV4(virtualUser, beginDate, true, new BigDecimal(18000), false, new BigDecimal(0), true, ip, device, stationId, true);

				//第十天 充值 20000 送彩金1400 余额21400   【投注北京赛车 初级房 冠军大10700 冠军小10700】 提现21079
				beginDate = DateTimeTool.getDaysByDate2Days(3, new Date());
				this.initVirtualDataV4(virtualUser, beginDate, true, new BigDecimal(20000), false, new BigDecimal(0), true, ip, device, stationId, true);

				//第十一天 充值 25000 送彩金 1750 余额26750【投注北京赛车 初级房 冠军大13375 冠军小13375】 提现26349
				beginDate = DateTimeTool.getDaysByDate2Days(2, new Date());
				this.initVirtualDataV4(virtualUser, beginDate, true, new BigDecimal(25000), false, new BigDecimal(0), true, ip, device, stationId, true);

				//第十二天 充值 20000 送彩金 1400 余额21400【投注北京赛车 初级房 冠军大10700 冠军小10700】 提现21079
				beginDate = DateTimeTool.getDaysByDate2Days(1, new Date());
				this.initVirtualDataV4(virtualUser, beginDate, true, new BigDecimal(20000), false, new BigDecimal(0), true, ip, device, stationId, true);
      }else if (stationId == 17 && StringUtils.isNotBlank(stationId17VirtualReportType)&&"时时彩".equals(stationId17VirtualReportType)){//时时彩
        /***初始化最近七天的投注信息*/
        //第一天，上分5000，下注两期，下方；
        Date beginDate = DateTimeTool.getDaysByDate2Days(7, new Date());
        this.initVirtualDataV3(virtualUser, beginDate, true, new BigDecimal(5000), true,ip, device,stationId,true);

        //第二天，上分8000，下注两期，不下方；
        beginDate = DateTimeTool.getDaysByDate2Days(6, new Date());
        this.initVirtualDataV3(virtualUser, beginDate, true, new BigDecimal(8000), false,ip, device,stationId,false);

        //第三天，不上分，下注两期，不下方；
        beginDate = DateTimeTool.getDaysByDate2Days(5, new Date());
        this.initVirtualDataV3(virtualUser, beginDate, false, new BigDecimal(0), false,ip, device,stationId,false);

        //第四天，不上分，下注两期，不下方；
        beginDate = DateTimeTool.getDaysByDate2Days(4, new Date());
        this.initVirtualDataV3(virtualUser, beginDate, false, new BigDecimal(0), false,ip, device,stationId,false);

        //第五天，上分20000，下注两期，不下方；
        beginDate = DateTimeTool.getDaysByDate2Days(3, new Date());
        this.initVirtualDataV3(virtualUser, beginDate, true, new BigDecimal(20000), false,ip, device,stationId,false);

        //第六天，不上分，下注两期，下方；
        beginDate = DateTimeTool.getDaysByDate2Days(2, new Date());
        this.initVirtualDataV3(virtualUser, beginDate, false, new BigDecimal(0), true,ip, device,stationId,false);

        //第七天，上分30000，下注两期，不下方；
        beginDate = DateTimeTool.getDaysByDate2Days(1, new Date());
        this.initVirtualDataV3(virtualUser, beginDate, true, new BigDecimal(30000), false,ip, device,stationId,false);
        /***初始化最近七天的投注信息*/
			}else if (stationId == 17 && StringUtils.isNotBlank(stationId17VirtualReportType)&&"北京赛车".equals(stationId17VirtualReportType)){//北京赛车
				/***初始化最近七天的投注信息*/
				//第一天，上分5000，下注两期，下方；
				Date beginDate = DateTimeTool.getDaysByDate2Days(7, new Date());
				this.initVirtualData(virtualUser, beginDate, true, new BigDecimal(5000), true,ip, device,stationId,true);
				
				//第二天，上分8000，下注两期，不下方；
				beginDate = DateTimeTool.getDaysByDate2Days(6, new Date());
				this.initVirtualData(virtualUser, beginDate, true, new BigDecimal(8000), false,ip, device,stationId,false);
				
				//第三天，不上分，下注两期，不下方；
				beginDate = DateTimeTool.getDaysByDate2Days(5, new Date());
				this.initVirtualData(virtualUser, beginDate, false, new BigDecimal(0), false,ip, device,stationId,false);
				
				//第四天，不上分，下注两期，不下方；
				beginDate = DateTimeTool.getDaysByDate2Days(4, new Date());
				this.initVirtualData(virtualUser, beginDate, false, new BigDecimal(0), false,ip, device,stationId,false);
				
				//第五天，上分20000，下注两期，不下方；
				beginDate = DateTimeTool.getDaysByDate2Days(3, new Date());
				this.initVirtualData(virtualUser, beginDate, true, new BigDecimal(20000), false,ip, device,stationId,false);

				//第六天，不上分，下注两期，下方；
				beginDate = DateTimeTool.getDaysByDate2Days(2, new Date());
				this.initVirtualData(virtualUser, beginDate, false, new BigDecimal(0), true,ip, device,stationId,false);
				
				//第七天，上分30000，下注两期，不下方；
				beginDate = DateTimeTool.getDaysByDate2Days(1, new Date());
				this.initVirtualData(virtualUser, beginDate, true, new BigDecimal(30000), false,ip, device,stationId,false);
				/***初始化最近七天的投注信息*/
			}
		}

		if(StringUtils.isNoneBlank(device)) {
			memberLogService.saveLog(user, username, "新增虚拟号", ip, device);
		}
		return user;
	}
	
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void initVirtualData(User user,Date beginDate,Boolean isRecharge,BigDecimal rechargeAmount,Boolean isWithdraw,
			String ip,String device,Integer stationId,Boolean joinActivity) throws Exception {
		System.out.println("=====================");
		Integer lotteryType = 4;
		//1、是否充值
		if(isRecharge) {
			Date rechargeTime = DateTimeTool.dateFormat(null, DateTimeTool.dateFormat("yyyy-MM-dd 18:00:00", beginDate));
			rechargeTime = DateTimeTool.addTime(rechargeTime,RandomUtil.getRangeRandom(0, 3*60*60-5*60));
			rechargeService.rechargeHandle(user.getUsername(), rechargeAmount, 1,"充值",stationId,rechargeTime,joinActivity);
			System.out.println(DateTimeTool.dateFormat(null, rechargeTime).concat(",充值").concat(rechargeAmount.toString()));
		}
		
		//2、两次下注操作，先随机生成下注赛道，以及下注金额
		int dropCount = 2;
		//当天晚上9点11分、9点36分下注北京赛车
		for (int i = 0; i < dropCount; i++) {
			String dropTime = null;
			if(i==0) {
				dropTime = DateTimeTool.dateFormat("yyyy-MM-dd 21:"+RandomUtil.getRangeRandom(11, 13)+":"+RandomUtil.getRangeRandom(10, 60)+"", beginDate);
			}else if(i==1) {
				dropTime = DateTimeTool.dateFormat("yyyy-MM-dd 21:"+RandomUtil.getRangeRandom(36, 39)+":"+RandomUtil.getRangeRandom(10, 60)+"", beginDate);
			}
			
			user = this.find(user.getId());

			BigDecimal allDropMoney = new BigDecimal(0);
			LotteryPeriods lotteryPeriods = lotteryPeriodsService.findLotteryPeriodsByDate(lotteryType, dropTime);
			if(lotteryPeriods != null) {
				
				List<LotteryOrderItemDTO> orderItemList = new ArrayList<LotteryOrderItemDTO>();	
				LotteryRoom lotteryRoom = lotteryRoomService.getLotteryRoomList(lotteryType, stationId).get(0);
				if(lotteryRoom != null) {
					String paramName = "4-大";
					LotteryRule lotteryRule = lotteryRuleService.getLotteryRule(lotteryRoom.getLotteryHall().getId(), paramName, stationId);
					BigDecimal dropMoney = user.getBalance().add(user.getLotteryBalance()).divide(new BigDecimal(2)).setScale(0, BigDecimal.ROUND_DOWN);
					if(lotteryRule != null) {
						LotteryOrderItemDTO orderItem = new LotteryOrderItemDTO(); 
						orderItem.setRuleId(lotteryRule.getId());
						orderItem.setBetContent(paramName);
						orderItem.setBetMoney(dropMoney.toString());
						orderItemList.add(orderItem);
						
						allDropMoney = allDropMoney.add(dropMoney);
					}
					
					paramName = "4-小";
					lotteryRule = lotteryRuleService.getLotteryRule(lotteryRoom.getLotteryHall().getId(), paramName, stationId);
					dropMoney = user.getBalance().add(user.getLotteryBalance()).subtract(dropMoney).setScale(0, BigDecimal.ROUND_DOWN);
					if(lotteryRule != null) {
						LotteryOrderItemDTO orderItem = new LotteryOrderItemDTO(); 
						orderItem.setRuleId(lotteryRule.getId());
						orderItem.setBetContent(paramName);
						orderItem.setBetMoney(dropMoney.toString());
						orderItemList.add(orderItem);
						allDropMoney = allDropMoney.add(dropMoney);
					}
					
					String orderCode = RandomUtil.getSeqNumber("CP", "yyyyMMddHHmmss", 3);
					//投注
					lotteryOrderService.submitOrder(user, ip, device, orderCode, lotteryRoom.getId(), lotteryPeriods.getLotteryPeriods(), 
							allDropMoney, orderItemList, stationId,false,DateTimeTool.dateFormat(null, dropTime),false);
					//派奖
					lotteryOrderService.lotteryAwardHandle(lotteryPeriods.getLotteryPeriods(),lotteryType,stationId,lotteryPeriods.getLotterySourceCollectTime());
					System.out.println(dropTime.concat(",投注").concat(allDropMoney.toString()));
				}
			}
		}

		//是否提现
		if(isWithdraw) {
			Date applyTime = DateTimeTool.dateFormat(null, DateTimeTool.dateFormat("yyyy-MM-dd 22:00:00", beginDate));
			applyTime = DateTimeTool.addTime(applyTime,RandomUtil.getRangeRandom(0, 30*60));
			//System.out.println("applyTime:"+applyTime);
			String timestamp = DateTimeTool.dateFormat("yyyyMMddHHmmss", applyTime);
			//System.out.println(timestamp);
			String securityCode = MD5Util.MD5Encode(user.getPayPassword().concat(timestamp), "UTF-8").toLowerCase();
			System.out.println(DateTimeTool.dateFormat(null, applyTime).concat(",提现").concat(user.getBalance().toString()).concat(","));
			
			BankCard bankCard = null;
			List<BankCard> bankCardList = bankCardService.getBankCardList(user.getUsername(),stationId);
			if(bankCardList !=null && bankCardList.size()>0) {
				bankCard = bankCardList.get(0);
			}
			
			if(bankCard !=null) {
				Withdraw withdraw = withdrawService.applyWithdraw(user,ip,device,timestamp,securityCode, bankCard.getId(), user.getBalance(),applyTime);
				if(withdraw !=null && withdraw.getStatus()==1) {
					Date handleTime = DateTimeTool.getDaysByDate2Minute(10, applyTime);
					withdrawService.withdrawHandle(withdraw.getId(), 1, "提现成功", stationId,handleTime);
				}
			}
		}
	}

  @Transactional(propagation=Propagation.REQUIRED,readOnly=false)
  public void initVirtualDataV3(User user,Date beginDate,Boolean isRecharge,BigDecimal rechargeAmount,Boolean isWithdraw,
                              String ip,String device,Integer stationId,Boolean joinActivity) throws Exception {
    System.out.println("=====================");
    Integer lotteryType = 5;
    //1、是否充值
    if(isRecharge) {
      Date rechargeTime = DateTimeTool.dateFormat(null, DateTimeTool.dateFormat("yyyy-MM-dd 18:00:00", beginDate));
      rechargeTime = DateTimeTool.addTime(rechargeTime,RandomUtil.getRangeRandom(0, 3*60*60-5*60));
      rechargeService.rechargeHandle(user.getUsername(), rechargeAmount, 1,"充值",stationId,rechargeTime,joinActivity);
      System.out.println(DateTimeTool.dateFormat(null, rechargeTime).concat(",充值").concat(rechargeAmount.toString()));
    }

    //2、两次下注操作，先随机生成下注赛道，以及下注金额
    int dropCount = 2;
    //当天晚上9点11分、9点36分下注北京赛车
    for (int i = 0; i < dropCount; i++) {
      String dropTime = null;
      if(i==0) {
        dropTime = DateTimeTool.dateFormat("yyyy-MM-dd 21:"+RandomUtil.getRangeRandom(11, 13)+":"+RandomUtil.getRangeRandom(10, 60)+"", beginDate);
      }else if(i==1) {
        dropTime = DateTimeTool.dateFormat("yyyy-MM-dd 21:"+RandomUtil.getRangeRandom(36, 39)+":"+RandomUtil.getRangeRandom(10, 60)+"", beginDate);
      }

      user = this.find(user.getId());

      BigDecimal allDropMoney = new BigDecimal(0);
      LotteryPeriods lotteryPeriods = lotteryPeriodsService.findLotteryPeriodsByDate(lotteryType, dropTime);
      if(lotteryPeriods != null) {

        List<LotteryOrderItemDTO> orderItemList = new ArrayList<LotteryOrderItemDTO>();
        LotteryRoom lotteryRoom = lotteryRoomService.getLotteryRoomList(lotteryType, stationId).get(0);
        if(lotteryRoom != null) {
          String paramName = "4-大";
          LotteryRule lotteryRule = lotteryRuleService.getLotteryRule(lotteryRoom.getLotteryHall().getId(), paramName, stationId);
          BigDecimal dropMoney = user.getBalance().add(user.getLotteryBalance()).divide(new BigDecimal(2)).setScale(0, BigDecimal.ROUND_DOWN);
          if(lotteryRule != null) {
            LotteryOrderItemDTO orderItem = new LotteryOrderItemDTO();
            orderItem.setRuleId(lotteryRule.getId());
            orderItem.setBetContent(paramName);
            orderItem.setBetMoney(dropMoney.toString());
            orderItemList.add(orderItem);

            allDropMoney = allDropMoney.add(dropMoney);
          }

          paramName = "4-小";
          lotteryRule = lotteryRuleService.getLotteryRule(lotteryRoom.getLotteryHall().getId(), paramName, stationId);
          dropMoney = user.getBalance().add(user.getLotteryBalance()).subtract(dropMoney).setScale(0, BigDecimal.ROUND_DOWN);
          if(lotteryRule != null) {
            LotteryOrderItemDTO orderItem = new LotteryOrderItemDTO();
            orderItem.setRuleId(lotteryRule.getId());
            orderItem.setBetContent(paramName);
            orderItem.setBetMoney(dropMoney.toString());
            orderItemList.add(orderItem);
            allDropMoney = allDropMoney.add(dropMoney);
          }

          String orderCode = RandomUtil.getSeqNumber("CP", "yyyyMMddHHmmss", 3);
          //投注
          lotteryOrderService.submitOrder(user, ip, device, orderCode, lotteryRoom.getId(), lotteryPeriods.getLotteryPeriods(),
            allDropMoney, orderItemList, stationId,false,DateTimeTool.dateFormat(null, dropTime),false);
          //派奖
          lotteryOrderService.lotteryAwardHandle(lotteryPeriods.getLotteryPeriods(),lotteryType,stationId,lotteryPeriods.getLotterySourceCollectTime());
          System.out.println(dropTime.concat(",投注").concat(allDropMoney.toString()));
        }
      }
    }

    //是否提现
    if(isWithdraw) {
      Date applyTime = DateTimeTool.dateFormat(null, DateTimeTool.dateFormat("yyyy-MM-dd 22:00:00", beginDate));
      applyTime = DateTimeTool.addTime(applyTime,RandomUtil.getRangeRandom(0, 30*60));
      //System.out.println("applyTime:"+applyTime);
      String timestamp = DateTimeTool.dateFormat("yyyyMMddHHmmss", applyTime);
      //System.out.println(timestamp);
      String securityCode = MD5Util.MD5Encode(user.getPayPassword().concat(timestamp), "UTF-8").toLowerCase();
      System.out.println(DateTimeTool.dateFormat(null, applyTime).concat(",提现").concat(user.getBalance().toString()).concat(","));

      BankCard bankCard = null;
      List<BankCard> bankCardList = bankCardService.getBankCardList(user.getUsername(),stationId);
      if(bankCardList !=null && bankCardList.size()>0) {
        bankCard = bankCardList.get(0);
      }

      if(bankCard !=null) {
        Withdraw withdraw = withdrawService.applyWithdraw(user,ip,device,timestamp,securityCode, bankCard.getId(), user.getBalance(),applyTime);
        if(withdraw !=null && withdraw.getStatus()==1) {
          Date handleTime = DateTimeTool.getDaysByDate2Minute(10, applyTime);
          withdrawService.withdrawHandle(withdraw.getId(), 1, "提现成功", stationId,handleTime);
        }
      }
    }
  }
	
	
	/**
	 * 注销下级账号
	 * @param user
	 * @param username
	 * @param ip
	 * @param device
	 * @return
	 * @throws Exception
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public User disableUser(User user,String username,String ip,String device,Integer stationId) throws Exception {
		this.validateUser(user, true, true);//验证用户
		
		if(user.getUserType()!=3 && user.getUserType()!=2) {
			throw new BusinessException(ApiUtil.getErrorCode("151"));
		}
		
		User childUser = this.findUser(username,stationId);
		if(childUser==null) {
			throw new BusinessException(ApiUtil.getErrorCode("152"));
		}
		
		if(childUser.getParent() == null || childUser.getParent().getId() != user.getId()) {
			throw new BusinessException(ApiUtil.getErrorCode("153"));
		}
		
		if(user.getUserType()==2 && childUser.getUserType() != 3) {
			throw new BusinessException(ApiUtil.getErrorCode("155"));
		}
		
		if(user.getUserType()==3 && childUser.getUserType() != 4) {
			throw new BusinessException(ApiUtil.getErrorCode("156"));
		}
		
		childUser.setStatus(0);
		this.update(childUser); 

		memberLogService.saveLog(user, username, "注销下级账号", ip, device);
		return user;
	}

	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void subTransfer(Integer stationId,Integer uid,Integer pid) throws Exception {
		User user = this.find(uid);
		this.validateUser(user, true, true);//验证用户
		
		if(user.getStation().getId() != stationId) {
			throw new BusinessException(ApiUtil.getErrorCode("117"));
		}
		
		if(pid != null) {
			User parent = this.find(pid);
			if(parent == null || parent.getStatus()==0 || parent.getStation().getId() != stationId) {
				throw new BusinessException(ApiUtil.getErrorCode("119"));
			}
			
			if(user.getUserType()==1 && parent.getUserType() != 3) {
				throw new BusinessException(ApiUtil.getErrorCode("119"));
			}
			
			if(user.getUserType()== 3 && parent.getUserType() != 2) {
				throw new BusinessException(ApiUtil.getErrorCode("119"));
			}
			
			if(user.getUserType() == 2) {
				throw new BusinessException(ApiUtil.getErrorCode("119"));
			}

			if(user.getUserType()==1 && parent.getUserType() == 3) {
				if(user.getParent()==null || user.getParent().getId() != parent.getId()) {
					
					LotteryDailyOrderTotal lotteryDailyOrderTotal = lotteryDailyOrderTotalService.getLotteryDailyOrderTotal(DateTimeTool.dateFormat("yyyy-MM-dd", new Date()), parent.getId());
					if(lotteryDailyOrderTotal==null) {
						lotteryDailyOrderTotal = new LotteryDailyOrderTotal();
						
						lotteryDailyOrderTotal.setUser(parent);//玩家帐号
						lotteryDailyOrderTotal.setTotalDate(DateTimeTool.dateFormat("yyyy-MM-dd", DateTimeTool.dateFormat("yyyy-MM-dd", new Date())));//统计日期
						lotteryDailyOrderTotal.setBetMoney(new BigDecimal(0));//流水金额
						lotteryDailyOrderTotal.setProfitMoney(new BigDecimal(0));//盈亏金额
						lotteryDailyOrderTotal.setBackWaterMoney(new BigDecimal(0));// //回水金额
						lotteryDailyOrderTotal.setRechargeMoney(new BigDecimal(0));//充值金额
						lotteryDailyOrderTotal.setWithdrawMoney(new BigDecimal(0));//提现金额
						lotteryDailyOrderTotal.setWinMoney(new BigDecimal(0));//中奖金额
						lotteryDailyOrderTotal.setExpandUserNum(1);//拓展玩家数
						lotteryDailyOrderTotal.setBalance(parent.getBalance());
						lotteryDailyOrderTotal.setLotteryBalance(user.getLotteryBalance());//赠送彩金统计
						lotteryDailyOrderTotalService.save(lotteryDailyOrderTotal);
					}else{
						lotteryDailyOrderTotal.setBalance(parent.getBalance());
						lotteryDailyOrderTotal.setLotteryBalance(user.getLotteryBalance());//赠送彩金统计
						lotteryDailyOrderTotal.setExpandUserNum(lotteryDailyOrderTotal.getExpandUserNum()+1);//拓展玩家数
						lotteryDailyOrderTotalService.update(lotteryDailyOrderTotal);
					}
				}
			}
			user.setParent(parent);
			this.update(user);
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public User weChatLogin(String openId,String nickname,String headImg,String ip,String device,Integer stationId) {
		User user = this.checkUser(openId,stationId);
		if(user == null) {
			Station station = stationService.find(stationId);
			if(station == null || station.getStatus() !=1) {
				throw new BusinessException(ApiUtil.getErrorCode("108"));
			}
			
			String username = this.getHanShort(nickname);
			user = this.findUser(username,station.getId());
			if(user!=null) {
				username = username.concat(RandomUtil.generateInt(4));
			}
			
			user = new User();
			user.setParent(null);
			user.setHeadImg(headImg);
			user.setUsername(username);
			user.setPassword(MD5Util.MD5Encode(MD5Util.MD5Encode("123456", "utf-8").toLowerCase(),"utf-8").toLowerCase());
			user.setNickName(username);//游戏昵称
			user.setUserType(1);//0游客、1玩家、2代理、3推广员、4虚拟号
			user.setCreateTime(new Date());
			user.setStatus(1);
			user.setBalance(new BigDecimal(0));//余额
			user.setLotteryBalance(new BigDecimal(0));//彩金
			user.setFreezeBalance(new BigDecimal(0));//冻结金额
			user.setRegSource(device);//注册来源
			user.setIsGag(1);//是否被禁言（1是 0否）
			user.setIsBet(1);//是否允许下注
			user.setPoints(0);
			user.setPointsLevel(pointsLevelService.findPointsLevel(0));
			user.setStation(station);
			user.setFlowRequire(new BigDecimal(0));
			user.setWechatOpenId(openId);
			this.save(user);
		}
		
		if(user.getStatus()==0) {
			throw new BusinessException(ApiUtil.getErrorCode("130"));
		}
		
		user.setHeadImg(headImg);
		user.setLoginNum(user.getLoginNum()!=null?user.getLoginNum()+1:1);
		user.setLoginLastDate(user.getLoginDate());
		user.setLoginDate(new Date());
		this.update(user);
		
		this.resetToken(user);//token重置
		
		memberLogService.saveLog(user, null, "微信授权登陆", ip, device);
		return user;
	}
	
	private String getHanShort(String s) {
		StringBuffer sbr = new StringBuffer();
		String reg_charset = "[\u4E00-\u9FA5]";
		Pattern p = Pattern.compile(reg_charset);     
	    Matcher m = p.matcher(s);     
	    while (m.find()) {
	     sbr.append(m.group(0));
	    }
	    if(sbr.length()==0) {
	    	sbr.append(s);
	    }
	    return sbr.length() >= 2?sbr.substring(0, 2):sbr.toString();
	    
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void kickOutUsers(String username,Integer stationId) {
		User user = this.findUser(username, stationId);
		this.resetToken(user);//token重置
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void initVirtualDataV2(User user,Date beginDate,Boolean isRecharge,BigDecimal rechargeAmount,Boolean isGiveLotteryAmount,BigDecimal giveLotteryAmount,Boolean isWithdraw,
			String ip,String device,Integer stationId,Boolean joinActivity) throws Exception {
		System.out.println("=====================");
		Integer lotteryType = 3;
		
		int minMinute = RandomUtil.getRangeRandom(1, 5) * 10 + 1;
		int maxMinute = minMinute + 2;
		String rechargeTimeStr =  DateTimeTool.dateFormat("yyyy-MM-dd "+RandomUtil.getRangeRandom(14,21)+":"+RandomUtil.getRangeRandom(minMinute, maxMinute)+":"+RandomUtil.getRangeRandom(10, 60)+"", beginDate);
		Date rechargeTime = DateTimeTool.dateFormat(null, rechargeTimeStr);
		//System.out.println("生成时间："+rechargeTimeStr);
		
		user = this.find(user.getId());
		//1、是否充值
		if(isRecharge) {
			rechargeService.rechargeHandle(user.getUsername(), rechargeAmount, 1,"充值",stationId,rechargeTime,joinActivity);
			System.out.println(DateTimeTool.dateFormat(null, rechargeTime).concat(",充值").concat(rechargeAmount.toString()));
			
			if(isGiveLotteryAmount){//是否送彩金
				rechargeTime = DateTimeTool.addTime(rechargeTime, RandomUtil.getRangeRandom(5,30));
				rechargeService.rechargeHandle(user.getUsername(), giveLotteryAmount, 2,"后台充值彩金",stationId,rechargeTime,true);
				System.out.println(DateTimeTool.dateFormat(null, rechargeTime).concat(",手工送彩金").concat(giveLotteryAmount.toString()));
			}
		}

		//2、下注操作，投注幸运飞艇 初级房 冠军大 冠军小
		BigDecimal allDropMoney = new BigDecimal(0);
		Date dropTimeDate = DateTimeTool.addTime(rechargeTime, 60*5*RandomUtil.getRangeRandom(1,5));
		dropTimeDate = DateTimeTool.addTime(rechargeTime, RandomUtil.getRangeRandom(1,15));
		String dropTime = DateTimeTool.dateFormat(null, dropTimeDate);
		//System.out.println("下注时间："+dropTime);
		LotteryPeriods lotteryPeriods = lotteryPeriodsService.findLotteryPeriodsByDate(lotteryType, dropTime);
		if(lotteryPeriods != null) {
			List<LotteryOrderItemDTO> orderItemList = new ArrayList<LotteryOrderItemDTO>();	
			LotteryRoom lotteryRoom = lotteryRoomService.getLotteryRoomList(lotteryType, stationId,"初级厅").get(0);
			if(lotteryRoom != null) {
				String paramName = "1-大";
				LotteryRule lotteryRule = lotteryRuleService.getLotteryRule(lotteryRoom.getLotteryHall().getId(), paramName, stationId);
				BigDecimal dropMoney = user.getBalance().add(user.getLotteryBalance()).divide(new BigDecimal(2)).setScale(0, BigDecimal.ROUND_DOWN);
				if(lotteryRule != null) {
					LotteryOrderItemDTO orderItem = new LotteryOrderItemDTO(); 
					orderItem.setRuleId(lotteryRule.getId());
					orderItem.setBetContent(paramName);
					orderItem.setBetMoney(dropMoney.toString());
					orderItemList.add(orderItem);
					
					allDropMoney = allDropMoney.add(dropMoney);
				}
				
				paramName = "1-小";
				lotteryRule = lotteryRuleService.getLotteryRule(lotteryRoom.getLotteryHall().getId(), paramName, stationId);
				dropMoney = user.getBalance().add(user.getLotteryBalance()).subtract(dropMoney).setScale(0, BigDecimal.ROUND_DOWN);
				if(lotteryRule != null) {
					LotteryOrderItemDTO orderItem = new LotteryOrderItemDTO(); 
					orderItem.setRuleId(lotteryRule.getId());
					orderItem.setBetContent(paramName);
					orderItem.setBetMoney(dropMoney.toString());
					orderItemList.add(orderItem);
					allDropMoney = allDropMoney.add(dropMoney);
				}
				
				String orderCode = RandomUtil.getSeqNumber("CP", "yyyyMMddHHmmss", 3);
				//投注
				lotteryOrderService.submitOrder(user, ip, device, orderCode, lotteryRoom.getId(), lotteryPeriods.getLotteryPeriods(), 
						allDropMoney, orderItemList, stationId,false,DateTimeTool.dateFormat(null, dropTime),false);
				//派奖
				lotteryOrderService.lotteryAwardHandle(lotteryPeriods.getLotteryPeriods(),lotteryType,stationId,lotteryPeriods.getLotterySourceCollectTime());
				System.out.println(dropTime.concat(",投注").concat(allDropMoney.toString()));
			}else{
				System.out.println(dropTime+",找不到对应房间");
			}
		}else{
			System.out.println(dropTime+",找不到对应期数");
		}

		//是否提现
		if(isWithdraw) {
			Date applyTime = DateTimeTool.dateFormat(null, dropTime);
			applyTime = DateTimeTool.addTime(applyTime, 60*5*RandomUtil.getRangeRandom(5,10));
			applyTime = DateTimeTool.addTime(applyTime, RandomUtil.getRangeRandom(1,15));
			//System.out.println("applyTime:"+applyTime);
			String timestamp = DateTimeTool.dateFormat("yyyyMMddHHmmss", applyTime);
			//System.out.println(timestamp);
			String securityCode = MD5Util.MD5Encode(user.getPayPassword().concat(timestamp), "UTF-8").toLowerCase();
			System.out.println(DateTimeTool.dateFormat(null, applyTime).concat(",提现").concat(user.getBalance().toString()).concat(","));
			
			BankCard bankCard = null;
			List<BankCard> bankCardList = bankCardService.getBankCardList(user.getUsername(),stationId);
			if(bankCardList !=null && bankCardList.size()>0) {
				bankCard = bankCardList.get(0);
			}
			
			if(bankCard !=null) {
				Withdraw withdraw = withdrawService.applyWithdraw(user,ip,device,timestamp,securityCode, bankCard.getId(), user.getBalance(),applyTime);
				if(withdraw !=null && withdraw.getStatus()==1) {
					Date handleTime = DateTimeTool.getDaysByDate2Minute(10, applyTime);
					withdrawService.withdrawHandle(withdraw.getId(), 1, "提现成功", stationId,handleTime);
				}
			}
		}
	}

	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void initVirtualDataV4(User user,Date beginDate,Boolean isRecharge,BigDecimal rechargeAmount,Boolean isGiveLotteryAmount,BigDecimal giveLotteryAmount,Boolean isWithdraw,
																String ip,String device,Integer stationId,Boolean joinActivity) throws Exception {
		System.out.println("=====================");
		Integer lotteryType = 4;

		int minMinute = RandomUtil.getRangeRandom(1, 5) * 10 + 1;
		int maxMinute = minMinute + 2;
		String rechargeTimeStr =  DateTimeTool.dateFormat("yyyy-MM-dd "+RandomUtil.getRangeRandom(14,20)+":"+RandomUtil.getRangeRandom(minMinute, maxMinute)+":"+RandomUtil.getRangeRandom(10, 60)+"", beginDate);
		Date rechargeTime = DateTimeTool.dateFormat(null, rechargeTimeStr);
		//System.out.println("生成时间："+rechargeTimeStr);

		user = this.find(user.getId());
		//1、是否充值
		if(isRecharge) {
			rechargeService.rechargeHandle(user.getUsername(), rechargeAmount, 1,"充值",stationId,rechargeTime,joinActivity);
			System.out.println(DateTimeTool.dateFormat(null, rechargeTime).concat(",充值").concat(rechargeAmount.toString()));

			if(isGiveLotteryAmount){//是否送彩金
				rechargeTime = DateTimeTool.addTime(rechargeTime, RandomUtil.getRangeRandom(5,30));
				rechargeService.rechargeHandle(user.getUsername(), giveLotteryAmount, 2,"后台充值彩金",stationId,rechargeTime,true);
				System.out.println(DateTimeTool.dateFormat(null, rechargeTime).concat(",手工送彩金").concat(giveLotteryAmount.toString()));
			}
		}

		//2、下注操作，投注幸运飞艇 初级房 冠军大 冠军小
		BigDecimal allDropMoney = new BigDecimal(0);
		//Date dropTimeDate = DateTimeTool.addTime(rechargeTime, 60*5*RandomUtil.getRangeRandom(1,5));
		//dropTimeDate = DateTimeTool.addTime(rechargeTime, RandomUtil.getRangeRandom(1,15));
		//String dropTime = DateTimeTool.dateFormat(null, dropTimeDate);

		String dropTime = DateTimeTool.dateFormat("yyyy-MM-dd 21:"+RandomUtil.getRangeRandom(11, 13)+":"+RandomUtil.getRangeRandom(10, 60)+"", beginDate);
		//System.out.println("下注时间："+dropTime);
		LotteryPeriods lotteryPeriods = lotteryPeriodsService.findLotteryPeriodsByDate(lotteryType, dropTime);
		if(lotteryPeriods != null) {
			List<LotteryOrderItemDTO> orderItemList = new ArrayList<LotteryOrderItemDTO>();
			LotteryRoom lotteryRoom = lotteryRoomService.getLotteryRoomList(lotteryType, stationId,"初级厅").get(0);
			if(lotteryRoom != null) {
				String paramName = "1-大";
				LotteryRule lotteryRule = lotteryRuleService.getLotteryRule(lotteryRoom.getLotteryHall().getId(), paramName, stationId);
				BigDecimal dropMoney = user.getBalance().add(user.getLotteryBalance()).divide(new BigDecimal(2)).setScale(0, BigDecimal.ROUND_DOWN);
				if(lotteryRule != null) {
					LotteryOrderItemDTO orderItem = new LotteryOrderItemDTO();
					orderItem.setRuleId(lotteryRule.getId());
					orderItem.setBetContent(paramName);
					orderItem.setBetMoney(dropMoney.toString());
					orderItemList.add(orderItem);

					allDropMoney = allDropMoney.add(dropMoney);
				}

				paramName = "1-小";
				lotteryRule = lotteryRuleService.getLotteryRule(lotteryRoom.getLotteryHall().getId(), paramName, stationId);
				dropMoney = user.getBalance().add(user.getLotteryBalance()).subtract(dropMoney).setScale(0, BigDecimal.ROUND_DOWN);
				if(lotteryRule != null) {
					LotteryOrderItemDTO orderItem = new LotteryOrderItemDTO();
					orderItem.setRuleId(lotteryRule.getId());
					orderItem.setBetContent(paramName);
					orderItem.setBetMoney(dropMoney.toString());
					orderItemList.add(orderItem);
					allDropMoney = allDropMoney.add(dropMoney);
				}

				String orderCode = RandomUtil.getSeqNumber("CP", "yyyyMMddHHmmss", 3);
				//投注
				lotteryOrderService.submitOrder(user, ip, device, orderCode, lotteryRoom.getId(), lotteryPeriods.getLotteryPeriods(),
					allDropMoney, orderItemList, stationId,false,DateTimeTool.dateFormat(null, dropTime),false);
				//派奖
				lotteryOrderService.lotteryAwardHandle(lotteryPeriods.getLotteryPeriods(),lotteryType,stationId,lotteryPeriods.getLotterySourceCollectTime());
				System.out.println(dropTime.concat(",投注").concat(allDropMoney.toString()));
			}else{
				System.out.println(dropTime+",找不到对应房间");
			}
		}else{
			System.out.println(dropTime+",找不到对应期数");
		}

		//是否提现
		if(isWithdraw) {
			Date applyTime = DateTimeTool.dateFormat(null, dropTime);
			applyTime = DateTimeTool.addTime(applyTime, 60*5*RandomUtil.getRangeRandom(5,10));
			applyTime = DateTimeTool.addTime(applyTime, RandomUtil.getRangeRandom(1,15));
			//System.out.println("applyTime:"+applyTime);
			String timestamp = DateTimeTool.dateFormat("yyyyMMddHHmmss", applyTime);
			//System.out.println(timestamp);
			String securityCode = MD5Util.MD5Encode(user.getPayPassword().concat(timestamp), "UTF-8").toLowerCase();
			System.out.println(DateTimeTool.dateFormat(null, applyTime).concat(",提现").concat(user.getBalance().toString()).concat(","));

			BankCard bankCard = null;
			List<BankCard> bankCardList = bankCardService.getBankCardList(user.getUsername(),stationId);
			if(bankCardList !=null && bankCardList.size()>0) {
				bankCard = bankCardList.get(0);
			}

			if(bankCard !=null) {
				Withdraw withdraw = withdrawService.applyWithdraw(user,ip,device,timestamp,securityCode, bankCard.getId(), user.getBalance(),applyTime);
				if(withdraw !=null && withdraw.getStatus()==1) {
					Date handleTime = DateTimeTool.getDaysByDate2Minute(10, applyTime);
					withdrawService.withdrawHandle(withdraw.getId(), 1, "提现成功", stationId,handleTime);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		BigDecimal dropMoney = new BigDecimal(8232);
		dropMoney = dropMoney.divide(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(100));
		String betMoney = new DecimalFormat("#00.00").format(dropMoney.doubleValue());
		System.out.println(betMoney);
	}
}
