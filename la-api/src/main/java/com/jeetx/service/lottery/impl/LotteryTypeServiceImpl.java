package com.jeetx.service.lottery.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryHall;
import com.jeetx.bean.lottery.LotteryRobotPlantConfig;
import com.jeetx.bean.lottery.LotteryRoom;
import com.jeetx.bean.lottery.LotteryRule;
import com.jeetx.bean.lottery.LotteryRuleDefault;
import com.jeetx.bean.lottery.LotteryType;
import com.jeetx.bean.lottery.StationLotteryType;
import com.jeetx.bean.system.Station;
import com.jeetx.common.constant.Globals;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryHallService;
import com.jeetx.service.lottery.LotteryRobotPlantConfigService;
import com.jeetx.service.lottery.LotteryRoomService;
import com.jeetx.service.lottery.LotteryRuleDefaultService;
import com.jeetx.service.lottery.LotteryRuleService;
import com.jeetx.service.lottery.LotteryTypeService;
import com.jeetx.service.lottery.StationLotteryTypeService;
import com.jeetx.service.system.StationService;

@Service
@Transactional
public class LotteryTypeServiceImpl extends DaoSupport<LotteryType> implements LotteryTypeService {

	@Autowired StationService stationService;
	@Autowired LotteryRoomService lotteryRoomService;
	@Autowired LotteryHallService lotteryHallService;
	@Autowired LotteryRuleDefaultService lotteryRuleDefaultService;
	@Autowired LotteryRuleService lotteryRuleService;
	@Autowired LotteryRobotPlantConfigService lotteryRobotPlantConfigService;
	@Autowired StationLotteryTypeService stationLotteryTypeService;
	@Autowired LotteryTypeService lotteryTypeService;
	
	/*** 88彩新增新西兰28游戏*/
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void initLotteryType(Integer lotteryTypeId,Integer stationId) throws Exception {
	
		Station station = stationService.find(stationId);
		LotteryType lotteryType = lotteryTypeService.find(6);

		//1初始化大厅
		List<LotteryHall> LotteryHallList = lotteryHallService.getLotteryHallList(1, Globals.GLOBALS_STATION_ID);
		for (LotteryHall lotteryHall : LotteryHallList) {
			LotteryHall newLotteryHall = lotteryHallService.getLotteryHall(lotteryType.getId(), station.getId(), lotteryHall.getTitle());
			if(newLotteryHall == null) {
				newLotteryHall = new LotteryHall();
				BeanUtils.copyProperties(lotteryHall, newLotteryHall);
				newLotteryHall.setLotteryType(lotteryType);
				newLotteryHall.setStation(station);
				newLotteryHall.setRuleRemarks(lotteryType.getRemarks());
				newLotteryHall.setId(null);
				
				lotteryHallService.save(newLotteryHall);
			}else {
				newLotteryHall.setRuleRemarks(lotteryType.getRemarks());
				lotteryHallService.update(newLotteryHall);
			}

			//2初始化大厅赔率
			List<LotteryRuleDefault> lotteryRuleDefaultList = lotteryRuleDefaultService.getLotteryRuleListByLotteryType(newLotteryHall.getLotteryType().getId());
			for (LotteryRuleDefault lotteryRuleDefault : lotteryRuleDefaultList) {
				LotteryRule lotteryRule =  lotteryRuleService.getLotteryRule(newLotteryHall.getId(), lotteryRuleDefault.getParamName(), station.getId());
				if(lotteryRule == null) {
					lotteryRule = new LotteryRule();
					lotteryRule.setStation(station);
					lotteryRule.setLotteryHall(newLotteryHall);
					lotteryRule.setLotteryType(lotteryRuleDefault.getLotteryType());
					lotteryRule.setParamName(lotteryRuleDefault.getParamName());
					lotteryRule.setParamValues(lotteryRuleDefault.getParamValues());
					lotteryRule.setRemarks(lotteryRuleDefault.getRemarks());
					lotteryRule.setRuleType(lotteryRuleDefault.getRuleType());
					lotteryRule.setRuleRegular(lotteryRuleDefault.getRuleRegular());
		
					lotteryRuleService.save(lotteryRule);
				}
			}

			//3初始化房间
			List<LotteryRoom> lotteryRoomList = lotteryRoomService.getLotteryRoomListByStationId2HallId(lotteryHall.getId(), Globals.GLOBALS_STATION_ID);
			for (LotteryRoom lotteryRoom : lotteryRoomList) {
				LotteryRoom newLotteryRoom = lotteryRoomService.getLotteryRoomByStationId2HallId(newLotteryHall.getId(), station.getId(), lotteryRoom.getTitle());
				if(newLotteryRoom == null) {
					newLotteryRoom = new LotteryRoom();
					BeanUtils.copyProperties(lotteryRoom, newLotteryRoom);
					newLotteryRoom.setLotteryType(lotteryType);
					newLotteryRoom.setStation(station);
					newLotteryRoom.setLotteryHall(newLotteryHall);
					newLotteryRoom.setIconImg(null);
					newLotteryRoom.setId(null);
					lotteryRoomService.save(newLotteryRoom);

					//4初始化房间假人
					LotteryRobotPlantConfig lotteryRobotPlantConfig = new LotteryRobotPlantConfig();
					lotteryRobotPlantConfig.setCreateTime(new Date());
					lotteryRobotPlantConfig.setStatus(1);
					lotteryRobotPlantConfig.setRandomCount(20);
					lotteryRobotPlantConfig.setMinMoney(100);
					lotteryRobotPlantConfig.setMaxMoney(1000);
					lotteryRobotPlantConfig.setLotteryHall(newLotteryHall);
					lotteryRobotPlantConfig.setLotteryRoom(newLotteryRoom);
					lotteryRobotPlantConfigService.save(lotteryRobotPlantConfig);
				}
			}
		}

		//5初始化游戏类型
		StationLotteryType stationLotteryType = stationLotteryTypeService.getStationLotteryTypeByLotteryType(lotteryType.getId(), station.getId());
		if(stationLotteryType == null) {
			stationLotteryType = new StationLotteryType();
			stationLotteryType.setLotteryName(lotteryType.getLotteryName());
			stationLotteryType.setPicLink(null);
			stationLotteryType.setSortNum(6);
			stationLotteryType.setStatus(1);//状态(1启用、0禁用、2隐藏)
			stationLotteryType.setLotteryType(lotteryType);//彩票类型
			stationLotteryType.setStation(station);

			stationLotteryTypeService.save(stationLotteryType);
		}
	}
}
