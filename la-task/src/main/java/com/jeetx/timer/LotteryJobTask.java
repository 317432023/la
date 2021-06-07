package com.jeetx.timer;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jeetx.common.pay.fbd.FbdToolKit;
import com.jeetx.common.pay.yfb.YfbToolKit;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jeetx.bean.lottery.LotteryHall;
import com.jeetx.bean.lottery.LotteryOrder;
import com.jeetx.bean.lottery.LotteryOrderItem;
import com.jeetx.bean.lottery.LotteryPeriods;
import com.jeetx.bean.lottery.LotteryRobotPlant;
import com.jeetx.bean.lottery.LotteryRobotPlantConfig;
import com.jeetx.bean.lottery.LotteryRoom;
import com.jeetx.bean.lottery.LotteryRule;
import com.jeetx.bean.lottery.LotteryWaterConfig;
import com.jeetx.bean.member.Recharge;
import com.jeetx.common.dpc.ApiUtil;
import com.jeetx.common.dpc.OrderJson;
import com.jeetx.common.pay.alipays.AlipaysToolKit;
import com.jeetx.common.pay.ifuniu.IfuniuToolKit;
import com.jeetx.common.pay.jdzf.JdzfToolKit;
import com.jeetx.common.pay.lyf.LyfToolKit;
import com.jeetx.common.pay.msf.MsfToolKit;
import com.jeetx.common.pay.xPay.XpayToolKit;
import com.jeetx.common.pay.xl.XlToolKit;
import com.jeetx.common.pay.ytb.YtbToolKit;
import com.jeetx.common.redis.JedisClient;
import com.jeetx.service.lottery.LotteryHallService;
import com.jeetx.service.lottery.LotteryOrderService;
import com.jeetx.service.lottery.LotteryPeriodsService;
import com.jeetx.service.lottery.LotteryRobotPlantConfigService;
import com.jeetx.service.lottery.LotteryRobotPlantService;
import com.jeetx.service.lottery.LotteryRoomMessageService;
import com.jeetx.service.lottery.LotteryRoomService;
import com.jeetx.service.lottery.LotteryRuleService;
import com.jeetx.service.lottery.LotteryTypeService;
import com.jeetx.service.lottery.LotteryWaterConfigService;
import com.jeetx.service.lottery.LotteryWaterRecordService;
import com.jeetx.service.member.RechargeService;
import com.jeetx.service.system.SystemConfigService;
import com.jeetx.timer.lotteryTask.FetchResult;
import com.jeetx.timer.lotteryTask.FetchTask;
import com.jeetx.timer.lotteryTask.LotteryDTO;
import com.jeetx.util.DateTimeTool;
import com.jeetx.util.HttpUtil;
import com.jeetx.util.HttpsUtil;
import com.jeetx.util.JsonUtil;
import com.jeetx.util.LogUtil;
import com.jeetx.util.MD5Util;
import com.jeetx.util.RandomUtil;

@Component
public class LotteryJobTask {
	public static Logger logger = Logger.getLogger(LotteryJobTask.class);
	
    @Autowired JedisClient jedisClient;
	@Autowired LotteryHallService lotteryHallService;
	@Autowired LotteryPeriodsService lotteryPeriodsService;
	@Autowired SystemConfigService systemConfigService;
	@Autowired LotteryRoomService lotteryRoomService;
	@Autowired LotteryOrderService lotteryOrderService;
	@Autowired LotteryRobotPlantConfigService lotteryRobotPlantConfigService;
	@Autowired LotteryWaterConfigService lotteryWaterConfigService;
	@Autowired LotteryWaterRecordService lotteryWaterRecordService;
	@Autowired LotteryTypeService lotteryTypeService;
	@Autowired LotteryRobotPlantService lotteryRobotPlantService;
	@Autowired LotteryRuleService lotteryRuleService;
	@Autowired RechargeService rechargeService;
	@Autowired LotteryRoomMessageService lotteryRoomMessageService;
	
	@Value("${countDownSecond}")
	private Integer countDownSecond;
	
	@Value("${openLotteryMessage}")
	private String openLotteryMessage;
	
	@Value("${intoLotteryMessage}")
	private String intoLotteryMessage;	
	
