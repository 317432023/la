package com.jeetx.service.lottery;

import java.util.List;

import com.jeetx.bean.lottery.LotteryRule;
import com.jeetx.service.dao.DAO;

public interface LotteryRuleService extends DAO<LotteryRule> {
	public List<LotteryRule> getListByHallId(Integer hallId) ;
	public List<LotteryRule> getListByHallIdNotType3(Integer hallId);
	public List<LotteryRule> getLotteryRuleList(Integer hallId,Integer ruleType);
	public String getParamValue(Integer lotteryHallId,String paramName);
	public void submitXxlLotteryRule(Integer stationId);
}
