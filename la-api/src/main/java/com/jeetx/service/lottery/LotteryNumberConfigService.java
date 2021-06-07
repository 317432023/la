package com.jeetx.service.lottery;

import com.jeetx.bean.lottery.LotteryNumberConfig;
import com.jeetx.service.dao.DAO;

public interface LotteryNumberConfigService extends DAO<LotteryNumberConfig> {
	public LotteryNumberConfig findLotteryNumberConfig(Integer lotteryType,Integer code) ;
}
