package com.jeetx.service.lottery;

import java.util.List;

import com.jeetx.bean.lottery.LotteryOrder;
import com.jeetx.bean.lottery.LotteryPeriods;
import com.jeetx.bean.lottery.LotteryRoom;
import com.jeetx.service.dao.DAO;

public interface LotteryOrderService extends DAO<LotteryOrder> {
	public LotteryOrder findLotteryOrderByOrderCode(String orderCode);
	public LotteryOrder findLotteryOrder(String username,Integer id);  
	public List<LotteryOrder> findLotteryOrderList(String lotteryPeriod,Integer lotteryRoomId);
	public void settlementOrder(LotteryOrder lotteryOrder ,LotteryRoom lotteryRoom,LotteryPeriods lotteryPeriods) throws Exception;
//	public List<LotteryOrder> findLotteryOrderListByPeriod2HallId(String lotteryPeriod,Integer lotteryHallId);
}
