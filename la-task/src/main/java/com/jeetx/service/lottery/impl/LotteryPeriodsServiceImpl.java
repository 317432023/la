package com.jeetx.service.lottery.impl;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryNumberConfig;
import com.jeetx.bean.lottery.LotteryPeriods;
import com.jeetx.bean.lottery.LotteryRoom;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryNumberConfigService;
import com.jeetx.service.lottery.LotteryPeriodsService;
import com.jeetx.service.lottery.LotteryRoomMessageService;
import com.jeetx.service.lottery.LotteryTypeService;
import com.jeetx.service.system.SystemConfigService;
import com.jeetx.timer.lotteryTask.FetchResult;
import com.jeetx.timer.lotteryTask.LotteryDTO;
import com.jeetx.util.DateTimeTool;
import com.jeetx.util.LogUtil;

@Service
@Transactional
public class LotteryPeriodsServiceImpl extends DaoSupport<LotteryPeriods> implements LotteryPeriodsService {

	@Autowired SystemConfigService systemConfigService;
	@Autowired LotteryTypeService lotteryTypeService;
	@Autowired LotteryNumberConfigService lotteryNumberConfigService;
	@Autowired LotteryRoomMessageService lotteryRoomMessageService;
	
	@Value("${beforeCloseLotteryMessage}")
	private String beforeCloseLotteryMessage;
	
	@Value("${closeLotteryMessage}")
	private String closeLotteryMessage;
	
