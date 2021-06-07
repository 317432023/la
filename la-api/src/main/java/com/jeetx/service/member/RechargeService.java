package com.jeetx.service.member;

import java.math.BigDecimal;
import java.util.Date;

import com.jeetx.bean.lottery.LotteryActivity;
import com.jeetx.bean.member.Recharge;
import com.jeetx.bean.member.User;
import com.jeetx.service.dao.DAO;

import net.sf.json.JSONObject;

public interface RechargeService extends DAO<Recharge> {
	public Long getUserRechargeCount(User user) throws Exception;
	public void rechargeHandle(String username,BigDecimal amount,Integer type,String remark,Integer stationId,Date createTime,Boolean joinActivity) throws Exception;
	public JSONObject submitOnlineRecharge(User user,String ip,String device,Integer providerId,String netwayCode,String amount);
	public void onlineRechargeSuccess(String orderNum,Integer payType,BigDecimal rechargeAmount) throws Exception;
	public Recharge findRechargeByTradeCode2PayType(String tradeCode,Integer payType);
	public void virtualUserRecharge(User user,String username,BigDecimal amount,Integer type,String ip,String device,Integer stationId) throws Exception;
	public boolean isNewUserRecharge(User user) throws Exception;
	public boolean isDailyFirstRecharge(User user) throws Exception;
	public BigDecimal activityLotteryAmount(LotteryActivity activity,User user,BigDecimal rechargeAmount,Integer stationId
			,boolean isFirstRecharge,boolean isNewUser) throws Exception ;
	public BigDecimal sumAllRechargeAmount(User user,Date beginDate,Date endDate) throws Exception ;
}
