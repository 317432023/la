package com.jeetx.service.lottery.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryActivityDuration;
import com.jeetx.bean.member.PointsLevel;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryActivityDurationService;

@Service
@Transactional
public class LotteryActivityDurationServiceImpl extends DaoSupport<LotteryActivityDuration> implements LotteryActivityDurationService {

	@SuppressWarnings("unchecked")
	public LotteryActivityDuration findLotteryActivityDuration(Integer amount,Integer lotteryActivityId) {
		List<LotteryActivityDuration> list = this.getSession().createQuery("from LotteryActivityDuration o where o.minAmount <= ? and o.maxAmount >= ? and lotteryActivity.id = ?")
				.setParameter(0, amount).setParameter(1, amount).setParameter(2, lotteryActivityId).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (LotteryActivityDuration) list.get(0);
		}
	}
	
}
