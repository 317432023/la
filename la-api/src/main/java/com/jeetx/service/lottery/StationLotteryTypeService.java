package com.jeetx.service.lottery;

import java.util.List;

import com.jeetx.bean.lottery.StationLotteryType;
import com.jeetx.service.dao.DAO;

public interface StationLotteryTypeService extends DAO<StationLotteryType> {
	public List<StationLotteryType> getStationLotteryTypeByStationId(Integer stationId);
	public List<StationLotteryType> getStationLotteryTypeByStationId(Integer stationId,Integer status);
	public StationLotteryType getStationLotteryTypeByLotteryType(Integer lotteryType,Integer stationId);
}
