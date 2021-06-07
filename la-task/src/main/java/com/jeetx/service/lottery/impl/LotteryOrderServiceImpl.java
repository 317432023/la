package com.jeetx.service.lottery.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryDailyOrderTotal;
import com.jeetx.bean.lottery.LotteryNumberConfig;
import com.jeetx.bean.lottery.LotteryOrder;
import com.jeetx.bean.lottery.LotteryOrderItem;
import com.jeetx.bean.lottery.LotteryPeriods;
import com.jeetx.bean.lottery.LotteryRoom;
import com.jeetx.bean.lottery.LotteryRule;
import com.jeetx.bean.lottery.LotteryRulePlanItem;
import com.jeetx.bean.member.TransRecord;
import com.jeetx.bean.member.User;
import com.jeetx.common.redis.JedisClient;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryDailyOrderTotalService;
import com.jeetx.service.lottery.LotteryNumberConfigService;
import com.jeetx.service.lottery.LotteryOrderItemService;
import com.jeetx.service.lottery.LotteryOrderService;
import com.jeetx.service.lottery.LotteryPeriodsService;
import com.jeetx.service.lottery.LotteryRoomMessageService;
import com.jeetx.service.lottery.LotteryRoomService;
import com.jeetx.service.lottery.LotteryRulePlanItemService;
import com.jeetx.service.lottery.LotteryRuleService;
import com.jeetx.service.member.PointsLevelService;
import com.jeetx.service.member.TransRecordService;
import com.jeetx.service.member.UserService;
import com.jeetx.service.system.StationConfigService;
import com.jeetx.timer.lotteryTask.FetchResult;
import com.jeetx.util.DateTimeTool;
import com.jeetx.util.LogUtil;

@Service
@Transactional
public class LotteryOrderServiceImpl extends DaoSupport<LotteryOrder> implements LotteryOrderService {
	 
	@Autowired JedisClient jedisClient;
	@Autowired LotteryPeriodsService lotteryPeriodsService;
	@Autowired LotteryRoomService lotteryRoomService;
	@Autowired UserService userService;
	@Autowired LotteryRuleService lotteryRuleService;
	@Autowired LotteryNumberConfigService lotteryNumberConfigService;
	@Autowired LotteryOrderItemService lotteryOrderItemService;
	@Autowired TransRecordService transRecordService;
	@Autowired LotteryDailyOrderTotalService lotteryDailyOrderTotalService;
	@Autowired LotteryRoomMessageService lotteryRoomMessageService;
	@Autowired PointsLevelService pointsLevelService;
	@Autowired StationConfigService stationConfigService;
	@Autowired LotteryRulePlanItemService lotteryRulePlanItemService;
	
	@Value("${openLotteryMessage}")
	private String openLotteryMessage;
	