	public LotteryDTO executorInvokeAny(String providerList,String currentPeriods,Integer lotteryType) {
		String sourceUrl = null;
		LotteryDTO lotteryDTO = null;
		List<Callable<LotteryDTO>> tasks = new ArrayList<Callable<LotteryDTO>>();
		
		ExecutorService executorService = Executors.newCachedThreadPool();
		for (int i = 0; i < providerList.split("\\|").length; i++) {
			String provider = providerList.split("\\|")[i];

			if("PC28AM".equalsIgnoreCase(provider)) {
				sourceUrl = systemConfigService.getValueByName("lottery_pc28am_path");
			}else if("彩讯网".equalsIgnoreCase(provider)) {
				sourceUrl = systemConfigService.getValueByName("lottery_cxw_path");
			}else if("28T".equalsIgnoreCase(provider)) {
				sourceUrl = systemConfigService.getValueByName("lottery_28t_path");
			}else if("ASCXW".equalsIgnoreCase(provider)) {
				sourceUrl = systemConfigService.getValueByName("lottery_ascxw_path");
			}

			tasks.add(new FetchTask(provider,lotteryType,currentPeriods,sourceUrl));
		}
		
		try {
			lotteryDTO = executorService.invokeAny(tasks);
			executorService.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		return lotteryDTO;
	}
	
	public void getOpenResult(Integer lotteryType,String lotteryTitle,String openProvider) {
		try{
			String currentPeriods = lotteryPeriodsService.getCurrentPeriods(lotteryType,lotteryTitle,openProvider);//当前期数
			if(StringUtils.isNotBlank(currentPeriods) ){
				//封盘
				LotteryPeriods currentLotteryPeriods = lotteryPeriodsService.findLotteryPeriodsByPeriods(lotteryType, currentPeriods);
				if(currentLotteryPeriods != null && currentLotteryPeriods.getStatus()==1) {
					currentLotteryPeriods.setStatus(2);
					lotteryPeriodsService.update(currentLotteryPeriods);
				}
				
				//北京赛车|时时彩
				if(lotteryType==4||lotteryType==5) {
					String nextPeriods = String.valueOf(Long.valueOf(currentLotteryPeriods.getLotteryPeriods()) + 1);
					LotteryPeriods nextLotteryPeriods = lotteryPeriodsService.findLotteryPeriodsByPeriods(lotteryType, nextPeriods);
					if(nextLotteryPeriods==null) {
						nextLotteryPeriods = lotteryPeriodsService.createLotteryPeriods(lotteryType,nextPeriods,DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", currentLotteryPeriods.getLotteryOpenTime()));
						if(nextLotteryPeriods!=null) {
							doLotteryPeriodsOpen(nextLotteryPeriods,false,true);
						}
					}
				}
				
				LotteryDTO lotteryDTO = executorInvokeAny(openProvider,currentPeriods,lotteryType);
				if(lotteryDTO!=null && lotteryDTO.isBool()){
					currentLotteryPeriods = lotteryPeriodsService.findLotteryPeriodsByPeriods(lotteryType, lotteryDTO.getPeriod());
					if(currentLotteryPeriods != null && currentLotteryPeriods.getStatus() != 3){
						LogUtil.info(lotteryTitle+", "+lotteryDTO.getDataSource()+", 第" + lotteryDTO.getPeriod() + "期, 开奖时间：" +lotteryDTO.openTime+ ", 开奖号码：" + lotteryDTO.getOpenContent());
						lotteryPeriodsService.openLotteryPeriods(lotteryType, lotteryDTO,true);
						
						// 开奖处理
						final LotteryPeriods lotteryPeriods = lotteryPeriodsService.findLotteryPeriodsByPeriods(lotteryType, lotteryDTO.getPeriod());
						if(lotteryPeriods !=null) {
							//doLotteryPeriodsOpen(lotteryPeriods,true,false);
							doLotteryPeriodsOpen(lotteryPeriods,true,lotteryType==4||lotteryType==5?false:true);
						}
					}else {
						logger.info(lotteryTitle+", "+lotteryDTO.getDataSource()+", 第" + lotteryDTO.getPeriod() + "期, 已开奖");
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			LogUtil.info("获取"+lotteryTitle+"开奖结果异常："+e);
		}  
	}
    
	/*** 获取幸运28开奖结果*/
	@Scheduled(cron="30 0/5 9-23 * * ?") 
	public void getPC28OpenResult() {
    	Integer lotteryType = 1;
    	String lotteryTitle = "北京28";
    	String bjOpenProvider = systemConfigService.getValueByName("lottery_bj_open_provider");
    	this.getOpenResult(lotteryType,lotteryTitle,bjOpenProvider);
	}
	
	/*** 获取加拿大28开奖结果*/
	@Scheduled(cron="15,45 * * * * ?") 
	public void getJND28OpenResult() {
    	Integer lotteryType = 2;
    	String lotteryTitle = "加拿大28";
    	String jndOpenProvider = systemConfigService.getValueByName("lottery_jnd_open_provider");
    	this.getOpenResult(lotteryType,lotteryTitle,jndOpenProvider);
	}
	
	/*** 获取幸运飞艇开奖结果
	 * 每日发行180期，13:00~04:00每5分钟开奖*/ 
	@Scheduled(cron="30 * 13-23,0-4 * * ?") 
	public void getXYFTOpenResult() {
    	Integer lotteryType = 3;
    	String lotteryTitle = "幸运飞艇";
    	String xyftOpenProvider = systemConfigService.getValueByName("lottery_xyft_open_provider");
    	this.getOpenResult(lotteryType,lotteryTitle,xyftOpenProvider);
	}
	
	/*** 
	 * 获取北京赛车开奖结果
	 * 每日发行44期，09:10~23:50每20分钟开奖*/
	@Scheduled(cron="30 0/10 9-23 * * ?") 
	public void getPK10OpenResult() {
    	Integer lotteryType = 4;
    	String lotteryTitle = "北京赛车";
    	String pk10OpenProvider = systemConfigService.getValueByName("lottery_pk10_open_provider");
    	this.getOpenResult(lotteryType,lotteryTitle,pk10OpenProvider);
	}
	
	/*** 获取重庆时时彩开奖结果
	 * 每日发行59期，07:10~3:10每20分钟开奖*/
	@Scheduled(cron="35 0/10 7-23,0-3 * * ?") 
	public void getCQSSCOpenResult() {
    	Integer lotteryType = 5;
    	String lotteryTitle = "重庆时时彩";
    	String sscOpenProvider = systemConfigService.getValueByName("lottery_ssc_open_provider");
    	this.getOpenResult(lotteryType,lotteryTitle,sscOpenProvider);
	}
	
	/*** 获取新西兰开奖结果
	 * 每20分钟开奖，4:00-6:00停盘*/
	@Scheduled(cron="15 0/10 6-23,0-3 * * ?") 
	public void getXXLCOpenResult() {
    	Integer lotteryType = 6;
    	String lotteryTitle = "新西兰";
    	String sscOpenProvider = "处理中心";
    	this.getOpenResult(lotteryType,lotteryTitle,sscOpenProvider);
	}
	
	/*** 结算每日回水*/
	@Scheduled(cron="0 5 0 * * ?") 
	public void settlementBackwater() {
    	LogUtil.info("======================开始结算每日回水======================");
		List<LotteryHall> lotteryHallList = lotteryHallService.getLotteryHallList();
		
		String totalDate = DateTimeTool.dateFormat("yyyy-MM-dd", DateTimeTool.getDaysByDate2Days(1,new Date()));
		for (LotteryHall lotteryHall : lotteryHallList) {
			try{
				List<LotteryWaterConfig> lotteryWaterConfigList = lotteryWaterConfigService.getLotteryWaterConfigList(lotteryHall.getId());
				if(lotteryWaterConfigList != null && lotteryWaterConfigList.size()>0) {
					lotteryWaterRecordService.createLotteryWaterRecord(totalDate, lotteryHall);
				}
			}catch (Exception e) {
				e.printStackTrace();
				LogUtil.info("["+lotteryHall.getLotteryType().getLotteryName()+"-"+lotteryHall.getTitle()+"]结算每日回水异常："+e);
			}
		}
		LogUtil.info("======================结算每日回水结束======================");
	}
    
	/***自动清除历史业务数据*/
	@Scheduled(cron="0 30 0 * * ?") 
	public void autoClearData(){
		LogUtil.info("======================开始自动清除历史业务数据======================");
		try{
			lotteryTypeService.autoClearData();
		}catch (Exception e) {
			e.printStackTrace();
			logger.info("自动清除历史业务数据异常："+e);
		}
		LogUtil.info("======================自动清除历史业务数据结束======================");
	}
	
	/**
	 * 开奖处理
	 * @param lotteryPeriods
	 * @throws Exception
	 */
	public void doLotteryPeriodsOpen(final LotteryPeriods lotteryPeriods,final Boolean isPeriodsOpen,final Boolean isPeriodsTask) throws Exception{
		String lotteryName = null;
		switch (lotteryPeriods.getLotteryType().getId()) {
		case 1:
			lotteryName = "幸运28";
			break; 
		case 2:
			lotteryName = "加拿大28";
			break;
		case 3:
			lotteryName = "幸运飞艇"; 
			break;
		case 4:
			lotteryName = "北京赛车"; 
			break;
		case 5:
			lotteryName = "重庆时时彩"; 
			break;
		case 6:
			lotteryName = "新西兰28"; 
			break;
		}
		
		final String methodName = lotteryName;
		//LogUtil.info(methodName+"["+lotteryPeriods.getLotteryPeriods()+"期]开奖处理:"+DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", new Date()));
		List<LotteryRoom> lotteryRoomList = lotteryRoomService.getLotteryRoomList(lotteryPeriods.getLotteryType().getId());
		for (final LotteryRoom lotteryRoom : lotteryRoomList) {
			if(lotteryRoom !=null && lotteryRoom.getStatus() == 1 && lotteryRoom.getStation().getStatus() == 1){//过滤开启的房间
				new Thread(){
					public void run(){
						/***1、开奖结算，发送开奖信息:【[开奖期数]】开奖结果[时间]：[开奖内容]。*/
						if(isPeriodsOpen) {
							try {
								List<LotteryOrder> orderList = lotteryOrderService.findLotteryOrderList(lotteryPeriods.getLotteryPeriods(), lotteryRoom.getId());
								for (LotteryOrder lotteryOrder : orderList) {
									try {
										lotteryOrderService.settlementOrder(lotteryOrder, lotteryRoom, lotteryPeriods);
									}catch (Exception e) { 
										e.printStackTrace();
										LogUtil.info(methodName+"["+lotteryPeriods.getLotteryPeriods()+"期]-["+lotteryRoom.getLotteryType().getLotteryName()+"-订单号"+lotteryOrder.getOrderCode()+"]开奖处理异常："+e);
									}
								}
								
								/**发送开奖信息*/
								lotteryRoomMessageService.sendBroadcastMessage(lotteryRoom, lotteryPeriods, openLotteryMessage);
							} catch (Exception e) {
								e.printStackTrace();
								LogUtil.info(methodName+"["+lotteryPeriods.getLotteryPeriods()+"期]-["+lotteryRoom.getLotteryType().getLotteryName()+"-"+lotteryRoom.getLotteryHall().getTitle()+"-"+lotteryRoom.getTitle()+"]开奖处理异常："+e);
							}
						}else {
							//发送开始投注信息
							final LotteryPeriods currentPeriodsLottery = lotteryPeriodsService.findCurrentLotteryPeriods(lotteryPeriods.getLotteryType().getId());
							if(currentPeriodsLottery !=null && currentPeriodsLottery.getStatus() == 1){
								String betRange = lotteryRuleService.getParamValue(lotteryRoom.getLotteryHall().getId(), "总注限制");
								if(StringUtils.isNotBlank(betRange)&&betRange.contains("-")){
									intoLotteryMessage = intoLotteryMessage.replace("[最大值]", betRange.split("-")[1]).replace("[最小值]", betRange.split("-")[0]);
								}
								lotteryRoomMessageService.sendBroadcastMessage(lotteryRoom, currentPeriodsLottery, intoLotteryMessage);
							}
						}

						if(isPeriodsTask) {
							/** 判断当前正在投注的期数*/
							final LotteryPeriods currentPeriodsLottery = lotteryPeriodsService.findCurrentLotteryPeriods(lotteryPeriods.getLotteryType().getId());
							if(currentPeriodsLottery !=null && currentPeriodsLottery.getStatus() == 1){
								//获取游戏规则列表
								List<LotteryRule> lotteryRuleList = null;
								if(lotteryRoom.getLotteryType().getId()== 1 || lotteryRoom.getLotteryType().getId()== 2 || lotteryRoom.getLotteryType().getId()== 5) {
									lotteryRuleList = lotteryRuleService.getListByHallIdNotType3(lotteryRoom.getLotteryHall().getId());
								}else {
									lotteryRuleList = lotteryRuleService.getListByHallId(lotteryRoom.getLotteryHall().getId());
								}
								final List<LotteryRule> orderPlanList = lotteryRuleList;
								
								/**2、启动自动托线程*/
								final LotteryRobotPlantConfig lotteryRobotPlantConfig = lotteryRobotPlantConfigService.findByLotteryRoomId(lotteryRoom.getId());
	    						if(lotteryRobotPlantConfig!=null){
	    							new Thread(){
	    								public void run(){
	    									try {
	    		    							int countDownSecond = DateTimeTool.calLastedTime(currentPeriodsLottery.getLotteryOpenTime());
	    		    							int k = countDownSecond/60;//需要循环多少次
	    		       							
	    		    							Long robotPlantCount = lotteryRobotPlantService.getLotteryRobotPlantCount();
	    		    							//System.out.println("robotPlantCount"+robotPlantCount);
	    		       							Set<Integer> allSet = new HashSet<Integer>();
	    		    							for (int i = 0; i < k; i++) {
	    		    								final int begin = Math.abs(60*(i)-5);
	    											final int end = 60*(i+1);
	    											
	    											Set<Integer> set = new HashSet<Integer>();
	    											int n = lotteryRobotPlantConfig.getRandomCount();
	    											for (int j = 0; j < n; j++) {
	    												int robotNum = RandomUtil.getRangeRandom(1,robotPlantCount.intValue());
	    												if(!allSet.contains(robotNum)) {
	    													set.add(robotNum);// 随机获取假人信息
	    													allSet.add(robotNum);
	    												}
	    											}
	    														
	    											//LogUtil.info(methodName+"["+currentPeriodsLottery.getLotteryPeriods()+"期]-["+lotteryRoom.getLotteryHall().getTitle()+"-"+lotteryRoom.getTitle()+"]自动投注数量："+i+"-"+set.size());
	    											for (final Integer robotNum : set) {
	    												new Thread(){
	    													public void run(){
	    														try {
	    															Integer randomCount = RandomUtil.getRangeRandom(begin, end);//开盘20-100秒内启用多线程随机发送
	    															Thread.sleep(randomCount*1000);//线程休眠
	    															
	    															LotteryRobotPlant lotteryRobotPlant = lotteryRobotPlantService.find(robotNum);
	    															if(lotteryRobotPlant != null){
	    																lotteryRobotPlantConfigService.submitPlantOrder(lotteryRoom,currentPeriodsLottery,lotteryRobotPlantConfig,lotteryRobotPlant,orderPlanList);
	    				    					    					//LogUtil.info(methodName+"["+currentPeriodsLottery.getLotteryPeriods()+"期]-["+lotteryRoom.getLotteryHall().getTitle()+"-"+lotteryRoom.getTitle()+"-"+lotteryRobotPlant.getNickName()+"]自动投注："+betContent);
	    															}
	    														} catch (Exception e) {
	    															e.printStackTrace();
	    								    						LogUtil.info(methodName+"["+currentPeriodsLottery.getLotteryPeriods()+"期]-["+lotteryRoom.getLotteryHall().getTitle()+"-"+lotteryRoom.getTitle()+"]假人自动投注异常1："+e);
	    														}
	    													};
	    												}.start();
	    											}
	    										}
	    									} catch (Exception e) {
	    										e.printStackTrace();
	    										LogUtil.info(methodName+"["+currentPeriodsLottery.getLotteryPeriods()+"期]-["+lotteryRoom.getLotteryType().getLotteryName()+"-"+lotteryRoom.getLotteryHall().getTitle()+"-"+lotteryRoom.getTitle()+"]假人自动投注异常2："+e);
	    									}
	    								};
	    							}.start();
	    						}

								/**3、发送封盘倒计时提示信息：【[开奖期数]】距离封盘时间还有30秒，请抓紧时间下注。*/
								new Thread(){
									public void run(){
										try {
											int second = DateTimeTool.calLastedTime(currentPeriodsLottery.getLotteryOpenTime());
											int sleepSecond = second - countDownSecond;//线程休眠时间
											if(sleepSecond > 0 ) {
												Thread.sleep(sleepSecond*1000);
												lotteryPeriodsService.sendMemInfoBeforeCloseMessage(lotteryRoom, currentPeriodsLottery);
											}
										} catch (Exception e) {
											e.printStackTrace();
											LogUtil.info(methodName+"["+currentPeriodsLottery.getLotteryPeriods()+"期]-["+lotteryRoom.getLotteryType().getLotteryName()+"-"+lotteryRoom.getLotteryHall().getTitle()+"-"+lotteryRoom.getTitle()+"]发送封盘倒计时提示信息异常："+e);
										}
									};
								}.start();
								
								/**4、发送封盘线提示信息：【[开奖期数]】已封盘，下注结果以系统开奖为准，如有异议，请及时联系客服。*/
								new Thread(){
									public void run(){
										try {
											int sleepSecond = DateTimeTool.calLastedTime(currentPeriodsLottery.getLotteryOpenTime());
											if(sleepSecond > 0 ) {
												Thread.sleep(sleepSecond*1000);
												try {
													LotteryPeriods lotteryPeriods = lotteryPeriodsService.find(currentPeriodsLottery.getId());
													if(lotteryPeriods != null && lotteryPeriods.getStatus() == 1) {
														lotteryPeriods.setStatus(2);
														lotteryPeriodsService.update(lotteryPeriods);
													}
												}catch (Exception e) {
													System.out.println("**************Exception*****************");
													//e.printStackTrace();
												}
												lotteryPeriodsService.sendMemCloseInfoMessage(lotteryRoom, currentPeriodsLottery);
											}
										} catch (Exception e) {
											e.printStackTrace();
											LogUtil.info(methodName+"["+currentPeriodsLottery.getLotteryPeriods()+"期]-["+lotteryRoom.getLotteryType().getLotteryName()+"-"+lotteryRoom.getLotteryHall().getTitle()+"-"+lotteryRoom.getTitle()+"]发送封盘线提示信息异常："+e);
										}
										
										/**封盘后88彩新西兰彩种,提交订单信息至处理中心*/
										try {
											boolean isSubmitOrder = false;
											String stationIds = systemConfigService.getValueByName("xxl_subimt_station");
											if(StringUtils.isNotBlank(stationIds)) {
												String[] idArrs = stationIds.split(",");
												for (String id : idArrs) {
													if(lotteryRoom.getStation().getId() == Integer.valueOf(id)) {
														isSubmitOrder = true;
														break;
													}
												}
											}
											
											if(isSubmitOrder) {
												Integer lotteryType = 8;
												Map<String, OrderJson> orderMap = new HashMap<String, OrderJson>();
												
												if(currentPeriodsLottery.getLotteryType().getId() == 6) {
													List<LotteryOrder> lotteryOrderList =  lotteryOrderService.findLotteryOrderList(currentPeriodsLottery.getLotteryPeriods(), lotteryRoom.getId());
													if(lotteryOrderList!=null && lotteryOrderList.size()>0) {
														for (LotteryOrder lotteryOrder : lotteryOrderList) {
															if(lotteryOrder!=null && lotteryOrder.getUser().getUserType() == 1) {
																OrderJson orderJson = null;
																StringBuffer betContent = new StringBuffer("");
																for (LotteryOrderItem lotteryOrderItem : lotteryOrder.getLotteryOrderItems()) {
																	switch (lotteryOrderItem.getLotteryRule().getRuleType()) {
																	case 1:
																	case 2:
																		if(StringUtils.isNotBlank(betContent.toString())) betContent.append("|");
																		betContent.append(lotteryOrderItem.getRuleName()).append(lotteryOrderItem.getBetMoney().setScale(0, BigDecimal.ROUND_HALF_UP));
																		break;
																	case 3:
																		if(StringUtils.isNotBlank(betContent.toString())) betContent.append("|");
																		betContent.append(lotteryOrderItem.getRuleName()).append("点").append(lotteryOrderItem.getBetMoney().setScale(0, BigDecimal.ROUND_HALF_UP));
																		break;
																	}
																}
																
																if(orderMap.containsKey(lotteryOrder.getUser().getNickName())) {
																	orderJson = orderMap.get(lotteryOrder.getUser().getNickName());
																	orderJson.setBetMoney((lotteryOrder.getBetMoney().add(new BigDecimal(orderJson.getBetMoney()))).toString());
																	orderJson.setBetContent(orderJson.getBetContent().concat("|").concat(betContent.toString()));
																}else {
																	orderJson = new OrderJson();
																	orderJson.setToyType(1);
																	orderJson.setRoomKey("app_room_"+lotteryRoom.getId());
																	orderJson.setLotteryType(lotteryType);//彩票类型
																	orderJson.setLotteryPeriod(currentPeriodsLottery.getLotteryPeriods());//当前期数
																	orderJson.setOrderCode(lotteryOrder.getOrderCode());
																	orderJson.setNickname(lotteryOrder.getUser().getNickName());
																	orderJson.setBetMoney(lotteryOrder.getBetMoney().toString());
																	orderJson.setBetContent(betContent.toString());
																}
																orderMap.put(lotteryOrder.getUser().getNickName(), orderJson);
															}
														}

														List<OrderJson> ordersJsonList = new ArrayList<OrderJson>();
														for (Map.Entry<String, OrderJson> entry : orderMap.entrySet()) {
															ordersJsonList.add(entry.getValue());
														}
				
														if(ordersJsonList != null && ordersJsonList.size() >0) {
															String ordersJson = JsonUtil.toJSONString(ordersJsonList);
															//System.out.println(ordersJson);
															
															String appId = "app_station_".concat(lotteryRoom.getStation().getId().toString()); 
															String appSecret = ApiUtil.SECRETKEY; 
															Integer platform = 3;
															String timestamp = JSONObject.fromObject(ApiUtil.serverTime()).getJSONObject("result").getString("serverTime");
															
															Map<String,Object> parms = new HashMap<String,Object>(); 
															parms.put("appId", appId);
															parms.put("platform", platform);
															parms.put("timestamp", timestamp);
															String sign = ApiUtil.generateSign(parms,appSecret);
															
															JSONObject jsonParms = new JSONObject (); 
															jsonParms.put("appId", appId);
															jsonParms.put("platform", platform);
															jsonParms.put("timestamp", timestamp);
															jsonParms.put("objectJson", JSONArray.fromObject(ordersJson));
															jsonParms.put("sign", sign);
															
															//System.out.println(jsonParms);
															JSONObject response = ApiUtil.doJsonPost(ApiUtil.BASE_URL+"/api/lottery/submitOrder", jsonParms);
															if(response.getInt("code")!=0)
																logger.info(lotteryRoom.getStation().getStationName()+",提交订单信息至处理中心异常:"+response.toString());
														}
													}
												}
											}
										} catch (Exception e) {
											e.printStackTrace();
											logger.info(methodName+"["+currentPeriodsLottery.getLotteryPeriods()+"期]-" +
													"["+lotteryRoom.getStation().getStationName()+"]提交订单信息至处理中心异常："+e);
										}
									};
								}.start();
							}
						}
					}
				}.start();
			}
		}
	}
	
	/***网银支付订单状态查询*/
	@Scheduled(cron="0 * * * * ?") 
	public void queryPayOrder() {
    	//LogUtil.info("======================网银支付订单状态查询======================");
    	List<Recharge> list = rechargeService.getRechargeListByPayType2status();
    	for (Recharge recharge : list) {
    		try {
    			BigDecimal rechargeAmount = null;
    			boolean bool = false;
    			if(recharge != null && (recharge.getStatus() == 1 || recharge.getStatus() ==4)) {
    				String orderNo = recharge.getTradeCode();
    				switch (recharge.getPayType()) {
					case 9://码上付
						String appid = MsfToolKit.APP_ID;
						String appsecect = MsfToolKit.APP_SECECT;
				        String sign = MsfToolKit.getMD5(appid+appsecect).toLowerCase();

				        String content = new HttpUtil().getMethod("http://merchant.huitongworld.cn/query/FindOrder?orderNo=" + orderNo+ "&appid=" + appid + "&sign=" + sign, "UTF-8");
				        logger.info("码上付["+orderNo+"]query result:"+content);
				        if(StringUtils.isNotBlank(content)) {
				            JSONObject resultJsonObj = JSONObject.fromObject(content);
				    		String status = resultJsonObj.getString("status");
				    		if (status.equals("order_payed")) {
				    			bool = true;
				    		}
				        }
						break;
					case 10://龙易付
						Map<String, String> metaSignMap = new TreeMap<String, String>();
						metaSignMap.put("appid", LyfToolKit.APPID);
						metaSignMap.put("order_no",orderNo);
						metaSignMap.put("start_time", DateTimeTool.dateFormat("yyyy-MM-dd", new Date()));
						metaSignMap.put("end_time", DateTimeTool.dateFormat("yyyy-MM-dd", new Date()));
						metaSignMap.put("sign", LyfToolKit.generateSign(metaSignMap));

						String resultJsonStr = new HttpUtil().getMethod("http://api.525dt.xyz/api/order/check_order".concat("?").concat(new HttpUtil().mapToString(metaSignMap)), "utf-8");
						logger.info("龙易付["+orderNo+"]query result:"+resultJsonStr);
						JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
						String status = resultJsonObj.getString("status");
						if (status.equals("1")) {
							JSONObject dataJson = resultJsonObj.getJSONObject("data");
							if (orderNo.equals(dataJson.getString("order_no")) && "1".equalsIgnoreCase(dataJson.getString("pay_status"))) {
								bool = true;
							}
						}
						break;
					case 11://易通宝
						String sdorderno = orderNo;
						String reqtime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
						String signStr = "customerid="+YtbToolKit.APP_ID+"&sdorderno="+sdorderno+"&reqtime="+reqtime+"&"+YtbToolKit.APP_SECECT;
						
						Map<String, String> parms = new TreeMap<String, String>();
						parms.put("customerid", YtbToolKit.APP_ID);
						parms.put("sdorderno", sdorderno);
						parms.put("reqtime", reqtime);
						parms.put("sign", MD5Util.MD5Encode(signStr, "utf-8"));
						
						resultJsonStr = new HttpUtil().postMethod("http://pay.51haohuo.cn/apiorderquery", parms,"UTF-8");
						logger.info("易通宝["+orderNo+"]query result:"+resultJsonStr);

						resultJsonObj = JSONObject.fromObject(resultJsonStr);
						status = resultJsonObj.getString("status");
						if (status.equals("1")) {
							rechargeAmount = new BigDecimal(resultJsonObj.getString("total_fee"));
							bool = true;
						}
						break;
					case 12://喜力
						String version = "100";//接口版本[参与签名]
						String bizCode = "H0001";//业务编号[参与签名]
						if(orderNo.contains("S")) {
							bizCode = "S0001";//业务编号[参与签名]
						}
		
						parms = new TreeMap<String, String>();
						parms.put("version", version);
						parms.put("appId", XlToolKit.APP_ID);
						parms.put("appSecret", XlToolKit.APP_SECRET);
						parms.put("merId", XlToolKit.MERCHANT_ID);
						parms.put("orderNo", orderNo);
						parms.put("bizCode", bizCode);
						parms.put("sign", XlToolKit.generateSign(parms));

						resultJsonStr = new HttpUtil().postMethod("http://api.cfpay188.com:8083/gateway/payapi/1.0/doQuery", parms,"UTF-8");
						logger.info("喜力["+orderNo+"]query result:"+resultJsonStr);
						
						resultJsonObj = JSONObject.fromObject(resultJsonStr);
						status = resultJsonObj.getString("status");
						String retCode = resultJsonObj.getString("retCode");
						if("01".contentEquals(status) && "200".contentEquals(retCode)) {
							rechargeAmount = new BigDecimal(resultJsonObj.getString("orderPrice"));
							bool = true;
						}
						break;
					case 13://阿里付(腾云付)
						String pay_orderid = orderNo; 

						metaSignMap = new TreeMap<String, String>();
						metaSignMap.put("pay_memberid", AlipaysToolKit.PAY_MEMBERID);
						metaSignMap.put("pay_orderid", pay_orderid);
						metaSignMap.put("pay_md5sign", AlipaysToolKit.generateSign(metaSignMap));
						
						resultJsonStr = new HttpUtil().postMethod("https://www.668915.com/Pay_Trade_query.html", metaSignMap, "UTF-8");
						logger.info("阿里付["+orderNo+"]query result:"+resultJsonStr);
						if(resultJsonStr.contains("orderid")) {
							resultJsonObj = JSONObject.fromObject(resultJsonStr);
							String trade_state = resultJsonObj.getString("trade_state");
							if("SUCCESS".equalsIgnoreCase(trade_state)) {//NOTPAY-未支付 SUCCESS已支付
								bool = true;
							}
						}
						break;
					case 14://丽狮
						String nonce_str = RandomUtil.generateString(32);
						String request_no = orderNo; 
						String request_time = String.valueOf(new Date().getTime()/1000); 
						
						metaSignMap = new TreeMap<String, String>();
						metaSignMap.put("merchant_no", IfuniuToolKit.PAY_MEMBERID);
						metaSignMap.put("nonce_str", nonce_str);
						metaSignMap.put("request_no", request_no);
						metaSignMap.put("request_time", request_time);
						
						metaSignMap.put("sign", IfuniuToolKit.generateSign(metaSignMap));
						resultJsonStr = HttpsUtil.postMethod("https://b.ifuniu.store/v1/orderquery", metaSignMap, "UTF-8");
						logger.info("丽狮["+orderNo+"]query result:"+resultJsonStr);
						
						if(StringUtils.isNotBlank(resultJsonStr)) {
							resultJsonObj = JSONObject.fromObject(resultJsonStr);
							String isSuccess = resultJsonObj.getString("success");
							if("true".equalsIgnoreCase(isSuccess)) {
								JSONObject dataObject = resultJsonObj.getJSONObject("data");
								if(dataObject!=null) {
									if("3".equalsIgnoreCase(dataObject.getString("status"))) {
										bool = true;
									}
								}
							}
						}
						break;
					case 15://金樽支付
						parms = new TreeMap<String, String>();
						parms.put("businessId", JdzfToolKit.MERCHANT_ID);
						parms.put("outTradeNo", orderNo);
						parms.put("random", new Date().getTime()+"");
						
						Map<String, String> signMap = parms;
						signMap.put("secret", JdzfToolKit.MERCHANT_SECRET);
						
						parms.put("sign", JdzfToolKit.generateSign(signMap));
						resultJsonStr = new HttpUtil().postMethod("http://api.jdzf.net/pay/queryOrder", parms,"UTF-8");
						logger.info("金樽支付["+orderNo+"]query result:"+resultJsonStr);
						
						if(StringUtils.isNotBlank(resultJsonStr)) {
							resultJsonObj = JSONObject.fromObject(resultJsonStr);
							String successed = resultJsonObj.getString("successed");
							if("true".equalsIgnoreCase(successed)) {
								JSONObject dataJsonObj = JSONObject.fromObject(resultJsonObj.getString("returnValue"));
								if(dataJsonObj!=null) {
									if("2".equalsIgnoreCase(dataJsonObj.getString("orderState"))) {
										bool = true;
									}
								}
							}
						}
						break;
					case 16://星支付
						parms = new TreeMap<String, String>();
						parms.put("shid", XpayToolKit.SHID);//商户账号
						parms.put("orderid", orderNo);//充值订单号
						parms.put("sign", XpayToolKit.generateSign(parms));
						parms.put("pay", "1");
						
						resultJsonStr = HttpsUtil.postMethod("http://new.p8kajr.cn/Gk/shorder", parms, "UTF-8");
						logger.info("星支付["+orderNo+"]query result:"+resultJsonStr);
						
						if(StringUtils.isNotBlank(resultJsonStr)) {
							resultJsonObj = JSONObject.fromObject(resultJsonStr);
							status = resultJsonObj.getString("status");
							if("3".equalsIgnoreCase(status)) {
								bool = true;
							}
						}
						break;
					case 17://付必达
						String exp = new Date().getTime()+"";//时间戳（单位：毫秒，⼗三位数）

						metaSignMap = new TreeMap<String, String>();
						metaSignMap.put("partnerId", FbdToolKit.PARTNERID);//商家编号（参阅附注-商家资讯）
						metaSignMap.put("orderId", orderNo);
						metaSignMap.put("exp", exp);
						metaSignMap.put("sign", FbdToolKit.generateSign(metaSignMap));//签名（参阅附注-加密⽅式）

						Map<String, String> header = new TreeMap<String, String>();
						header.put("Content-type", "application/json");
						resultJsonStr = HttpsUtil.postMethod("https://fullbitpay.co/api/wallet/v1/orderStatus", JsonUtil.map2json(metaSignMap),header, "UTF-8");

						logger.info("付必达["+orderNo+"]query result:"+resultJsonStr);
						if(org.apache.commons.lang3.StringUtils.isNotBlank(resultJsonStr)) {
							resultJsonObj = JSONObject.fromObject(resultJsonStr);
							status = resultJsonObj.getString("status");
							if("1".equalsIgnoreCase(status)) {
								String data = resultJsonObj.getString("data");
								JSONObject dataObj = resultJsonObj.fromObject(data);
								String _status = dataObj.getString("status");
								if("1".equalsIgnoreCase(_status)){
									bool = true;
								}
							}
						}
						break;
					case 18://优付宝
						metaSignMap = new TreeMap<String, String>();
						metaSignMap.put("merchantNo", YfbToolKit.merchantNo);//商户编号 (由隍寶支付平台提供,见:商户信息)
						//metaSignMap.put("tradeNo", tradeNo);//交易单号 (隍寶支付平台的交易单号)
						metaSignMap.put("orderNo", orderNo);//商户单号 (用于对账、查询; 不超过32个字符)
						metaSignMap.put("time", new Date().getTime()+"");//
						metaSignMap.put("appSecret", YfbToolKit.appSecret);//(由隍寶支付平台提供,见:商户信息/appSecret) (不参加加密)
						metaSignMap.put("sign", YfbToolKit.generateSign(metaSignMap));

						resultJsonStr = HttpsUtil.postMethod("https://hb.thepay.co.nz/order/status", metaSignMap, "UTF-8");
						logger.info("优付宝["+orderNo+"]query result:"+resultJsonStr);

						if(StringUtils.isNotBlank(resultJsonStr)) {
							resultJsonObj = JSONObject.fromObject(resultJsonStr);
							String code = resultJsonObj.getString("code");
							status = resultJsonObj.getString("status");
							if("0".equalsIgnoreCase(code) && "PAID".equalsIgnoreCase(status)) {
								bool = true;
							}
						}
						break;
					default:
						break;
					}
    				
    				if(bool) {
    					rechargeService.onlineRechargeSuccess(orderNo,recharge.getPayType(),rechargeAmount);
    				}
    			}
    		}catch (Exception e) {
    			e.printStackTrace();
    			logger.info("网银支付订单状态查询异常："+recharge.getTradeCode()+"-"+e);
			}
		}
		//LogUtil.info("======================网银支付订单状态查询======================");
	}
	
	/***自动修复最近十期的开奖数据*/
	@Scheduled(cron="0 * * * * ?")   
	public void repairLotteryPeriods(){
		//非新西兰彩种期数
		String dataSourcePath = null;
		String dataSourceName = systemConfigService.getValueByName("lottery_history_path");
		if(StringUtils.isBlank(dataSourceName)) {
			dataSourceName = "PC28AM";
		}

		if("PC28AM".equalsIgnoreCase(dataSourceName)) {
			dataSourcePath = systemConfigService.getValueByName("lottery_pc28am_path");
		}else if("ASCXW".equalsIgnoreCase(dataSourceName)){
			dataSourcePath = systemConfigService.getValueByName("lottery_ascxw_path");
		}else if("处理中心".equalsIgnoreCase(dataSourceName)){
			dataSourcePath = "";
		}
		
		for (int i = 1; i <= 6; i++) {
			try{
				List<LotteryDTO> historyList = null;
				if(i==6) {
					historyList = FetchResult.getOpenHis(i,"处理中心","");
				}else {
					historyList = FetchResult.getOpenHis(i,dataSourceName,dataSourcePath);
				}
				
				if(historyList !=null && historyList.size()>0) {
					for (LotteryDTO lotteryDTO : historyList) {
						lotteryPeriodsService.openLotteryPeriods(i,lotteryDTO,false);
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
				logger.info("自动修复[lotteryType="+i+"]彩种最近十期的开奖数据异常："+e);
			}
		}
	}
	
	/***提交新西兰赔率信息至处理中心*/
	@Scheduled(cron="0 0/5 * * * ?")
	public void submitXxlLotteryRule() {
		String stationIds = systemConfigService.getValueByName("xxl_subimt_station");
		if(StringUtils.isNotBlank(stationIds)) {
			String[] idArrs = stationIds.split(",");
			for (String id : idArrs) {
				try{
					lotteryRuleService.submitXxlLotteryRule(Integer.valueOf(id));
				}catch (Exception e) {
					e.printStackTrace();
					logger.info(id+"提交新西兰赔率信息至处理中心异常："+e);
				}
			}
		}
	}
}