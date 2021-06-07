package com.jeetx.service.lottery;

import java.util.List;

import com.jeetx.bean.lottery.LotteryRoom;
import com.jeetx.service.dao.DAO;

public interface LotteryRoomService extends DAO<LotteryRoom> {
	public List<LotteryRoom> getLotteryRoomList(Integer lotteryType,Integer lotteryHallId);
	public List<LotteryRoom> getLotteryRoomList(Integer lotteryType);
}