	@SuppressWarnings("unchecked")
	public LotteryOrder findLotteryOrderByOrderCode(String orderCode) {
		List<LotteryOrder> list = this.getSession().createQuery("from LotteryOrder o where o.orderCode = ?").setParameter(0, orderCode).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (LotteryOrder) list.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public LotteryOrder findLotteryOrder(String username,Integer id) {
		List<LotteryOrder> list = this.getSession().createQuery("from LotteryOrder o where o.user.username = ? and o.id = ? ")
				.setParameter(0, username).setParameter(1, id).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (LotteryOrder) list.get(0);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public List<LotteryOrder> findLotteryOrderList(String lotteryPeriod,Integer lotteryRoomId) {
		return this.getSession().createQuery("from LotteryOrder o where o.status = 1 and o.lotteryPeriod=? and o.lotteryRoom.id = ? ")
				.setParameter(0, lotteryPeriod).setParameter(1, lotteryRoomId).list();
	}
	
//	@SuppressWarnings("unchecked")
//	public List<LotteryOrder> findLotteryOrderListByPeriod2HallId(String lotteryPeriod,Integer lotteryHallId) {
//		return this.getSession().createQuery("from LotteryOrder o where o.status = 1 and o.lotteryPeriod=? and o.lotteryRoom.lotteryHall.id = ? ")
//				.setParameter(0, lotteryPeriod).setParameter(1, lotteryHallId).list();
//	}
	
	/*** 开奖结算，发送开奖信息*/ 
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void settlementOrder(LotteryOrder lotteryOrder ,LotteryRoom lotteryRoom,LotteryPeriods lotteryPeriods) throws Exception {	
		lotteryOrder = this.find(lotteryOrder.getId());
		Integer combinationCount = 0;//组合期数
		if(lotteryOrder !=null && lotteryOrder.getStatus() == 1){
			LotteryRule lotteryRule = null;
			BigDecimal totalWinMoney = BigDecimal.ZERO;
			List<LotteryOrderItem> LotteryOrderItemList = lotteryOrder.getLotteryOrderItems();

			for (LotteryOrderItem lotteryOrderItem : LotteryOrderItemList) {
				BigDecimal winMoney = new BigDecimal(0);
				String lotteryRuleValues = null;
				switch (lotteryOrder.getLotteryType().getId()) {
				case 1:
				case 2:
				case 6:
					//含0的PC顺子集合
					String pcStraightCombine = stationConfigService.getValueByName("pc_straight_combine", lotteryOrder.getUser().getStation().getId());
					if(StringUtils.isNotBlank(pcStraightCombine) && StringUtils.isNotBlank(lotteryPeriods.getLotteryOpenContent())) {
						String[] numberArr = lotteryPeriods.getLotteryOpenContent().split("\\+");
						if(numberArr != null && numberArr.length == 3) {
							String numbers = FetchResult.sortNumber(Integer.valueOf(numberArr[0]), Integer.valueOf(numberArr[1]), Integer.valueOf(numberArr[2]));
							if(Arrays.asList(pcStraightCombine.split(",")).contains(numbers) && !lotteryPeriods.getLotteryShowContent().contains("顺子")) {
								lotteryPeriods.setLotteryShowContent(lotteryPeriods.getLotteryShowContent().replaceAll("\\)", "、顺子)"));
							}
						}
					}
					
					lotteryRule = lotteryOrderItem.getLotteryRule();
					LotteryRulePlanItem lotteryRulePlanItem = lotteryRulePlanItemService.findLotteryRulePlanItem(lotteryOrder.getUser().getStation().getId(), lotteryRule.getLotteryHall().getId(), lotteryRule.getId(),lotteryPeriods.getLotteryOpenTime());
					if(lotteryRulePlanItem !=null && StringUtils.isNotBlank(lotteryRulePlanItem.getParamValues())) {
						lotteryRuleValues = lotteryRulePlanItem.getParamValues();
					}else {
						lotteryRuleValues = lotteryRule.getParamValues();
					}
					
					LotteryNumberConfig lotteryNumberConfig = lotteryNumberConfigService.findLotteryNumberConfig(lotteryOrder.getLotteryType().getId(),lotteryPeriods.getLotteryOpenNumber());
					if(lotteryNumberConfig != null && lotteryRule.getParamName().equals(lotteryOrderItem.getRuleName())) {
						String[] groupCollectionArray = lotteryNumberConfig.getGroupCollection().split(":");
						if (Arrays.asList(groupCollectionArray).contains(lotteryRule.getParamName()) || 
								(lotteryPeriods.getLotteryShowContent().contains("顺子") && "顺子".equals(lotteryRule.getParamName()))||
								(lotteryPeriods.getLotteryShowContent().contains("豹子") && "豹子".equals(lotteryRule.getParamName()))||
								(lotteryPeriods.getLotteryShowContent().contains("对子") && "对子".equals(lotteryRule.getParamName()))) { // 中奖
							
							if(Arrays.asList("大单|小单|大双|小双".split("\\|")).contains(lotteryRule.getParamName())){
								combinationCount = 1;
							}

							BigDecimal paramValues = new BigDecimal(lotteryRuleValues);//赔率
							//开13,14的情况
							if(lotteryPeriods.getLotteryOpenNumber().equals(13) || lotteryPeriods.getLotteryOpenNumber().equals(14)){
								//大小单双开始13,14的情况
								if(Arrays.asList("大|小|单|双".split("\\|")).contains(lotteryRule.getParamName())){
									//大小单双特殊情况一：如遇开13、14，总注>S1时，赔率值S2
									String dxdsTotalGtSpecialValue1 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "大小单双特殊情况一：如遇开13、14，总注>S1时，赔率值S2");
									if(StringUtils.isNotBlank(dxdsTotalGtSpecialValue1)&&dxdsTotalGtSpecialValue1.contains("|")
											&&lotteryOrder.getBetMoney().compareTo(new BigDecimal(dxdsTotalGtSpecialValue1.split("\\|")[0])) >0){
										paramValues = new BigDecimal(dxdsTotalGtSpecialValue1.split("\\|")[1]);
									}
									
									//大小单双特殊情况二：如遇开13、14，总注>S1时，赔率值S2
									String dxdsTotalGtSpecialValue2 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "大小单双特殊情况二：如遇开13、14，总注>S1时，赔率值S2");
									if(StringUtils.isNotBlank(dxdsTotalGtSpecialValue2)&&dxdsTotalGtSpecialValue2.contains("|")
											&&lotteryOrder.getBetMoney().compareTo(new BigDecimal(dxdsTotalGtSpecialValue2.split("\\|")[0])) >0){
										paramValues = new BigDecimal(dxdsTotalGtSpecialValue2.split("\\|")[1]);
									}
								//组合开13,14的情况
								}else if(Arrays.asList("大单|小单|大双|小双".split("\\|")).contains(lotteryRule.getParamName())){
									//组合特殊情况一：如遇开13、14，总注>S1时，赔率值S2
									String zhheTotalGtSpecialValue1 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "组合特殊情况一：如遇开13、14，总注>S1时，赔率值S2");
									if(StringUtils.isNotBlank(zhheTotalGtSpecialValue1)&&zhheTotalGtSpecialValue1.contains("|")
											&&lotteryOrder.getBetMoney().compareTo(new BigDecimal(zhheTotalGtSpecialValue1.split("\\|")[0])) >0){
										paramValues = new BigDecimal(zhheTotalGtSpecialValue1.split("\\|")[1]);
									}
									
									//组合特殊情况二：如遇开13、14，总注>S1时，赔率值S2
									String zhheTotalGtSpecialValue2 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "组合特殊情况二：如遇开13、14，总注>S1时，赔率值S2");
									if(StringUtils.isNotBlank(zhheTotalGtSpecialValue2)&&zhheTotalGtSpecialValue2.contains("|")
											&&lotteryOrder.getBetMoney().compareTo(new BigDecimal(zhheTotalGtSpecialValue2.split("\\|")[0])) >0){
										paramValues = new BigDecimal(zhheTotalGtSpecialValue2.split("\\|")[1]);
									}
								}
								
								
								//总注特殊情况一：如遇开13、14，总注>S1时，赔率值S2
								String totalGtSpecialValue1 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "总注特殊情况一：如遇开13、14，总注>S1时，赔率值S2");
								if(StringUtils.isNotBlank(totalGtSpecialValue1)&&totalGtSpecialValue1.contains("|")
										&&lotteryOrder.getBetMoney().compareTo(new BigDecimal(totalGtSpecialValue1.split("\\|")[0])) >0){
									paramValues = new BigDecimal(totalGtSpecialValue1.split("\\|")[1]);
								}
								
								//总注特殊情况二：如遇开13、14，总注>S1时，赔率值S2
								String totalGtSpecialValue2 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "总注特殊情况二：如遇开13、14，总注>S1时，赔率值S2");
								if(StringUtils.isNotBlank(totalGtSpecialValue2)&&totalGtSpecialValue2.contains("|")
										&&lotteryOrder.getBetMoney().compareTo(new BigDecimal(totalGtSpecialValue2.split("\\|")[0])) >0){
									paramValues = new BigDecimal(totalGtSpecialValue2.split("\\|")[1]);
								}
							}
							
							//开顺豹对的情况
							if(lotteryPeriods.getLotteryShowContent().contains("顺子") ||lotteryPeriods.getLotteryShowContent().contains("豹子") ||
									lotteryPeriods.getLotteryShowContent().contains("对子")) {

								//大小单双开顺豹对的情况
								if(Arrays.asList("大|小|单|双".split("\\|")).contains(lotteryRule.getParamName())){
									//大小单双特殊情况三：如遇开顺豹对，总注>S1时，赔率值S2
									String dxdsTotalGtSpecialValue3 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "大小单双特殊情况三：如遇开顺豹对，总注>S1时，赔率值S2");
									if(StringUtils.isNotBlank(dxdsTotalGtSpecialValue3)&&dxdsTotalGtSpecialValue3.contains("|")
											&&lotteryOrder.getBetMoney().compareTo(new BigDecimal(dxdsTotalGtSpecialValue3.split("\\|")[0])) >0
											&& (new BigDecimal(dxdsTotalGtSpecialValue3.split("\\|")[1])).compareTo(new BigDecimal(0)) >0){//设置0时无效
										paramValues = new BigDecimal(dxdsTotalGtSpecialValue3.split("\\|")[1]);
									}
									
									//大小单双特殊情况四：如遇开顺豹对，总注>S1时，赔率值S2
									String dxdsTotalGtSpecialValue4 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "大小单双特殊情况四：如遇开顺豹对，总注>S1时，赔率值S2");
									if(StringUtils.isNotBlank(dxdsTotalGtSpecialValue4)&&dxdsTotalGtSpecialValue4.contains("|")
											&&lotteryOrder.getBetMoney().compareTo(new BigDecimal(dxdsTotalGtSpecialValue4.split("\\|")[0])) >0
											&& (new BigDecimal(dxdsTotalGtSpecialValue4.split("\\|")[1])).compareTo(new BigDecimal(0)) >0){//设置0时无效
										paramValues = new BigDecimal(dxdsTotalGtSpecialValue4.split("\\|")[1]);
									}
								//组合开顺豹对的情况
								}else if(Arrays.asList("大单|小单|大双|小双".split("\\|")).contains(lotteryRule.getParamName())){
									//组合特殊情况三：如遇开顺豹对，总注>S1时，赔率值S2
									String zhheTotalGtSpecialValue3 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "组合特殊情况三：如遇开顺豹对，总注>S1时，赔率值S2");
									if(StringUtils.isNotBlank(zhheTotalGtSpecialValue3)&&zhheTotalGtSpecialValue3.contains("|")
											&&lotteryOrder.getBetMoney().compareTo(new BigDecimal(zhheTotalGtSpecialValue3.split("\\|")[0])) >0
											&& (new BigDecimal(zhheTotalGtSpecialValue3.split("\\|")[1])).compareTo(new BigDecimal(0)) >0){//设置0时无效
										paramValues = new BigDecimal(zhheTotalGtSpecialValue3.split("\\|")[1]);
									}
									
									//组合特殊情况四：如遇开顺豹对，总注>S1时，赔率值S2
									String zhheTotalGtSpecialValue4 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "组合特殊情况四：如遇开顺豹对，总注>S1时，赔率值S2");
									if(StringUtils.isNotBlank(zhheTotalGtSpecialValue4)&&zhheTotalGtSpecialValue4.contains("|")
											&&lotteryOrder.getBetMoney().compareTo(new BigDecimal(zhheTotalGtSpecialValue4.split("\\|")[0])) >0
											&& (new BigDecimal(zhheTotalGtSpecialValue4.split("\\|")[1])).compareTo(new BigDecimal(0)) >0){//设置0时无效
										paramValues = new BigDecimal(zhheTotalGtSpecialValue4.split("\\|")[1]);
									}
								}
								
								
								//总注特殊情况三：如遇开顺豹对，总注>S1时，赔率值S2
								String totalGtSpecialValue3 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "总注特殊情况三：如遇开顺豹对，总注>S1时，赔率值S2");
								if(StringUtils.isNotBlank(totalGtSpecialValue3)&&totalGtSpecialValue3.contains("|")
										&&lotteryOrder.getBetMoney().compareTo(new BigDecimal(totalGtSpecialValue3.split("\\|")[0])) >0
										&& (new BigDecimal(totalGtSpecialValue3.split("\\|")[1])).compareTo(new BigDecimal(0)) >0){//设置0时无效
									paramValues = new BigDecimal(totalGtSpecialValue3.split("\\|")[1]);
								}
								
								//总注特殊情况四：如遇开顺豹对，总注>S1时，赔率值S2
								String totalGtSpecialValue4 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "总注特殊情况四：如遇开顺豹对，总注>S1时，赔率值S2");
								if(StringUtils.isNotBlank(totalGtSpecialValue4)&&totalGtSpecialValue4.contains("|")
										&&lotteryOrder.getBetMoney().compareTo(new BigDecimal(totalGtSpecialValue4.split("\\|")[0])) >0
										&& (new BigDecimal(totalGtSpecialValue4.split("\\|")[1])).compareTo(new BigDecimal(0)) >0){//设置0时无效
									paramValues = new BigDecimal(totalGtSpecialValue4.split("\\|")[1]);
								}
							}

							if(paramValues.compareTo(new BigDecimal(0))>0){
								winMoney = lotteryOrderItem.getBetMoney().multiply(paramValues);//中奖金额
								winMoney = winMoney.setScale(0, BigDecimal.ROUND_HALF_UP);
								totalWinMoney = totalWinMoney.add(winMoney);//总中奖金额
							}
						}
					}
					break;
				case 3:
				case 4:
					BigDecimal paramValues = new BigDecimal(0);
					List<String> munberList = Arrays.asList(lotteryPeriods.getLotteryOpenContent().split("\\+"));
					lotteryRule = lotteryOrderItem.getLotteryRule();
					lotteryRulePlanItem = lotteryRulePlanItemService.findLotteryRulePlanItem(lotteryOrder.getUser().getStation().getId(), lotteryRule.getLotteryHall().getId(), lotteryRule.getId(),lotteryPeriods.getLotteryOpenTime());
					if(lotteryRulePlanItem !=null && StringUtils.isNotBlank(lotteryRulePlanItem.getParamValues())) {
						lotteryRuleValues = lotteryRulePlanItem.getParamValues();
					}else {
						lotteryRuleValues = lotteryRule.getParamValues();
					}
					
					//猜双面、猜号码
					if(lotteryRule.getRuleType()==1 || lotteryRule.getRuleType()==2) {//^(10|[1-9])-(大)$   |  ^(10|[1-9])-(10|[1-9])$
						Integer digit = Integer.valueOf(lotteryOrderItem.getRuleName().split("-")[0]);//位数
						Integer number = Integer.valueOf(munberList.get(digit-1));//对应位数的开奖号码
						String ruleName = lotteryOrderItem.getRuleName().split("-")[1];//玩法
						
						lotteryNumberConfig = lotteryNumberConfigService.findLotteryNumberConfig(lotteryOrder.getLotteryType().getId(),number);
						if(lotteryNumberConfig != null && Arrays.asList(lotteryNumberConfig.getGroupCollection().split(":")).contains(ruleName)) {// 中奖
							paramValues = new BigDecimal(lotteryRuleValues);//赔率
						}
					//龙虎斗
					}else if(lotteryRule.getRuleType()==3) {//^([1-5])-(龙|虎)$
						Integer digit = Integer.valueOf(lotteryOrderItem.getRuleName().split("-")[0]);//位数
						Integer number1 = Integer.valueOf(munberList.get(digit-1));//对应位数的开奖号码1
						Integer number2 = Integer.valueOf(munberList.get(10-digit));//对应位数的开奖号码2
						String ruleName = lotteryOrderItem.getRuleName().split("-")[1];//玩法
						
						if(number1>number2 && "龙".contains(ruleName)) {
							paramValues = new BigDecimal(lotteryRuleValues);//赔率
						}else if(number1<number2 && "虎".contains(ruleName)) {
							paramValues = new BigDecimal(lotteryRuleValues);//赔率
						}
					//猜庄闲
					}else if(lotteryRule.getRuleType()==4) {//^(庄|闲)$
						Integer number1 = Integer.valueOf(munberList.get(0));//对应位数的开奖号码1
						Integer number2 = Integer.valueOf(munberList.get(1));//对应位数的开奖号码2
						String ruleName = lotteryOrderItem.getRuleName();//玩法
						
						if(number1>number2 && "庄".contains(ruleName)) {
							paramValues = new BigDecimal(lotteryRuleValues);//赔率
						}else if(number1<number2 && "闲".contains(ruleName)) {
							paramValues = new BigDecimal(lotteryRuleValues);//赔率
						}
					//猜冠亚
					}else if(lotteryRule.getRuleType()==5) {//^(?!.*?(10|[1-9]).*?1)((10|[1-9])(,(10|[1-9])))$
						Integer number1 = Integer.valueOf(munberList.get(0));//对应位数的开奖号码1
						Integer number2 = Integer.valueOf(munberList.get(1));//对应位数的开奖号码2
						
						if((number1.toString().equals(lotteryOrderItem.getRuleName().split(",")[0]) 
								&& number2.toString().equals(lotteryOrderItem.getRuleName().split(",")[1])) ||
								(number1.toString().equals(lotteryOrderItem.getRuleName().split(",")[1]) 
										&& number2.toString().equals(lotteryOrderItem.getRuleName().split(",")[0]))) {
							paramValues = new BigDecimal(lotteryRuleValues);//赔率
						}
					//冠亚和:冠亚军的号码相加
					}else if(lotteryRule.getRuleType()==6) {//^(大|双)$\^(小|单)$
						Integer number1 = Integer.valueOf(munberList.get(0));//对应位数的开奖号码1
						Integer number2 = Integer.valueOf(munberList.get(1));//对应位数的开奖号码2
						String ruleName = lotteryOrderItem.getRuleName();//玩法
						
						Integer sumNumber = number1 + number2;
						if(sumNumber > 11 && "大".equals(ruleName)) {
							paramValues = new BigDecimal(lotteryRuleValues);//赔率
						}else if(sumNumber <= 11 && "小".equals(ruleName)) {
							paramValues = new BigDecimal(lotteryRuleValues);//赔率
						}else if(sumNumber%2!=0 && "单".equals(ruleName)) {
							paramValues = new BigDecimal(lotteryRuleValues);//赔率
						}else if(sumNumber%2==0 && "双".equals(ruleName)) {
							paramValues = new BigDecimal(lotteryRuleValues);//赔率
						}
					//冠亚和:冠亚军的号码和值:[3,4,18,19]
					}else if(lotteryRule.getRuleType()==7) {//^(3|4|18|19)$
						Integer number1 = Integer.valueOf(munberList.get(0));//对应位数的开奖号码1
						Integer number2 = Integer.valueOf(munberList.get(1));//对应位数的开奖号码2
						String ruleName = lotteryOrderItem.getRuleName();//玩法
						
						Integer sumNumber = number1 + number2;
						if(sumNumber.toString().equals(ruleName)) {
							paramValues = new BigDecimal(lotteryRuleValues);//赔率
						}
					//冠亚和 A:冠亚军的号码和值:[3,4,5,6,7]、B：冠亚军的号码和值:[8,9,10,11,12,13,14]、c:冠亚军的号码和值:[15,16,17,18,19]	
					}else if(lotteryRule.getRuleType()==7) {//^(3|4|18|19)$	
						Integer number1 = Integer.valueOf(munberList.get(0));//对应位数的开奖号码1
						Integer number2 = Integer.valueOf(munberList.get(1));//对应位数的开奖号码2
						String ruleName = lotteryOrderItem.getRuleName();//玩法
						
						Integer sumNumber = number1 + number2;
						if(Arrays.asList("3,4,5,6,7".split(",")).contains(sumNumber.toString()) && "A".equals(ruleName)) {
							paramValues = new BigDecimal(lotteryRuleValues);//赔率
						}else if(Arrays.asList("8,9,10,11,12,13,14".split(",")).contains(sumNumber.toString())  && "B".equals(ruleName)) {
							paramValues = new BigDecimal(lotteryRuleValues);//赔率
						}else if(Arrays.asList("15,16,17,18,19".split(",")).contains(sumNumber.toString())  && "C".equals(ruleName)) {
							paramValues = new BigDecimal(lotteryRuleValues);//赔率
						}
					}

					if(paramValues.compareTo(new BigDecimal(0))>0){
						winMoney = lotteryOrderItem.getBetMoney().multiply(paramValues);//中奖金额
						winMoney = winMoney.setScale(0, BigDecimal.ROUND_HALF_UP);
						totalWinMoney = totalWinMoney.add(winMoney);//总中奖金额
					}
					break;
				case 5:
					paramValues = new BigDecimal(0);
					munberList = Arrays.asList(lotteryPeriods.getLotteryOpenContent().split("\\+"));
					lotteryRule = lotteryOrderItem.getLotteryRule();
					lotteryRulePlanItem = lotteryRulePlanItemService.findLotteryRulePlanItem(lotteryOrder.getUser().getStation().getId(), lotteryRule.getLotteryHall().getId(), lotteryRule.getId(),lotteryPeriods.getLotteryOpenTime());
					if(lotteryRulePlanItem !=null && StringUtils.isNotBlank(lotteryRulePlanItem.getParamValues())) {
						lotteryRuleValues = lotteryRulePlanItem.getParamValues();
					}else {
						lotteryRuleValues = lotteryRule.getParamValues();
					}
					
					//猜双面、猜数字
					if(lotteryRule.getRuleType()==1 || lotteryRule.getRuleType()==2) {//^([1-5])-(大)$   |  ^([1-5])-([0-9])$
						Integer digit = Integer.valueOf(lotteryOrderItem.getRuleName().split("-")[0]);//位数
						Integer number = Integer.valueOf(munberList.get(digit-1));//对应位数的开奖号码
						String ruleName = lotteryOrderItem.getRuleName().split("-")[1];//玩法
						
						lotteryNumberConfig = lotteryNumberConfigService.findLotteryNumberConfig(lotteryOrder.getLotteryType().getId(),number);
						if(lotteryNumberConfig != null && Arrays.asList(lotteryNumberConfig.getGroupCollection().split(":")).contains(ruleName)) {// 中奖
							paramValues = new BigDecimal(lotteryRuleValues);//赔率
						}
					//后两位数的和值
					}else if(lotteryRule.getRuleType()==3) {
						Integer number1 = Integer.valueOf(munberList.get(3));//对应位数的开奖号码1
						Integer number2 = Integer.valueOf(munberList.get(4));//对应位数的开奖号码2
						String ruleName = lotteryOrderItem.getRuleName().split("-")[1];//玩法
						
						Integer sumNumber = number1 + number2;
						if(sumNumber.toString().equals(ruleName)) {
							paramValues = new BigDecimal(lotteryRuleValues);//赔率
						}
					//龙虎斗 第一位数比第五位数大为龙，小为虎
					}else if(lotteryRule.getRuleType()==3) {
						String[] digit = lotteryOrderItem.getRuleName().replace("龙", "").replace("虎", "").replace("龙", "").split("-");//玩法
						Integer number1 = Integer.valueOf(munberList.get(Integer.valueOf(digit[0])-1));//对应位数的开奖号码1
						Integer number2 = Integer.valueOf(munberList.get(Integer.valueOf(digit[1])-1));//对应位数的开奖号码2
						String ruleName = lotteryOrderItem.getRuleName();//玩法
						
						if(number1>number2 && ruleName.contains("龙")) {
							paramValues = new BigDecimal(lotteryRuleValues);//赔率
						}else if(number1<number2 && ruleName.contains("虎")) {
							paramValues = new BigDecimal(lotteryRuleValues);//赔率
						}else if(number1==number2 && ruleName.contains("和")) {
							paramValues = new BigDecimal(lotteryRuleValues);//赔率
						}
					}

					if(paramValues.compareTo(new BigDecimal(0))>0){
						winMoney = lotteryOrderItem.getBetMoney().multiply(paramValues);//中奖金额
						winMoney = winMoney.setScale(0, BigDecimal.ROUND_HALF_UP);
						totalWinMoney = totalWinMoney.add(winMoney);//总中奖金额
					}

					break;
				}
				
				/**1、派奖*/
				if(winMoney.compareTo(new BigDecimal(0))>0){
					User user = userService.find(lotteryOrder.getUser().getId());
					user.setPoints(user.getPoints()+winMoney.setScale(0, BigDecimal.ROUND_DOWN).intValue());//投注送积分
					user.setPointsLevel(pointsLevelService.findPointsLevel(user.getPoints().intValue()));
					user.setBalance(user.getBalance().add(winMoney));
					userService.update(user);
					
					//生成派奖记录
					TransRecord tr = new TransRecord();
					tr.setUser(user);
					tr.setCreateTime(new Date());
					tr.setTransCategory(7);//交易类型（1充值、2提现、3抽奖、4投注、5撤单、6赠送、7中奖、8回水、9调账）
					tr.setTransAmount(winMoney);
					tr.setEndBalance(user.getBalance());
					tr.setTransLotteryAmount(new BigDecimal(0));
					tr.setEndLotteryBalance(user.getLotteryBalance());
					tr.setFlag(1);
					tr.setRemark("第" + lotteryOrder.getLotteryPeriod() + "期"+lotteryOrderItem.getRuleName()+"("+lotteryOrderItem.getBetMoney()+")中奖" + winMoney + "(含本金)");
					transRecordService.save(tr);
				}
				
				/**2、更新订单明细状态*/
				lotteryOrderItem.setWinMoney(winMoney);
				lotteryOrderItemService.update(lotteryOrderItem);
			}
			
			/**3、变更订单状态*/
			lotteryOrder.setCombinationCount(combinationCount);//组合期数（有组合1、没有组合0）
			lotteryOrder.setWinMoney(totalWinMoney);//中奖金额（含本）
			lotteryOrder.setLotteryOpenContent(lotteryPeriods.getLotteryShowContent());
			lotteryOrder.setProfitMoney(lotteryOrder.getWinMoney().subtract(lotteryOrder.getBetMoney()));
			lotteryOrder.setStatus(totalWinMoney.compareTo(new BigDecimal(0))>0?2:4);//状态(1待开奖、2已中奖、3已取消、4未中奖)
			this.update(lotteryOrder);
			
			
			/**4、生成统计信息*/
			LotteryDailyOrderTotal lotteryDailyOrderTotal = lotteryDailyOrderTotalService.getLotteryDailyOrderTotal(DateTimeTool.dateFormat("yyyy-MM-dd", new Date()), lotteryOrder.getUser().getId());
			if(lotteryDailyOrderTotal==null) {
				lotteryDailyOrderTotal = new LotteryDailyOrderTotal();
				
				lotteryDailyOrderTotal.setUser(lotteryOrder.getUser());//玩家帐号
				lotteryDailyOrderTotal.setTotalDate(DateTimeTool.dateFormat("yyyy-MM-dd", DateTimeTool.dateFormat("yyyy-MM-dd", new Date())));//统计日期
				lotteryDailyOrderTotal.setBetMoney(new BigDecimal(0));//流水金额
				lotteryDailyOrderTotal.setProfitMoney(lotteryOrder.getProfitMoney());//盈亏金额
				lotteryDailyOrderTotal.setBackWaterMoney(new BigDecimal(0));// //回水金额
				lotteryDailyOrderTotal.setRechargeMoney(new BigDecimal(0));//充值金额
				lotteryDailyOrderTotal.setWithdrawMoney(new BigDecimal(0));//提现金额
				lotteryDailyOrderTotal.setWinMoney(lotteryOrder.getWinMoney());//中奖金额
				lotteryDailyOrderTotal.setExpandUserNum(0);//拓展玩家数
				lotteryDailyOrderTotal.setBalance(lotteryOrder.getUser().getBalance());
				lotteryDailyOrderTotal.setLotteryBalance(lotteryOrder.getUser().getLotteryBalance());//赠送彩金统计
				lotteryDailyOrderTotalService.save(lotteryDailyOrderTotal);
			}else{
				lotteryDailyOrderTotal.setProfitMoney(lotteryDailyOrderTotal.getProfitMoney().add(lotteryOrder.getProfitMoney()));
				lotteryDailyOrderTotal.setWinMoney(lotteryDailyOrderTotal.getWinMoney().add(lotteryOrder.getWinMoney()));//中奖金额
				lotteryDailyOrderTotal.setBalance(lotteryOrder.getUser().getBalance());
				lotteryDailyOrderTotal.setLotteryBalance(lotteryOrder.getUser().getLotteryBalance());//赠送彩金统计
				lotteryDailyOrderTotalService.update(lotteryDailyOrderTotal);
			}
			
			if(lotteryOrder.getWinMoney().compareTo(new BigDecimal(0))>0){
				BigDecimal winMoney = new BigDecimal(0); 
				String key = "win_".concat(lotteryOrder.getUser().getId().toString());
				
				try {
					String winMoneyStr = jedisClient.get(key);
					if(StringUtils.isNotBlank(winMoneyStr)) {
						winMoney = new BigDecimal(winMoneyStr); 
					}
					
					winMoney = winMoney.add(lotteryOrder.getWinMoney());
					jedisClient.set(key, winMoney.toString());
					jedisClient.expire(key, 60*3); //设置会话过期时间180秒=3分钟
					
					LogUtil.info(lotteryOrder.getUser().getUsername()+"的订单(".concat(lotteryOrder.getOrderCode()).concat(")派奖完成，推送中奖金额：".concat(winMoney.toString())));
				} catch (Exception e) {
					e.printStackTrace();
					LogUtil.info(lotteryOrder.getUser().getUsername()+"的订单(".concat(lotteryOrder.getOrderCode()).concat(")派奖完成，推送中奖金额异常：")+e);
				}
			}
		}
	}
}
