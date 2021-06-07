package com.jeetx.service.lottery.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryOrder;
import com.jeetx.bean.lottery.LotteryRobotPlantConfig;
import com.jeetx.bean.lottery.LotteryRoom;
import com.jeetx.common.constant.Globals;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryRobotPlantConfigService;
import com.jeetx.service.lottery.LotteryRoomService;

@Service
@Transactional
public class LotteryRobotPlantConfigServiceImpl extends DaoSupport<LotteryRobotPlantConfig> implements LotteryRobotPlantConfigService {
	@Autowired LotteryRoomService lotteryRoomService;
	
	@SuppressWarnings("unchecked")
	public LotteryRobotPlantConfig findLotteryRobotPlantConfig(Integer lotteryRoomId) {
		List<LotteryRobotPlantConfig> list = this.getSession().createQuery("from LotteryRobotPlantConfig o where o.lotteryRoom.id = ? ").setParameter(0, lotteryRoomId).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (LotteryRobotPlantConfig) list.get(0);
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void initRobotPlantConfig() throws Exception {
		List<LotteryRoom> lotteryRoomList = lotteryRoomService.findByHql("from LotteryRoom");
		for (LotteryRoom lotteryRoom : lotteryRoomList) {
			if(lotteryRoom != null && lotteryRoom.getStatus() !=0) {
				LotteryRobotPlantConfig lotteryRobotPlantConfig = this.findLotteryRobotPlantConfig(lotteryRoom.getId());
				if(lotteryRobotPlantConfig == null) {
					lotteryRobotPlantConfig = new LotteryRobotPlantConfig();
					lotteryRobotPlantConfig.setCreateTime(new Date());
					lotteryRobotPlantConfig.setStatus(1);
					lotteryRobotPlantConfig.setRandomCount(20);
					lotteryRobotPlantConfig.setMinMoney(100);
					lotteryRobotPlantConfig.setMaxMoney(1000);
					lotteryRobotPlantConfig.setLotteryHall(lotteryRoom.getLotteryHall());
					lotteryRobotPlantConfig.setLotteryRoom(lotteryRoom);

					this.save(lotteryRobotPlantConfig);
					System.out.println(lotteryRoom.getLotteryHall().getLotteryType().getLotteryName().concat("-")
							.concat(lotteryRoom.getLotteryHall().getTitle()).concat("-")
							.concat(lotteryRoom.getTitle()));
				}
			}
		}
	}

}
