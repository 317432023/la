package com.jeetx.service.lottery.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.jeetx.common.exception.BusinessException;
import com.jeetx.common.swagger.model.lottery.LotteryOrderItemDTO;
import com.jeetx.controller.api.ApiUtil;
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
import com.jeetx.service.member.MemberLogService;
import com.jeetx.service.member.PointsLevelService;
import com.jeetx.service.member.TransRecordService;
import com.jeetx.service.member.UserService;
import com.jeetx.service.system.StationConfigService;
import com.jeetx.util.DateTimeTool;
import com.jeetx.util.LogUtil;

@Service
@Transactional
public class LotteryOrderServiceImpl extends DaoSupport<LotteryOrder> implements LotteryOrderService {
	@Autowired LotteryPeriodsService lotteryPeriodsService;
	@Autowired LotteryRoomService lotteryRoomService;
	@Autowired UserService userService;
	@Autowired LotteryRuleService lotteryRuleService;
	@Autowired LotteryOrderItemService lotteryOrderItemService;
	@Autowired TransRecordService transRecordService;
	@Autowired LotteryDailyOrderTotalService lotteryDailyOrderTotalService;
	@Autowired LotteryRoomMessageService lotteryRoomMessageService;
	@Autowired PointsLevelService pointsLevelService;
	@Autowired LotteryNumberConfigService lotteryNumberConfigService;
	@Autowired MemberLogService memberLogService;
	@Autowired StationConfigService stationConfigService;
	@Autowired LotteryRulePlanItemService lotteryRulePlanItemService;
	
	@Value("${openLotteryMessage}")
	private String openLotteryMessage;
	
	public BigDecimal sumAllBetMoney(User user,Date beginDate,Date endDate) throws Exception { 
		BigDecimal allMoney = new BigDecimal(0);
		if(beginDate == null) {
			allMoney = (BigDecimal)this.getSession().createQuery("select sum(o.betMoney) from LotteryOrder o where o.status in (2,4) and o.user.id = ? and o.createTime <=str_to_date(?,'%Y-%m-%d %H:%i:%s') ")
					.setParameter(0, user.getId()).setParameter(1, endDate).uniqueResult();
		}else {
			allMoney = (BigDecimal)this.getSession().createQuery("select sum(o.betMoney) from LotteryOrder o where o.status in (2,4) and o.user.id = ? and o.createTime >=str_to_date(?,'%Y-%m-%d %H:%i:%s') and o.createTime <=str_to_date(?,'%Y-%m-%d %H:%i:%s') ")
					.setParameter(0, user.getId()).setParameter(1, beginDate).setParameter(2, endDate).uniqueResult();
		}
		if(allMoney == null) {
			allMoney = new BigDecimal(0);
		}
		return allMoney;
	}
	
	@SuppressWarnings("unchecked")
	public List<LotteryOrder> findLotteryOrderItemByUserId2Period(Integer userId,String lotteryPeriod,Integer lotteryType) {
		return this.getSession().createQuery("from LotteryOrder o where o.user.id = ? and o.lotteryPeriod = ? and o.lotteryType.id = ? ")
				.setParameter(0, userId).setParameter(1, lotteryPeriod).setParameter(2, lotteryType).list();
	}
	
