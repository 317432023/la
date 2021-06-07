package com.jeetx.service.lottery;

import java.util.List;

import com.jeetx.bean.lottery.LotteryRuleDefault;
import com.jeetx.service.dao.DAO;

public interface LotteryRuleDefaultService extends DAO<LotteryRuleDefault> {
	public List<LotteryRuleDefault> getLotteryRuleListByLotteryType(Integer lotteryTypeId);
}
