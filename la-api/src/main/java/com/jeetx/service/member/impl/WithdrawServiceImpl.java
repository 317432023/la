package com.jeetx.service.member.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.base.WithdrawConfig;
import com.jeetx.bean.lottery.LotteryDailyOrderTotal;
import com.jeetx.bean.member.BankCard;
import com.jeetx.bean.member.TransRecord;
import com.jeetx.bean.member.User;
import com.jeetx.bean.member.Withdraw;
import com.jeetx.common.exception.BusinessException;
import com.jeetx.controller.api.ApiUtil;
import com.jeetx.service.base.WithdrawConfigService;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryDailyOrderTotalService;
import com.jeetx.service.lottery.LotteryOrderService;
import com.jeetx.service.member.BankCardService;
import com.jeetx.service.member.LetterService;
import com.jeetx.service.member.MemberLogService;
import com.jeetx.service.member.RechargeService;
import com.jeetx.service.member.TransRecordService;
import com.jeetx.service.member.UserService;
import com.jeetx.service.member.WithdrawService;
import com.jeetx.service.system.StationConfigService;
import com.jeetx.util.DateTimeTool;
import com.jeetx.util.MD5Util;
import com.jeetx.util.RandomUtil;

@Service
@Transactional
public class WithdrawServiceImpl extends DaoSupport<Withdraw> implements WithdrawService {

	@Autowired UserService userService;
	@Autowired BankCardService bankCardService;
	@Autowired TransRecordService transRecordService;
	@Autowired LotteryDailyOrderTotalService lotteryDailyOrderTotalService;
	@Autowired LetterService letterService;
	@Autowired WithdrawConfigService withdrawConfigService;
	@Autowired MemberLogService memberLogService;
	@Autowired StationConfigService stationConfigService;
	@Autowired RechargeService rechargeService;
	@Autowired LotteryOrderService lotteryOrderService;
	
	public Withdraw getLastWithdraw(Integer userId) {
		List<Withdraw> list = this.getSession().createQuery("from Withdraw o where o.user.id = ? and o.status in (1,2) order by createTime desc").setParameter(0, userId).setFirstResult(0).setMaxResults(1).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (Withdraw) list.get(0);
		}
	} 
	
	/** * 判断是否新用户提现*/
