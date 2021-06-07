package com.jeetx.service.lottery.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryActivity;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryActivityService;

@Service
@Transactional
public class LotteryActivityServiceImpl extends DaoSupport<LotteryActivity> implements LotteryActivityService {

	@SuppressWarnings("unchecked")
	public List<LotteryActivity> getLotteryActivityByStationId(Integer stationId) {
		return this.getSession().createQuery("from LotteryActivity o where o.status = 1 and o.station.id = ? ").setParameter(0, stationId).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<LotteryActivity> getListByStationId2Type(Integer stationId,Integer activityType) {
		List<LotteryActivity> list =  this.getSession().createQuery("from LotteryActivity o where o.status = 1 and o.station.id = ? and o.activityType = ? order by o.sortNum asc")
				.setParameter(0, stationId).setParameter(1, activityType).list();
		return list;
	}
	
}
