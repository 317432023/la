package com.jeetx.service.lottery.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryHall;
import com.jeetx.bean.lottery.LotteryRoom;
import com.jeetx.bean.lottery.LotteryRule;
import com.jeetx.bean.lottery.LotteryRulePlanItem;
import com.jeetx.bean.system.Station;
import com.jeetx.common.dpc.ApiUtil;
import com.jeetx.common.dpc.RoomJson;
import com.jeetx.common.dpc.RuleJson;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryHallService;
import com.jeetx.service.lottery.LotteryRobotPlantConfigService;
import com.jeetx.service.lottery.LotteryRoomService;
import com.jeetx.service.lottery.LotteryRulePlanItemService;
import com.jeetx.service.lottery.LotteryRuleService;
import com.jeetx.service.lottery.LotteryTypeService;
import com.jeetx.service.system.StationService;
import com.jeetx.util.JsonUtil;
import com.jeetx.util.LogUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
@Transactional
public class LotteryRuleServiceImpl extends DaoSupport<LotteryRule> implements LotteryRuleService {
	@Autowired StationService stationService;
	@Autowired LotteryRoomService lotteryRoomService;
	@Autowired LotteryHallService lotteryHallService;
	@Autowired LotteryRuleService lotteryRuleService;
	@Autowired LotteryRobotPlantConfigService lotteryRobotPlantConfigService;
	@Autowired LotteryTypeService lotteryTypeService;
	@Autowired LotteryRulePlanItemService lotteryRulePlanItemService;
	
