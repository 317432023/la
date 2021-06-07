package com.jeetx.service.lottery.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryHall;
import com.jeetx.bean.lottery.LotteryRule;
import com.jeetx.bean.lottery.LotteryRuleDefault;
import com.jeetx.bean.lottery.LotteryRulePlanItem;
import com.jeetx.bean.lottery.LotteryType;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryHallService;
import com.jeetx.service.lottery.LotteryRuleDefaultService;
import com.jeetx.service.lottery.LotteryRulePlanItemService;
import com.jeetx.service.lottery.LotteryRuleService;
import com.jeetx.service.lottery.LotteryTypeService;

@Service
@Transactional
public class LotteryRuleServiceImpl extends DaoSupport<LotteryRule> implements LotteryRuleService {

	@Autowired LotteryHallService lotteryHallService;
	@Autowired LotteryTypeService lotteryTypeService;
	@Autowired LotteryRuleDefaultService lotteryRuleDefaultService;
	@Autowired LotteryRulePlanItemService lotteryRulePlanItemService;
	
	@SuppressWarnings("unchecked")
	public List<LotteryRule> getLotteryRuleList(Integer hallId,Integer stationId) {
		return this.getSession().createQuery("from LotteryRule o where o.lotteryHall.id = ? and o.station.id = ? order by id")
				.setParameter(0, hallId).setParameter(1, stationId).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<LotteryRule> getLotteryRuleList(String paramName,Integer ruleType,Integer lotteryTypeId) {
		return this.getSession().createQuery("from LotteryRule o where o.paramName = ? and o.ruleType = ? and o.lotteryType.id = ?")
				.setParameter(0, paramName).setParameter(1, ruleType).setParameter(2, lotteryTypeId).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<LotteryRule> getLotteryRuleList(Integer hallId,Integer ruleType,Integer stationId) {
		return this.getSession().createQuery("from LotteryRule o where o.lotteryHall.id = ? and o.ruleType = ? and o.station.id = ? order by id")
				.setParameter(0, hallId).setParameter(1, ruleType).setParameter(2, stationId).list();
	}
	
	@SuppressWarnings("unchecked")
	public LotteryRule getLotteryRule(Integer lotteryHallId,String paramName,Integer stationId) {
		List<LotteryRule> list = this.getSession().createQuery("from LotteryRule o where o.lotteryHall.id = ? and o.paramName = ? and o.station.id = ?")
				.setParameter(0, lotteryHallId).setParameter(1, paramName).setParameter(2, stationId).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (LotteryRule) list.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public String getParamValue(Integer lotteryHallId,String paramName,Integer stationId) {
		List<LotteryRule> list = this.getSession().createQuery("from LotteryRule o where o.lotteryHall.id = ? and o.paramName = ? and o.station.id = ?")
				.setParameter(0, lotteryHallId).setParameter(1, paramName).setParameter(2, stationId).list();
		if (list!=null&&list.size()>0) {
			LotteryRule lotteryRule = list.get(0);
			if(lotteryRule!=null && lotteryRule.getStatus() ==1) {
				LotteryRulePlanItem lotteryRulePlanItem = lotteryRulePlanItemService.findLotteryRulePlanItem(stationId, lotteryRule.getLotteryHall().getId(), lotteryRule.getId(),new Date());
				if(lotteryRulePlanItem !=null && StringUtils.isNotBlank(lotteryRulePlanItem.getParamValues())) {
					return lotteryRulePlanItem.getParamValues();
				}
				return lotteryRule.getParamValues();
			}
		}
		return null;
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void initLotteryRule() throws Exception {
		List<LotteryType> lotteryTypeList = lotteryTypeService.findByHql("from LotteryType");
		for (LotteryType lotteryType : lotteryTypeList) {
			
			//1、初始化赛车分车道赔率
			List<LotteryRuleDefault> lotteryRuleDefaultList = lotteryRuleDefaultService.getLotteryRuleListByLotteryType(lotteryType.getId());
			for (LotteryRuleDefault lotteryRuleDefault : lotteryRuleDefaultList) {
				if(lotteryRuleDefault.getLotteryType().getId() == 5 && lotteryRuleDefault.getRuleType()== 1 && !lotteryRuleDefault.getParamName().contains("-")) {
					for (int i = 1; i <= 5; i++) {
						LotteryRuleDefault lotteryRuleDefaultTemp = new LotteryRuleDefault();
						lotteryRuleDefaultTemp.setParamName(i+"-"+lotteryRuleDefault.getParamName());
						lotteryRuleDefaultTemp.setLotteryType(lotteryType);
						lotteryRuleDefaultTemp.setParamValues(lotteryRuleDefault.getParamValues());
						lotteryRuleDefaultTemp.setRemarks(lotteryRuleDefault.getRemarks());
						lotteryRuleDefaultTemp.setRuleType(lotteryRuleDefault.getRuleType());
						lotteryRuleDefaultTemp.setRuleRegular(lotteryRuleDefault.getRuleRegular());
						lotteryRuleDefaultService.save(lotteryRuleDefaultTemp);
					}
					
//					List<LotteryRule> lotteryRuleList = this.getLotteryRuleList(lotteryRuleDefault.getParamName(), lotteryRuleDefault.getRuleType(), lotteryType.getId());
//					for (LotteryRule lotteryRule : lotteryRuleList) {
//						lotteryRule.setStatus(-1);
//						this.update(lotteryRule);
//					}
					
					lotteryRuleDefaultService.delete(lotteryRuleDefault);
				}
			}
			
			//2、初始化缺省的游戏规则
			lotteryRuleDefaultList = lotteryRuleDefaultService.getLotteryRuleListByLotteryType(lotteryType.getId());
			for (LotteryRuleDefault lotteryRuleDefault : lotteryRuleDefaultList) {

				List<LotteryHall> LotteryHallList = lotteryHallService.getLotteryHallListByLotteryType(lotteryType.getId());
				for (LotteryHall lotteryHall : LotteryHallList) {
					LotteryRule lotteryRule = this.getLotteryRule(lotteryHall.getId(), lotteryRuleDefault.getParamName(), lotteryHall.getStation().getId());
					if(lotteryRule == null) {
						StringBuffer sb = new StringBuffer();
						sb.append(lotteryHall.getStation().getStationName()).append("-").append(lotteryHall.getLotteryType().getLotteryName()).append("-").append(lotteryHall.getTitle())
						.append("缺少规则：").append(lotteryRuleDefault.getParamName());
						System.out.println(sb.toString());
						
						lotteryRule = new LotteryRule();
						lotteryRule.setLotteryHall(lotteryHall);
						lotteryRule.setLotteryType(lotteryType);
						lotteryRule.setParamName(lotteryRuleDefault.getParamName());
						lotteryRule.setParamValues(lotteryRuleDefault.getParamValues());
						lotteryRule.setRemarks(lotteryRuleDefault.getRemarks());
						lotteryRule.setRuleType(lotteryRuleDefault.getRuleType());
						lotteryRule.setRuleRegular(lotteryRuleDefault.getRuleRegular());
						lotteryRule.setStatus(1);
						lotteryRule.setStation(lotteryHall.getStation());

						if(lotteryRule.getLotteryType().getId() == 5 && lotteryRule.getRuleType()== 1 && !lotteryRule.getParamName().contains("-")) {
							LotteryRule lotteryRule2 = this.getLotteryRule(lotteryHall.getId(), lotteryRule.getParamName().split("-")[1], lotteryHall.getStation().getId());
							if(lotteryRule2 !=null) {
								lotteryRule.setParamValues(lotteryRule2.getParamValues());
							}
						}
						this.save(lotteryRule);
					}
				}
			}
		}
	}
}