	@SuppressWarnings("unchecked")
	public LotteryOrder findLotteryOrderByOrderCode(String orderCode,Integer stationId) {
		List<LotteryOrder> list = this.getSession().createQuery("from LotteryOrder o where o.orderCode = ? and o.user.station.id = ? ").setParameter(0, orderCode).setParameter(1, stationId).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (LotteryOrder) list.get(0);
		}
	}

	public BigDecimal getAllBetMoney(Integer userId,String lotteryPeriod) {
		Object money = this.getSession().createQuery("select sum(o.betMoney) from LotteryOrder o where o.status <> 3 and o.user.id = ? and o.lotteryPeriod = ? ").setParameter(0, userId).setParameter(1, lotteryPeriod).uniqueResult();
		if(money == null) {
			return new BigDecimal(0);
		}
		return (BigDecimal)money;
	}
	
	public BigDecimal getAllMoney(Integer userId,String key,String lotteryPeriod) {
		Object money = this.getSession().createQuery("select sum(o."+key+") from LotteryOrder o where o.status <> 3 and o.user.id = ? and o.lotteryPeriod = ? ").setParameter(0, userId).setParameter(1, lotteryPeriod).uniqueResult();
		if(money == null) {
			return new BigDecimal(0);
		}
		return (BigDecimal)money;
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
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void submitOrder(User user,String ip,String device,String orderCode,Integer roomId,String lotteryPeriod,BigDecimal dropMoney,
			List<LotteryOrderItemDTO> orderItemList,Integer stationId,Boolean verifyStatus,Date dropTime,Boolean isSendMessage) throws Exception{
		user = userService.find(user.getId());
		if(user == null) {
			throw new BusinessException(ApiUtil.getErrorCode("117"));
		}
		
		if(user.getStatus()==0) {
			throw new BusinessException(ApiUtil.getErrorCode("130"));
		}
		
		if(user.getStatus()==2) {
			throw new BusinessException(ApiUtil.getErrorCode("162"));
		}
		
		if(user.getUserType() == 2 || user.getUserType() == 3) {
			throw new BusinessException(ApiUtil.getErrorCode("131"));//代理及推广员类型用户不允许下注
		}
		
		LotteryRoom lotteryRoom = lotteryRoomService.find(roomId);
		if(lotteryRoom == null || lotteryRoom.getStatus() != 1 || 
				lotteryRoom.getLotteryHall().getStatus() !=1 || lotteryRoom.getLotteryType().getStatus() != 1
				|| lotteryRoom.getStation().getId() != stationId) {
			throw new BusinessException(ApiUtil.getErrorCode("127"));//房间不存在或不允许投注
		}
		
		LotteryPeriods lotteryPeriods = lotteryPeriodsService.findLotteryPeriodsByPeriods(lotteryRoom.getLotteryType().getId(), lotteryPeriod);
		if(lotteryPeriods == null) {
			throw new BusinessException(ApiUtil.getErrorCode("129"));//彩票期数不存在
		}
		
		if(verifyStatus && lotteryPeriods.getStatus() != 1) {
			throw new BusinessException(ApiUtil.getErrorCode("128"));//已封盘，不允许投注
		}
		
		//验证当前时间对于的期数是不是提交的期数
		LotteryPeriods currentLotteryPeriods = lotteryPeriodsService.findCurrentLotteryPeriods(lotteryRoom.getLotteryType().getId());
		if(verifyStatus && (currentLotteryPeriods == null || currentLotteryPeriods.getId() != lotteryPeriods.getId())) {
			throw new BusinessException(ApiUtil.getErrorCode("128"));//已封盘，不允许投注
		}
		
		/**1生成订单*/
		LotteryOrder lotteryOrder = this.findLotteryOrderByOrderCode(orderCode,stationId);
		if(lotteryOrder != null) {
			throw new BusinessException(ApiUtil.getErrorCode("135"));//订单已存在
		}
		
		lotteryOrder = new LotteryOrder();
		lotteryOrder.setBetMoney(dropMoney);
		lotteryOrder.setOrderCode(orderCode);//订单编号
		lotteryOrder.setUser(user);//玩家
		lotteryOrder.setCreateTime(dropTime);//下注时间
		lotteryOrder.setLotteryType(lotteryRoom.getLotteryType()); //彩票类型
		lotteryOrder.setLotteryRoom(lotteryRoom);//游戏房间
		lotteryOrder.setLotteryPeriod(lotteryPeriod);//当前期数
		lotteryOrder.setStatus(1);//状态(1待开奖、2已开奖、3已取消)
		this.save(lotteryOrder);


		/**2生成订单明细*/
		BigDecimal dxdsMoney = new BigDecimal(0); //下注大小单双金额
		BigDecimal combinationMoney = new BigDecimal(0); //下注组合金额
		BigDecimal jzMoney = new BigDecimal(0); //下注极值金额
		BigDecimal sbdMoney = new BigDecimal(0); //下注顺豹对金额
		BigDecimal ddMoney = new BigDecimal(0); //下注单点金额
		Integer combinationCount = 0;//组合期数（有组合1、没有组合0）
		BigDecimal allDropMoney = new BigDecimal(0);
		
		String allBetContent = null;
		if(lotteryRoom.getLotteryType().getId()==1||lotteryRoom.getLotteryType().getId()==2||lotteryRoom.getLotteryType().getId()==6) {
			List<LotteryOrder> orderList = this.findLotteryOrderItemByUserId2Period(user.getId(), lotteryPeriod, lotteryRoom.getLotteryType().getId());
			if(orderList != null) {
				for (LotteryOrder order : orderList) {
					if(order.getStatus()==1) {
						for (LotteryOrderItem orderItem : order.getLotteryOrderItems()) {
							allBetContent = StringUtils.isNotBlank(allBetContent)?allBetContent.concat("|").concat(orderItem.getLotteryRule().getParamName().concat(orderItem.getLotteryRule().getParamValues())):orderItem.getLotteryRule().getParamName().concat(orderItem.getLotteryRule().getParamValues());
						}
					}
				}
			}
		}
		
		//投注内容格式
		for (LotteryOrderItemDTO lotteryOrderItemDTO : orderItemList) {
			if(lotteryOrderItemDTO.getRuleId() == null || StringUtils.isBlank(lotteryOrderItemDTO.getBetContent()) ||
					StringUtils.isBlank(lotteryOrderItemDTO.getBetMoney())) {
				throw new BusinessException(ApiUtil.getErrorCode("126"));//订单JOSN格式有误
			}
			
			LotteryRule lotteryRule = lotteryRuleService.find(lotteryOrderItemDTO.getRuleId());
			if(lotteryRule == null || lotteryRule.getStatus() != 1) {
				throw new BusinessException(ApiUtil.getErrorCode("133").concat(",ruleId=").concat(lotteryOrderItemDTO.getRuleId()+""));//ruleId对应的玩法不存在
			}
			
			if(!lotteryRule.getLotteryHall().getId().equals(lotteryRoom.getLotteryHall().getId())) {
				throw new BusinessException(ApiUtil.getErrorCode("138").concat(",ruleId=").concat(lotteryOrderItemDTO.getRuleId()+""));//ruleId对应的玩法不存在
			}

		    Pattern pattern = Pattern.compile(lotteryRule.getRuleRegular());
		    Matcher matcher = pattern.matcher(lotteryOrderItemDTO.getBetContent());
		    if(!matcher.matches()) {
		    	throw new BusinessException(ApiUtil.getErrorCode("134").concat(",内容：").concat(lotteryOrderItemDTO.getBetContent()));//投注内容格式有误
		    }
			
			allDropMoney = allDropMoney.add(new BigDecimal(lotteryOrderItemDTO.getBetMoney()));
			
			LotteryOrderItem lotteryOrderItem = new LotteryOrderItem();
			lotteryOrderItem.setLotteryOrder(lotteryOrder);//订单
			lotteryOrderItem.setBetMoney(new BigDecimal(lotteryOrderItemDTO.getBetMoney()));//下注金额
			lotteryOrderItem.setRuleName(lotteryOrderItemDTO.getBetContent());
			lotteryOrderItem.setLotteryRule(lotteryRule);//赔率规则
			lotteryOrderItemService.save(lotteryOrderItem);
			
			//PC类投注限制
			if(lotteryRoom.getLotteryType().getId()==1||lotteryRoom.getLotteryType().getId()==2||lotteryRoom.getLotteryType().getId()==6) {
				if(Arrays.asList("大|小|单|双".split("\\|")).contains(lotteryRule.getParamName())){
					dxdsMoney = dxdsMoney.add(new BigDecimal(lotteryOrderItemDTO.getBetMoney()));

					String betRange = lotteryRuleService.getParamValue(lotteryRoom.getLotteryHall().getId(), "大小单双限制",stationId);
					//System.out.println("大小单双限制:"+betRange);
					if(StringUtils.isNotBlank(betRange)&&betRange.contains("-")){
						if(new BigDecimal(lotteryOrderItemDTO.getBetMoney()).compareTo(new BigDecimal(betRange.split("-")[0]))<0 
								|| new BigDecimal(lotteryOrderItemDTO.getBetMoney()).compareTo(new BigDecimal(betRange.split("-")[1]))>0) {
							throw new BusinessException(ApiUtil.getErrorCode("201").replace("[下注类型]", "大小单双").replace("[最小值]", betRange.split("-")[0]).replace("[最大值]", betRange.split("-")[1]));//"下注无效，系统限制'[下注类型]'下注范围为：[下注范围]"
						}
					}
					allBetContent = StringUtils.isNotBlank(allBetContent)?allBetContent.concat("|").concat(lotteryRule.getParamName().concat(lotteryRule.getParamValues())):lotteryRule.getParamName().concat(lotteryRule.getParamValues());
				}else if(Arrays.asList("大单|小单|大双|小双".split("\\|")).contains(lotteryRule.getParamName())) {
					combinationMoney = combinationMoney.add(new BigDecimal(lotteryOrderItemDTO.getBetMoney()));
					combinationCount = 1;
					
					String betRange = lotteryRuleService.getParamValue(lotteryRoom.getLotteryHall().getId(), "组合限制",stationId);
					//System.out.println("组合限制:"+betRange);
					if(StringUtils.isNotBlank(betRange)&&betRange.contains("-")){
						if(new BigDecimal(lotteryOrderItemDTO.getBetMoney()).compareTo(new BigDecimal(betRange.split("-")[0]))<0 
								|| new BigDecimal(lotteryOrderItemDTO.getBetMoney()).compareTo(new BigDecimal(betRange.split("-")[1]))>0) {
							throw new BusinessException(ApiUtil.getErrorCode("201").replace("[下注类型]", "组合").replace("[最小值]", betRange.split("-")[0]).replace("[最大值]", betRange.split("-")[1]));//"下注无效，系统限制'[下注类型]'下注范围为：[下注范围]"
						}
					}
					allBetContent = StringUtils.isNotBlank(allBetContent)?allBetContent.concat("|").concat(lotteryRule.getParamName().concat(lotteryRule.getParamValues())):lotteryRule.getParamName().concat(lotteryRule.getParamValues());
				}else if(Arrays.asList("极大|极小".split("\\|")).contains(lotteryRule.getParamName())) {
					jzMoney = jzMoney.add(new BigDecimal(lotteryOrderItemDTO.getBetMoney()));
					
					String betRange = lotteryRuleService.getParamValue(lotteryRoom.getLotteryHall().getId(), "极值限制",stationId);
					//System.out.println("极值限制:"+betRange);
					if(StringUtils.isNotBlank(betRange)&&betRange.contains("-")){
						if(new BigDecimal(lotteryOrderItemDTO.getBetMoney()).compareTo(new BigDecimal(betRange.split("-")[0]))<0 
								|| new BigDecimal(lotteryOrderItemDTO.getBetMoney()).compareTo(new BigDecimal(betRange.split("-")[1]))>0) {
							throw new BusinessException(ApiUtil.getErrorCode("201").replace("[下注类型]", "极值").replace("[最小值]", betRange.split("-")[0]).replace("[最大值]", betRange.split("-")[1]));//"下注无效，系统限制'[下注类型]'下注范围为：[下注范围]"
						}
					}
				}else if(Arrays.asList("顺子|豹子|对子|红|绿|蓝".split("\\|")).contains(lotteryRule.getParamName())) {
					sbdMoney = sbdMoney.add(new BigDecimal(lotteryOrderItemDTO.getBetMoney()));

					String betRange = lotteryRuleService.getParamValue(lotteryRoom.getLotteryHall().getId(), "顺豹对限制",stationId);
					//System.out.println("顺豹对限制:"+betRange);
					if(StringUtils.isNotBlank(betRange)&&betRange.contains("-")){
						if(new BigDecimal(lotteryOrderItemDTO.getBetMoney()).compareTo(new BigDecimal(betRange.split("-")[0]))<0 
								|| new BigDecimal(lotteryOrderItemDTO.getBetMoney()).compareTo(new BigDecimal(betRange.split("-")[1]))>0) {
							throw new BusinessException(ApiUtil.getErrorCode("201").replace("[下注类型]", "顺豹对|红绿蓝").replace("[最小值]", betRange.split("-")[0]).replace("[最大值]", betRange.split("-")[1]));//"下注无效，系统限制'[下注类型]'下注范围为：[下注范围]"
						}
					}
				}else {
				    Pattern pattern1 = Pattern.compile("^([0-9]|1[0-9]|2[0-7])$");
				    Matcher matcher1 = pattern1.matcher(lotteryRule.getParamName());
				    if(matcher1.matches()) {
				    	ddMoney = ddMoney.add(new BigDecimal(lotteryOrderItemDTO.getBetMoney()));
				    	
						String betRange = lotteryRuleService.getParamValue(lotteryRoom.getLotteryHall().getId(), "单点限制",stationId);
						//System.out.println("单点限制:"+betRange);
						if(StringUtils.isNotBlank(betRange)&&betRange.contains("-")){
							if(new BigDecimal(lotteryOrderItemDTO.getBetMoney()).compareTo(new BigDecimal(betRange.split("-")[0]))<0 
									|| new BigDecimal(lotteryOrderItemDTO.getBetMoney()).compareTo(new BigDecimal(betRange.split("-")[1]))>0) {
								throw new BusinessException(ApiUtil.getErrorCode("201").replace("[下注类型]", "单点").replace("[最小值]", betRange.split("-")[0]).replace("[最大值]", betRange.split("-")[1]));//"下注无效，系统限制'[下注类型]'下注范围为：[下注范围]"
							}
						}
				    }
				}
			}else {
				if(Arrays.asList("大单|小单|大双|小双".split("\\|")).contains(lotteryRule.getParamName())){
					combinationMoney = combinationMoney.add(new BigDecimal(lotteryOrderItemDTO.getBetMoney()));
					combinationCount = 1;
				}
			}
		}

		//验证投注金额
		if(allDropMoney.compareTo(dropMoney) != 0) {
			throw new BusinessException(ApiUtil.getErrorCode("132"));//订单总金额与明细总金额不符
		}
		
		//总注限制
		String betRange = lotteryRuleService.getParamValue(lotteryRoom.getLotteryHall().getId(), "总注限制",stationId);
		//System.out.println("总注限制:"+betRange);
		BigDecimal allBetMoney = this.getAllBetMoney(user.getId(), lotteryPeriod);
		//System.out.println(allBetMoney);
		if(StringUtils.isNotBlank(betRange)&&betRange.contains("-")){
			if(allBetMoney.compareTo(new BigDecimal(betRange.split("-")[0]))<0 
					|| allBetMoney.compareTo(new BigDecimal(betRange.split("-")[1]))>0) {
				throw new BusinessException(ApiUtil.getErrorCode("201").replace("[下注类型]", "总注").replace("[最小值]", betRange.split("-")[0]).replace("[最大值]", betRange.split("-")[1]));//"下注无效，系统限制'[下注类型]'下注范围为：[下注范围]"
			}
		}

		//反组合|杀组合验证
		//System.out.println("allBetContent:"+allBetContent);
		if(StringUtils.isNotBlank(allBetContent)) {
			//System.out.println(allBetContent);
			//1是否开启反组合
			String chkFanZuhe = lotteryRuleService.getParamValue(lotteryRoom.getLotteryHall().getId(), "开启禁反组合",stationId);
			if(StringUtils.isNotBlank(chkFanZuhe) && chkFanZuhe.equals("1") &&isFanZuhe(allBetContent)){
				throw new BusinessException(ApiUtil.getErrorCode("202"));//下注无效,禁止反组合下注
			}
			
			//2是否开启杀组合
			String chkShaZuhe = lotteryRuleService.getParamValue(lotteryRoom.getLotteryHall().getId(), "开启禁杀组合",stationId);
			if(StringUtils.isNotBlank(chkShaZuhe) && chkShaZuhe.equals("1") &&isShaZuhe(allBetContent)){
				throw new BusinessException(ApiUtil.getErrorCode("203"));//下注无效,禁止杀组合下注
			}
		}

		lotteryOrder.setDxdsMoney(dxdsMoney);
		lotteryOrder.setJzMoney(jzMoney);
		lotteryOrder.setSbdMoney(sbdMoney);
		lotteryOrder.setDdMoney(ddMoney);
		lotteryOrder.setCombinationCount(combinationCount);
		lotteryOrder.setCombinationMoney(combinationMoney);
		this.update(lotteryOrder);
		
		/**3验证余额，扣款处理*/
		BigDecimal transAmount = new BigDecimal(0);
		BigDecimal transLotteryAmount = new BigDecimal(0);
		
		user.setPoints(user.getPoints()+dropMoney.setScale(0, BigDecimal.ROUND_DOWN).intValue());//投注送积分
		user.setPointsLevel(pointsLevelService.findPointsLevel(user.getPoints().intValue()));
		if(user.getBalance().subtract(user.getFreezeBalance()).add(user.getLotteryBalance()).compareTo(dropMoney) <0) {
			throw new BusinessException(ApiUtil.getErrorCode("137"));//下注失败，余额不足
		}
		
		//处理流水要求
		if(dropMoney.compareTo(user.getFlowRequire()) > 0) {
			user.setFlowRequire(new BigDecimal(0));
		}else {
			user.setFlowRequire(user.getFlowRequire().subtract(dropMoney));
		}
		
		//彩金足够支付的情况
		if(user.getLotteryBalance().compareTo(dropMoney) >= 0) {
			transLotteryAmount = dropMoney;
			user.setLotteryBalance(user.getLotteryBalance().subtract(dropMoney));
			userService.update(user);
		//彩金不足支付的情况
		}else {
			transLotteryAmount = user.getLotteryBalance();
			transAmount = dropMoney.subtract(user.getLotteryBalance());
			user.setLotteryBalance(new BigDecimal(0));
			
			user.setBalance(user.getBalance().subtract(transAmount));
			userService.update(user);
		}
		
		if(transAmount.add(transLotteryAmount).subtract(dropMoney).compareTo(new BigDecimal(0)) != 0) {
			throw new BusinessException(ApiUtil.getErrorCode("136"));//下注失败，订单扣除异常
		}
		
//		List<Integer> lotteryRoomCounts = getSession().createSQLQuery("select o.lottery_room_id from tb_lottery_order o where o.lottery_periods = '"+lotteryPeriods.getLotteryPeriods()+"' and o.user_id = "+user.getId()+"  group by o.lottery_room_id").list();;
//		//System.out.println(lotteryRoomCounts.size());
//		if(lotteryRoomCounts != null && lotteryRoomCounts.size() > 1) {
//			throw new BusinessException(ApiUtil.getErrorCode("207"));//同类型期数，只能在一个房间下注
//		}

		/**4生成资金明细*/
		TransRecord transRecord = new TransRecord();
		transRecord.setUser(user);//用户
		transRecord.setCreateTime(dropTime);//交易时间
		transRecord.setTransCategory(4);//交易类型（1充值、2提现、3抽奖、4投注、5撤单、6赠送、7中奖、8回水、9调账）
		transRecord.setTransAmount(transAmount);//交易账户金额
		transRecord.setEndBalance(user.getBalance());//剩余账户金额
		transRecord.setTransLotteryAmount(transLotteryAmount);//交易彩金
		transRecord.setEndLotteryBalance(user.getLotteryBalance());//剩余彩金
		transRecord.setRemark("订单编号:".concat(orderCode));
		transRecord.setFlag(0);
		transRecordService.save(transRecord);

		/**5生成统计明细*/
		LotteryDailyOrderTotal lotteryDailyOrderTotal = lotteryDailyOrderTotalService.getLotteryDailyOrderTotal(DateTimeTool.dateFormat("yyyy-MM-dd", dropTime), user.getId());
		if(lotteryDailyOrderTotal==null) {
			lotteryDailyOrderTotal = new LotteryDailyOrderTotal();
			
			lotteryDailyOrderTotal.setUser(user);//玩家帐号
			lotteryDailyOrderTotal.setTotalDate(DateTimeTool.dateFormat("yyyy-MM-dd", DateTimeTool.dateFormat("yyyy-MM-dd", dropTime)));//统计日期
			lotteryDailyOrderTotal.setBetMoney(dropMoney);//流水金额
			lotteryDailyOrderTotal.setProfitMoney(new BigDecimal(0));//盈亏金额
			lotteryDailyOrderTotal.setBackWaterMoney(new BigDecimal(0));// //回水金额
			lotteryDailyOrderTotal.setRechargeMoney(new BigDecimal(0));//充值金额
			lotteryDailyOrderTotal.setWithdrawMoney(new BigDecimal(0));//提现金额
			lotteryDailyOrderTotal.setWinMoney(new BigDecimal(0));//中奖金额
			lotteryDailyOrderTotal.setExpandUserNum(0);//拓展玩家数
			lotteryDailyOrderTotal.setBalance(user.getBalance());
			lotteryDailyOrderTotal.setLotteryBalance(user.getLotteryBalance());//赠送彩金统计
			lotteryDailyOrderTotalService.save(lotteryDailyOrderTotal);
		}else{
			lotteryDailyOrderTotal.setBetMoney(lotteryDailyOrderTotal.getBetMoney().add(dropMoney));//流水金额
			lotteryDailyOrderTotal.setBalance(user.getBalance());
			lotteryDailyOrderTotal.setLotteryBalance(user.getLotteryBalance());//赠送彩金统计
			lotteryDailyOrderTotalService.update(lotteryDailyOrderTotal);
		}
		
		/**6生成消息记录，插入MQ队列*/
		if(isSendMessage) {
			lotteryRoomMessageService.sendOrderMessage(lotteryRoom, lotteryOrder.getUser().getNickName(),1,
					lotteryOrder.getUser().getHeadImg(),lotteryOrder.getUser().getPointsLevel().getTitle(), currentLotteryPeriods, orderItemList,lotteryOrder.getOrderCode());
		
			memberLogService.saveLog(user, lotteryOrder.getOrderCode(), "提交投注订单", ip, device);
		}
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
	
	@SuppressWarnings("unchecked")
	public List<LotteryOrder> findLotteryOrderList(String lotteryPeriod,Integer lotteryRoomId,Integer stationId) {
		return this.getSession().createQuery("from LotteryOrder o where o.status = 1 and o.lotteryPeriod=? and o.lotteryRoom.id = ? and o.user.station.id = ?")
				.setParameter(0, lotteryPeriod).setParameter(1, lotteryRoomId).setParameter(2, stationId).list();
	}
	
	/**
	 * 接口派奖处理
	 * @param lotteryPeriods
	 * @throws Exception
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void lotteryAwardHandle(String lotteryPeriod,Integer lotteryType,Integer stationId,Date openTime) throws Exception{
		LotteryPeriods lotteryPeriods = lotteryPeriodsService.findLotteryPeriodsByPeriods(lotteryType, lotteryPeriod);
		if(lotteryPeriods == null || lotteryPeriods.getStatus() != 3) {
			throw new BusinessException(ApiUtil.getErrorCode("141"));
		}
		
		List<LotteryRoom> lotteryRoomList = lotteryRoomService.getLotteryRoomList(lotteryType,stationId);
		for (LotteryRoom lotteryRoom : lotteryRoomList) {
			if(lotteryRoom != null) {
				this.settlementOrder(lotteryRoom,lotteryPeriods,stationId,openTime);
			}
		}
	}
	

	/*** 开奖结算，发送开奖信息*/
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void settlementOrder(LotteryRoom lotteryRoom,LotteryPeriods lotteryPeriods,Integer stationId,Date openTime) throws Exception {	
		lotteryRoom = lotteryRoomService.find(lotteryRoom.getId());
		if(lotteryRoom != null && lotteryRoom.getStatus() == 1) {
			
			Integer combinationCount = 0;//组合期数
			List<LotteryOrder> orderList = this.findLotteryOrderList(lotteryPeriods.getLotteryPeriods(), lotteryRoom.getId(),stationId);
			for (LotteryOrder lotteryOrder : orderList) {
				lotteryOrder = this.find(lotteryOrder.getId());
				if(lotteryOrder !=null && lotteryOrder.getStatus() == 1){
					LotteryRule lotteryRule = null;
					BigDecimal totalWinMoney = BigDecimal.ZERO;
					List<LotteryOrderItem> LotteryOrderItemList = lotteryOrderItemService.findLotteryOrderItemList(lotteryOrder.getId());
	
					for (LotteryOrderItem lotteryOrderItem : LotteryOrderItemList) {
						BigDecimal winMoney = new BigDecimal(0);
						String lotteryRuleValues = null;
						switch (lotteryOrder.getLotteryType().getId()) {
						case 1:
						case 2:
						case 6:
							String pcStraightCombine = stationConfigService.getValueByName("pc_straight_combine", lotteryOrder.getUser().getStation().getId());
							if(StringUtils.isNotBlank(pcStraightCombine) && StringUtils.isNotBlank(lotteryPeriods.getLotteryOpenContent())) {
								String[] numberArr = lotteryPeriods.getLotteryOpenContent().split("\\+");
								if(numberArr != null && numberArr.length == 3) {
									String numbers = ApiUtil.sortNumber(Integer.valueOf(numberArr[0]), Integer.valueOf(numberArr[1]), Integer.valueOf(numberArr[2]));
									if(Arrays.asList(pcStraightCombine.split(",")).contains(numbers) && !lotteryPeriods.getLotteryShowContent().contains("顺子")) {
										lotteryPeriods.setLotteryShowContent(lotteryPeriods.getLotteryShowContent().replaceAll("\\)", "、顺子)"));
									}
								}
							}
							
							lotteryRule = lotteryOrderItem.getLotteryRule();
							LotteryRulePlanItem lotteryRulePlanItem = lotteryRulePlanItemService.findLotteryRulePlanItem(stationId, lotteryRule.getLotteryHall().getId(), lotteryRule.getId(),lotteryPeriods.getLotteryOpenTime());
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
											String dxdsTotalGtSpecialValue1 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "大小单双特殊情况一：如遇开13、14，总注>S1时，赔率值S2",stationId);
											if(StringUtils.isNotBlank(dxdsTotalGtSpecialValue1)&&dxdsTotalGtSpecialValue1.contains("|")
													&&lotteryOrder.getBetMoney().compareTo(new BigDecimal(dxdsTotalGtSpecialValue1.split("\\|")[0])) >0){
												paramValues = new BigDecimal(dxdsTotalGtSpecialValue1.split("\\|")[1]);
											}
											
											//大小单双特殊情况二：如遇开13、14，总注>S1时，赔率值S2
											String dxdsTotalGtSpecialValue2 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "大小单双特殊情况二：如遇开13、14，总注>S1时，赔率值S2",stationId);
											if(StringUtils.isNotBlank(dxdsTotalGtSpecialValue2)&&dxdsTotalGtSpecialValue2.contains("|")
													&&lotteryOrder.getBetMoney().compareTo(new BigDecimal(dxdsTotalGtSpecialValue2.split("\\|")[0])) >0){
												paramValues = new BigDecimal(dxdsTotalGtSpecialValue2.split("\\|")[1]);
											}
										//组合开13,14的情况
										}else if(Arrays.asList("大单|小单|大双|小双".split("\\|")).contains(lotteryRule.getParamName())){
											//组合特殊情况一：如遇开13、14，总注>S1时，赔率值S2
											String zhheTotalGtSpecialValue1 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "组合特殊情况一：如遇开13、14，总注>S1时，赔率值S2",stationId);
											if(StringUtils.isNotBlank(zhheTotalGtSpecialValue1)&&zhheTotalGtSpecialValue1.contains("|")
													&&lotteryOrder.getBetMoney().compareTo(new BigDecimal(zhheTotalGtSpecialValue1.split("\\|")[0])) >0){
												paramValues = new BigDecimal(zhheTotalGtSpecialValue1.split("\\|")[1]);
											}
											
											//组合特殊情况二：如遇开13、14，总注>S1时，赔率值S2
											String zhheTotalGtSpecialValue2 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "组合特殊情况二：如遇开13、14，总注>S1时，赔率值S2",stationId);
											if(StringUtils.isNotBlank(zhheTotalGtSpecialValue2)&&zhheTotalGtSpecialValue2.contains("|")
													&&lotteryOrder.getBetMoney().compareTo(new BigDecimal(zhheTotalGtSpecialValue2.split("\\|")[0])) >0){
												paramValues = new BigDecimal(zhheTotalGtSpecialValue2.split("\\|")[1]);
											}
										}
										
										
										//总注特殊情况一：如遇开13、14，总注>S1时，赔率值S2
										String totalGtSpecialValue1 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "总注特殊情况一：如遇开13、14，总注>S1时，赔率值S2",stationId);
										if(StringUtils.isNotBlank(totalGtSpecialValue1)&&totalGtSpecialValue1.contains("|")
												&&lotteryOrder.getBetMoney().compareTo(new BigDecimal(totalGtSpecialValue1.split("\\|")[0])) >0){
											paramValues = new BigDecimal(totalGtSpecialValue1.split("\\|")[1]);
										}
										
										//总注特殊情况二：如遇开13、14，总注>S1时，赔率值S2
										String totalGtSpecialValue2 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "总注特殊情况二：如遇开13、14，总注>S1时，赔率值S2",stationId);
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
											String dxdsTotalGtSpecialValue3 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "大小单双特殊情况三：如遇开顺豹对，总注>S1时，赔率值S2",stationId);
											if(StringUtils.isNotBlank(dxdsTotalGtSpecialValue3)&&dxdsTotalGtSpecialValue3.contains("|")
													&&lotteryOrder.getBetMoney().compareTo(new BigDecimal(dxdsTotalGtSpecialValue3.split("\\|")[0])) >0
													&& (new BigDecimal(dxdsTotalGtSpecialValue3.split("\\|")[1])).compareTo(new BigDecimal(0)) >0){//设置0时无效
												paramValues = new BigDecimal(dxdsTotalGtSpecialValue3.split("\\|")[1]);
											}
											
											//大小单双特殊情况四：如遇开顺豹对，总注>S1时，赔率值S2
											String dxdsTotalGtSpecialValue4 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "大小单双特殊情况四：如遇开顺豹对，总注>S1时，赔率值S2",stationId);
											if(StringUtils.isNotBlank(dxdsTotalGtSpecialValue4)&&dxdsTotalGtSpecialValue4.contains("|")
													&&lotteryOrder.getBetMoney().compareTo(new BigDecimal(dxdsTotalGtSpecialValue4.split("\\|")[0])) >0
													&& (new BigDecimal(dxdsTotalGtSpecialValue4.split("\\|")[1])).compareTo(new BigDecimal(0)) >0){//设置0时无效
												paramValues = new BigDecimal(dxdsTotalGtSpecialValue4.split("\\|")[1]);
											}
										//组合开顺豹对的情况
										}else if(Arrays.asList("大单|小单|大双|小双".split("\\|")).contains(lotteryRule.getParamName())){
											//组合特殊情况三：如遇开顺豹对，总注>S1时，赔率值S2
											String zhheTotalGtSpecialValue3 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "组合特殊情况三：如遇开顺豹对，总注>S1时，赔率值S2",stationId);
											if(StringUtils.isNotBlank(zhheTotalGtSpecialValue3)&&zhheTotalGtSpecialValue3.contains("|")
													&&lotteryOrder.getBetMoney().compareTo(new BigDecimal(zhheTotalGtSpecialValue3.split("\\|")[0])) >0
													&& (new BigDecimal(zhheTotalGtSpecialValue3.split("\\|")[1])).compareTo(new BigDecimal(0)) >0){//设置0时无效
												paramValues = new BigDecimal(zhheTotalGtSpecialValue3.split("\\|")[1]);
											}
											
											//组合特殊情况四：如遇开顺豹对，总注>S1时，赔率值S2
											String zhheTotalGtSpecialValue4 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "组合特殊情况四：如遇开顺豹对，总注>S1时，赔率值S2",stationId);
											if(StringUtils.isNotBlank(zhheTotalGtSpecialValue4)&&zhheTotalGtSpecialValue4.contains("|")
													&&lotteryOrder.getBetMoney().compareTo(new BigDecimal(zhheTotalGtSpecialValue4.split("\\|")[0])) >0
													&& (new BigDecimal(zhheTotalGtSpecialValue4.split("\\|")[1])).compareTo(new BigDecimal(0)) >0){//设置0时无效
												paramValues = new BigDecimal(zhheTotalGtSpecialValue4.split("\\|")[1]);
											}
										}
										
										
										//总注特殊情况三：如遇开顺豹对，总注>S1时，赔率值S2
										String totalGtSpecialValue3 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "总注特殊情况三：如遇开顺豹对，总注>S1时，赔率值S2",stationId);
										if(StringUtils.isNotBlank(totalGtSpecialValue3)&&totalGtSpecialValue3.contains("|")
												&&lotteryOrder.getBetMoney().compareTo(new BigDecimal(totalGtSpecialValue3.split("\\|")[0])) >0
												&& (new BigDecimal(totalGtSpecialValue3.split("\\|")[1])).compareTo(new BigDecimal(0)) >0){//设置0时无效
											paramValues = new BigDecimal(totalGtSpecialValue3.split("\\|")[1]);
										}
										
										//总注特殊情况四：如遇开顺豹对，总注>S1时，赔率值S2
										String totalGtSpecialValue4 = lotteryRuleService.getParamValue(lotteryOrder.getLotteryRoom().getLotteryHall().getId(), "总注特殊情况四：如遇开顺豹对，总注>S1时，赔率值S2",stationId);
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
							tr.setCreateTime(openTime);
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
					LotteryDailyOrderTotal lotteryDailyOrderTotal = lotteryDailyOrderTotalService.getLotteryDailyOrderTotal(DateTimeTool.dateFormat("yyyy-MM-dd", openTime), lotteryOrder.getUser().getId());
					if(lotteryDailyOrderTotal==null) {
						lotteryDailyOrderTotal = new LotteryDailyOrderTotal();
						
						lotteryDailyOrderTotal.setUser(lotteryOrder.getUser());//玩家帐号
						lotteryDailyOrderTotal.setTotalDate(DateTimeTool.dateFormat("yyyy-MM-dd", DateTimeTool.dateFormat("yyyy-MM-dd", openTime)));//统计日期
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
						lotteryDailyOrderTotal.setBalance(lotteryOrder.getUser().getBalance());
						lotteryDailyOrderTotal.setLotteryBalance(lotteryOrder.getUser().getLotteryBalance());//赠送彩金统计
						lotteryDailyOrderTotal.setProfitMoney(lotteryDailyOrderTotal.getProfitMoney().add(lotteryOrder.getProfitMoney()));
						lotteryDailyOrderTotal.setWinMoney(lotteryDailyOrderTotal.getWinMoney().add(lotteryOrder.getWinMoney()));//中奖金额
						lotteryDailyOrderTotalService.update(lotteryDailyOrderTotal);
					}
					
					LogUtil.info("订单(".concat(lotteryOrder.getOrderCode()).concat(")派奖完成，下注金额：".concat(lotteryOrder.getBetMoney().toString()).concat(",中奖金额：").concat(lotteryOrder.getWinMoney().toString())));
				}
			}
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void adminCancelOrder(String orderIds,Integer stationId) throws Exception{
		String[] idsArr = orderIds.split(",");
		for (int i = 0; i < idsArr.length; i++) {
			LotteryOrder lotteryOrder = this.find(Integer.valueOf(idsArr[i]));
			if(lotteryOrder != null && lotteryOrder.getUser().getStation().getId() == stationId) {
				this.cancelOrder(lotteryOrder.getUser(), null, null, lotteryOrder.getOrderCode(),stationId);
			}else {
				throw new BusinessException(ApiUtil.getErrorCode("204").concat(",订单ID:").concat(Integer.valueOf(idsArr[i])+""));
			}
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void cancelOrder(User user,String ip,String device,String orderCode,Integer stationId) throws Exception{
		if(user == null) {
			throw new BusinessException(ApiUtil.getErrorCode("117"));
		}
		
		if(user.getStatus()==0) {
			throw new BusinessException(ApiUtil.getErrorCode("130"));
		}
		
		if(user.getStatus()==2) {
			throw new BusinessException(ApiUtil.getErrorCode("162"));
		}
		
		LotteryOrder lotteryOrder = this.findLotteryOrderByOrderCode(orderCode,stationId);
		if(lotteryOrder == null || lotteryOrder.getUser().getId() != user.getId()) {
			throw new BusinessException(ApiUtil.getErrorCode("204").concat(",订单号:").concat(orderCode));
		}
		
		if(lotteryOrder.getStatus()==3) {
			throw new BusinessException(ApiUtil.getErrorCode("206").concat(",订单号:").concat(orderCode));
		}
		
		if(lotteryOrder.getStatus()!=1) {
			throw new BusinessException(ApiUtil.getErrorCode("208").concat(",订单号:").concat(orderCode));
		}
		
		LotteryPeriods lotteryPeriods = lotteryPeriodsService.findLotteryPeriodsByPeriods(lotteryOrder.getLotteryType().getId(), lotteryOrder.getLotteryPeriod());
		if(lotteryPeriods == null || lotteryPeriods.getStatus()!=1) {
			throw new BusinessException(ApiUtil.getErrorCode("205").concat(",订单号:").concat(orderCode));
		}
		
		/**1变更订单*/
		lotteryOrder.setStatus(3);//状态(1待开奖、2已开奖、3已取消)
		this.save(lotteryOrder);

		/**2退款处理*/
		user.setPoints(user.getPoints()-lotteryOrder.getBetMoney().setScale(0, BigDecimal.ROUND_DOWN).intValue());//扣除赠送积分
		user.setPointsLevel(pointsLevelService.findPointsLevel(user.getPoints().intValue()));
		user.setLotteryBalance(user.getLotteryBalance().add(lotteryOrder.getBetMoney()));
		user.setFlowRequire(user.getFlowRequire().add(lotteryOrder.getBetMoney()));//处理流水要求
		userService.update(user);

		/**3生成资金明细*/
		TransRecord transRecord = new TransRecord();
		transRecord.setUser(user);//用户
		transRecord.setCreateTime(new Date());//交易时间
		transRecord.setTransCategory(5);//交易类型（1充值、2提现、3抽奖、4投注、5撤单、6赠送、7中奖、8回水、9调账）
		transRecord.setTransAmount(new BigDecimal(0));//交易账户金额
		transRecord.setEndBalance(user.getBalance());//剩余账户金额
		transRecord.setTransLotteryAmount(lotteryOrder.getBetMoney());//交易彩金
		transRecord.setEndLotteryBalance(user.getLotteryBalance());//剩余彩金
		transRecord.setRemark("订单编号:".concat(orderCode));
		transRecord.setFlag(1);
		transRecordService.save(transRecord);

		/**4生成统计明细*/
		LotteryDailyOrderTotal lotteryDailyOrderTotal = lotteryDailyOrderTotalService.getLotteryDailyOrderTotal(DateTimeTool.dateFormat("yyyy-MM-dd", new Date()), user.getId());
		if(lotteryDailyOrderTotal!=null) {
			lotteryDailyOrderTotal.setBalance(user.getBalance());
			lotteryDailyOrderTotal.setLotteryBalance(user.getLotteryBalance());//赠送彩金统计
			lotteryDailyOrderTotal.setBetMoney(lotteryDailyOrderTotal.getBetMoney().subtract(lotteryOrder.getBetMoney()));//流水金额
			lotteryDailyOrderTotalService.update(lotteryDailyOrderTotal);
		}
		
		if(StringUtils.isNotBlank(ip)) {
			memberLogService.saveLog(user, lotteryOrder.getOrderCode(), "取消投注订单", ip, device);
		}
	}
}
