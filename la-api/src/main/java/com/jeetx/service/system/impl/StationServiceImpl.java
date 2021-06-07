package com.jeetx.service.system.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.base.Advert;
import com.jeetx.bean.base.AppVersion;
import com.jeetx.bean.base.SiteDomain;
import com.jeetx.bean.base.WithdrawConfig;
import com.jeetx.bean.lottery.LotteryActivity;
import com.jeetx.bean.lottery.LotteryHall;
import com.jeetx.bean.lottery.LotteryRobotPlantConfig;
import com.jeetx.bean.lottery.LotteryRoom;
import com.jeetx.bean.lottery.LotteryRule;
import com.jeetx.bean.lottery.LotteryRuleDefault;
import com.jeetx.bean.lottery.StationLotteryType;
import com.jeetx.bean.member.User;
import com.jeetx.bean.system.Station;
import com.jeetx.bean.system.StationConfig;
import com.jeetx.bean.system.SystemUser;
import com.jeetx.common.constant.Globals;
import com.jeetx.common.exception.BusinessException;
import com.jeetx.controller.api.ApiUtil;
import com.jeetx.service.base.AdvertService;
import com.jeetx.service.base.AppVersionService;
import com.jeetx.service.base.SiteDomainService;
import com.jeetx.service.base.WithdrawConfigService;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryActivityService;
import com.jeetx.service.lottery.LotteryHallService;
import com.jeetx.service.lottery.LotteryRobotPlantConfigService;
import com.jeetx.service.lottery.LotteryRoomService;
import com.jeetx.service.lottery.LotteryRuleDefaultService;
import com.jeetx.service.lottery.LotteryRuleService;
import com.jeetx.service.lottery.StationLotteryTypeService;
import com.jeetx.service.member.UserService;
import com.jeetx.service.system.StationConfigService;
import com.jeetx.service.system.StationService;
import com.jeetx.service.system.SystemUserService;
import com.jeetx.util.IpUtil;
import com.jeetx.util.MD5Util;

@Service
@Transactional
public class StationServiceImpl extends DaoSupport<Station> implements StationService {

	@Autowired LotteryActivityService lotteryActivityService;
	@Autowired WithdrawConfigService withdrawConfigService;
	@Autowired StationConfigService stationConfigService;
	@Autowired LotteryRoomService lotteryRoomService;
	@Autowired LotteryHallService lotteryHallService;
	@Autowired LotteryRuleDefaultService lotteryRuleDefaultService;
	@Autowired LotteryRuleService lotteryRuleService;
	@Autowired LotteryRobotPlantConfigService lotteryRobotPlantConfigService;
	@Autowired SiteDomainService siteDomainService;
	@Autowired AppVersionService appVersionService;
	@Autowired AdvertService advertService;
	@Autowired SystemUserService systemUserService;
	@Autowired StationLotteryTypeService stationLotteryTypeService;
	@Autowired UserService userService;
	
