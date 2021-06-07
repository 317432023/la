package com.jeetx.service.lottery;

import java.util.List;

import com.jeetx.bean.lottery.LotteryHall;
import com.jeetx.service.dao.DAO;

public interface LotteryHallService extends DAO<LotteryHall> {
	public List<LotteryHall> getLotteryHallListByLotteryType(Integer lotteryType);
	public List<LotteryHall> getLotteryHallList(Integer lotteryType,Integer stationId);
	public List<LotteryHall> getLotteryHallByStationId(Integer stationId);
	public LotteryHall getLotteryHall(Integer lotteryType,Integer stationId,String title);
}