	@SuppressWarnings("unchecked")
	public LotteryPeriods currentFinishLotteryPeriods(Integer lotteryType) {
		List<LotteryPeriods> list = this.getSession().createQuery("from LotteryPeriods o where o.lotteryType.id=? and o.status = 3 ORDER BY o.lotteryOpenTime DESC ")
				.setParameter(0, lotteryType).setFirstResult(0).setMaxResults(1).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (LotteryPeriods) list.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public LotteryPeriods findCurrentLotteryPeriods(Integer lotteryType) {
		String date = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", new Date());
		List<LotteryPeriods> list = this.getSession().createQuery("from LotteryPeriods o where o.lotteryType.id=? and o.lotteryBeginTime <=str_to_date(?,'%Y-%m-%d %H:%i:%s') and o.lotteryOpenTime >=str_to_date(?,'%Y-%m-%d %H:%i:%s')")
				.setParameter(0, lotteryType).setParameter(1, date).setParameter(2, date).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (LotteryPeriods) list.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public LotteryPeriods findTopLotteryPeriods(Integer lotteryType) {
		List<LotteryPeriods> list = this.getSession().createQuery("from LotteryPeriods o where o.lotteryType.id=? order by o.lotteryOpenTime desc ").setParameter(0, lotteryType)
				.setFirstResult(0).setMaxResults(1).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (LotteryPeriods) list.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public LotteryPeriods findLotteryPeriodsByDate(Integer lotteryType,String date) {
		List<LotteryPeriods> list = this.getSession().createQuery("from LotteryPeriods o where o.lotteryType.id=? and o.lotteryBeginTime <=str_to_date(?,'%Y-%m-%d %H:%i:%s') and o.lotteryOpenTime >=str_to_date(?,'%Y-%m-%d %H:%i:%s')")
				.setParameter(0, lotteryType).setParameter(1, date).setParameter(2, date).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (LotteryPeriods) list.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public LotteryPeriods findLotteryPeriodsByPeriods(Integer lotteryType,String lotteryPeriods) {
		List<LotteryPeriods> list = this.getSession().createQuery("from LotteryPeriods o where o.lotteryType.id=? and o.lotteryPeriods = ?")
				.setParameter(0, lotteryType).setParameter(1, lotteryPeriods).list();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return (LotteryPeriods) list.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean checkExistJND28LotteryPeriods(String beginDate,String endDate) {
		List<LotteryPeriods> list = this.getSession().createQuery("from LotteryPeriods o where o.lotteryType.id = 2 and o.status = 3 and o.lotteryOpenTime >str_to_date(?,'%Y-%m-%d %H:%i:%s') and o.lotteryOpenTime <str_to_date(?,'%Y-%m-%d %H:%i:%s')")
			.setParameter(0, beginDate).setParameter(1, endDate).list();
		if (list == null || list.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	/**获取当前期数**/
	public String getCurrentPeriods(Integer lotteryType,String lotteryTitle,String openProvider) {
		String currentPeriods = null;
		int duration = 0;
		String openTime = null;
		LotteryPeriods lastPeriodsLottery = null;
		try{
			switch (lotteryType) {
			case 1:
				duration = 300;
				break;
			case 2:
				duration = 210;
				break;
			case 3://每日发行179期，13:00~04:00每5分钟开奖
				duration = 300;
				break;
			case 4:
				duration = 1200;
				break;
			case 5:
				duration = 1200;
				break;
			case 6:
				duration = 1200;
				break;
			}
			
			Date lastBeginDate = DateTimeTool.addTime(new Date(), -duration);
			lastPeriodsLottery = this.findLotteryPeriodsByDate(lotteryType, DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", lastBeginDate));
			if(lastPeriodsLottery != null){
				if(lastPeriodsLottery.getStatus() != 3 && new Date().compareTo(lastPeriodsLottery.getLotteryOpenTime())>=0){
					currentPeriods = lastPeriodsLottery.getLotteryPeriods();
					LogUtil.info("数据库获取"+lotteryTitle+"当前期数:"+currentPeriods);
				}
			}else{
				LotteryDTO lotteryDTO = this.getCurrentLotteryDTO(openProvider, lotteryType);

		    	if(lotteryDTO!=null && lotteryDTO.isBool()){
					currentPeriods = lotteryDTO.getPeriod();
					openTime = lotteryDTO.getOpenTime();
		    	}

		    	if(currentPeriods != null && openTime != null){
					if(new Date().compareTo(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", openTime))>=0){
						currentPeriods = String.valueOf(Long.valueOf(currentPeriods) + 1);
					}
					LogUtil.info("接口中获取"+lotteryTitle+"当前期数:"+currentPeriods);
					
					if(lotteryType == 5) {
						int nunber = Integer.valueOf(currentPeriods.substring(8, 11));
						if(nunber > 59){
							currentPeriods = DateTimeTool.dateFormat("yyyyMMdd", DateTimeTool.getDaysByDate2Days(-1, DateTimeTool.dateFormat("yyyyMMdd", currentPeriods.substring(0, 8)))).concat("001");
						}
					}else if(lotteryType == 3) {
						int nunber = Integer.valueOf(currentPeriods.substring(8, 11));
						if(nunber > 180){
							currentPeriods = DateTimeTool.dateFormat("yyyyMMdd", DateTimeTool.getDaysByDate2Days(-1, DateTimeTool.dateFormat("yyyyMMdd", currentPeriods.substring(0, 8)))).concat("001");
						}
					}
					this.createLotteryPeriods(lotteryType, currentPeriods, openTime);
		    	}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return currentPeriods;
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public LotteryPeriods createLotteryPeriods(Integer lotteryType,String nextPeriods,String openTime) throws Exception{
		int duration = 0;
		boolean isCreate = true;
		String openTimeFormat = null;
		Date openTimeTemp = null;
		LotteryPeriods nextLotteryPeriods = this.findLotteryPeriodsByPeriods(lotteryType, nextPeriods);
		if(nextLotteryPeriods == null){
			String curTime = null;
			switch (lotteryType) {
			case 1:
				duration = 300;
				openTimeTemp = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", openTime);
				int minutes11 = (openTimeTemp.getMinutes())/10;
				int minutes22 = (openTimeTemp.getMinutes())%10;
				if(minutes22<5) {
					openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd HH:"+minutes11+"0:00", openTimeTemp);
				}else {
					openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd HH:"+minutes11+"5:00", openTimeTemp);
				}
				//openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:00", DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", openTime));
				curTime = DateTimeTool.dateFormat("HH:mm", DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", openTimeFormat));
				if(DateTimeTool.isInTime("23:55-00:00", curTime)){
					isCreate = false;
				}
				break;
			case 2:
				openTimeTemp = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", openTime);
				int prevPeriods = Integer.valueOf(nextPeriods) - 1;
				LotteryPeriods prevLotteryPeriods = this.findLotteryPeriodsByPeriods(lotteryType, String.valueOf(prevPeriods));
				if(prevLotteryPeriods != null) {
					long diff = openTimeTemp.getTime() - prevLotteryPeriods.getLotteryOpenTime().getTime();//这样得到的差值是微秒级别
				    long seconds = diff/1000;
				    if(seconds>=30&&seconds<60) {
				    	openTimeTemp = DateTimeTool.addTime(openTimeTemp, -30);
				    }else if(seconds>=60&&seconds<90) {
				    	openTimeTemp = DateTimeTool.addTime(openTimeTemp, -60);
				    }else if(seconds>=90&&seconds<120) {
				    	openTimeTemp = DateTimeTool.addTime(openTimeTemp, -90);
				    }else if(seconds>=120&&seconds<150) {
				    	openTimeTemp = DateTimeTool.addTime(openTimeTemp, -120);
				    }else if(seconds>=150&&seconds<180) {
				    	openTimeTemp = DateTimeTool.addTime(openTimeTemp, -150);
				    }else if(seconds>=180&&seconds<210) {
				    	openTimeTemp = DateTimeTool.addTime(openTimeTemp, -180);
				    }
				}
				
				duration = 210;
				if(openTimeTemp.getSeconds()<30){
					openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:00", openTimeTemp);
				}else{
					openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:30", openTimeTemp);
				}

				curTime = DateTimeTool.dateFormat("HH:mm", DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", openTimeFormat));
				String jndTimeInterval = systemConfigService.getValueByName("lottery_jnd_time_interval");
				if(StringUtils.isNotBlank(jndTimeInterval)&&"1".equals(jndTimeInterval)) {//夏令时
					if(DateTimeTool.isInTime("19:00-20:00", curTime)){
						isCreate = false;
					}
				}else if(StringUtils.isNotBlank(jndTimeInterval)&&"2".equals(jndTimeInterval)){//冬令时
					if(DateTimeTool.isInTime("20:00-21:00", curTime)){
						isCreate = false;
					} 
				}
				break;
			case 3://幸运飞艇13:00~04:00每5分钟开奖
				duration = 300;
				openTimeTemp = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", openTime);
				minutes11 = (openTimeTemp.getMinutes())/10;
				minutes22 = (openTimeTemp.getMinutes())%10;
				if(minutes22>=4&&minutes22<9) {
					openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd HH:"+minutes11+"4:00", openTimeTemp);
				}else if(minutes22>=0&&minutes22<4){
					if(minutes11 == 0) {
						minutes11 = 6;
					}
					minutes11 = minutes11 - 1;
					openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd HH:"+minutes11+"9:00", openTimeTemp);
				}else if(minutes22>=9){	
					openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd HH:"+minutes11+"9:00", openTimeTemp);
				}else {
					openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", openTimeTemp);
				}
				
				curTime = DateTimeTool.dateFormat("HH:mm", DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", openTimeFormat));
				if(DateTimeTool.isInTime("4:04-13:00", curTime)){
					isCreate = false;
				}
				break;
				
			case 4://每日发行44期，09:10~23:50每20分钟开奖
				duration = 1200;
				openTimeTemp = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", openTime);
				minutes11 = (openTimeTemp.getMinutes())/10;
				minutes22 = (openTimeTemp.getMinutes())%10;
				openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd HH:"+minutes11+"0:00", openTimeTemp);
				
				curTime = DateTimeTool.dateFormat("HH:mm", DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", openTimeFormat));
				if(!DateTimeTool.isInTime("09:10-23:49", curTime)){
					isCreate = false;
				}
				break;
			case 5:
				duration = 1200;
				openTimeTemp = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", openTime);
				minutes11 = (openTimeTemp.getMinutes())/10;
				minutes22 = (openTimeTemp.getMinutes())%10;
				openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd HH:"+minutes11+"0:00", openTimeTemp);

				int nunber = Integer.valueOf(nextPeriods.substring(8, 11));
				if(nunber == 1){
					//nextPeriods = DateTimeTool.dateFormat("yyyyMMdd", DateTimeTool.getDaysByDate2Days(-1, DateTimeTool.dateFormat("yyyyMMdd", nextPeriods.substring(0, 8)))).concat("001");
					openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd 00:10:00", new Date());
				}else {
					curTime = DateTimeTool.dateFormat("HH:mm", DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", openTimeFormat));
					if(!DateTimeTool.isInTime("07:10-23:49", curTime) && !DateTimeTool.isInTime("00:00-03:00", curTime) ){
						isCreate = false;
					}
				}
				break;
			case 6:
				duration = 1200;
				openTimeTemp = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", openTime);
				openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:00", DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", openTime));
				curTime = DateTimeTool.dateFormat("HH:mm", DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", openTimeFormat));
				if(DateTimeTool.isInTime("4:00-5:59", curTime)){
					isCreate = false;
				}
				break;
			}
			
			if(isCreate){
				nextLotteryPeriods = new LotteryPeriods();
				nextLotteryPeriods.setLotteryType(lotteryTypeService.find(lotteryType));
				nextLotteryPeriods.setLotteryPeriods(nextPeriods);////游戏期数
				nextLotteryPeriods.setLotteryBeginTime(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", openTimeFormat));//开始时间
				nextLotteryPeriods.setLotteryOpenTime(DateTimeTool.addTime(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", openTimeFormat), duration));//开奖时间
				nextLotteryPeriods.setStatus(1);//（1投注中 、2开奖中、3已开奖 ）
				this.save(nextLotteryPeriods);
			}
		}
		
		return nextLotteryPeriods;
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void openLotteryPeriods(Integer lotteryType,LotteryDTO lotteryDTO,boolean isCreateNext) throws Exception{
		int duration = 0;
		String openTimeFormat = null;
		String lotteryShowContent = null;
		Date openTimeTemp = null;
		LotteryNumberConfig lotteryNumber = null;
		switch (lotteryType) {
		case 1:
			duration = 300;
			openTimeTemp = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", lotteryDTO.getOpenTime());
			int minutes11 = (openTimeTemp.getMinutes())/10;
			int minutes22 = (openTimeTemp.getMinutes())%10;
			if(minutes22<5) {
				openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd HH:"+minutes11+"0:00", openTimeTemp);
			}else {
				openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd HH:"+minutes11+"5:00", openTimeTemp);
			}
	    	lotteryNumber = lotteryNumberConfigService.findLotteryNumberConfig(lotteryType,lotteryDTO.getLotteryNumber());
	    	lotteryShowContent = lotteryDTO.getOpenContent().concat("=").concat(String.format("%02d", lotteryNumber.getCode())+"(").concat(lotteryNumber.getGroupName())
	    			.concat(StringUtils.isNotBlank(lotteryDTO.getGroupName())?"、".concat(lotteryDTO.getGroupName()):"").concat(")");
			break;
		case 2:
			duration = 210;
			openTimeTemp = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", lotteryDTO.getOpenTime());
			if(openTimeTemp.getSeconds()<30){
				openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:00", openTimeTemp);
			}else{
				openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:30", openTimeTemp);
			}
			lotteryNumber = lotteryNumberConfigService.findLotteryNumberConfig(lotteryType,lotteryDTO.getLotteryNumber());
	    	lotteryShowContent = lotteryDTO.getOpenContent().concat("=").concat(String.format("%02d", lotteryNumber.getCode())+"(").concat(lotteryNumber.getGroupName())
	    			.concat(StringUtils.isNotBlank(lotteryDTO.getGroupName())?"、".concat(lotteryDTO.getGroupName()):"").concat(")");
			break;
		case 3:
			duration = 300;
			openTimeTemp = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", lotteryDTO.getOpenTime());
			minutes11 = (openTimeTemp.getMinutes())/10;
			minutes22 = (openTimeTemp.getMinutes())%10;
			if(minutes22>=4&&minutes22<9) {
				openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd HH:"+minutes11+"4:00", openTimeTemp);
			}else if(minutes22>=0&&minutes22<4){
				if(minutes11 == 0) {
					minutes11 = 6;
				}
				minutes11 = minutes11 - 1;
				openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd HH:"+minutes11+"9:00", openTimeTemp);
			}else if(minutes22>=9){	
				openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd HH:"+minutes11+"9:00", openTimeTemp);
			}else {
				openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", openTimeTemp);
			}
			
			lotteryShowContent = lotteryDTO.getOpenContent();
			break;
			
		case 4:
			duration = 1200;
			openTimeTemp = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", lotteryDTO.getOpenTime());
			minutes11 = (openTimeTemp.getMinutes())/10;
			minutes22 = (openTimeTemp.getMinutes())%10;
			openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd HH:"+minutes11+"0:00", openTimeTemp);
			
			lotteryShowContent = lotteryDTO.getOpenContent();
			break;
		case 5:
			duration = 1200;
			openTimeTemp = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", lotteryDTO.getOpenTime());
			minutes11 = (openTimeTemp.getMinutes())/10;
			minutes22 = (openTimeTemp.getMinutes())%10;
			openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd HH:"+minutes11+"0:00", openTimeTemp);
			
	    	lotteryShowContent = lotteryDTO.getOpenContent();
			break;
		case 6:
			duration = 1200;
			openTimeTemp = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", lotteryDTO.getOpenTime());
			openTimeFormat = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:00", openTimeTemp);

			lotteryNumber = lotteryNumberConfigService.findLotteryNumberConfig(lotteryType,lotteryDTO.getLotteryNumber());
	    	lotteryShowContent = lotteryDTO.getOpenContent().concat("=").concat(String.format("%02d", lotteryNumber.getCode())+"(").concat(lotteryNumber.getGroupName())
	    			.concat(StringUtils.isNotBlank(lotteryDTO.getGroupName())?"、".concat(lotteryDTO.getGroupName()):"").concat(")");
			break; 
		}
		
		//更新开奖记录
		LotteryPeriods lotteryPeriods = this.findLotteryPeriodsByPeriods(lotteryType, lotteryDTO.getPeriod());
		if(lotteryPeriods != null){
			//System.out.println("lotteryPeriods != null");
			if(lotteryPeriods.getStatus()!= 3){
				long diff = DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", lotteryDTO.getOpenTime()).getTime()
						- lotteryPeriods.getLotteryOpenTime().getTime();//这样得到的差值是微秒级别
			    long seconds = diff/1000; 
			    
			    long diff1 = new Date().getTime()- lotteryPeriods.getLotteryOpenTime().getTime();//采集耗时
			    long seconds1 = diff1/1000;
				
				lotteryPeriods.setLotteryDataSource(lotteryDTO.getDataSource().concat("(").concat(seconds+"/"+seconds1+"秒").concat(")"));//数据来源
				lotteryPeriods.setLotterySourceCollectTime(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", lotteryDTO.getOpenTime()));//数据源上的开奖时间
				lotteryPeriods.setLotteryCollectTime(new Date());//采集时间
				lotteryPeriods.setLotteryOpenContent(lotteryDTO.getOpenContent());//开奖号码 如PC28格式：03-06-04；重庆时时彩格式：02+03+05+03+04
				lotteryPeriods.setLotteryOpenNumber(lotteryDTO.getLotteryNumber());
				lotteryPeriods.setStatus(3);//（1投注中 、2封盘、3已开奖 ）
				lotteryPeriods.setLotteryShowContent(lotteryShowContent);
				this.update(lotteryPeriods);
			}
		}else{
			//System.out.println("lotteryPeriods == null");
			lotteryPeriods = new LotteryPeriods();
			lotteryPeriods.setLotteryType(lotteryTypeService.find(lotteryType));
			lotteryPeriods.setLotteryPeriods(lotteryDTO.getPeriod());////游戏期数
			lotteryPeriods.setLotteryBeginTime(DateTimeTool.addTime(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", openTimeFormat), -duration));//开始时间
			lotteryPeriods.setLotteryOpenTime(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", openTimeFormat));//开奖时间
			
			lotteryPeriods.setLotteryDataSource(lotteryDTO.getDataSource());//数据来源
			lotteryPeriods.setLotterySourceCollectTime(DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", lotteryDTO.getOpenTime()));//数据源上的开奖时间
			lotteryPeriods.setLotteryCollectTime(new Date());//采集时间
			lotteryPeriods.setLotteryOpenContent(lotteryDTO.getOpenContent());//开奖号码 如PC28格式：03-06-04；重庆时时彩格式：02+03+05+03+04
			lotteryPeriods.setLotteryOpenNumber(lotteryDTO.getLotteryNumber());
			lotteryPeriods.setStatus(3);//（1投注中 、2开奖中、3已开奖 ）
			lotteryPeriods.setLotteryShowContent(lotteryShowContent);
			this.save(lotteryPeriods);
		}
		
		//开启下一期期数
		if(isCreateNext) {
			String nextPeriods = String.valueOf(Long.valueOf(lotteryDTO.getPeriod()) + 1);
			this.createLotteryPeriods(lotteryType,nextPeriods,lotteryDTO.getOpenTime());
		}
	}
	
	
	/*** 发送封盘倒计时提示信息:【第[开奖期数]期】距离封盘时间还有[剩余秒数]秒，请抓紧时间下注。*/
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void sendMemInfoBeforeCloseMessage(LotteryRoom lotteryRoom,LotteryPeriods lotteryPeriods) throws Exception{
		lotteryRoomMessageService.sendBroadcastMessage(lotteryRoom, lotteryPeriods, beforeCloseLotteryMessage);
	}
	
	/*** 发送封盘线提示信息:【第[开奖期数]期】已封盘，下注结果以系统开奖为准，如有异议，请及时联系客服。*/ 
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void sendMemCloseInfoMessage(LotteryRoom lotteryRoom,LotteryPeriods lotteryPeriods) throws Exception{
		lotteryRoomMessageService.sendBroadcastMessage(lotteryRoom, lotteryPeriods, closeLotteryMessage);
	}
	
	public static void main(String[] args) throws ParseException {
		String currentPeriods = String.valueOf(Long.valueOf("20190306059") + 1);
		System.out.println(DateTimeTool.dateFormat("yyyyMMdd", DateTimeTool.getDaysByDate2Days(-1, DateTimeTool.dateFormat("yyyyMMdd", currentPeriods.substring(0, 8)))).concat("001"));
	}
	
	
	public LotteryDTO getCurrentLotteryDTO(String providerStr,Integer lotteryType) throws Exception {
		LotteryDTO lotteryDTO = null;
		String[] pathArr = providerStr.split("\\|");
		for (int i = 0; i < pathArr.length; i++) {
			if(pathArr[i].equals("PC268")) {
				lotteryDTO = FetchResult.getOpenCur("PC268",null,"",lotteryType, 1);
//			}else if(pathArr[i].equals("彩讯网")){//开奖时间容易错，不考虑		
			}else if(pathArr[i].equals("28T")) {
				String t28Path = systemConfigService.getValueByName("lottery_28t_path");
				lotteryDTO = FetchResult.getOpenCur("28T",null,t28Path,lotteryType, 1);
			}else if(pathArr[i].equals("PC28AM")) {
				String pc28amPath = systemConfigService.getValueByName("lottery_pc28am_path");
				lotteryDTO = FetchResult.getOpenCur("PC28AM",null,pc28amPath,lotteryType, 1);
			}else if(pathArr[i].equals("ASCXW")) {
				String ascxwPath = systemConfigService.getValueByName("lottery_ascxw_path");
				lotteryDTO = FetchResult.getOpenCur("ASCXW",null,ascxwPath,lotteryType, 1);
			}else if(pathArr[i].equals("开彩网")) {
				lotteryDTO = FetchResult.getOpenCur("开彩网",null,"",lotteryType, 1);
			}else if(pathArr[i].equals("处理中心")) {
				lotteryDTO = FetchResult.getOpenCur("处理中心",null,"",lotteryType, 1);
			}
			
			if(lotteryDTO!=null && lotteryDTO.isBool()){
				return lotteryDTO;
	    	}
		}
		return null;
	}
}
