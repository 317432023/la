package com.jeetx.service.lottery.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryWaterConfig;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryWaterConfigService;

@Service
@Transactional
public class LotteryWaterConfigServiceImpl extends DaoSupport<LotteryWaterConfig> implements LotteryWaterConfigService {

	@SuppressWarnings("unchecked")
	public List<LotteryWaterConfig> getLotteryWaterConfigList(Integer lotteryHallId) {
		return this.getSession().createQuery("from LotteryWaterConfig o where o.lotteryHall.id = ?").setParameter(0, lotteryHallId).list();
	}
	

	@SuppressWarnings("unchecked")
	public LotteryWaterConfig findLotteryWaterConfig(BigDecimal money,Integer lotteryHallId) {
		List<LotteryWaterConfig> list = this.getSession().createQuery("from LotteryWaterConfig o where o.lowMoney <= ? and o.upMoney > ? and o.lotteryHall.id = ?")
				.setParameter(0, money).setParameter(1, money).setParameter(2, lotteryHallId).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (LotteryWaterConfig) list.get(0);
		}
	}
}
