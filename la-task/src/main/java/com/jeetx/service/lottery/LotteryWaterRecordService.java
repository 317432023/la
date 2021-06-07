package com.jeetx.service.lottery;

import com.jeetx.bean.lottery.LotteryHall;
import com.jeetx.bean.lottery.LotteryWaterRecord;
import com.jeetx.service.dao.DAO;

public interface LotteryWaterRecordService extends DAO<LotteryWaterRecord> {
	public LotteryWaterRecord getLotteryWaterRecord(Integer userId,String totalDate,Integer lotteryHallId);
	public void createLotteryWaterRecord(String totalDate,LotteryHall lotteryHall) throws Exception;
}
