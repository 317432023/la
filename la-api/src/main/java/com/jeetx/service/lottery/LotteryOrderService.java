package com.jeetx.service.lottery;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.jeetx.bean.lottery.LotteryOrder;
import com.jeetx.bean.lottery.LotteryPeriods;
import com.jeetx.bean.lottery.LotteryRoom;
import com.jeetx.bean.member.User;
import com.jeetx.common.swagger.model.lottery.LotteryOrderItemDTO;
import com.jeetx.service.dao.DAO;

public interface LotteryOrderService extends DAO<LotteryOrder> {
	public BigDecimal sumAllBetMoney(User user,Date beginDate,Date endDate) throws Exception ;
	public LotteryOrder findLotteryOrderByOrderCode(String orderCode,Integer stationId);
	public LotteryOrder findLotteryOrder(String username,Integer id);
	public void submitOrder(User user,String ip,String device,String orderCode,Integer roomId,String lotteryPeriod,BigDecimal dropMoney,
			List<LotteryOrderItemDTO> orderItemList,Integer stationId,Boolean verifyStatus,Date dropTime,Boolean isSendMessage) throws Exception;
	public List<LotteryOrder> findLotteryOrderList(String lotteryPeriod,Integer lotteryRoomId,Integer stationId) ;
	public BigDecimal getAllBetMoney(Integer userId,String lotteryPeriod) ;
	public BigDecimal getAllMoney(Integer userId,String key,String lotteryPeriod);
	public void lotteryAwardHandle(String lotteryPeriods,Integer lotteryType,Integer stationId,Date openTime) throws Exception;
	public void settlementOrder(LotteryRoom lotteryRoom,LotteryPeriods lotteryPeriods,Integer stationId,Date openTime) throws Exception ;
	public void cancelOrder(User user,String ip,String device,String orderCode,Integer stationId) throws Exception;
	public void adminCancelOrder(String orderIds,Integer stationId) throws Exception;
	public List<LotteryOrder> findLotteryOrderItemByUserId2Period(Integer userId,String lotteryPeriod,Integer lotteryType) ;
}
