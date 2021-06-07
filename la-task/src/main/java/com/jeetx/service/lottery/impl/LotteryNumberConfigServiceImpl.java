package com.jeetx.service.lottery.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryNumberConfig;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryNumberConfigService;

@Service
@Transactional
public class LotteryNumberConfigServiceImpl extends DaoSupport<LotteryNumberConfig> implements LotteryNumberConfigService {

	@SuppressWarnings("unchecked")
	public LotteryNumberConfig findLotteryNumberConfig(Integer lotteryType,Integer code) {
		List<LotteryNumberConfig> list = this.getSession().createQuery("from LotteryNumberConfig o where o.lotteryType.id = ? and o.code = ?").setParameter(0, lotteryType).setParameter(1, code).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (LotteryNumberConfig) list.get(0);
		}
	}
}
