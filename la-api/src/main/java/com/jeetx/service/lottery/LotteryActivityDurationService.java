package com.jeetx.service.lottery;

import com.jeetx.bean.lottery.LotteryActivityDuration;
import com.jeetx.service.dao.DAO;

public interface LotteryActivityDurationService extends DAO<LotteryActivityDuration> {
	public LotteryActivityDuration findLotteryActivityDuration(Integer amount,Integer lotteryActivityId) ;
}
