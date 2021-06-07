package com.jeetx.service.lottery;

import java.util.List;

import com.jeetx.bean.lottery.LotteryRoom;
import com.jeetx.service.dao.DAO;

public interface LotteryRoomService extends DAO<LotteryRoom> {
	public List<LotteryRoom> getLotteryRoomList(Integer lotteryType,Integer lotteryHallId,Integer stationId);
	public List<LotteryRoom> getLotteryRoomList(Integer lotteryType,Integer stationId) ;
	public List<LotteryRoom> getLotteryRoomListByStationId2HallId(Integer lotteryHallId,Integer stationId) ;
	public List<LotteryRoom> getLotteryRoomListByStationId(Integer stationId);
	public LotteryRoom getLotteryRoomByStationId2HallId(Integer lotteryHallId,Integer stationId,String title);
	public List<LotteryRoom> getLotteryRoomList(Integer lotteryType,Integer stationId,String lotteryHallTitle);
}
