package com.jeetx.service.member;

import java.math.BigDecimal;
import java.util.List;

import com.jeetx.bean.lottery.LotteryActivity;
import com.jeetx.bean.member.Recharge;
import com.jeetx.bean.member.User;
import com.jeetx.service.dao.DAO;

public interface RechargeService extends DAO<Recharge> {
	public Long getUserRechargeCount(User user) throws Exception;
	public List<Recharge> getRechargeListByPayType2status();
	public Recharge findRechargeByTradeCode2PayType(String tradeCode,Integer payType);
	public void onlineRechargeSuccess(String orderNum,Integer payType,BigDecimal rechargeAmount) throws Exception;
	public boolean isNewUserRecharge(User user) throws Exception ;
	public boolean isDailyFirstRecharge(User user) throws Exception ;
	public BigDecimal activityLotteryAmount(LotteryActivity activity,User user,BigDecimal rechargeAmount,Integer stationId
			,boolean isFirstRecharge,boolean isNewUser) throws Exception ;
}
