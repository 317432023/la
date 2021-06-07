package com.jeetx.service.lottery.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryHall;
import com.jeetx.bean.lottery.LotteryRoom;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryRoomService;

@Service
@Transactional
public class LotteryRoomServiceImpl extends DaoSupport<LotteryRoom> implements LotteryRoomService {

	@SuppressWarnings("unchecked")
	public List<LotteryRoom> getLotteryRoomList(Integer lotteryType,Integer lotteryHallId,Integer stationId) {
		return this.getSession().createQuery("from LotteryRoom o where o.status <> 2 and o.lotteryType.id = ? and o.lotteryHall.id = ? and o.station.id = ? order by sort desc ")
				.setParameter(0, lotteryType).setParameter(1, lotteryHallId).setParameter(2, stationId).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<LotteryRoom> getLotteryRoomListByStationId2HallId(Integer lotteryHallId,Integer stationId) {
		return this.getSession().createQuery("from LotteryRoom o where o.lotteryHall.id = ? and o.station.id = ? order by sort desc ")
				.setParameter(0, lotteryHallId).setParameter(1, stationId).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<LotteryRoom> getLotteryRoomList(Integer lotteryType,Integer stationId) {
		return this.getSession().createQuery("from LotteryRoom o where o.status <> 2 and o.lotteryType.id = ? and o.station.id = ? order by sort desc ")
				.setParameter(0, lotteryType).setParameter(1, stationId).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<LotteryRoom> getLotteryRoomListByStationId(Integer stationId) {
		return this.getSession().createQuery("from LotteryRoom o where o.status <> 2 and o.station.id = ? order by sort desc ").setParameter(0, stationId).list();
	}
	
	@SuppressWarnings("unchecked")
	public LotteryRoom getLotteryRoomByStationId2HallId(Integer lotteryHallId,Integer stationId,String title) {
		List<LotteryRoom> list =  this.getSession().createQuery("from LotteryRoom o where o.lotteryHall.id = ? and o.station.id = ?  and o.title = ? order by sort desc ")
				.setParameter(0, lotteryHallId).setParameter(1, stationId).setParameter(2, title).list();
		if (list!=null&&list.size()>0) {
			return list.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<LotteryRoom> getLotteryRoomList(Integer lotteryType,Integer stationId,String lotteryHallTitle) {
		return this.getSession().createQuery("from LotteryRoom o where o.status <> 2 and o.lotteryType.id = ? and o.station.id = ? and o.lotteryHall.title = ? order by sort desc ")
				.setParameter(0, lotteryType).setParameter(1, stationId).setParameter(2, lotteryHallTitle).list();
	}
}
