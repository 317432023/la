package com.jeetx.service.lottery;

import java.util.List;

import com.jeetx.bean.lottery.LotteryHall;
import com.jeetx.service.dao.DAO;

public interface LotteryHallService extends DAO<LotteryHall> {
	public List<LotteryHall> getLotteryHallList(Integer lotteryType);
	public List<LotteryHall> getLotteryHallList();
	public List<LotteryHall> getLotteryHallList(Integer lotteryType,Integer stationId) ;
}
