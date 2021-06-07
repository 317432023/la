package com.jeetx.service.lottery;

import java.util.List;

import com.jeetx.bean.lottery.LotteryActivity;
import com.jeetx.service.dao.DAO;

public interface LotteryActivityService extends DAO<LotteryActivity> {
	public List<LotteryActivity> getLotteryActivityByStationId(Integer stationId);
	public List<LotteryActivity> getListByStationId2Type(Integer stationId,Integer activityType) ;
}
