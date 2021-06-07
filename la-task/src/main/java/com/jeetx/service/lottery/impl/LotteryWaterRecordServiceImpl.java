package com.jeetx.service.lottery.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryDailyOrderTotal;
import com.jeetx.bean.lottery.LotteryHall;
import com.jeetx.bean.lottery.LotteryWaterConfig;
import com.jeetx.bean.lottery.LotteryWaterRecord;
import com.jeetx.bean.member.TransRecord;
import com.jeetx.bean.member.User;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryDailyOrderTotalService;
import com.jeetx.service.lottery.LotteryWaterConfigService;
import com.jeetx.service.lottery.LotteryWaterRecordService;
import com.jeetx.service.member.TransRecordService;
import com.jeetx.service.member.UserService;
import com.jeetx.util.DateTimeTool;
import com.jeetx.util.LogUtil;

@Service
@Transactional
public class LotteryWaterRecordServiceImpl extends DaoSupport<LotteryWaterRecord> implements LotteryWaterRecordService {
	@Autowired LotteryWaterConfigService lotteryWaterConfigService;
	@Autowired UserService userService;
	@Autowired TransRecordService transRecordService;
	@Autowired LotteryDailyOrderTotalService lotteryDailyOrderTotalService;
	
	@SuppressWarnings("unchecked")
	public LotteryWaterRecord getLotteryWaterRecord(Integer userId,String totalDate,Integer lotteryHallId) {
		List<LotteryWaterRecord> list = this.getSession().createQuery("from LotteryWaterRecord o where o.user.id = ? and o.totalDate <=str_to_date(?,'%Y-%m-%d %H:%i:%s') and o.totalDate >=str_to_date(?,'%Y-%m-%d %H:%i:%s') and o.lotteryHall.id = ? ")
				.setParameter(0, userId).setParameter(1, totalDate).setParameter(2, totalDate).setParameter(3, lotteryHallId).list();
		if (list!=null&&list.size()>0) {
			return list.get(0);
		}
		return null;
	}
	
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void createLotteryWaterRecord(String totalDate,LotteryHall lotteryHall) throws Exception {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("SELECT t.user_id,count(t.id) as order_count,sum(t.bet_money) as bet_money,sum(t.combination_count) as combination_count,sum(t.combination_money) as combination_money,SUM(t.profit_money) as profit_money from tb_lottery_order t,tb_lottery_room r");
		sqlBuffer.append(" where (t.`status` = 2 or t.`status` = 4) and t.lottery_room_id = r.id");
		sqlBuffer.append(" and r.lottery_hall_id =").append(lotteryHall.getId());
		sqlBuffer.append(" and t.create_time >=str_to_date('").append(DateTimeTool.queryStartDate(totalDate)).append("','%Y-%m-%d %H:%i:%s')");
		sqlBuffer.append(" and t.create_time <=str_to_date('").append(DateTimeTool.queryEndDate(totalDate)).append("','%Y-%m-%d %H:%i:%s')");
		sqlBuffer.append(" GROUP BY t.user_id");
		
		//System.out.println(sqlBuffer.toString());
		List<Object[]> list = this.getSession().createSQLQuery(sqlBuffer.toString()).list();
		for (Object[] objects : list) {
			Integer userId = (Integer)objects[0];
			Integer orderCount = ((BigInteger)objects[1]).intValue();
			BigDecimal betMoney = objects[2]!=null?(BigDecimal)objects[2]:new BigDecimal(0);
			Integer combinationCount = objects[3]!=null?((BigDecimal)objects[3]).intValue():0;
			BigDecimal combinationMoney = objects[4]!=null?(BigDecimal)objects[4]:new BigDecimal(0);
			BigDecimal profitMoney = objects[5]!=null?(BigDecimal)objects[5]:new BigDecimal(0);
			BigDecimal combinationRatio = combinationMoney.divide(betMoney,2,BigDecimal.ROUND_HALF_UP);//组合占总投注金额比例（组合比）
			//System.out.println(userId + ":" + profitMoney);
			
			if(profitMoney.compareTo(new BigDecimal(0))<0) {
				profitMoney = profitMoney.multiply(new BigDecimal(-1));
				LotteryWaterConfig lotteryWaterConfig = lotteryWaterConfigService.findLotteryWaterConfig(profitMoney, lotteryHall.getId());
				
				//判断是否满足回水条件：金额、下注笔数、组合笔数、组合比
				//System.out.println(lotteryWaterConfig.getOrderCount()+"-"+orderCount);
				//System.out.println(lotteryWaterConfig.getCombinationCount()+"-"+combinationCount);
				//System.out.println(lotteryWaterConfig.getCombinationRatio()+"-"+combinationRatio);
				if(lotteryWaterConfig!=null && orderCount>=lotteryWaterConfig.getOrderCount() && 
						combinationCount >= lotteryWaterConfig.getCombinationCount() && combinationRatio.compareTo(lotteryWaterConfig.getCombinationRatio()) >=0) {
					
					LotteryWaterRecord lotteryWaterRecord = this.getLotteryWaterRecord(userId, totalDate, lotteryHall.getId());
					if(lotteryWaterRecord==null) {
						User user = userService.find(userId);
						BigDecimal backWaterMoney = profitMoney.multiply(lotteryWaterConfig.getBackWaterRatio()).setScale(0, BigDecimal.ROUND_HALF_UP);
						String remark = "["+lotteryHall.getLotteryType().getLotteryName()+"-"+lotteryHall.getTitle()+"]"+user.getNickName()+"结算"+totalDate+"回水："+backWaterMoney;
						
						if(backWaterMoney.compareTo(new BigDecimal(0))>0) {
							/**1、生成回水记录*/
							lotteryWaterRecord = new LotteryWaterRecord();
							lotteryWaterRecord.setLotteryHall(lotteryHall);
							lotteryWaterRecord.setTotalDate(DateTimeTool.dateFormat("yyyy-MM-dd", totalDate));//统计日期
							lotteryWaterRecord.setBackWaterMoney(backWaterMoney);
							lotteryWaterRecord.setUser(user);
							this.save(lotteryWaterRecord);
							
							/**2、变更账号信息*/
							user.setBalance(user.getBalance().add(backWaterMoney));
							userService.update(user);

							/**3、生成资金明细*/
							TransRecord tr = new TransRecord();
							tr.setUser(user);
							tr.setCreateTime(new Date());
							tr.setTransCategory(8);//交易类型（1充值、2提现、3抽奖、4投注、5撤单、6赠送、7中奖、8回水、9调账）
							tr.setTransAmount(backWaterMoney);
							tr.setEndBalance(user.getBalance());
							tr.setTransLotteryAmount(new BigDecimal(0));
							tr.setEndLotteryBalance(user.getLotteryBalance());
							tr.setFlag(1);
							tr.setRemark(remark);
							transRecordService.save(tr);
							
							/**4、生成统计信息*/
							LotteryDailyOrderTotal lotteryDailyOrderTotal = lotteryDailyOrderTotalService.getLotteryDailyOrderTotal(totalDate,user.getId());
							if(lotteryDailyOrderTotal==null) {
								lotteryDailyOrderTotal = new LotteryDailyOrderTotal();
								
								lotteryDailyOrderTotal.setUser(user);//玩家帐号
								lotteryDailyOrderTotal.setTotalDate(DateTimeTool.dateFormat("yyyy-MM-dd", totalDate));//统计日期
								lotteryDailyOrderTotal.setBetMoney(new BigDecimal(0));//流水金额
								lotteryDailyOrderTotal.setProfitMoney(new BigDecimal(0));//盈亏金额
								lotteryDailyOrderTotal.setBackWaterMoney(backWaterMoney);//回水金额
								lotteryDailyOrderTotal.setRechargeMoney(new BigDecimal(0));//充值金额
								lotteryDailyOrderTotal.setWithdrawMoney(new BigDecimal(0));//提现金额
								lotteryDailyOrderTotal.setWinMoney(new BigDecimal(0));//中奖金额
								lotteryDailyOrderTotal.setExpandUserNum(0);//拓展玩家数
								lotteryDailyOrderTotal.setBalance(user.getBalance());
								lotteryDailyOrderTotal.setLotteryBalance(user.getLotteryBalance());//赠送彩金统计
								lotteryDailyOrderTotalService.save(lotteryDailyOrderTotal);
							}else{
								lotteryDailyOrderTotal.setBalance(user.getBalance());
								lotteryDailyOrderTotal.setLotteryBalance(user.getLotteryBalance());//赠送彩金统计
								lotteryDailyOrderTotal.setBackWaterMoney(lotteryDailyOrderTotal.getBackWaterMoney().add(backWaterMoney));
								lotteryDailyOrderTotalService.update(lotteryDailyOrderTotal);
							}

							LogUtil.info(remark);
						}
					}
				}
			}
		}
	}

}
