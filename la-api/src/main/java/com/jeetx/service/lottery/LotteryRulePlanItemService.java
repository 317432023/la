package com.jeetx.service.lottery;

import java.util.Date;

import com.jeetx.bean.lottery.LotteryRulePlanItem;
import com.jeetx.service.dao.DAO;

public interface LotteryRulePlanItemService extends DAO<LotteryRulePlanItem> {
	public LotteryRulePlanItem findLotteryRulePlanItem(Integer stationId,Integer lotteryHallId,Integer lotteryRuleId,Date openTime);

}
