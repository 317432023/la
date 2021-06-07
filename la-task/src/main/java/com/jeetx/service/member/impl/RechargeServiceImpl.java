package com.jeetx.service.member.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryActivity;
import com.jeetx.bean.lottery.LotteryActivityDuration;
import com.jeetx.bean.lottery.LotteryDailyOrderTotal;
import com.jeetx.bean.member.Recharge;
import com.jeetx.bean.member.TransRecord;
import com.jeetx.bean.member.User;
import com.jeetx.common.exception.BusinessException;
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
import com.jeetx.util.DateTimeTool;

@Service
@Transactional
public class RechargeServiceImpl extends DaoSupport<Recharge> implements RechargeService {
	@Autowired UserService userService;
	@Autowired RechargeService rechargeService;
	@Autowired TransRecordService transRecordService;
	@Autowired LotteryDailyOrderTotalService lotteryDailyOrderTotalService;
	@Autowired LetterService letterService;
	@Autowired PointsLevelService pointsLevelService;
	@Autowired MemberLogService memberLogService;
	@Autowired LotteryActivityService lotteryActivityService;
	@Autowired LotteryActivityDurationService lotteryActivityDurationService;
	@Autowired StationConfigService stationConfigService;
	
	@SuppressWarnings("unchecked")
	public List<Recharge> getRechargeListByPayType2status() {
		Date startTime = DateTimeTool.addTime(new Date(), -60*60);
		return this.getSession().createQuery("from Recharge o where o.payType in (6,8,9,10,11,12,13,14,15,16,17,18) and o.status in (1,4) and o.createTime >= ? ").setParameter(0, startTime).list();
	}

	public Long getUserRechargeCount(User user) throws Exception {
		Long count = (Long)this.getSession().createQuery("select count(o.id) from Recharge o where o.user.id = ? and o.status = 2 ").setParameter(0, user.getId()).uniqueResult();
		if(count == null || count == 0) {
			count = 0l;
		}
		return count;
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
	public void onlineRechargeSuccess(String orderNum,Integer payType,BigDecimal rechargeAmount) throws Exception {
		Recharge recharge = this.findRechargeByTradeCode2PayType(orderNum,payType);
		if(recharge != null && recharge.getStatus() == 1){
			User user = userService.find(recharge.getUser().getId());
			if(user == null || user.getStatus()!=1) {
				throw new BusinessException("用户不存在");
			}

			if(rechargeAmount == null || recharge.getRechargeAmount().compareTo(rechargeAmount) ==0) {
				rechargeAmount = recharge.getRechargeAmount();
			}

			BigDecimal lotteryAmount = new BigDecimal(0);//赠送积分
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
