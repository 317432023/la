package com.jeetx.service.lottery;

import com.jeetx.bean.lottery.LotteryPeriods;
import com.jeetx.service.dao.DAO;

public interface LotteryPeriodsService extends DAO<LotteryPeriods> {
	public LotteryPeriods currentFinishLotteryPeriods(Integer lotteryType);
	public LotteryPeriods findCurrentLotteryPeriods(Integer lotteryType);
	public LotteryPeriods findTopLotteryPeriods(Integer lotteryType);
	public LotteryPeriods findLotteryPeriodsByDate(Integer lotteryType,String date);
	public LotteryPeriods findLotteryPeriodsByPeriods(Integer lotteryType,String lotteryPeriods);
}
