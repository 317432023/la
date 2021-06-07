package com.jeetx.service.lottery;

import java.util.List;

import com.jeetx.bean.lottery.LotteryPeriods;
import com.jeetx.bean.lottery.LotteryRobotPlant;
import com.jeetx.bean.lottery.LotteryRobotPlantConfig;
import com.jeetx.bean.lottery.LotteryRoom;
import com.jeetx.bean.lottery.LotteryRule;
import com.jeetx.service.dao.DAO;

public interface LotteryRobotPlantConfigService extends DAO<LotteryRobotPlantConfig> {
	public LotteryRobotPlantConfig findByLotteryRoomId(Integer lotteryRoomId);
	public String submitPlantOrder(LotteryRoom lotteryRoom,LotteryPeriods lotteryPeriods,LotteryRobotPlantConfig lotteryRobotPlantConfig,
			LotteryRobotPlant lotteryRobotPlant,List<LotteryRule> orderPlanList) throws Exception;
}
