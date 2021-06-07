package com.jeetx.service.member.impl;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import com.jeetx.common.pay.fbd.FbdToolKit;
import com.jeetx.common.pay.yfb.YfbToolKit;
import com.jeetx.util.*;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import sun.misc.BASE64Encoder;

import com.jeetx.bean.base.RechargeOnLineConfig;
import com.jeetx.bean.lottery.LotteryActivity;
import com.jeetx.bean.lottery.LotteryActivityDuration;
import com.jeetx.bean.lottery.LotteryDailyOrderTotal;
import com.jeetx.bean.member.Recharge;
import com.jeetx.bean.member.TransRecord;
import com.jeetx.bean.member.User;
import com.jeetx.common.exception.BusinessException;
import com.jeetx.common.pay.alipays.AlipaysToolKit;
import com.jeetx.common.pay.bf.BfToolKit;
import com.jeetx.common.pay.ifuniu.IfuniuToolKit;
import com.jeetx.common.pay.jdzf.JdzfToolKit;
import com.jeetx.common.pay.lyf.LyfToolKit;
import com.jeetx.common.pay.mjf.MfjToolKit;
import com.jeetx.common.pay.msf.MsfToolKit;
import com.jeetx.common.pay.xPay.XpayToolKit;
import com.jeetx.common.pay.xl.XlToolKit;
import com.jeetx.common.pay.ytb.YtbToolKit;
import com.jeetx.controller.api.ApiUtil;
import com.jeetx.service.base.RechargeOnLineConfigService;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryActivityDurationService;
import com.jeetx.service.lottery.LotteryActivityService;
import com.jeetx.service.lottery.LotteryDailyOrderTotalService;
import com.jeetx.service.member.LetterService;
import com.jeetx.service.member.MemberLogService;
import com.jeetx.service.member.PointsLevelService;
import com.jeetx.service.member.RechargeService;
import com.jeetx.service.member.TransRecordService;
import com.jeetx.service.member.UserService;
import com.jeetx.service.system.StationConfigService;

@Service
@Transactional
public class RechargeServiceImpl extends DaoSupport<Recharge> implements RechargeService {

	@Autowired UserService userService;
	@Autowired RechargeService rechargeService;
	@Autowired TransRecordService transRecordService;
	@Autowired LotteryDailyOrderTotalService lotteryDailyOrderTotalService;
	@Autowired LetterService letterService;
	@Autowired PointsLevelService pointsLevelService;
	@Autowired RechargeOnLineConfigService rechargeOnLineConfigService; 
	@Autowired MemberLogService memberLogService;
	@Autowired LotteryActivityDurationService lotteryActivityDurationService;
	@Autowired LotteryActivityService lotteryActivityService;
	@Autowired StationConfigService stationConfigService;
	
	public BigDecimal sumAllRechargeAmount(User user,Date beginDate,Date endDate) throws Exception { 
		BigDecimal allMoney = new BigDecimal(0);
		if(beginDate == null) {
			allMoney = (BigDecimal)this.getSession().createQuery("select sum(o.rechargeAmount+o.lotteryAmount) from Recharge o where o.user.id = ? and o.status = 2 and o.createTime <=str_to_date(?,'%Y-%m-%d %H:%i:%s') ")
					.setParameter(0, user.getId()).setParameter(1, endDate).uniqueResult();
		}else {
			allMoney = (BigDecimal)this.getSession().createQuery("select sum(o.rechargeAmount+o.lotteryAmount) from Recharge o where o.user.id = ? and o.status = 2 and o.createTime >=str_to_date(?,'%Y-%m-%d %H:%i:%s') and o.createTime <=str_to_date(?,'%Y-%m-%d %H:%i:%s') ")
					.setParameter(0, user.getId()).setParameter(1, beginDate).setParameter(2, endDate).uniqueResult();
		}
		if(allMoney == null) {
			allMoney = new BigDecimal(0);
		}
		return allMoney;
	}
	
