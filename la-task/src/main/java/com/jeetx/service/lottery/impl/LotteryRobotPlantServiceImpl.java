package com.jeetx.service.lottery.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryRobotPlant;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryRobotPlantService;

@Service
@Transactional
public class LotteryRobotPlantServiceImpl extends DaoSupport<LotteryRobotPlant> implements LotteryRobotPlantService {
	
	public Long getLotteryRobotPlantCount() {
		long counts = (long)this.getSession().createQuery("select count(o.id) from LotteryRobotPlant o ").uniqueResult();
		return counts;
	}
}
