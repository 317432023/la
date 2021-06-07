package com.jeetx.service.lottery;

import java.util.List;

import com.jeetx.bean.lottery.LotteryRule;
import com.jeetx.service.dao.DAO;

public interface LotteryRuleService extends DAO<LotteryRule> {
	public List<LotteryRule> getLotteryRuleList(Integer hallId,Integer stationId);
	public List<LotteryRule> getLotteryRuleList(String paramName,Integer ruleType,Integer lotteryTypeId);
	public List<LotteryRule> getLotteryRuleList(Integer hallId,Integer ruleType,Integer stationId);
	public LotteryRule getLotteryRule(Integer lotteryHallId,String paramName,Integer stationId);
	public String getParamValue(Integer lotteryHallId,String paramName,Integer stationId);
	public void initLotteryRule() throws Exception;
}
