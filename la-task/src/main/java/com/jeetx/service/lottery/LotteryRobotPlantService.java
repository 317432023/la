package com.jeetx.service.lottery;

import com.jeetx.bean.lottery.LotteryRobotPlant;
import com.jeetx.service.dao.DAO;

public interface LotteryRobotPlantService extends DAO<LotteryRobotPlant> {
	public Long getLotteryRobotPlantCount();
}
