package com.jeetx.service.lottery;

import com.jeetx.bean.lottery.LotteryRobotPlantConfig;
import com.jeetx.service.dao.DAO;

public interface LotteryRobotPlantConfigService extends DAO<LotteryRobotPlantConfig> {
	public LotteryRobotPlantConfig findLotteryRobotPlantConfig(Integer lotteryRoomId);
	public void initRobotPlantConfig() throws Exception;
}
