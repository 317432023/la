package com.jeetx.service.lottery.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryHall;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryHallService;

@Service
@Transactional
public class LotteryHallServiceImpl extends DaoSupport<LotteryHall> implements LotteryHallService {

	@SuppressWarnings("unchecked")
	public List<LotteryHall> getLotteryHallList(Integer lotteryType) {
		return this.getSession().createQuery("from LotteryHall o where o.status = 1 and o.lotteryType.id = ? order by sort desc ").setParameter(0, lotteryType).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<LotteryHall> getLotteryHallList() {
		return this.getSession().createQuery("from LotteryHall o where o.status = 1 order by sort desc ").list();
	}
	
	@SuppressWarnings("unchecked")
	public List<LotteryHall> getLotteryHallList(Integer lotteryType,Integer stationId) {
		return this.getSession().createQuery("from LotteryHall o where o.lotteryType.id = ? and o.station.id = ? order by sort desc ")
				.setParameter(0, lotteryType).setParameter(1, stationId).list();
	}
}
