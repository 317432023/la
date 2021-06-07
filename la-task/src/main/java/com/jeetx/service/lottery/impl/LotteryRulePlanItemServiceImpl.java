package com.jeetx.service.lottery.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryRulePlan;
import com.jeetx.bean.lottery.LotteryRulePlanItem;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryRulePlanItemService;
import com.jeetx.util.DateTimeTool;

@Service
@Transactional
public class LotteryRulePlanItemServiceImpl extends DaoSupport<LotteryRulePlanItem> implements LotteryRulePlanItemService {

	@SuppressWarnings("unchecked")
	public LotteryRulePlanItem findLotteryRulePlanItem(Integer stationId,Integer lotteryHallId,Integer lotteryRuleId,Date openTime) {
		List<LotteryRulePlanItem> list = this.getSession().createQuery
				("from LotteryRulePlanItem o where o.lotteryRulePlan.status = 1 and  o.lotteryRulePlan.station.id = ? and o.lotteryRulePlan.lotteryHall.id = ? and o.lotteryRule.id = ? order by o.lotteryRulePlan.sortNum ")
				.setParameter(0, stationId).setParameter(1, lotteryHallId).setParameter(2, lotteryRuleId).list();
		for (LotteryRulePlanItem lotteryRulePlanItem : list) {
			LotteryRulePlan lotteryRulePlan = lotteryRulePlanItem.getLotteryRulePlan();
			if(lotteryRulePlan != null && StringUtils.isNotBlank(lotteryRulePlan.getBeginTime()) && StringUtils.isNotBlank(lotteryRulePlan.getEndTime())) {
				String dateFormat = "HH:mm:ss";
				String beignTime = lotteryRulePlan.getBeginTime();
				String endTime  = lotteryRulePlan.getEndTime();
//				try {
//					beignTime = DateTimeTool.dateFormat(dateFormat, DateTimeTool.addTime(DateTimeTool.dateFormat(dateFormat, beignTime), 5));
//					endTime = DateTimeTool.dateFormat(dateFormat, DateTimeTool.addTime(DateTimeTool.dateFormat(dateFormat, endTime), 5));
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}

				String curTime = DateTimeTool.dateFormat(dateFormat, openTime);
				System.out.println(beignTime.concat("-").concat(endTime));
				if(StringUtils.isNotBlank(beignTime) && StringUtils.isNotBlank(endTime) && 
						DateTimeTool.isInTime(beignTime.concat("-").concat(endTime), curTime,dateFormat)){
					return lotteryRulePlanItem;
				}
			}
		}
		return null;
	}
	
}
