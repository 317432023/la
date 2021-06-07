package com.jeetx.service.lottery.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryPeriods;
import com.jeetx.bean.lottery.LotteryRobotPlant;
import com.jeetx.bean.lottery.LotteryRobotPlantConfig;
import com.jeetx.bean.lottery.LotteryRoom;
import com.jeetx.bean.lottery.LotteryRule;
import com.jeetx.common.swagger.model.lottery.LotteryOrderItemDTO;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryRobotPlantConfigService;
import com.jeetx.service.lottery.LotteryRobotPlantService;
import com.jeetx.service.lottery.LotteryRoomMessageService;
import com.jeetx.service.lottery.LotteryRoomService;
import com.jeetx.service.lottery.LotteryRuleService;
import com.jeetx.service.member.PointsLevelService;
import com.jeetx.util.JsonUtil;
import com.jeetx.util.RandomUtil;

@Service
@Transactional
public class LotteryRobotPlantConfigServiceImpl extends DaoSupport<LotteryRobotPlantConfig> implements LotteryRobotPlantConfigService {

	@Autowired LotteryRoomMessageService lotteryRoomMessageService;
	@Autowired LotteryRoomService lotteryRoomService;
	@Autowired LotteryRuleService lotteryRuleService;
	@Autowired PointsLevelService pointsLevelService;
	@Autowired LotteryRobotPlantService lotteryRobotPlantService;
	
	public LotteryRobotPlantConfig findByLotteryRoomId(Integer lotteryRoomId){
		List<LotteryRobotPlantConfig> list = this.getSession().createQuery("from LotteryRobotPlantConfig o where o.lotteryRoom.id=? and o.status = 1").setParameter(0, lotteryRoomId).list();
		if (list!=null&&list.size()>0) {
			return list.get(0);
		}
		return null;
	}

