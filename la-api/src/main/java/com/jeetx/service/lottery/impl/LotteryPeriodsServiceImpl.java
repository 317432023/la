package com.jeetx.service.lottery.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryPeriods;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryNumberConfigService;
import com.jeetx.service.lottery.LotteryPeriodsService;
import com.jeetx.service.lottery.LotteryTypeService;
import com.jeetx.service.system.SystemConfigService;
import com.jeetx.util.DateTimeTool;

@Service
@Transactional
public class LotteryPeriodsServiceImpl extends DaoSupport<LotteryPeriods> implements LotteryPeriodsService {

	@Autowired SystemConfigService systemConfigService;
	@Autowired LotteryTypeService lotteryTypeService;
	@Autowired LotteryNumberConfigService lotteryNumberConfigService;
	
	@SuppressWarnings("unchecked")
	public LotteryPeriods currentFinishLotteryPeriods(Integer lotteryType) {
		List<LotteryPeriods> list = this.getSession().createQuery("from LotteryPeriods o where o.lotteryType.id=? and o.status = 3 ORDER BY o.lotteryOpenTime DESC ")
				.setParameter(0, lotteryType).setFirstResult(0).setMaxResults(1).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (LotteryPeriods) list.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public LotteryPeriods findCurrentLotteryPeriods(Integer lotteryType) {
		String date = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", new Date());
		List<LotteryPeriods> list = this.getSession().createQuery("from LotteryPeriods o where o.lotteryType.id=? and o.lotteryBeginTime <=str_to_date(?,'%Y-%m-%d %H:%i:%s') and o.lotteryOpenTime >=str_to_date(?,'%Y-%m-%d %H:%i:%s')")
				.setParameter(0, lotteryType).setParameter(1, date).setParameter(2, date).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (LotteryPeriods) list.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public LotteryPeriods findTopLotteryPeriods(Integer lotteryType) {
		List<LotteryPeriods> list = this.getSession().createQuery("from LotteryPeriods o where o.lotteryType.id=? order by o.lotteryOpenTime desc ").setParameter(0, lotteryType)
				.setFirstResult(0).setMaxResults(1).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (LotteryPeriods) list.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public LotteryPeriods findLotteryPeriodsByDate(Integer lotteryType,String date) {
		List<LotteryPeriods> list = this.getSession().createQuery("from LotteryPeriods o where o.lotteryType.id=? and o.lotteryBeginTime <=str_to_date(?,'%Y-%m-%d %H:%i:%s') and o.lotteryOpenTime >=str_to_date(?,'%Y-%m-%d %H:%i:%s')")
				.setParameter(0, lotteryType).setParameter(1, date).setParameter(2, date).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (LotteryPeriods) list.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public LotteryPeriods findLotteryPeriodsByPeriods(Integer lotteryType,String lotteryPeriods) {
		List<LotteryPeriods> list = this.getSession().createQuery("from LotteryPeriods o where o.lotteryType.id=? and o.lotteryPeriods = ?")
				.setParameter(0, lotteryType).setParameter(1, lotteryPeriods).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (LotteryPeriods) list.get(0);
		}
	}
}
