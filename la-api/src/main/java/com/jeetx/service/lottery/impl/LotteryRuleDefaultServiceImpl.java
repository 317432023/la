package com.jeetx.service.lottery.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryRuleDefault;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryRuleDefaultService;

@Service
@Transactional
public class LotteryRuleDefaultServiceImpl extends DaoSupport<LotteryRuleDefault> implements LotteryRuleDefaultService {

	@SuppressWarnings("unchecked")
	public List<LotteryRuleDefault> getLotteryRuleListByLotteryType(Integer lotteryTypeId) {
		return this.getSession().createQuery("from LotteryRuleDefault o where o.lotteryType.id = ? order by id").setParameter(0, lotteryTypeId).list();
	}
}
