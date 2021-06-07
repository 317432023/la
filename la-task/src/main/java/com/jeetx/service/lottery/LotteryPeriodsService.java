package com.jeetx.service.lottery;

import com.jeetx.bean.lottery.LotteryPeriods;
import com.jeetx.bean.lottery.LotteryRoom;
import com.jeetx.service.dao.DAO;
import com.jeetx.timer.lotteryTask.LotteryDTO;

public interface LotteryPeriodsService extends DAO<LotteryPeriods> {
	public LotteryPeriods currentFinishLotteryPeriods(Integer lotteryType);
	public LotteryPeriods findCurrentLotteryPeriods(Integer lotteryType);
	public LotteryPeriods findTopLotteryPeriods(Integer lotteryType);
	public LotteryPeriods findLotteryPeriodsByDate(Integer lotteryType,String date);
	public LotteryPeriods findLotteryPeriodsByPeriods(Integer lotteryType,String lotteryPeriods);
	public boolean checkExistJND28LotteryPeriods(String beginDate,String endDate) ;
	public String getCurrentPeriods(Integer lotteryType,String lotteryTitle,String openProvider);
	public LotteryPeriods createLotteryPeriods(Integer lotteryType,String nextPeriods,String openTime) throws Exception;
	public void openLotteryPeriods(Integer lotteryType,LotteryDTO lotteryDTO,boolean isCreateNext) throws Exception;
	public void sendMemInfoBeforeCloseMessage(LotteryRoom lotteryRoom,LotteryPeriods lotteryPeriods) throws Exception;
	public void sendMemCloseInfoMessage(LotteryRoom lotteryRoom,LotteryPeriods currentPeriodsLottery) throws Exception;
}
