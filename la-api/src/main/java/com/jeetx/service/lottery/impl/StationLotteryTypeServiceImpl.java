package com.jeetx.service.lottery.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryActivity;
import com.jeetx.bean.lottery.LotteryDailyOrderTotal;
import com.jeetx.bean.lottery.LotteryOrder;
import com.jeetx.bean.lottery.StationLotteryType;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.StationLotteryTypeService;

@Service
@Transactional
public class StationLotteryTypeServiceImpl extends DaoSupport<StationLotteryType> implements StationLotteryTypeService {

	@SuppressWarnings("unchecked")
	public List<StationLotteryType> getStationLotteryTypeByStationId(Integer stationId) {
		return this.getSession().createQuery("from StationLotteryType o where o.station.id = ? order by sortNum desc").setParameter(0, stationId).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<StationLotteryType> getStationLotteryTypeByStationId(Integer stationId,Integer status) {
		return this.getSession().createQuery("from StationLotteryType o where o.station.id = ? and o.status = ? order by sortNum desc")
				.setParameter(0, stationId).setParameter(1, status).list();
	}
	
	@SuppressWarnings("unchecked")
	public StationLotteryType getStationLotteryTypeByLotteryType(Integer lotteryType,Integer stationId) {
		List<StationLotteryType> list = this.getSession().createQuery("from StationLotteryType o where o.lotteryType.id = ? and o.station.id = ?")
				.setParameter(0, lotteryType).setParameter(1, stationId).list();
		if (list!=null&&list.size()>0) {
			return list.get(0);
		}
		return null;
	}
}