//	public boolean isNewUserWithdraw(User user) throws Exception {
//		Long count = (Long)this.getSession().createQuery("select count(o.id) from Withdraw o where o.user.id = ?").setParameter(0, user.getId()).uniqueResult();
//		if(count == null || count == 0) {
//			return true;
//		}
//		return false;
//	}
	
	public Long getWithdrawCount(String queryDate,Integer userId) {
		BigInteger counts = (BigInteger)this.getSession().createSQLQuery("SELECT COUNT(t.id) from tb_member_withdraw t where t.`status` in (1,2) and t.user_id = "+userId+" and date_format(t.create_time,'%Y-%m-%d') = '"+queryDate+"'").uniqueResult();
		if(counts != null) {
			return counts.longValue();
		}
		return 0l;
	}
	
	
	@SuppressWarnings("unchecked")
	public Withdraw findWithdraw(String username,Integer id) {
		List<Withdraw> list = this.getSession().createQuery("from Withdraw o where o.user.username = ? and o.id = ? ")
				.setParameter(0, username).setParameter(1, id).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (Withdraw) list.get(0);
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void withdrawHandle(Integer id,Integer type,String remark,Integer stationId,Date handleTime) throws Exception {
		Withdraw withdraw = this.find(id);
		if(withdraw==null || withdraw.getStatus()!=1) {
			throw new BusinessException(ApiUtil.getErrorCode("121"));
		}

		User user = userService.find(withdraw.getUser().getId());
		if(user.getStation().getId() != stationId) {
			throw new BusinessException(ApiUtil.getErrorCode("117"));
		}
		
		switch (type) {
		case 1:
			//同意提现
			withdraw.setStatus(2);//状态（1申请中、2提现成功、3打款失败）
			withdraw.setConfirmTime(handleTime);
			withdraw.setConfirmRemark("提现成功");
			this.update(withdraw);
		
			//扣除冻结金额、扣除总积分
			user.setFreezeBalance(user.getFreezeBalance().subtract(withdraw.getApplyAmount()));
			if(user.getFreezeBalance().compareTo(new BigDecimal(0))<0) {
				throw new BusinessException(ApiUtil.getErrorCode("122"));
			}
			
			user.setBalance(user.getBalance().subtract(withdraw.getApplyAmount()));
			if(user.getBalance().compareTo(new BigDecimal(0))<0) {
				throw new BusinessException(ApiUtil.getErrorCode("123"));
			}
			this.update(user);
			
			//交易明细
			TransRecord transRecord = new TransRecord();
			transRecord.setUser(user);//用户
			transRecord.setCreateTime(handleTime);//交易时间
			transRecord.setTransCategory(2);//交易类型（1充值、2提现、3抽奖、4投注、5撤单、6赠送、7中奖、8回水、9调账）
			transRecord.setTransAmount(withdraw.getApplyAmount());//交易账户金额
			transRecord.setEndBalance(user.getBalance());//剩余账户金额
			transRecord.setTransLotteryAmount(new BigDecimal(0));//交易彩金
			transRecord.setEndLotteryBalance(new BigDecimal(0));//剩余彩金
			transRecord.setRemark(StringUtils.isNotBlank(remark)?"提现成功,".concat(remark):"提现成功");
			transRecord.setFlag(0);//1加,0扣
			transRecordService.save(transRecord);
			
			//提现统计
			LotteryDailyOrderTotal lotteryDailyOrderTotal = lotteryDailyOrderTotalService.getLotteryDailyOrderTotal(DateTimeTool.dateFormat("yyyy-MM-dd", handleTime), user.getId());
			if(lotteryDailyOrderTotal==null) {
				lotteryDailyOrderTotal = new LotteryDailyOrderTotal();
				lotteryDailyOrderTotal.setUser(user);//玩家帐号
				lotteryDailyOrderTotal.setTotalDate(DateTimeTool.dateFormat("yyyy-MM-dd", DateTimeTool.dateFormat("yyyy-MM-dd", handleTime)));//统计日期
				lotteryDailyOrderTotal.setBetMoney(new BigDecimal(0));//流水金额
				lotteryDailyOrderTotal.setProfitMoney(new BigDecimal(0));//盈亏金额
				lotteryDailyOrderTotal.setBackWaterMoney(new BigDecimal(0));// //回水金额
				lotteryDailyOrderTotal.setRechargeMoney(new BigDecimal(0));//充值金额
				lotteryDailyOrderTotal.setWithdrawMoney(withdraw.getApplyAmount());//提现金额
				lotteryDailyOrderTotal.setWinMoney(new BigDecimal(0));//中奖金额
				lotteryDailyOrderTotal.setExpandUserNum(0);//拓展玩家数
				lotteryDailyOrderTotal.setBalance(user.getBalance());
				lotteryDailyOrderTotal.setLotteryBalance(user.getLotteryBalance());//赠送彩金统计
				lotteryDailyOrderTotalService.save(lotteryDailyOrderTotal);
			}else{
				lotteryDailyOrderTotal.setBalance(user.getBalance());
				lotteryDailyOrderTotal.setLotteryBalance(user.getLotteryBalance());//赠送彩金统计
				lotteryDailyOrderTotal.setWithdrawMoney(lotteryDailyOrderTotal.getWithdrawMoney().add(withdraw.getApplyAmount()));//提现金额
				lotteryDailyOrderTotalService.update(lotteryDailyOrderTotal);
			}
			
			//发站内信
			letterService.sendLetter(user, "提现受理成功", "本次提现金额：".concat(withdraw.getApplyAmount().toString()).concat(",款项已打至您提交的银行号,请注意查收").concat(",如有异议请联系客服。"));
			break;
		case 2:
			//拒绝提现
			withdraw.setStatus(3);//状态（1申请中、2提现成功、3打款失败）
			withdraw.setConfirmTime(handleTime);
			withdraw.setConfirmRemark("提现被拒绝，拒绝原因：".concat(remark));
			this.update(withdraw);
		
			//冻结积分解冻
			user.setFreezeBalance(user.getFreezeBalance().subtract(withdraw.getApplyAmount()));
			if(user.getFreezeBalance().compareTo(new BigDecimal(0))<0) {
				throw new BusinessException(ApiUtil.getErrorCode("122"));
			}
			this.update(user);
			
			//发站内信
			letterService.sendLetter(user, "提现受理失败", "本次提现金额：".concat(withdraw.getApplyAmount().toString()).concat(",处理不通过理由：").concat(remark).concat(",如有异议请联系客服。"));
			
			break;
		default:
			throw new BusinessException(ApiUtil.getErrorCode("102"));
		}
	}
	

	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public Withdraw applyWithdraw(User user,String ip,String device,String timestamp,
			String securityCode,Integer bankCardId,BigDecimal applyAmount,Date applyTime) throws Exception {
		user = userService.find(user.getId());
		userService.validateUser(user, true, true);//验证用户
		
//		//1、验证码安全码 MD5(安全码(两次MD5加密后的结果，结果小写)+timestamp),结果小写
//		if(StringUtils.isBlank(user.getPayPassword())) {
//			throw new BusinessException(ApiUtil.getErrorCode("158"));
//		}
//		
//		String securityCodeTemp = MD5Util.MD5Encode(user.getPayPassword().concat(timestamp), "UTF-8").toLowerCase();
//		if(!securityCodeTemp.equals(securityCode)) {
//			throw new BusinessException(ApiUtil.getErrorCode("142"));
//		}
		
		//2、验证银行卡信息
		BankCard bankCard = bankCardService.findBankCard(user.getUsername(), bankCardId,user.getStation().getId());
		if(bankCard == null) {
			throw new BusinessException(ApiUtil.getErrorCode("143"));
		}
		
		//3、验证码提现设置
		WithdrawConfig withdrawConfig = withdrawConfigService.getWithdrawConfigByStationId(user.getStation().getId());
		if(withdrawConfig != null) {
			if(StringUtils.isNotBlank(withdrawConfig.getBeginTime()) || StringUtils.isNotBlank(withdrawConfig.getEndTime())) {
		        Date startTime = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", DateTimeTool.dateFormat("yyyy-MM-dd", applyTime).concat(" ").concat(withdrawConfig.getBeginTime()));
		        Date endTime = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", DateTimeTool.dateFormat("yyyy-MM-dd", applyTime).concat(" ").concat(withdrawConfig.getEndTime()));
		        if(startTime.getHours()>endTime.getHours()) {
		        	if(new Date().getHours()>endTime.getHours()) {
		        		endTime = DateTimeTool.getDaysByDate2Days(-1, endTime);
		        	}else if(new Date().getHours()<=endTime.getHours()) {
		        		startTime = DateTimeTool.getDaysByDate2Days(1, startTime);
		        	}
		        }

		        //System.out.println(DateTimeTool.dateFormat(null, startTime));
		        //System.out.println(DateTimeTool.dateFormat(null, endTime));
		        if(!DateTimeTool.isEffectiveDate(applyTime, startTime, endTime)) {
		        	throw new BusinessException(ApiUtil.getErrorCode("144").concat(",非允许提现时间"));
		        }
			}

			if(applyAmount.compareTo(withdrawConfig.getMaxApplyAmount())>0) {
				throw new BusinessException(ApiUtil.getErrorCode("144").concat(",超过最大允许提现金额").concat(withdrawConfig.getMaxApplyAmount().toString()));
			}
			
			if(applyAmount.compareTo(withdrawConfig.getMinApplyAmount())<0) {
				throw new BusinessException(ApiUtil.getErrorCode("144").concat(",不得低于最小提现金额").concat(withdrawConfig.getMinApplyAmount().toString()));
			}

			String queryDate = DateTimeTool.dateFormat("yyyy-MM-dd", applyTime);
			Long counts = this.getWithdrawCount(queryDate, user.getId());
			//System.out.println(counts);
			if(counts != null && counts >= withdrawConfig.getApplyDailyTimes()) {
				throw new BusinessException(ApiUtil.getErrorCode("144").concat(",当天申请次数已达上限"));
			}
		}
		
		//4、开启提现卡流水功能
		
		String isOpenLimitAccount = stationConfigService.getValueByName("is_open_limit_account", user.getStation().getId());
		if(user.getUserType() == 1 && StringUtils.isNotBlank(isOpenLimitAccount) && "1".equalsIgnoreCase(isOpenLimitAccount) 
				&& user.getFlowRequire().compareTo(new BigDecimal(0)) > 0) {
			throw new BusinessException(ApiUtil.getErrorCode("159").concat(",未达到流水要求[").concat(user.getFlowRequire()+"]").concat(""));
		}

		//5、验证可提现金额是否大于申请提现金额
		if(user.getBalance().subtract(user.getFreezeBalance()).compareTo(applyAmount) < 0) {
			throw new BusinessException(ApiUtil.getErrorCode("144").concat(",大于账户可提现金额"));
		}
		
		//6、生成申请单据、冻结提现金额
		Withdraw withdraw = new Withdraw();
		withdraw.setUser(user);
		withdraw.setTradeCode(RandomUtil.getSeqNumber("W", "yyyyMMddHHmmssSSS", 3));//流水号
		withdraw.setApplyAmount(applyAmount);//申请金额
		withdraw.setCreateTime(applyTime);//申请时间
		withdraw.setApplyRemark("申请提现");
		withdraw.setStatus(1);//状态（1申请中、2提现成功、3打款失败）
		withdraw.setBankCardInfo(bankCard.getBankName().concat(" ").concat(bankCard.getBankCard()).concat(" ").concat(bankCard.getCardholder()));//处理摘要信息
		this.save(withdraw);
		
		user.setFreezeBalance(user.getFreezeBalance().add(applyAmount));
		userService.update(user);

		memberLogService.saveLog(user, null, "提现申请", ip, device);
		
//		//如果非真实玩家提现，系统自动处理掉
//		if(StringUtils.isNotBlank(device) && !device.equalsIgnoreCase("H5") && withdraw != null && user.getUserType()!=1) {
//			Date handleTime = DateTimeTool.getDaysByDate2Minute(10, applyTime);
//			this.withdrawHandle(withdraw.getId(), 1, "提现成功",user.getStation().getId(),handleTime);
//		}
		
		return withdraw;
	}
	
}
