package com.jeetx.service.lottery;

import java.math.BigDecimal;
import java.util.List;

import com.jeetx.bean.lottery.LotteryWaterConfig;
import com.jeetx.service.dao.DAO;

public interface LotteryWaterConfigService extends DAO<LotteryWaterConfig> {
	public List<LotteryWaterConfig> getLotteryWaterConfigList(Integer lotteryHallId);
	public LotteryWaterConfig findLotteryWaterConfig(BigDecimal money,Integer lotteryHallId);
}