	/***托自动下单*/
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false) 
	public String submitPlantOrder(LotteryRoom lotteryRoom,LotteryPeriods lotteryPeriods,LotteryRobotPlantConfig lotteryRobotPlantConfig,
			LotteryRobotPlant lotteryRobotPlant,List<LotteryRule> orderPlanList) throws Exception{
		List<LotteryOrderItemDTO> orderItemList = new ArrayList<LotteryOrderItemDTO>();;
		lotteryRoom = lotteryRoomService.find(lotteryRoom.getId());
				
		String allBetContent = null;
		BigDecimal allBetMoney = new BigDecimal(0);//总金额
		int k = RandomUtil.getRangeRandom(1,100)%2;
		if(orderPlanList != null && orderPlanList.size()>0) {
			for(int i=0;i<=k;i++) {
				LotteryRule lotteryRule = orderPlanList.get(RandomUtil.getRangeRandom(0, orderPlanList.size()-1));
				if(lotteryRule != null) {
					BigDecimal betMoney = new BigDecimal(RandomUtil.getRangeRandom(lotteryRobotPlantConfig.getMinMoney(),lotteryRobotPlantConfig.getMaxMoney()));//投注金额
					String betContent = null;
					String ruleName =  lotteryRule.getParamName();
					
					betMoney = betMoney.compareTo(new BigDecimal(10))<0?new BigDecimal(10):betMoney;
					if(betMoney.compareTo(new BigDecimal(100))<0) {
						betMoney = betMoney.divide(new BigDecimal(10)).setScale(0, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(10));
					}else {
						betMoney = betMoney.divide(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(100));
					}
					
					switch (lotteryRoom.getLotteryType().getId(	)) {
					case 1:
					case 2:
					case 6:
						if(Arrays.asList("红|绿|蓝".split("\\|")).contains(ruleName)) {
							continue; 
						}
						
						if(Arrays.asList("大|小|单|双".split("\\|")).contains(ruleName)){
							String betRange = lotteryRuleService.getParamValue(lotteryRoom.getLotteryHall().getId(), "大小单双限制");
							if(StringUtils.isNotBlank(betRange)&&betRange.contains("-")){
								if(betMoney.compareTo(new BigDecimal(betRange.split("-")[0]))<0 
										|| betMoney.compareTo(new BigDecimal(betRange.split("-")[1]))>0) {
									//System.out.println("大小单双限制");
									continue;
								}
							}
							allBetContent = StringUtils.isNotBlank(allBetContent)?allBetContent.concat("|").concat(lotteryRule.getParamName().concat(lotteryRule.getParamValues())):lotteryRule.getParamName().concat(lotteryRule.getParamValues());
						}else if(Arrays.asList("大单|小单|大双|小双".split("\\|")).contains(ruleName)) {
							String betRange = lotteryRuleService.getParamValue(lotteryRoom.getLotteryHall().getId(), "组合限制");
							if(StringUtils.isNotBlank(betRange)&&betRange.contains("-")){
								if(betMoney.compareTo(new BigDecimal(betRange.split("-")[0]))<0 
										|| betMoney.compareTo(new BigDecimal(betRange.split("-")[1]))>0) {
									//System.out.println("组合限制");
									continue;
								}
							}
							allBetContent = StringUtils.isNotBlank(allBetContent)?allBetContent.concat("|").concat(lotteryRule.getParamName().concat(lotteryRule.getParamValues())):lotteryRule.getParamName().concat(lotteryRule.getParamValues());
						}else if(Arrays.asList("极大|极小".split("\\|")).contains(ruleName)) {
							String betRange = lotteryRuleService.getParamValue(lotteryRoom.getLotteryHall().getId(), "极值限制");
							if(StringUtils.isNotBlank(betRange)&&betRange.contains("-")){
								if(betMoney.compareTo(new BigDecimal(betRange.split("-")[0]))<0 
										|| betMoney.compareTo(new BigDecimal(betRange.split("-")[1]))>0) {
									//System.out.println("极值限制");
									continue;
								}
							}
						}else if(Arrays.asList("顺子|豹子|对子|红|绿|蓝".split("\\|")).contains(ruleName)) {
							String betRange = lotteryRuleService.getParamValue(lotteryRoom.getLotteryHall().getId(), "顺豹对限制");
							if(StringUtils.isNotBlank(betRange)&&betRange.contains("-")){
								if(betMoney.compareTo(new BigDecimal(betRange.split("-")[0]))<0 
										|| betMoney.compareTo(new BigDecimal(betRange.split("-")[1]))>0) {
									//System.out.println("顺豹对限制");
									continue;
								}
							}
						}else {
							continue;
//						    Pattern pattern1 = Pattern.compile("^([0-9]|1[0-9]|2[0-7])$");
//						    Matcher matcher1 = pattern1.matcher(ruleName);
//						    if(matcher1.matches()) {
//								String betRange = lotteryRuleService.getLotteryRule(lotteryRoom.getLotteryHall().getId(), "单点限制").getParamValues();
//								if(StringUtils.isNotBlank(betRange)&&betRange.contains("-")){
//									if(betMoney.compareTo(new BigDecimal(betRange.split("-")[0]))<0 
//											|| betMoney.compareTo(new BigDecimal(betRange.split("-")[1]))>0) {
//										System.out.println("单点限制");
//										continue;
//									}
//								}
//						    }
						}
						betContent = ruleName.replace("^", "").replace("$", "").replace("(", "").replace(")", "");
						break;
					case 3:
					case 4:
						ruleName =  lotteryRule.getRuleRegular();
						if(lotteryRule.getRuleType()==1 || lotteryRule.getRuleType()==8) {
							betContent = ruleName.replace("^", "").replace("$", "")
									.replace("(10|[1-9])", RandomUtil.getRangeRandom(1,10)+"").replace("(", "").replace(")", "");
						}else if(lotteryRule.getRuleType()==2) {
							betContent = ruleName.replace("^", "").replace("$", "")
									.replace("(10|[1-9])", RandomUtil.getRangeRandom(1,10)+"")
									.replace("(10|[1-9])", RandomUtil.getRangeRandom(1,10)+"");
						}else if(lotteryRule.getRuleType()==3) {
							betContent = ruleName.replace("^", "").replace("$", "")
									.replace("([1-5])", RandomUtil.getRangeRandom(1,5)+"")
									.replace("(龙|虎)", "龙|虎".split("\\|")[RandomUtil.getRangeRandom(0, 1)]);
						}else if(lotteryRule.getRuleType()==6 || lotteryRule.getRuleType()==4) {
							betContent = ruleName.replace("^", "").replace("$", "")
									.replace("(庄|闲)", "庄|闲".split("\\|")[RandomUtil.getRangeRandom(0, 1)])
									.replace("(大|双)", "大|双".split("\\|")[RandomUtil.getRangeRandom(0, 1)])
									.replace("(小|单)", "小|单".split("\\|")[RandomUtil.getRangeRandom(0, 1)]);
						}else if(lotteryRule.getRuleType()==5 || lotteryRule.getRuleType()==7) {
							continue;
						}

						break;
					case 5:
						ruleName =  lotteryRule.getRuleRegular();
						if(lotteryRule.getRuleType()==1) {
							betContent = ruleName.replace("^", "").replace("$", "")
									.replace("([1-5])", RandomUtil.getRangeRandom(1,10)+"").replace("(", "").replace(")", "");
						}else if(lotteryRule.getRuleType()==2) {
							betContent = ruleName.replace("^", "").replace("$", "")
									.replace("([1-5])", RandomUtil.getRangeRandom(1,10)+"")
									.replace("([0-9])", RandomUtil.getRangeRandom(1,10)+"");
						}else if(lotteryRule.getRuleType()==3) {
							continue;
//							betContent = ruleName.replace("^", "").replace("$", "").replace("(", "").replace(")", "")
//									.replace("(", "").replace(")", "");
						}else if(lotteryRule.getRuleType()==4) {
							betContent = ruleName.replace("^", "").replace("$", "")
									.replace("((1-[2-5])|(2-[3-5])|(3-[4-5])|(4-5))", "1-5")
									.replace("(龙|虎)", "龙|虎".split("\\|")[RandomUtil.getRangeRandom(0, 1)])
									.replace("(和)", "和");
						}else if(lotteryRule.getRuleType()==5 || lotteryRule.getRuleType()==7) {
							continue;
						}
						break;
					}
					
					//总注限制
					allBetMoney = allBetMoney.add(betMoney);
					String betRange = lotteryRuleService.getParamValue(lotteryRoom.getLotteryHall().getId(), "总注限制");
					if(StringUtils.isNotBlank(betRange)&&betRange.contains("-")){
						if(allBetMoney.compareTo(new BigDecimal(betRange.split("-")[0]))<0 
								|| allBetMoney.compareTo(new BigDecimal(betRange.split("-")[1]))>0) {
							//System.out.println("总注限制");
							continue;
						}
					}
					
					//反组合|杀组合验证
					if(StringUtils.isNotBlank(allBetContent)) {
						//1是否反组合
						if(isFanZuhe(allBetContent)){
							continue;
						}
						
						//2是否杀组合
						if(isShaZuhe(allBetContent)){
							continue;
						}
						
						//3是否同向组合
						if(isTongaZuhe(allBetContent)){
							continue;
						}
						
						//4是否反规则
						if(isContrary(allBetContent)){
							continue;
						}
					}
					
					//System.out.println(betContent+"-"+betMoney);
					LotteryOrderItemDTO lotteryOrderItemDTO = new LotteryOrderItemDTO();
					lotteryOrderItemDTO.setRuleId(lotteryRule.getId());
					lotteryOrderItemDTO.setBetContent(betContent);
					lotteryOrderItemDTO.setBetMoney(betMoney.toString());
					orderItemList.add(lotteryOrderItemDTO);
				}else {
					//System.out.println("无匹配规则");
				}
			}
		}
	
		if(orderItemList != null && orderItemList.size() >0) {
//			lotteryRobotPlant.setPoints(lotteryRobotPlant.getPoints()+allBetMoney.setScale(0, BigDecimal.ROUND_DOWN).intValue());//投注送积分
//			lotteryRobotPlant.setPointsLevel(pointsLevelService.findPointsLevel(lotteryRobotPlant.getPoints().intValue()));
//			lotteryRobotPlantService.update(lotteryRobotPlant);
			
			lotteryRoomMessageService.sendOrderMessage(lotteryRoom, lotteryRobotPlant.getNickName(),0,lotteryRobotPlant.getHeadImg(),lotteryRobotPlant.getPointsLevel().getTitle(), lotteryPeriods, orderItemList,null);
			return JsonUtil.toJSONString(orderItemList);
		}
		
		return "无法投注记录生成";
	}
	
	//反组合 小单|大双、大单|小双
	public static boolean isFanZuhe(String historyBtValue) {
		historyBtValue = historyBtValue.replaceAll("\\|", "&");
		if(Pattern.matches(".*(小单).*", historyBtValue) && Pattern.matches(".*(大双).*", historyBtValue)) {
			return true;
		}else if(Pattern.matches(".*(大单).*", historyBtValue) && Pattern.matches(".*(小双).*", historyBtValue)) {
			return true;
		}
	    return false;
	}
	
	//杀组合
	public static boolean isShaZuhe(String historyBtValue) {
		historyBtValue = historyBtValue.replaceAll("\\|", "&");
		if((Pattern.matches(".*(小单).*", historyBtValue) && Pattern.matches(".*(大)([1-9][0-9]*).*", historyBtValue))
				|| (Pattern.matches(".*(小单).*", historyBtValue) && Pattern.matches(".*([^小|^大]双)([1-9][0-9]*).*", historyBtValue))
				|| (Pattern.matches(".*(小单).*", historyBtValue) && historyBtValue.startsWith("双"))
				|| (Pattern.matches(".*(小单).*", historyBtValue)&& Pattern.matches(".*(大双).*", historyBtValue)&&Pattern.matches(".*(大单).*", historyBtValue))) {
			return true;
		}else if((Pattern.matches(".*(大单).*", historyBtValue) && Pattern.matches(".*(小)([1-9][0-9]*).*", historyBtValue))
				|| (Pattern.matches(".*(大单).*", historyBtValue) && Pattern.matches(".*([^小|^大]双)([1-9][0-9]*).*", historyBtValue))
				|| (Pattern.matches(".*(大单).*", historyBtValue) && historyBtValue.startsWith("双"))
				|| (Pattern.matches(".*(大单).*", historyBtValue) && Pattern.matches(".*(小双).*", historyBtValue) && Pattern.matches(".*(小单).*", historyBtValue))) {
			return true;
		}else if((Pattern.matches(".*(小双).*", historyBtValue) && Pattern.matches(".*(大)([1-9][0-9]*).*", historyBtValue))
				|| (Pattern.matches(".*(小双).*", historyBtValue) && Pattern.matches(".*([^小|^大]单)([1-9][0-9]*).*", historyBtValue))
				|| (Pattern.matches(".*(小双).*", historyBtValue) && historyBtValue.startsWith("单"))
				|| (Pattern.matches(".*(小双).*", historyBtValue) && Pattern.matches(".*(大双).*", historyBtValue) && Pattern.matches(".*(大单).*", historyBtValue))) {
			return true;
		}else if((Pattern.matches(".*(大双).*", historyBtValue) && Pattern.matches(".*(小)([1-9][0-9]*).*", historyBtValue))
				|| (Pattern.matches(".*(大双).*", historyBtValue) && Pattern.matches(".*([^小|^大]单)([1-9][0-9]*).*", historyBtValue))
				|| (Pattern.matches(".*(大双).*", historyBtValue) && historyBtValue.startsWith("单"))
				|| (Pattern.matches(".*(大双).*", historyBtValue) && Pattern.matches(".*(小双).*", historyBtValue) && Pattern.matches(".*(小单).*", historyBtValue))) {
			return true;
		}
	    return false;
	}
	
	//同向组合
	public static boolean isTongaZuhe(String historyBtValue) {
		historyBtValue = historyBtValue.replaceAll("\\|", "&");
		if(Pattern.matches(".*(大单).*", historyBtValue) && Pattern.matches(".*(小单).*", historyBtValue)) {
			return true;
		}else if(Pattern.matches(".*(大双).*", historyBtValue) && Pattern.matches(".*(小双).*", historyBtValue)) {
			return true;
		}else if(Pattern.matches(".*(大双).*", historyBtValue) && Pattern.matches(".*(大单).*", historyBtValue)) {
			return true;
		}else if(Pattern.matches(".*(小双).*", historyBtValue) && Pattern.matches(".*(小单).*", historyBtValue)) {
			return true;
		}
	    return false;
	}
	
	//反规则
	public static boolean isContrary(String historyBtValue) {
		historyBtValue = historyBtValue.replaceAll("\\|", "&");
		if(Pattern.matches(".*(小)([1-9][0-9]*).*", historyBtValue) && Pattern.matches(".*(大)([1-9][0-9]*).*", historyBtValue)) {
			return true;
		}else if(Pattern.matches(".*(单)([1-9][0-9]*).*", historyBtValue) && Pattern.matches(".*(双)([1-9][0-9]*).*", historyBtValue)) {
			return true;
		}else if(Pattern.matches(".*(极大)([1-9][0-9]*).*", historyBtValue) && Pattern.matches(".*(极小)([1-9][0-9]*).*", historyBtValue)) {
			return true;
		}
	    return false;
	}
	
	public static void main(String[] args) {
		String historyBtValue = "极大100|极小100";
		System.out.println(isContrary(historyBtValue));
	}
}