	@SuppressWarnings("unchecked")
	public Recharge findRechargeByTradeCode2PayType(String tradeCode,Integer payType) {
		List<Recharge> list = this.getSession().createQuery("from Recharge o where o.tradeCode = ? and o.payType = ? ").setParameter(0, tradeCode).setParameter(1, payType).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (Recharge) list.get(0);
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void rechargeHandle(String username,BigDecimal amount,Integer type,String remark,Integer stationId,Date createTime,Boolean joinActivity) throws Exception {
		User user = userService.findUser(username,stationId);
		userService.validateUser(user, true, true);//验证用户
		
		Integer flag = 1;//1加,0扣
		if(amount.compareTo(new BigDecimal(0))>0) {
			flag = 1;
		}else {
			flag = 0;
		}
		
		switch (type) {
		case 1://1账户充值
			Integer addPoints = flag==1?amount.setScale(0, BigDecimal.ROUND_DOWN).intValue():amount.multiply(new BigDecimal(-1)).setScale(0, BigDecimal.ROUND_DOWN).intValue();
			
			BigDecimal lotteryAmount = new BigDecimal(0);//赠送彩金
			if(joinActivity && flag==1) {
				//充值赠送积分活动
				boolean rebate = false;
				boolean isNewUser = isNewUserRecharge(user);//判断是否新用户
				boolean isFirstRecharge = isDailyFirstRecharge(user);//判断是否首充
				List<LotteryActivity> lotteryActivityList = lotteryActivityService.getListByStationId2Type(stationId,1);
				for (LotteryActivity lotteryActivity : lotteryActivityList) {
					if(lotteryActivity != null) {
						//判断是否叠加
						if(lotteryActivity.getIsSuperpose() == 0 && rebate) {
							continue;
						}
						
						BigDecimal lotteryAmount1 = activityLotteryAmount(lotteryActivity,user,amount,lotteryActivity.getId(),isFirstRecharge,isNewUser);
						if(lotteryAmount1.compareTo(new BigDecimal(0))>0) {
							rebate = true;
							remark = remark.concat(",").concat(lotteryActivity.getTitle());
							lotteryAmount = lotteryAmount.add(lotteryAmount1);
						}
					}
				}
			}
			
			user.setPoints(user.getPoints()+addPoints);
			user.setPointsLevel(pointsLevelService.findPointsLevel(user.getPoints().intValue()));
			user.setBalance(user.getBalance().add(amount));//账户金额
			user.setLotteryBalance(user.getLotteryBalance().add(lotteryAmount));//赠送彩金
			if(user.getBalance().subtract(user.getFreezeBalance()).compareTo(new BigDecimal(0))<0) {
				throw new BusinessException(ApiUtil.getErrorCode("118"));
			}
			
			String withdrawLimitAccount = stationConfigService.getValueByName("withdraw_limit_account", user.getStation().getId());
			if(flag==1 && StringUtils.isNotBlank(withdrawLimitAccount) && new BigDecimal(withdrawLimitAccount).compareTo(new BigDecimal(0)) > 0) {
				BigDecimal flowRequire = amount.add(lotteryAmount).multiply(new BigDecimal(withdrawLimitAccount)).setScale(2, BigDecimal.ROUND_HALF_UP);
				
				//开否新用户提现不卡流水
				String isOpenNewUserLimitAccount = stationConfigService.getValueByName("is_open_newUser_limit_account", user.getStation().getId());
				if(StringUtils.isNotBlank(isOpenNewUserLimitAccount) && "1".equalsIgnoreCase(isOpenNewUserLimitAccount) 
						&& rechargeService.getUserRechargeCount(user).intValue()<1) {
					flowRequire = new BigDecimal(0);
				}
				
				user.setFlowRequire(user.getFlowRequire().add(flowRequire));//流水要求
			}
			
			this.update(user);

			Recharge recharge = new Recharge();
			recharge.setUser(user);
			recharge.setTradeCode(RandomUtil.getSeqNumber("R", "yyyyMMddHHmmssSSS", 3));
			recharge.setRechargeAmount(amount);//充值金额
			recharge.setLotteryAmount(lotteryAmount);
			recharge.setCreateTime(createTime);//创建时间
			recharge.setTradeTime(createTime);//交易时间（到账时间）
			recharge.setStatus(2);//状态（1：初始化 2：交易成功 3：交易失败 4：异常待核实）
			recharge.setPayType(4);//支付方式（1支付宝、2微信、3网银转账、4后台手动充值、5扫码充值、6明捷支付、7后台充值彩金）
			recharge.setRemark(remark);
			rechargeService.save(recharge);
			
			TransRecord transRecord = new TransRecord();
			transRecord.setUser(user);//用户
			transRecord.setCreateTime(createTime);//交易时间
			transRecord.setTransCategory(9);//交易类型（1充值、2提现、3抽奖、4投注、5撤单、6赠送、7中奖、8回水、9调账）
			transRecord.setTransAmount(flag==1?amount:amount.multiply(new BigDecimal(-1)));//交易账户金额
			transRecord.setEndBalance(user.getBalance());//剩余账户金额
			transRecord.setTransLotteryAmount(lotteryAmount);//交易彩金
			transRecord.setEndLotteryBalance(user.getLotteryBalance());//剩余彩金
			transRecord.setRemark(remark);
			transRecord.setFlag(flag);
			transRecordService.save(transRecord);
			
			//充值统计
			LotteryDailyOrderTotal lotteryDailyOrderTotal = lotteryDailyOrderTotalService.getLotteryDailyOrderTotal(DateTimeTool.dateFormat("yyyy-MM-dd", createTime), user.getId());
			if(lotteryDailyOrderTotal==null) {
				lotteryDailyOrderTotal = new LotteryDailyOrderTotal();
				
				lotteryDailyOrderTotal.setUser(user);//玩家帐号
				lotteryDailyOrderTotal.setTotalDate(DateTimeTool.dateFormat("yyyy-MM-dd", DateTimeTool.dateFormat("yyyy-MM-dd", createTime)));//统计日期
				lotteryDailyOrderTotal.setBetMoney(new BigDecimal(0));//流水金额
				lotteryDailyOrderTotal.setProfitMoney(new BigDecimal(0));//盈亏金额
				lotteryDailyOrderTotal.setBackWaterMoney(new BigDecimal(0));// //回水金额
				lotteryDailyOrderTotal.setRechargeMoney(amount);//充值金额
				lotteryDailyOrderTotal.setWithdrawMoney(new BigDecimal(0));//提现金额
				lotteryDailyOrderTotal.setWinMoney(new BigDecimal(0));//中奖金额
				lotteryDailyOrderTotal.setExpandUserNum(0);//拓展玩家数
				lotteryDailyOrderTotal.setBalance(user.getBalance());
				lotteryDailyOrderTotal.setLotteryBalance(user.getLotteryBalance());//赠送彩金统计
				lotteryDailyOrderTotalService.save(lotteryDailyOrderTotal);
			}else{
				lotteryDailyOrderTotal.setBalance(user.getBalance());
				lotteryDailyOrderTotal.setLotteryBalance(user.getLotteryBalance());//赠送彩金统计
				lotteryDailyOrderTotal.setRechargeMoney(lotteryDailyOrderTotal.getRechargeMoney().add(amount));//充值金额
				lotteryDailyOrderTotalService.update(lotteryDailyOrderTotal);
			}
			
			//发站内信
			letterService.sendLetter(user, "充值到账通知", "本次调整金额：".concat(amount.toString()).concat(",调整理由：").concat(transRecord.getRemark()).concat(",如有异议请联系客服。"));
			
			break;
		case 2://2彩金充值
			addPoints = flag==1?amount.setScale(0, BigDecimal.ROUND_DOWN).intValue():amount.multiply(new BigDecimal(-1)).setScale(0, BigDecimal.ROUND_DOWN).intValue();
			user.setPoints(user.getPoints()+addPoints);
			user.setPointsLevel(pointsLevelService.findPointsLevel(user.getPoints().intValue()));
			user.setLotteryBalance(user.getLotteryBalance().add(amount));//彩金
			if(user.getLotteryBalance().compareTo(new BigDecimal(0))<0) {
				throw new BusinessException(ApiUtil.getErrorCode("118"));
			}
			
			withdrawLimitAccount = stationConfigService.getValueByName("withdraw_limit_account", user.getStation().getId());
			if(flag==1 && StringUtils.isNotBlank(withdrawLimitAccount) && new BigDecimal(withdrawLimitAccount).compareTo(new BigDecimal(0)) > 0) {
				BigDecimal flowRequire = amount.multiply(new BigDecimal(withdrawLimitAccount)).setScale(2, BigDecimal.ROUND_HALF_UP);
				
				//开否新用户提现不卡流水
				String isOpenNewUserLimitAccount = stationConfigService.getValueByName("is_open_newUser_limit_account", user.getStation().getId());
				if(StringUtils.isNotBlank(isOpenNewUserLimitAccount) && "1".equalsIgnoreCase(isOpenNewUserLimitAccount) 
						&& rechargeService.getUserRechargeCount(user).intValue()<1) {
					flowRequire = new BigDecimal(0);
				}
				
				user.setFlowRequire(user.getFlowRequire().add(flowRequire));//流水要求
			}
			
			this.update(user);
			
			recharge = new Recharge();
			recharge.setUser(user);
			recharge.setTradeCode(RandomUtil.getSeqNumber("R", "yyyyMMddHHmmssSSS", 3));
			recharge.setRechargeAmount(new BigDecimal(0));//充值金额
			recharge.setLotteryAmount(amount);
			recharge.setCreateTime(createTime);//创建时间
			recharge.setTradeTime(createTime);//交易时间（到账时间）
			recharge.setStatus(2);//状态（1：初始化 2：交易成功 3：交易失败 4：异常待核实）
			recharge.setPayType(7);//支付方式（1支付宝、2微信、3网银转账、4手台充值充值、5扫码充值、6明捷支付、7后台充值彩金）
			recharge.setRemark(remark);
			rechargeService.save(recharge);

			transRecord = new TransRecord();
			transRecord.setUser(user);//用户
			transRecord.setCreateTime(createTime);//交易时间
			transRecord.setTransCategory(9);//交易类型（1充值、2提现、3抽奖、4投注、5撤单、6赠送、7中奖、8回水、9调账）
			transRecord.setTransAmount(new BigDecimal(0));//交易账户金额
			transRecord.setEndBalance(user.getBalance());//剩余账户金额
			transRecord.setTransLotteryAmount(flag==1?amount:amount.multiply(new BigDecimal(-1)));//交易彩金
			transRecord.setEndLotteryBalance(user.getLotteryBalance());//剩余彩金
			transRecord.setRemark(remark);
			transRecord.setFlag(flag);
			transRecordService.save(transRecord);
			
			lotteryDailyOrderTotal = lotteryDailyOrderTotalService.getLotteryDailyOrderTotal(DateTimeTool.dateFormat("yyyy-MM-dd", createTime), user.getId());
			if(lotteryDailyOrderTotal==null) {
				lotteryDailyOrderTotal = new LotteryDailyOrderTotal();
				
				lotteryDailyOrderTotal.setUser(user);//玩家帐号
				lotteryDailyOrderTotal.setTotalDate(DateTimeTool.dateFormat("yyyy-MM-dd", DateTimeTool.dateFormat("yyyy-MM-dd", createTime)));//统计日期
				lotteryDailyOrderTotal.setBetMoney(new BigDecimal(0));//流水金额
				lotteryDailyOrderTotal.setProfitMoney(new BigDecimal(0));//盈亏金额
				lotteryDailyOrderTotal.setBackWaterMoney(new BigDecimal(0));// //回水金额
				lotteryDailyOrderTotal.setRechargeMoney(new BigDecimal(0));//充值金额
				lotteryDailyOrderTotal.setWithdrawMoney(new BigDecimal(0));//提现金额
				lotteryDailyOrderTotal.setWinMoney(new BigDecimal(0));//中奖金额
				lotteryDailyOrderTotal.setExpandUserNum(0);//拓展玩家数
				lotteryDailyOrderTotal.setBalance(user.getBalance());
				lotteryDailyOrderTotal.setLotteryBalance(user.getLotteryBalance());//赠送彩金统计
				lotteryDailyOrderTotalService.save(lotteryDailyOrderTotal);
			}else{
				lotteryDailyOrderTotal.setBalance(user.getBalance());
				lotteryDailyOrderTotal.setLotteryBalance(user.getLotteryBalance());//赠送彩金统计
				lotteryDailyOrderTotalService.update(lotteryDailyOrderTotal);
			}
			
			//发站内信
			letterService.sendLetter(user, "充值到账通知", "本次调整彩金：".concat(amount.toString()).concat(",调整理由：").concat(transRecord.getRemark()).concat(",如有异议请联系客服。"));
			break;
		default:
			throw new BusinessException(ApiUtil.getErrorCode("102"));
		}
	}
	
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public JSONObject submitOnlineRecharge(User user,String ip,String device,Integer providerId,String netwayCode,String amount) {
		JSONObject jsonbject = new JSONObject();
		userService.validateUser(user, true, true);//验证用户
		
		if(!Validation.isINTEGER_NEGATIVE(amount)) {
			throw new BusinessException(ApiUtil.getErrorCode("145")); 
		}
		BigDecimal rechargeAmount = new BigDecimal(amount).divide(new BigDecimal(100));//充值金额,分转成元

		RechargeOnLineConfig rechargeOnLineConfig = rechargeOnLineConfigService.find(providerId);
		if(rechargeOnLineConfig == null) {
			throw new BusinessException(ApiUtil.getErrorCode("146"));
		}
		
		if(rechargeAmount.compareTo(rechargeOnLineConfig.getMaxAmount())>0) {
			throw new BusinessException(ApiUtil.getErrorCode("147").concat(",超过最大允许充值金额").concat(rechargeOnLineConfig.getMaxAmount().toString()));
		}
		
		if(rechargeAmount.compareTo(rechargeOnLineConfig.getMinAmount())<0) {
			throw new BusinessException(ApiUtil.getErrorCode("147").concat(",不得低于最小充值金额").concat(rechargeOnLineConfig.getMinAmount().toString()));
		}
		
		Boolean bool = false;
		String qrcodeUrl = null;
		String orderNum = RandomUtil.getSeqNumber("R", "yyyyMMddHHmmssSSS", 3);
		System.out.println(rechargeOnLineConfig.getProviderType());
		switch (rechargeOnLineConfig.getProviderType()) {
		case 6://明捷支付-老狼
			try {
				Map<String, String> metaSignMap = new TreeMap<String, String>();
				metaSignMap.put("orderNum", orderNum);
				metaSignMap.put("version", "V3.0.0.0");
				metaSignMap.put("charset", MfjToolKit.CHARSET);//
				metaSignMap.put("randomNum", MfjToolKit.randomStr(4));// 4位随机数

				metaSignMap.put("merchNo", MfjToolKit.MERCH_NO);
				metaSignMap.put("netwayCode", netwayCode);// WX:微信支付,ZFB:支付宝支付
				metaSignMap.put("amount", amount);// 单位:分
				metaSignMap.put("goodsName", "点卡充值");// 商品名称：20位
				metaSignMap.put("callBackUrl", rechargeOnLineConfig.getNoticeURL());// 回调地址
				metaSignMap.put("callBackViewUrl", rechargeOnLineConfig.getNoticeURL());// 回显地址

				String metaSignJsonStr = MfjToolKit.mapToJson(metaSignMap);
				String sign = MfjToolKit.MD5(metaSignJsonStr + MfjToolKit.KEY, MfjToolKit.CHARSET);// 32位
				//System.out.println("sign=" + sign); // 英文字母大写
				metaSignMap.put("sign", sign);

				byte[] dataStr = MfjToolKit.encryptByPublicKey(MfjToolKit.mapToJson(metaSignMap).getBytes(MfjToolKit.CHARSET),MfjToolKit.PAY_PUBLIC_KEY);
				String param = new BASE64Encoder().encode(dataStr);
				String reqParam = "data=" + URLEncoder.encode(param, MfjToolKit.CHARSET) + "&merchNo=" + metaSignMap.get("merchNo") + "&version=" + metaSignMap.get("version");
				//String resultJsonStr = MfjToolKit.request(rechargeOnLineConfig.getPayUrl(), reqParam);
				String resultJsonStr = MfjToolKit.request(rechargeOnLineConfig.getPayUrl().replace("www", netwayCode.toLowerCase().replace("_", "")), reqParam);
				// 检查状态
				JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
				String stateCode = resultJsonObj.getString("stateCode");
				if (stateCode.equals("00")) {
					String resultSign = resultJsonObj.getString("sign");
					resultJsonObj.remove("sign");
					String targetString = MfjToolKit.MD5(resultJsonObj.toString() + MfjToolKit.KEY, MfjToolKit.CHARSET);
					if (targetString.equals(resultSign) && orderNum.equals(resultJsonObj.getString("orderNum"))) {
						qrcodeUrl = resultJsonObj.getString("qrcodeUrl");
						bool = true;
					}
				}else {
					throw new BusinessException(resultJsonObj.toString());
				}
			}catch (Exception e) {
				e.printStackTrace();
				LogUtil.info("明捷支付提交异常："+e);
				throw new BusinessException(ApiUtil.getErrorCode("149"));
			}
			break;
		case 8://佰富支付-老狼
			try {
				Map<String, String> metaSignMap = new TreeMap<String, String>();
				metaSignMap.put("merchantNo", BfToolKit.MER_NO);
				metaSignMap.put("netwayCode", netwayCode);// 网关代码
				metaSignMap.put("randomNum", BfToolKit.randomStr(4));// 4位随机数
				orderNum += BfToolKit.randomStr(3);
				
				metaSignMap.put("orderNum", orderNum);
				metaSignMap.put("payAmount", amount);// 单位:分
				metaSignMap.put("goodsName", "点卡充值");// 商品名称：20位
				metaSignMap.put("callBackUrl", rechargeOnLineConfig.getNoticeURL());// 回调地址
				metaSignMap.put("frontBackUrl", rechargeOnLineConfig.getNoticeURL());// 回显地址
				metaSignMap.put("requestIP",ip.equals("127.0.0.1")?"120.42.131.229":ip);// 客户ip地址
				
				String metaSignJsonStr = BfToolKit.mapToJson(metaSignMap);
				String sign = BfToolKit.MD5(metaSignJsonStr + BfToolKit.KEY, "UTF-8");// 32位
				//System.out.println("sign=" + sign); // 英文字母大写
				metaSignMap.put("sign", sign);
				
				String reqParam = "paramData=" + BfToolKit.mapToJson(metaSignMap);
				String resultJsonStr = BfToolKit.request(rechargeOnLineConfig.getPayUrl(), reqParam);

				// 检查状态̬
				JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
				String stateCode = resultJsonObj.getString("resultCode");
				if (stateCode.equals("00")) {
					String resultSign = resultJsonObj.getString("sign");
					resultJsonObj.remove("sign");
					String targetString = BfToolKit.MD5(resultJsonObj.toString() + BfToolKit.KEY, "UTF-8");
					if (targetString.equals(resultSign) && orderNum.equals(resultJsonObj.getString("orderNum"))) {
						qrcodeUrl = resultJsonObj.getString("CodeUrl");
						bool = true;
					}
				}else {
					throw new BusinessException(resultJsonObj.toString());
				}
			}catch (Exception e) {
				e.printStackTrace();
				LogUtil.info("佰富支付提交异常："+e);
				throw new BusinessException(ApiUtil.getErrorCode("149"));
			}
			break;
		case 9://码上付-升豪
			try {
				String appid = MsfToolKit.APP_ID;
				String appsecect = MsfToolKit.APP_SECECT;

				String order_id = orderNum;
				String pay_type = netwayCode;
				String price = rechargeAmount.setScale(2, BigDecimal.ROUND_UNNECESSARY).toString();
				String name = "product_name";
				String prod_desc = "product_desc";
				String notify_url = rechargeOnLineConfig.getNoticeURL();
				String return_url = "";
				SimpleDateFormat foramrt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String order_time = foramrt.format(new Date(System.currentTimeMillis()));
				String extend = "extend_".concat(user.getId().toString());
				String signStr = appid + name + pay_type + price + order_id + extend + notify_url + order_time + appsecect;
				
				Map<String, String> parms = new TreeMap<String, String>();
				parms.put("order_id", order_id);
				parms.put("appid", appid);
				parms.put("pay_type", pay_type);//支付方式（可选wechat/f2f）
				parms.put("price", price);// 4位随机数
				parms.put("name", name);
				parms.put("prod_desc", prod_desc);
				parms.put("return_url",return_url);
				parms.put("notify_url", notify_url);
				parms.put("extend", extend);
				parms.put("order_time", order_time);
				parms.put("sign", MsfToolKit.getMD5(signStr).toLowerCase());

				String resultJsonStr = new HttpUtil().postMethod(rechargeOnLineConfig.getPayUrl(), parms,"UTF-8");
				if(StringUtils.isNotBlank(resultJsonStr)) {
					resultJsonStr = resultJsonStr.substring(resultJsonStr.indexOf("window.location.href"), resultJsonStr.length());
					resultJsonStr = resultJsonStr.substring(resultJsonStr.indexOf("/"), resultJsonStr.indexOf(";")-1);
					
					qrcodeUrl = rechargeOnLineConfig.getPayUrl().concat(resultJsonStr);
					bool = true;
				}else {
					throw new BusinessException(resultJsonStr);
				}
			}catch (Exception e) {
				e.printStackTrace();
				LogUtil.info("码上付提交异常："+e);
				throw new BusinessException(ApiUtil.getErrorCode("149"));
			}
			break;
		case 10://龙易付-升豪
			try {
				Map<String, String> metaSignMap = new TreeMap<String, String>();
				metaSignMap.put("appid", LyfToolKit.APPID);
				metaSignMap.put("order_no", orderNum);
				metaSignMap.put("amount", amount);// 单位:分
				metaSignMap.put("openid", RandomUtil.generateLowerString(32));
				metaSignMap.put("product_name", "goodsName");// 商品名称：20位
				metaSignMap.put("bank_code", netwayCode);
				metaSignMap.put("attach", "attach");
				metaSignMap.put("notify_url", rechargeOnLineConfig.getNoticeURL());// 异步通知地址
				metaSignMap.put("return_url", rechargeOnLineConfig.getReturnUrl());// 同步通知地址
				metaSignMap.put("sign", LyfToolKit.generateSign(metaSignMap));
				System.out.println(rechargeOnLineConfig.getPayUrl());

				String resultJsonStr = new HttpUtil().postMethod(rechargeOnLineConfig.getPayUrl(), metaSignMap, "utf-8");
				JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
				
				LogUtil.info("龙易付提交："+resultJsonStr);
				String status = resultJsonObj.getString("status");
				//String message = LyfToolKit.unicodeToString(resultJsonObj.getString("message"));
				if (status.equals("1")) {
					JSONObject dataJson = resultJsonObj.getJSONObject("data");
					if (orderNum.equals(dataJson.getString("order_no"))) {
						qrcodeUrl = dataJson.getString("redirect_url");
						if("no_redirect".equalsIgnoreCase(qrcodeUrl)) {
							rechargeOnLineConfig.setUrlType(3);
						}
						bool = true;
					}
				}else {
					throw new BusinessException(resultJsonObj.toString());
				}
			}catch (Exception e) {
				e.printStackTrace();
				LogUtil.info("龙易付提交异常："+e);
				throw new BusinessException(ApiUtil.getErrorCode("149"));
			}
			break;
		case 11://易通宝-88c
			try {
				String version = "1.0";
				String sdorderno = orderNum;
				String total_fee = rechargeAmount.setScale(2, BigDecimal.ROUND_UNNECESSARY).toString();
				String paytype = netwayCode;//支付编号
				String bankcode = "";//银行编号
				String remark = "ytb";
				String notify_url = rechargeOnLineConfig.getNoticeURL();
				String return_url = rechargeOnLineConfig.getReturnUrl();
				String is_qrcode = "1";//值1为获取，值0不获取，只对扫码付款有效
				String signStr = "version="+version+"&customerid="+YtbToolKit.APP_ID+"&total_fee="+total_fee+"&sdorderno="+sdorderno+"&notifyurl="+notify_url+"&returnurl="+return_url+"&"+YtbToolKit.APP_SECECT;

				Map<String, String> parms = new TreeMap<String, String>();
				parms.put("version", version);
				parms.put("customerid", YtbToolKit.APP_ID);
				parms.put("sdorderno", sdorderno);
				parms.put("total_fee", total_fee);
				parms.put("paytype", paytype);
				parms.put("bankcode", bankcode);
				parms.put("senne", "2");
				parms.put("returnurl",return_url);
				parms.put("notifyurl", notify_url);
				parms.put("remark", remark);
				parms.put("is_qrcode", is_qrcode);
				parms.put("sign", MD5Util.MD5Encode(signStr, "utf-8"));

				String resultJsonStr = new HttpUtil().getMethod(rechargeOnLineConfig.getPayUrl().concat("?").concat(new HttpUtil().mapToString(parms)), "utf-8");
				LogUtil.info("易通宝提交："+resultJsonStr);
				if(resultJsonStr.contains("payUrl")) {
					JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
					String code = resultJsonObj.getString("code");
					if("0".contentEquals(code)) {
						JSONObject dataJsonObj = JSONObject.fromObject(resultJsonObj.getString("data"));
						qrcodeUrl = dataJsonObj.getString("payUrl");
						bool = true;
					}
				}else {
					throw new BusinessException(resultJsonStr.toString());
				}
			}catch (Exception e) {
				e.printStackTrace();
				LogUtil.info("易通宝提交异常："+e);
				throw new BusinessException(ApiUtil.getErrorCode("149"));
			}
			break;
		case 12://喜力-88c
			try {
				String version = "100";//接口版本[参与签名]
				String bizCode = "H0001";//业务编号[参与签名]
				String serviceType = netwayCode;//服务类别[参与签名]
				String orderNo = orderNum;//订单号[参与签名]
				String orderPrice = rechargeAmount.toString();;//交易金额(元)
				String orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//订单时间
				String terminalIp = ip;
				String notify_url = rechargeOnLineConfig.getNoticeURL();
				String return_url = rechargeOnLineConfig.getReturnUrl();
				
				if("ALIPAY_SCANPAY".equalsIgnoreCase(serviceType) || "WEIXIN_SCANPAY".equalsIgnoreCase(serviceType) ) {
					bizCode = "S0001";
					orderNo = "S".concat(orderNo);
				}else if("WEIXIN_H5PAY".equalsIgnoreCase(serviceType) || "ALIPAY_H5PAY".equalsIgnoreCase(serviceType) ) {
					bizCode = "H0001";
					orderNo = "H".concat(orderNo);
				}
				
				Map<String, String> metaSignMap = new TreeMap<String, String>();
				metaSignMap.put("version", version);
				metaSignMap.put("appId", XlToolKit.APP_ID);
				metaSignMap.put("appSecret", XlToolKit.APP_SECRET);
				metaSignMap.put("merId", XlToolKit.MERCHANT_ID);
				metaSignMap.put("bizCode", bizCode);
				metaSignMap.put("serviceType", serviceType);
				metaSignMap.put("orderPrice", orderPrice);
				metaSignMap.put("orderNo", orderNo);

				Map<String, String> parms = new TreeMap<String, String>();
				parms.put("version", version);
				parms.put("appId", XlToolKit.APP_ID);
				parms.put("appSecret", XlToolKit.APP_SECRET);
				parms.put("merId", XlToolKit.MERCHANT_ID);
				parms.put("bizCode", bizCode);
				parms.put("serviceType", serviceType);
				parms.put("orderNo", orderNo);
				parms.put("orderPrice", orderPrice);
				parms.put("goodsName", "充值卡");
				parms.put("goodsTag", "TAG");
				parms.put("orderTime", orderTime);
				parms.put("terminalIp", terminalIp);
				parms.put("returnUrl",return_url);
				parms.put("notifyUrl", notify_url);
				parms.put("settleCycle", "D0");
				parms.put("sign", XlToolKit.generateSign(metaSignMap));

				String resultJsonStr = new HttpUtil().postMethod(rechargeOnLineConfig.getPayUrl(), parms,"UTF-8");
				LogUtil.info("喜力提交："+resultJsonStr);
				if(resultJsonStr.contains("pay_url")) {
					JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
					String status = resultJsonObj.getString("status");
					String retCode = resultJsonObj.getString("retCode");
					if("01".contentEquals(status) && "200".contentEquals(retCode)) {
						JSONObject dataJsonObj = JSONObject.fromObject(resultJsonObj.getString("bodyMap"));
						qrcodeUrl = dataJsonObj.getString("pay_url");
						bool = true;
					}
				}else {
					throw new BusinessException(resultJsonStr.toString());
				}
			}catch (Exception e) {
				e.printStackTrace();
				LogUtil.info("喜力提交异常："+e);
				throw new BusinessException(ApiUtil.getErrorCode("149"));
			}
			break;
		case 13://阿里付-升豪
			try {
				String pay_orderid = orderNum; 
				String pay_applydate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); 
				String pay_bankcode = netwayCode;
				String pay_notifyurl = rechargeOnLineConfig.getNoticeURL();//服务端返回地址
				String pay_callbackurl = rechargeOnLineConfig.getReturnUrl();//页面跳转返回地址
				String pay_amount = rechargeAmount.toString();
				String pay_productname = "productname";
				
				Map<String, String> metaSignMap = new TreeMap<String, String>();
				metaSignMap.put("pay_memberid", AlipaysToolKit.PAY_MEMBERID);
				metaSignMap.put("pay_orderid", pay_orderid);
				metaSignMap.put("pay_applydate", pay_applydate);
				metaSignMap.put("pay_bankcode", pay_bankcode);
				metaSignMap.put("pay_notifyurl", pay_notifyurl);
				metaSignMap.put("pay_callbackurl", pay_callbackurl);
				metaSignMap.put("pay_amount", pay_amount);
				metaSignMap.put("pay_md5sign", AlipaysToolKit.generateSign(metaSignMap));
				metaSignMap.put("pay_productname", pay_productname);
				
				String resultJsonStr = new HttpUtil().postMethod(rechargeOnLineConfig.getPayUrl(), metaSignMap, "UTF-8");
				LogUtil.info("阿里付提交："+resultJsonStr);
				if(!resultJsonStr.contains("{\"status\":\"error\"")) {
					//qrcodeUrl = Base64.encodeBase64String(resultJsonStr.getBytes());
					qrcodeUrl = resultJsonStr;
					bool = true;
				}else {
					JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
					String msg = resultJsonObj.getString("msg");
					throw new BusinessException(msg);
				}
			}catch (Exception e) {
				e.printStackTrace();
				LogUtil.info("阿里付提交异常："+e);
				throw new BusinessException(ApiUtil.getErrorCode("149"));
			}
			break;
			
		case 14://丽狮
			try {
				String nonce_str = RandomUtil.generateString(32);
				String request_no = orderNum; 
				String pay_amount = rechargeAmount.setScale(2, BigDecimal.ROUND_UNNECESSARY).toString();//订单总金额,以元为单位
				String pay_channel = netwayCode;
				//String channel_id = "130";
				String request_time = String.valueOf(new Date().getTime()/1000); 
				String notify_url = rechargeOnLineConfig.getNoticeURL();//服务端返回地址
				String return_url = rechargeOnLineConfig.getReturnUrl();//页面跳转返回地址
				
				Map<String, String> metaSignMap = new TreeMap<String, String>();
				metaSignMap.put("merchant_no", IfuniuToolKit.PAY_MEMBERID);
				metaSignMap.put("nonce_str", nonce_str);
				metaSignMap.put("request_no", request_no);
				metaSignMap.put("amount", pay_amount);
				metaSignMap.put("pay_channel", pay_channel);
				//metaSignMap.put("channel_id", channel_id);
				metaSignMap.put("request_time", request_time);
				if(StringUtils.isNotBlank(notify_url)) {
					metaSignMap.put("notify_url", notify_url);
				}
				if(StringUtils.isNotBlank(return_url)) {
					metaSignMap.put("return_url", return_url);
				}
				
				metaSignMap.put("sign", IfuniuToolKit.generateSign(metaSignMap));
				String resultJsonStr = HttpsUtil.postMethod(rechargeOnLineConfig.getPayUrl(), metaSignMap, "UTF-8");

				LogUtil.info("丽狮提交："+resultJsonStr);
				if(StringUtils.isNotBlank(resultJsonStr)) {
					JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
					String isSuccess = resultJsonObj.getString("success");
					if("false".equalsIgnoreCase(isSuccess)) {
						JSONObject dataObject = resultJsonObj.getJSONObject("data");
						String message = dataObject.getString("message");
						throw new BusinessException(message);
					}else {
						JSONObject dataObject = resultJsonObj.getJSONObject("data");
						if(dataObject!=null) {
							String status = dataObject.getString("status");
							String bankUrl = dataObject.getString("bank_url");
							String urlType = dataObject.getString("url_type");
							
							if("0".equalsIgnoreCase(status) || "2".equalsIgnoreCase(status)) {
								qrcodeUrl = bankUrl;
								
								//url:链接地址，请使用浏览器打开bank_url； code:二维码地址，请自行生成二维码； html:页面内容，请直接输出到用户浏览器
								//1wap跳转、2二维码地址、3页面内容
								if("url".equalsIgnoreCase(urlType)) {
									rechargeOnLineConfig.setUrlType(1);
								}else if("code".equalsIgnoreCase(urlType)) {
									rechargeOnLineConfig.setUrlType(2);
								}else if("html".equalsIgnoreCase(urlType)) {
									rechargeOnLineConfig.setUrlType(3);
								}
								bool = true;
							}
						}
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
				LogUtil.info("丽狮提交异常："+e);
				throw new BusinessException(ApiUtil.getErrorCode("149"));
			}
			break;
			
		case 15://金樽支付
			try {
				Map<String, String> parms = new TreeMap<String, String>();
				parms.put("businessId", JdzfToolKit.MERCHANT_ID);
				parms.put("amount", rechargeAmount.setScale(2, BigDecimal.ROUND_UNNECESSARY).toString());
				parms.put("outTradeNo", orderNum);
				parms.put("payMethodId", netwayCode);
				parms.put("notifyUrl", rechargeOnLineConfig.getNoticeURL());//服务端返回地址
				parms.put("returnUrl", rechargeOnLineConfig.getReturnUrl());//页面跳转返回地址
				parms.put("random", new Date().getTime()+"");
				
				Map<String, String> signMap = parms;
				signMap.put("secret", JdzfToolKit.MERCHANT_SECRET);
				
				parms.put("sign", JdzfToolKit.generateSign(signMap));
				String resultJsonStr = new HttpUtil().postMethod(rechargeOnLineConfig.getPayUrl(), parms,"UTF-8");

				LogUtil.info("金樽支付提交："+resultJsonStr);
				if(StringUtils.isNotBlank(resultJsonStr)) {
					if(resultJsonStr.contains("successed")) {
						JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
						String errorDesc = resultJsonObj.getString("errorDesc");
						throw new BusinessException(errorDesc);
					}else{
						rechargeOnLineConfig.setUrlType(3);
						qrcodeUrl = resultJsonStr;
						bool = true;
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
				LogUtil.info("金樽支付提交异常："+e);
				throw new BusinessException(ApiUtil.getErrorCode("149"));
			}
			break;
		case 16://星支付
			try {
				Map<String, String> metaSignMap = new TreeMap<String, String>();
				metaSignMap.put("shid", XpayToolKit.SHID);//商户账号
				metaSignMap.put("orderid", orderNum);//充值订单号
				metaSignMap.put("amount", rechargeAmount.setScale(2, BigDecimal.ROUND_UNNECESSARY).toString());
				metaSignMap.put("sign", XpayToolKit.generateSign(metaSignMap));
				
				metaSignMap.put("pay", netwayCode);//支付通道1(微信)2(支付宝)3（H5支付宝）
				metaSignMap.put("ip", ip);
				metaSignMap.put("urlht", rechargeOnLineConfig.getNoticeURL());//回调通知 
				metaSignMap.put("urltz", rechargeOnLineConfig.getReturnUrl());//支付完跳转 

				String resultJsonStr = HttpsUtil.postMethod(rechargeOnLineConfig.getPayUrl(), metaSignMap, "UTF-8");
				LogUtil.info("星支付提交："+resultJsonStr);
				
				if(StringUtils.isNotBlank(resultJsonStr)) {
					JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
					String status = resultJsonObj.getString("status");
					if(!"1".equalsIgnoreCase(status)) {
						String msg = resultJsonObj.getString("msg");
						throw new BusinessException(msg);
					}else {
						String url = resultJsonObj.getString("url");
						rechargeOnLineConfig.setUrlType(1);
						qrcodeUrl = url;
						bool = true;
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
				LogUtil.info("星支付提交异常："+e);
				throw new BusinessException(ApiUtil.getErrorCode("149"));
			}
			break;
		case 17://付必达
			try {
				String orderId = orderNum; //商家订单编号
				String userId = user.getId().toString();//玩家编号（参阅重要通知-⿊名单功能）
				String gameCode = "2VE36FD4LT";//游戏参数（参阅附注-商家资讯）
				String type = netwayCode;//⽀付渠道 （1：⽀付宝，2：微信⽀付，3：银⾏卡，4: 云闪付，5:⽀转银 扫码）
				String exp = new Date().getTime()+"";//时间戳（单位：毫秒，⼗三位数）
				String notifyUrl = rechargeOnLineConfig.getNoticeURL();

				Map<String, String> header = new TreeMap<String, String>();
				header.put("Content-type", "application/json");
				header.put("X-Notify-URL", notifyUrl);

				Map<String, String> metaSignMap = new TreeMap<String, String>();
				metaSignMap.put("partnerId", FbdToolKit.PARTNERID);//商家编号（参阅附注-商家资讯）
				metaSignMap.put("userId", userId);
				metaSignMap.put("amounts", rechargeAmount.toString());
				metaSignMap.put("gameCode", gameCode);
				metaSignMap.put("orderId", orderId);
				metaSignMap.put("type", type);
				metaSignMap.put("exp", exp);
				metaSignMap.put("sign", FbdToolKit.generateSign(metaSignMap));//签名（参阅附注-加密⽅式）

				String resultJsonStr = HttpsUtil.postMethod(rechargeOnLineConfig.getPayUrl(), JsonUtil.map2json(metaSignMap), header,"UTF-8");
				LogUtil.info("付必达提交："+resultJsonStr);

				if(StringUtils.isNotBlank(resultJsonStr)) {
					JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
					String status = resultJsonObj.getString("status");
					if(!"1".equalsIgnoreCase(status)) {
						String msg = resultJsonObj.getString("msg");
						throw new BusinessException(msg);
					}else {
						String msg = resultJsonObj.getString("msg");
						String data = resultJsonObj.getString("data");
						JSONObject dataObj = resultJsonObj.fromObject(data);
						String tokenId = dataObj.getString("tokenId");
						String orderCode = dataObj.getString("orderCode");
						qrcodeUrl = "https://fullbitpay.co/api/wallet/provide/"+orderCode+"/"+tokenId;
						rechargeOnLineConfig.setUrlType(1);
						bool = true;
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
				LogUtil.info("星支付提交异常："+e);
				throw new BusinessException(ApiUtil.getErrorCode("149"));
			}
			break;
		case 18://优付宝
			try {
				String orderNo = orderNum;
				String channelNo = netwayCode;
				String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				String notifyUrl = rechargeOnLineConfig.getNoticeURL();
				String time = new Date().getTime()+"";
				String appSecret = YfbToolKit.appSecret;

				Map<String, String> metaSignMap = new HashMap<String, String>();
				metaSignMap.put("merchantNo", YfbToolKit.merchantNo);//商户编号 (由隍寶支付平台提供,见:商户信息)
				metaSignMap.put("orderNo", orderNo);//商户单号 (用于对账、查询; 不超过32个字符)
				metaSignMap.put("amount", rechargeAmount.setScale(2, BigDecimal.ROUND_UNNECESSARY).toString());//订单金额 (单位:元,2位小数; 最小充值金额1元,最大50000元。此金额可能会变动,请与隍寶支付平台确认),
				metaSignMap.put("channelNo", channelNo);//支付通道编号 (纯数字格式; 支付宝转支付宝:0 | 微信:1 | 支付宝转银联卡:2 | 云闪付扫码:3 | 卡转卡网关:4 | 聚合码:5) (不参加加密)
				metaSignMap.put("datetime", datetime);//日期时间 (格式:2018-01-01 23:59:59)
				metaSignMap.put("notifyUrl", notifyUrl);//异步通知地址 (当用户完成付款时,支付平台将向此URL地址,异步发送付款通知。建议使用 https)
				metaSignMap.put("time", time);//
				metaSignMap.put("appSecret", appSecret);//(由隍寶支付平台提供,见:商户信息/appSecret) (不参加加密)
				metaSignMap.put("sign", YfbToolKit.generateSign(metaSignMap));

				String resultJsonStr = HttpsUtil.postMethod(rechargeOnLineConfig.getPayUrl(), metaSignMap, "UTF-8");
				LogUtil.info("优付宝提交："+resultJsonStr);

				if(StringUtils.isNotBlank(resultJsonStr)) {
					JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
					String code = resultJsonObj.getString("code");
					if(!"0".equalsIgnoreCase(code)) {
						String text = resultJsonObj.getString("text");
						throw new BusinessException(text);
					}else {
						if(netwayCode.equalsIgnoreCase("2") || netwayCode.equalsIgnoreCase("4")){
							qrcodeUrl = resultJsonObj.getString("targetUrl");
							rechargeOnLineConfig.setUrlType(1);
						}else{
							qrcodeUrl = resultJsonObj.getString("qrcode");
							rechargeOnLineConfig.setUrlType(2);
						}
						bool = true;
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
				LogUtil.info("优付宝提交异常："+e);
				throw new BusinessException(ApiUtil.getErrorCode("149"));
			}
			break;
		default:
			throw new BusinessException(ApiUtil.getErrorCode("148"));
		}
		
		if(bool) {
			Recharge recharge = new Recharge();
			recharge.setUser(user);
			recharge.setTradeCode(orderNum);
			recharge.setRechargeAmount(rechargeAmount);//充值金额
			recharge.setLotteryAmount(new BigDecimal(0));
			recharge.setCreateTime(new Date());//创建时间
			recharge.setStatus(1);//状态（1：初始化 2：交易成功 3：交易失败 4：异常待核实）
			recharge.setPayType(rechargeOnLineConfig.getProviderType());//支付方式（1支付宝、2微信、3网银转账、4后台手动充值、5扫码充值、6明捷支付、7后台充值彩金）
			recharge.setRemark("网上充值");
			rechargeService.save(recharge);
			
			jsonbject.put("orderNum", orderNum);
			jsonbject.put("qrcodeUrl",qrcodeUrl);
			jsonbject.put("urlType",rechargeOnLineConfig.getUrlType());
		}else {
			throw new BusinessException(ApiUtil.getErrorCode("149"));
		}
		
		memberLogService.saveLog(user, null, "提交网上充值", ip, device);
		return jsonbject;
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void onlineRechargeSuccess(String orderNum,Integer payType,BigDecimal rechargeAmount) throws Exception {
		Recharge recharge = this.findRechargeByTradeCode2PayType(orderNum,payType);
		if(recharge == null || recharge.getStatus() != 1) {
			throw new BusinessException(ApiUtil.getErrorCode("160"));
		}

		User user = userService.find(recharge.getUser().getId());
		userService.validateUser(user, true, true);//验证用户
		
		if(rechargeAmount == null || recharge.getRechargeAmount().compareTo(rechargeAmount) ==0) {
			rechargeAmount = recharge.getRechargeAmount();
		}
		BigDecimal lotteryAmount = new BigDecimal(0);//赠送彩金
		//充值赠送积分活动
		boolean rebate = false;
		boolean isNewUser = isNewUserRecharge(user);//判断是否新用户
		boolean isFirstRecharge = isDailyFirstRecharge(user);//判断是否首充
		List<LotteryActivity> lotteryActivityList = lotteryActivityService.getListByStationId2Type(user.getStation().getId(),1);
		for (LotteryActivity lotteryActivity : lotteryActivityList) {
			if(lotteryActivity != null) {
				//判断是否叠加
				if(lotteryActivity.getIsSuperpose() == 0 && rebate) {
					continue;
				}
				
				BigDecimal lotteryAmount1 = activityLotteryAmount(lotteryActivity,user,rechargeAmount,lotteryActivity.getId(),isFirstRecharge,isNewUser);
				if(lotteryAmount1.compareTo(new BigDecimal(0))>0) {
					rebate = true;
					recharge.setRemark(recharge.getRemark().concat(",").concat(lotteryActivity.getTitle()));
					lotteryAmount = lotteryAmount.add(lotteryAmount1);
				}
			}
		}

		recharge.setRechargeAmount(rechargeAmount);
		recharge.setLotteryAmount(lotteryAmount);
		recharge.setTradeTime(new Date());//交易时间（到账时间）
		recharge.setStatus(2);//状态（1：初始化 2：交易成功 3：交易失败 4：异常待核实）
		rechargeService.update(recharge);
		
		Integer addPoints = rechargeAmount.setScale(0, BigDecimal.ROUND_DOWN).intValue();
		user.setPoints(user.getPoints()+addPoints);
		user.setPointsLevel(pointsLevelService.findPointsLevel(user.getPoints().intValue()));
		user.setBalance(user.getBalance().add(rechargeAmount));//账户金额
		user.setLotteryBalance(user.getLotteryBalance().add(lotteryAmount));//赠送彩金
		
		String withdrawLimitAccount = stationConfigService.getValueByName("withdraw_limit_account", user.getStation().getId());
		if(StringUtils.isNotBlank(withdrawLimitAccount) && new BigDecimal(withdrawLimitAccount).compareTo(new BigDecimal(0)) > 0) {
			BigDecimal flowRequire = rechargeAmount.add(lotteryAmount).multiply(new BigDecimal(withdrawLimitAccount)).setScale(2, BigDecimal.ROUND_HALF_UP);
			
			//开否新用户提现不卡流水
			String isOpenNewUserLimitAccount = stationConfigService.getValueByName("is_open_newUser_limit_account", user.getStation().getId());
			if(StringUtils.isNotBlank(isOpenNewUserLimitAccount) && "1".equalsIgnoreCase(isOpenNewUserLimitAccount) 
					&& rechargeService.getUserRechargeCount(user).intValue()<1) {
				flowRequire = new BigDecimal(0);
			}
			
			user.setFlowRequire(user.getFlowRequire().add(flowRequire));//流水要求
		}
		this.update(user);

		TransRecord transRecord = new TransRecord();
		transRecord.setUser(user);//用户
		transRecord.setCreateTime(new Date());//交易时间
		transRecord.setTransCategory(1);//交易类型（1充值、2提现、3抽奖、4投注、5撤单、6赠送、7中奖、8回水、9调账）
		transRecord.setTransAmount(rechargeAmount);//交易账户金额
		transRecord.setEndBalance(user.getBalance());//剩余账户金额
		transRecord.setTransLotteryAmount(lotteryAmount);//交易彩金
		transRecord.setEndLotteryBalance(user.getLotteryBalance());//剩余彩金
		transRecord.setRemark(recharge.getRemark().concat(recharge.getTradeCode()));
		transRecord.setFlag(1);
		transRecordService.save(transRecord);

		//充值统计
		LotteryDailyOrderTotal lotteryDailyOrderTotal = lotteryDailyOrderTotalService.getLotteryDailyOrderTotal(DateTimeTool.dateFormat("yyyy-MM-dd", new Date()), user.getId());
		if(lotteryDailyOrderTotal==null) {
			lotteryDailyOrderTotal = new LotteryDailyOrderTotal();
			
			lotteryDailyOrderTotal.setUser(user);//玩家帐号
			lotteryDailyOrderTotal.setTotalDate(DateTimeTool.dateFormat("yyyy-MM-dd", DateTimeTool.dateFormat("yyyy-MM-dd", new Date())));//统计日期
			lotteryDailyOrderTotal.setBetMoney(new BigDecimal(0));//流水金额
			lotteryDailyOrderTotal.setProfitMoney(new BigDecimal(0));//盈亏金额
			lotteryDailyOrderTotal.setBackWaterMoney(new BigDecimal(0));// //回水金额
			lotteryDailyOrderTotal.setRechargeMoney(rechargeAmount);//充值金额
			lotteryDailyOrderTotal.setWithdrawMoney(new BigDecimal(0));//提现金额
			lotteryDailyOrderTotal.setWinMoney(new BigDecimal(0));//中奖金额
			lotteryDailyOrderTotal.setExpandUserNum(0);//拓展玩家数
			lotteryDailyOrderTotal.setBalance(user.getBalance());
			lotteryDailyOrderTotal.setLotteryBalance(user.getLotteryBalance());//赠送彩金统计
			lotteryDailyOrderTotalService.save(lotteryDailyOrderTotal);
		}else{
			lotteryDailyOrderTotal.setBalance(user.getBalance());
			lotteryDailyOrderTotal.setLotteryBalance(user.getLotteryBalance());//赠送彩金统计
			lotteryDailyOrderTotal.setRechargeMoney(lotteryDailyOrderTotal.getRechargeMoney().add(rechargeAmount));//充值金额
			lotteryDailyOrderTotalService.update(lotteryDailyOrderTotal);
		}

		//发站内信
		letterService.sendLetter(user, "网上充值成功通知", "您于".concat(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", recharge.getCreateTime())).concat("提交的网上充值申请，已充值成功，本次充值金额：").concat(rechargeAmount.toString()).concat("，如有异议请联系客服。"));
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void virtualUserRecharge(User user,String username,BigDecimal amount,Integer type,String ip,String device,Integer stationId) throws Exception {
		userService.validateUser(user, true, true);//验证用户
		
		User childUser = userService.findUser(username,stationId);
		if(childUser==null) {
			throw new BusinessException(ApiUtil.getErrorCode("152"));
		}
		
		if(childUser.getUserType() != 4) {
			throw new BusinessException(ApiUtil.getErrorCode("154"));
		}

		if(user.getUserType()== 2) {//代理为虚拟号充值
			if(childUser.getParent() == null || childUser.getParent().getParent() == null 
					|| childUser.getParent().getParent().getId() != user.getId()) {
				throw new BusinessException(ApiUtil.getErrorCode("153"));
			}
			
			this.rechargeHandle(username, amount, type, type == 1?"余额充值":"充值彩金", stationId,new Date(),true);
		}else if(user.getUserType()== 3) {//推广号为虚拟号充值
			String salesmanRechargeVirtual = stationConfigService.getValueByName("salesman_recharge_virtual", user.getStation().getId());
			if(StringUtils.isNotBlank(salesmanRechargeVirtual) && "1".equalsIgnoreCase(salesmanRechargeVirtual)) {
				if(childUser.getParent() == null || childUser.getParent().getId() != user.getId()) {
					throw new BusinessException(ApiUtil.getErrorCode("153"));
				}
				
				this.rechargeHandle(username, amount, type, type == 1?"余额充值":"充值彩金", stationId,new Date(),true);
			}else {
				throw new BusinessException(ApiUtil.getErrorCode("161"));
			}
		}else {
			throw new BusinessException(ApiUtil.getErrorCode("151"));
		}
	}
	
	public Long getUserRechargeCount(User user) throws Exception {
		Long count = (Long)this.getSession().createQuery("select count(o.id) from Recharge o where o.user.id = ? and o.status = 2 ").setParameter(0, user.getId()).uniqueResult();
		if(count == null || count == 0) {
			count = 0l;
		}
		return count;
	}
	
	/** * 判断是否新用户充值*/
	public boolean isNewUserRecharge(User user) throws Exception {
		Long count = (Long)this.getSession().createQuery("select count(o.id) from Recharge o where o.user.id = ? and o.status = 2 ").setParameter(0, user.getId()).uniqueResult();
		if(user.getBalance().compareTo(new BigDecimal(0)) ==0 && (count == null || count == 0)) {
			return true;
		}
		return false;
	}
	
	/*** 判断是否每天第一次充值*/
	public boolean isDailyFirstRecharge(User user) throws Exception {
		Long count = (Long)this.getSession().createQuery("select count(o.id) from Recharge o where o.user.id = ? and o.payType <> 7 and date_format(o.tradeTime ,'%Y-%m-%d') = ? and o.status = 2 ")
				.setParameter(0, user.getId()).setParameter(1, DateTimeTool.dateFormat("yyyy-MM-dd", new Date())).uniqueResult();
		if(count == null || count == 0) {
			return true;
		}
		return false;
	}
	
	/*** 计算充值活动赠送彩金*/
	public BigDecimal activityLotteryAmount(LotteryActivity activity,User user,BigDecimal rechargeAmount,Integer stationId
			,boolean isFirstRecharge,boolean isNewUser) throws Exception { 
		BigDecimal lotteryAmount = new BigDecimal(0);
		if(activity != null && activity.getStatus() == 1 ) {
			Boolean timeAllowed = true;//时间允许
			Boolean userAllowed = false;//赠送对象允许
			Boolean userTypeAllowed = false;//用户类型允许
			if(activity.getBeginTime() != null && activity.getEndTime() != null && 
					(new Date().before(activity.getBeginTime()) || new Date().after(activity.getEndTime()))) {
				timeAllowed = false;
			}
			
			if(activity.getUserType() == 0) {
				userTypeAllowed = true;
			}else if(activity.getUserType() == 1 && user.getUserType() == 1 ){//玩家
				userTypeAllowed = true;
			}else if(activity.getUserType() == 2 && user.getUserType() == 4){//虚拟号
				userTypeAllowed = true;
			}
			
			if(activity.getGiftUserType() == 0) {
				userAllowed = true;
			}else if(activity.getGiftUserType() == 1 && isNewUser){
				userAllowed = true;
			}else if(activity.getGiftUserType() == 2 && !isNewUser){
				userAllowed = true;
			}
			
			if(timeAllowed && userAllowed && userTypeAllowed) {
				if((isFirstRecharge && activity.getIsFirstRecharge() == 1) ||  activity.getIsSostenuto() == 1) {
					if(activity.getGiftType() == 1) {//1定额
						lotteryAmount = activity.getMaxMoney();
					}else {//2比例
						LotteryActivityDuration duration = lotteryActivityDurationService.findLotteryActivityDuration(rechargeAmount.setScale(0,BigDecimal.ROUND_HALF_UP).intValue(),activity.getId());
						if(duration != null) {
							if(duration.getGiftType() == 1) {//1定额
								lotteryAmount = duration.getRatio();
							}else{
								lotteryAmount = rechargeAmount.multiply(duration.getRatio()).setScale(0,BigDecimal.ROUND_HALF_UP);
							}
						}
						
						if(activity.getIsLimitedMoney() == 1 && lotteryAmount.compareTo(activity.getMaxMoney()) >0) {//限制金额（1是、0否）
							lotteryAmount = activity.getMaxMoney();
						}
					}
				}
			}
		}
		return lotteryAmount;
	}
	
}