	public Integer getStationId(Integer stationId,String referer) {
		if(stationId == null) {
			//System.out.println(referer);
			if(referer == null) {
				throw new BusinessException(ApiUtil.getErrorCode("108"));
			}
			
			referer = referer.replace("http://", "");
			String stationDomain = referer.substring(0, referer.indexOf("/"));
			stationDomain = "http://".concat(stationDomain);
			
			//System.out.println(stationDomain);
			Station station = this.getStationByEntryDomain(stationDomain);
			if(station == null) {
				throw new BusinessException(ApiUtil.getErrorCode("108"));
			}
			
			if(station.getEffectiveTime() != null && new Date().after(station.getEffectiveTime())) {
				throw new BusinessException(ApiUtil.getErrorCode("163"));
			}
			
			stationId = station.getId();
			//System.out.println(stationDomain + ":" + stationId);
		}

		return stationId;
	}
	
	
	public Station getStationByEntryDomain(String entryDomain) {
		List<Station> list = this.getSession().createQuery("from Station where entryDomain = ? ").setParameter(0, entryDomain).list();
		if(!list.isEmpty()&&list.size()>=0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	public Station getStation(String stationName) {
		List<Station> list = this.getSession().createQuery("from Station where stationName = ? ").setParameter(0, stationName).list();
		if(!list.isEmpty()&&list.size()>=0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void createStation(String stationName,String stationDomain,String entryDomain,String imageDomain,String mqDomain) throws Exception {
		Station station = this.getStation(stationName);
		if(station != null) {
			throw new BusinessException(ApiUtil.getErrorCode("109"));
		}
		
		//1创建站点
		station = new Station();
		station.setCreateTime(new Date());
		station.setStationDomain(stationDomain);
		station.setStationName(stationName);
		station.setImageDomain(imageDomain);
		station.setMqDomain(mqDomain);
		station.setEntryDomain(entryDomain);
		station.setStatus(1);
		this.save(station);

		//2初始化活动管理
		List<LotteryActivity> lotteryActivityList = lotteryActivityService.getLotteryActivityByStationId(Globals.GLOBALS_STATION_ID);
		for (LotteryActivity lotteryActivity : lotteryActivityList) {
			LotteryActivity newLotteryActivity = new LotteryActivity();
			BeanUtils.copyProperties(lotteryActivity, newLotteryActivity);
			newLotteryActivity.setStation(station);
			newLotteryActivity.setId(null);

			lotteryActivityService.save(newLotteryActivity);
		}
		
		//3初始化提现配置信息表数据
		WithdrawConfig withdrawConfig = withdrawConfigService.getWithdrawConfigByStationId(Globals.GLOBALS_STATION_ID);
		if(withdrawConfig != null) {
			WithdrawConfig newWithdrawConfig = new WithdrawConfig();
			BeanUtils.copyProperties(withdrawConfig, newWithdrawConfig);
			newWithdrawConfig.setStation(station);
			newWithdrawConfig.setId(null);
			
			withdrawConfigService.save(newWithdrawConfig);
		}
		
		//4分站点配置数据表初始化
		List<StationConfig> stationConfigList = stationConfigService.getListByStationId(Globals.GLOBALS_STATION_ID);
		for (StationConfig stationConfig : stationConfigList) {
			StationConfig newStationConfig = new StationConfig();
			BeanUtils.copyProperties(stationConfig, newStationConfig);
			newStationConfig.setStation(station);
			newStationConfig.setId(null);
			
			stationConfigService.save(newStationConfig);
		}
		
		//5初始化大厅
		List<LotteryHall> LotteryHallList = lotteryHallService.getLotteryHallByStationId(Globals.GLOBALS_STATION_ID);
		for (LotteryHall lotteryHall : LotteryHallList) {
			LotteryHall newLotteryHall = new LotteryHall();
			BeanUtils.copyProperties(lotteryHall, newLotteryHall);
			newLotteryHall.setStation(station);
			newLotteryHall.setId(null);
			
			lotteryHallService.save(newLotteryHall);
			
			//6初始化大厅赔率
			List<LotteryRule> lotteryRuleDefaultList = lotteryRuleService.getLotteryRuleList(lotteryHall.getId(), Globals.GLOBALS_STATION_ID);
			for (LotteryRule lotteryRuleDefault : lotteryRuleDefaultList) {
				LotteryRule lotteryRule = new LotteryRule();
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
			
//			List<LotteryRuleDefault> lotteryRuleDefaultList = lotteryRuleDefaultService.getLotteryRuleListByLotteryType(newLotteryHall.getLotteryType().getId());
//			for (LotteryRuleDefault lotteryRuleDefault : lotteryRuleDefaultList) {
//				LotteryRule lotteryRule = new LotteryRule();
//				lotteryRule.setStation(station);
//				lotteryRule.setLotteryHall(newLotteryHall);
//				
//				lotteryRule.setLotteryType(lotteryRuleDefault.getLotteryType());
//				lotteryRule.setParamName(lotteryRuleDefault.getParamName());
//				lotteryRule.setParamValues(lotteryRuleDefault.getParamValues());
//				lotteryRule.setRemarks(lotteryRuleDefault.getRemarks());
//				lotteryRule.setRuleType(lotteryRuleDefault.getRuleType());
//				lotteryRule.setRuleRegular(lotteryRuleDefault.getRuleRegular());
//	
//				lotteryRuleService.save(lotteryRule);
//			}

			//7初始化房间
			List<LotteryRoom> lotteryRoomList = lotteryRoomService.getLotteryRoomListByStationId2HallId(lotteryHall.getId(), Globals.GLOBALS_STATION_ID);
			for (LotteryRoom lotteryRoom : lotteryRoomList) {
				LotteryRoom newLotteryRoom = new LotteryRoom();
				BeanUtils.copyProperties(lotteryRoom, newLotteryRoom);
				newLotteryRoom.setStation(station);
				newLotteryRoom.setLotteryHall(newLotteryHall);
				newLotteryRoom.setId(null);
				
				lotteryRoomService.save(newLotteryRoom);
				
				//8初始化房间假人
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
		
		//9初始化APP版本配置
		List<AppVersion> appVersionList = appVersionService.getAppVersionByStationId(Globals.GLOBALS_STATION_ID);
		for (AppVersion appVersion : appVersionList) {
			AppVersion newAppVersion = new AppVersion();
			BeanUtils.copyProperties(appVersion, newAppVersion);
			newAppVersion.setStation(station);
			newAppVersion.setDownloadLink("#");
			newAppVersion.setVersionCode("8.0.0");
			newAppVersion.setId(null);
			
			appVersionService.save(newAppVersion);
		}
		
		//10初始化线路
		List<SiteDomain> siteDomainList = siteDomainService.getSiteDomainByStationId(Globals.GLOBALS_STATION_ID);
		for (SiteDomain siteDomain : siteDomainList) {
			SiteDomain newsiteDomain = new SiteDomain();
			BeanUtils.copyProperties(siteDomain, newsiteDomain);
			newsiteDomain.setStation(station);
			newsiteDomain.setSiteDomain("#");
			newsiteDomain.setId(null);
			
			siteDomainService.save(newsiteDomain);
		}
		
		//11初始化轮播图
		Advert advert = new Advert();
		advert.setAdHttp("#");
		advert.setAdImg("upload/c_banner1.png");
		advert.setAdTitle("即将上线");
		advert.setCreateTime(new Date());
		advert.setSortNum(1);
		advert.setStatus(1);
		advert.setAdvertType(2);
		advert.setStation(station);
		advertService.save(advert);
		
		//12站点管理员
		SystemUser systemUser = new SystemUser();
		systemUser.setUsername("administrator");
		systemUser.setPassword(MD5Util.MD5Encode("123456", "UTF-8"));
		systemUser.setAuthorize("4");
		systemUser.setNickName("站点管理员");
		systemUser.setStatus(1);
//		systemUser.setDesc("站点"+station.getStationName()+"管理员");
		systemUser.setCreateTime(new Date());
		systemUser.setStation(station);
		systemUserService.save(systemUser);
		
		//13初始化游戏类型
		List<StationLotteryType> stationLotteryTypeList = stationLotteryTypeService.getStationLotteryTypeByStationId(Globals.GLOBALS_STATION_ID);
		for (StationLotteryType stationLotteryType : stationLotteryTypeList) {
			StationLotteryType newStationLotteryType = new StationLotteryType();
			BeanUtils.copyProperties(stationLotteryType, newStationLotteryType);
			newStationLotteryType.setStation(station);
			newStationLotteryType.setId(null);
			
			stationLotteryTypeService.save(newStationLotteryType);
		}
		
		//14初始化默认代理
		String password = MD5Util.MD5Encode(MD5Util.MD5Encode("123456", "utf-8").toLowerCase(),"utf-8").toLowerCase();
		User agent = userService.register(null, "默认代理",null,null,null,2, password, null, station.getId(),null);		
	
		//15初始化默认拓展
		if(agent != null) {
			userService.register(agent.getId(), "默认拓展",null,null,null,3, password, null, station.getId(),null);
		}
	}
}
