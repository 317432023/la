package com.jeetx.service.lottery;

import java.util.List;

import com.jeetx.bean.lottery.LotteryPeriods;
import com.jeetx.bean.lottery.LotteryRoom;
import com.jeetx.bean.lottery.LotteryRoomMessage;
import com.jeetx.common.swagger.model.lottery.LotteryOrderItemDTO;
import com.jeetx.service.dao.DAO;

public interface LotteryRoomMessageService extends DAO<LotteryRoomMessage> {
	public void sendBroadcastMessage(LotteryRoom lotteryRoom,LotteryPeriods lotteryPeriods,String configMessage) ;
	public void sendOrderMessage(LotteryRoom lotteryRoom,String sender,Integer userType,String headImg,String levelTitle,
			LotteryPeriods lotteryPeriods,List<LotteryOrderItemDTO> orderItemList,String orderCode);
}
