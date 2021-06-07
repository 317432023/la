package com.jeetx.service.lottery.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryRoom;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryRoomService;

@Service
@Transactional
public class LotteryRoomServiceImpl extends DaoSupport<LotteryRoom> implements LotteryRoomService {

	@SuppressWarnings("unchecked")
	public List<LotteryRoom> getLotteryRoomList(Integer lotteryType,Integer lotteryHallId) {
		return this.getSession().createQuery("from LotteryRoom o where o.status <> 2 and o.lotteryType.id = ? and o.lotteryHall.id = ?  order by sort desc ")
				.setParameter(0, lotteryType).setParameter(1, lotteryHallId).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<LotteryRoom> getLotteryRoomList(Integer lotteryType) {
		return this.getSession().createQuery("from LotteryRoom o where o.status = 1 and o.lotteryType.id = ?").setParameter(0, lotteryType).list();
	}
	
}