	@SuppressWarnings("unchecked")
	public List<LotteryRule> getLotteryRuleList(Integer hallId,Integer ruleType) {
		return this.getSession().createQuery("from LotteryRule o where o.status = 1 and o.lotteryHall.id = ? and o.ruleType = ? order by id")
				.setParameter(0, hallId).setParameter(1, ruleType).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<LotteryRule> getListByHallId(Integer hallId) {
		return this.getSession().createQuery("from LotteryRule o where o.status = 1 and o.lotteryHall.id = ? and o.ruleType >0 order by id").setParameter(0, hallId).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<LotteryRule> getListByHallIdNotType3(Integer hallId) {
		return this.getSession().createQuery("from LotteryRule o where o.status = 1 and o.lotteryHall.id = ? and o.ruleType <> 3 and o.ruleType >0  order by id").setParameter(0, hallId).list();
	}

	@SuppressWarnings("unchecked")
	public String getParamValue(Integer lotteryHallId,String paramName) {
		List<LotteryRule> list = this.getSession().createQuery("from LotteryRule o where o.lotteryHall.id = ? and o.paramName = ?")
				.setParameter(0, lotteryHallId).setParameter(1, paramName).list();
		if (list!=null&&list.size()>0) {
			LotteryRule lotteryRule = list.get(0);
			if(lotteryRule!=null && lotteryRule.getStatus() ==1) {
				LotteryRulePlanItem lotteryRulePlanItem = lotteryRulePlanItemService.findLotteryRulePlanItem(lotteryRule.getLotteryHall().getStation().getId(), lotteryRule.getLotteryHall().getId(), lotteryRule.getId(),new Date());
				if(lotteryRulePlanItem !=null && StringUtils.isNotBlank(lotteryRulePlanItem.getParamValues())) {
					return lotteryRulePlanItem.getParamValues();
				}
				return lotteryRule.getParamValues();
			}
		}
		return null;
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void submitXxlLotteryRule(Integer stationId) {
		Integer lotteryType = 6;
		Station station = stationService.find(stationId);
		if(station != null) {
			List<RoomJson> roomJsonList = new ArrayList<RoomJson>();	
			List<RuleJson> ruleList = new ArrayList<RuleJson>();

			List<LotteryHall> lotteryHallList = lotteryHallService.getLotteryHallList(lotteryType, stationId);
			for (LotteryHall lotteryHall : lotteryHallList) {
				if(lotteryHall != null && lotteryHall.getStatus()!=0) {
					List<LotteryRoom> lotteryRoomList  = lotteryRoomService.getLotteryRoomList(lotteryType, lotteryHall.getId());
					if(lotteryHall != null && lotteryHall.getStatus()!=0) {
						for (LotteryRoom lotteryRoom : lotteryRoomList) {
							RoomJson roomJson = new RoomJson();
							roomJson.setRoomKey("app_room_"+lotteryRoom.getId());
							roomJson.setRoomTitle(lotteryHall.getTitle().concat("_").concat(lotteryRoom.getTitle()));
							roomJson.setLotteryType(8);//7新加坡、8新西兰
							roomJson.setToyType(1);
							
							for (int i = 1; i <= 3; i++) {
								List<LotteryRule> lotteryRuleList = lotteryRuleService.getLotteryRuleList(lotteryHall.getId(), i);
								for (LotteryRule lotteryRule : lotteryRuleList) {
									RuleJson ruleJson = new RuleJson();
									ruleJson.setParamName(lotteryRule.getParamName());
									ruleJson.setParamValues(lotteryRule.getParamValues());
									ruleList.add(ruleJson);
								}
							}
							
							//开1314时，大小单双特殊
							RuleJson ruleJson = new RuleJson();
							ruleJson.setParamName("开1314时，大小单双特殊情况一");
							ruleJson.setParamValues(lotteryRuleService.getParamValue(lotteryHall.getId(), "大小单双特殊情况一：如遇开13、14，总注>S1时，赔率值S2"));
							ruleList.add(ruleJson);
							
							ruleJson = new RuleJson();
							ruleJson.setParamName("开1314时，大小单双特殊情况二");
							ruleJson.setParamValues(lotteryRuleService.getParamValue(lotteryHall.getId(), "大小单双特殊情况二：如遇开13、14，总注>S1时，赔率值S2"));
							ruleList.add(ruleJson);
							
							ruleJson = new RuleJson();
							ruleJson.setParamName("开1314时，大小单双特殊情况三");
							ruleJson.setParamValues("-1|-1");
							ruleList.add(ruleJson);
							
							//开1314时，组合特殊
							ruleJson = new RuleJson();
							ruleJson.setParamName("开1314时，组合特殊情况一");
							ruleJson.setParamValues(lotteryRuleService.getParamValue(lotteryHall.getId(), "组合特殊情况一：如遇开13、14，总注>S1时，赔率值S2"));
							ruleList.add(ruleJson);
							
							ruleJson = new RuleJson();
							ruleJson.setParamName("开1314时，组合特殊情况二");
							ruleJson.setParamValues(lotteryRuleService.getParamValue(lotteryHall.getId(), "组合特殊情况二：如遇开13、14，总注>S1时，赔率值S2"));
							ruleList.add(ruleJson);
							
							ruleJson = new RuleJson();
							ruleJson.setParamName("开1314时，组合特殊情况三");
							ruleJson.setParamValues("-1|-1");
							ruleList.add(ruleJson);
							
							//开1314时，总注特殊
							ruleJson = new RuleJson();
							String paramValue1 = lotteryRuleService.getParamValue(lotteryHall.getId(), "总注特殊情况一：如遇开13、14，总注>S1时，赔率值S2");
							ruleJson.setParamName("开1314时，总注特殊情况一");
							ruleJson.setParamValues(paramValue1.concat("|").concat(paramValue1.split("\\|")[1]));
							ruleList.add(ruleJson);
							
							ruleJson = new RuleJson();
							String paramValue2 = lotteryRuleService.getParamValue(lotteryHall.getId(), "总注特殊情况二：如遇开13、14，总注>S1时，赔率值S2");
							ruleJson.setParamName("开1314时，总注特殊情况二");
							ruleJson.setParamValues(paramValue2.concat("|").concat(paramValue2.split("\\|")[1]));
							ruleList.add(ruleJson);
							
							ruleJson = new RuleJson();
							ruleJson.setParamName("开1314时，总注特殊情况三");
							ruleJson.setParamValues("-1|-1|-1");
							ruleList.add(ruleJson);
							
							//开顺豹对时，大小单双特殊
							ruleJson = new RuleJson();
							ruleJson.setParamName("开顺豹对时，大小单双特殊情况一");
							ruleJson.setParamValues(lotteryRuleService.getParamValue(lotteryHall.getId(), "大小单双特殊情况三：如遇开顺豹对，总注>S1时，赔率值S2"));
							ruleList.add(ruleJson);
							
							ruleJson = new RuleJson();
							ruleJson.setParamName("开顺豹对时，大小单双特殊情况二");
							ruleJson.setParamValues(lotteryRuleService.getParamValue(lotteryHall.getId(), "大小单双特殊情况四：如遇开顺豹对，总注>S1时，赔率值S2"));
							ruleList.add(ruleJson);
							
							ruleJson = new RuleJson();
							ruleJson.setParamName("开顺豹对时，大小单双特殊情况三");
							ruleJson.setParamValues("-1|-1");
							ruleList.add(ruleJson);
							
							//开顺豹对时，组合特殊
							ruleJson = new RuleJson();
							ruleJson.setParamName("开顺豹对时，组合特殊情况一");
							ruleJson.setParamValues(lotteryRuleService.getParamValue(lotteryHall.getId(), "组合特殊情况三：如遇开顺豹对，总注>S1时，赔率值S2"));
							ruleList.add(ruleJson);
							
							ruleJson = new RuleJson();
							ruleJson.setParamName("开顺豹对时，组合特殊情况二");
							ruleJson.setParamValues(lotteryRuleService.getParamValue(lotteryHall.getId(), "组合特殊情况四：如遇开顺豹对，总注>S1时，赔率值S2"));
							ruleList.add(ruleJson);
							
							ruleJson = new RuleJson();
							ruleJson.setParamName("开顺豹对时，组合特殊情况三");
							ruleJson.setParamValues("-1|-1");
							ruleList.add(ruleJson);
							
							//开顺豹对时，总注特殊
							ruleJson = new RuleJson();
							String paramValue11 = lotteryRuleService.getParamValue(lotteryHall.getId(), "总注特殊情况三：如遇开顺豹对，总注>S1时，赔率值S2");
							ruleJson.setParamName("开顺豹对时，总注特殊情况一");
							ruleJson.setParamValues(paramValue11.concat("|").concat(paramValue11.split("\\|")[1]));
							ruleList.add(ruleJson);
							
							ruleJson = new RuleJson();
							String paramValue22 = lotteryRuleService.getParamValue(lotteryHall.getId(), "总注特殊情况四：如遇开顺豹对，总注>S1时，赔率值S2");
							ruleJson.setParamName("开顺豹对时，总注特殊情况二");
							ruleJson.setParamValues(paramValue22.concat("|").concat(paramValue22.split("\\|")[1]));
							ruleList.add(ruleJson);
							
							ruleJson = new RuleJson();
							ruleJson.setParamName("开顺豹对时，总注特殊情况三");
							ruleJson.setParamValues("-1|-1|-1");
							ruleList.add(ruleJson);

							roomJson.setRule(ruleList);
							roomJsonList.add(roomJson);
						}
					}
				}
			}
			
			String appId = "app_station_".concat(station.getId().toString()); 
			String appSecret = ApiUtil.SECRETKEY; 
			Integer platform = 3;
			String timestamp = JSONObject.fromObject(ApiUtil.serverTime()).getJSONObject("result").getString("serverTime");

			Map<String,Object> parms = new HashMap<String,Object>(); 
			parms.put("1appId", appId);
			parms.put("platform", platform);
			parms.put("timestamp", timestamp);
			String sign = ApiUtil.generateSign(parms,appSecret);
			
			String ruleJson = JsonUtil.toJSONString(roomJsonList);
			//System.out.println(ruleJson);
			
			JSONObject jsonParms = new JSONObject (); 
			jsonParms.put("appId", appId);
			jsonParms.put("platform", platform);
			jsonParms.put("timestamp", timestamp);
			jsonParms.put("objectJson", JSONArray.fromObject(ruleJson));
			jsonParms.put("sign", sign);
			
			JSONObject response = ApiUtil.doJsonPost(ApiUtil.BASE_URL+"/api/lottery/submitRule", jsonParms);
			if(response.getInt("code")!=0)
				LogUtil.info(station.getStationName()+",提交新西兰赔率信息至彩票中心异常:"+response.toString());
		}
		
	}
}
