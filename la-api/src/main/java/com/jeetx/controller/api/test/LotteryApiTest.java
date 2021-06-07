package com.jeetx.controller.api.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jeetx.controller.api.ApiUtil;
import com.jeetx.util.MD5Util;
import com.jeetx.util.RandomUtil;

import net.sf.json.JSONObject;

public class LotteryApiTest {

	private static final String VERSION = "1.0";
	private static final String USERNAME = "玩家1";
	private static final String PASSWORD = "123456";
//	private static final String STATION_ID = "";
//	private static final Integer DEVICE = 3;
//	private static final String REFERER = "http://www.411027.com/";
	
	private static final String STATION_ID = "1";
	private static final Integer DEVICE = 1;
	private static final String REFERER = "http://www.zz.com/";
	
	private static final String BASE_URL = "http://127.0.0.1:8080/la-api";
	public static void main(String[] args) {
		getRoom2HallList();
//		currentFinishPeriods();
//		periodsHistoryList();
//		currentPeriods();
//		getLotteryPeriods();
//		lotteryRule();
//		submitOrder();
//		cancelOrder();
//		createMQQueue();
//		clearMQQueue();
//		getWin();
		lottery();
	}

	/**获取服务器时间*/
	public static String serverTime(){
		String response = null;
		try {
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			response = new String(client.postMethod(BASE_URL+"/api/common/serverTime", parms,REFERER,"UTF-8"));
			//System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	public static String[] getToken(){
		String[] responseStr = new String[2]; 
		String token = "";
		String secretKey = ApiUtil.SECRETKEY;
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("password", MD5Util.MD5Encode(MD5Util.MD5Encode(PASSWORD, "utf-8").toLowerCase(),"utf-8").toLowerCase() );
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,DEVICE==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/common/login", parms,REFERER,"UTF-8"));
			//System.out.println(response);
			if(StringUtils.isNotBlank(response)) {
				token = JSONObject.fromObject(response).getJSONObject("result").getJSONObject("userInfo").getString("token");
				responseStr[0] = token;
				
				if(DEVICE == 3 && JSONObject.fromObject(response).getJSONObject("result").get("secretKey")!=null) {
					secretKey = JSONObject.fromObject(response).getJSONObject("result").getString("secretKey");
				}
				responseStr[1] = secretKey;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		System.out.println("token:"+token);
//		System.out.println("secretKey:"+secretKey);
		return responseStr;
	}
	
	/**获取房厅列表*/
	public static void getRoom2HallList(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("lottery_type", 2);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("device", DEVICE);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/lottery/getRoom2HallList", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**获取最近已开奖期数*/
	public static void currentFinishPeriods(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("device", DEVICE);
			parms.put("sign", ApiUtil.generateSign(parms,DEVICE==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY));

			String response = new String(client.postMethod(BASE_URL+"/api/lottery/currentFinishPeriods", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**获取彩票历史开奖列表*/
	public static void periodsHistoryList(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("lottery_type", "1");
			parms.put("page", "1");
			parms.put("limit", "10");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("device", DEVICE);
			parms.put("sign", ApiUtil.generateSign(parms,DEVICE==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/lottery/periodsHistoryList", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	 /**获取当前游戏期数*/
	public static void currentPeriods(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("room_id", 1);
			parms.put("lottery_type", 1);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("device", DEVICE);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/lottery/currentPeriods", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**获取游戏玩法赔率*/
	public static void lotteryRule(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("rule_types", "1");
			parms.put("hall_id", 41);
			//parms.put("hall_id", 11);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("device", DEVICE);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/lottery/lotteryRule", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**通过期号获取期数信息*/
	public static void getLotteryPeriods(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("lottery_type", 1);
			parms.put("lottery_period", "951716");
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("device", DEVICE);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/lottery/getLotteryPeriods", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	 /**提交订单|玩家下注*/
	public static void submitOrder(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
//			parms.put("room_id", 133);
//			parms.put("lottery_type", 6);
			
			parms.put("room_id", 28);
			parms.put("lottery_type", 4);
			
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("device", DEVICE);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/lottery/currentPeriods", parms,REFERER,"UTF-8"));
			if(StringUtils.isNotBlank(response)) {
				System.out.println(response);
				String lotteryPeriods = JSONObject.fromObject(response).getJSONObject("result").getJSONObject("data").getString("lotteryPeriods");
				Integer status = Integer.valueOf(JSONObject.fromObject(response).getJSONObject("result").getJSONObject("data").getString("status"));

				if(status == 1) {
					timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
					parms = new HashMap<String,Object>(); 
					client = new ApiUtil();
					
					String order_code = RandomUtil.getSeqNumber("CP", "yyyyMMddHHmmss", 3);
//					parms.put("room_id", 133);
//					parms.put("order_code", order_code);
//					parms.put("lottery_period", lotteryPeriods);
//					parms.put("drop_money", "100");
//					parms.put("orders_json", "[{ruleId:2441,betContent:\"大单\",betMoney:\"100\"}]");
					
					//parms.put("orders_json", "[{ruleId:233,betContent:\"大\",betMoney:\"1000\"},{ruleId:237,betContent:\"大单\",betMoney:\"1000\"},{ruleId:248,betContent:\"对子\",betMoney:\"1000\"}]");
					//parms.put("orders_json", "[{ruleId:234,betContent:\"小\",betMoney:\"100\"},{ruleId:237,betContent:\"大单\",betMoney:\"100\"}]");
					//parms.put("orders_json", "[{ruleId:234,betContent:\"小\",betMoney:\"100\"}]");
					//parms.put("orders_json", "[{ruleId:237,betContent:\"大单\",betMoney:\"100\"}]");
					

					parms.put("room_id", 28);
					parms.put("order_code", order_code);
					parms.put("lottery_period", lotteryPeriods);
					parms.put("drop_money", "200");
					//parms.put("orders_json", "[{ruleId:2683,betContent:\"4-大\",betMoney:\"100\"}]");
					//parms.put("orders_json", "[{ruleId:2713,betContent:\"4-小\",betMoney:\"100\"}]");
					parms.put("orders_json", "[{ruleId:2713,betContent:\"4-小\",betMoney:\"100\"},{ruleId:2683,betContent:\"4-大\",betMoney:\"100\"}]");
					//parms.put("orders_json", "[{ruleId:1,betContent:\"大\",betMoney:\"100\"},{ruleId:2,betContent:\"小\",betMoney:\"100\"}]");
					//parms.put("orders_json", "[{ruleId:1,betContent:\"大\",betMoney:\"100\"},{ruleId:5,betContent:\"大单\",betMoney:\"100\"}]");
					//parms.put("orders_json", "[{ruleId:2,betContent:\"小\",betMoney:\"100\"},{ruleId:5,betContent:\"大单\",betMoney:\"100\"}]");
					//parms.put("orders_json", "[{ruleId:2,betContent:\"小\",betMoney:\"100\"}]");
					//parms.put("orders_json", "[{ruleId:5,betContent:\"大单\",betMoney:\"100\"}]");
					
					parms.put("station_id", STATION_ID);
					parms.put("device", DEVICE);
					parms.put("username", USERNAME);
					parms.put("token",tokenArray[0]);
					parms.put("timestamp", timestamp);
					parms.put("version", VERSION);
					parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
			    	
					response = new String(client.postMethod(BASE_URL+"/api/lottery/submitOrder", parms,REFERER,"UTF-8"));
					System.out.println(response);
				}else {
					System.out.println("非投注时间");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**取消订单*/
	public static void cancelOrder(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("order_code", "CP20181231130208550");
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/lottery/cancelOrder", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**创建队列*/
	public static void createMQQueue(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("room_id", 134);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("device", DEVICE);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/lottery/createMQQueue", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**清空队列*/
	public static void clearMQQueue(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("room_id", 1);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("device", DEVICE);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/lottery/clearMQQueue", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**获取中奖信息*/
	public static void getWin(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("device", DEVICE);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/lottery/getWin", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**获取房间聚合信息(需登录权限)*/
	public static void lottery(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("rule_types", "1");
			parms.put("room_id", 1);
			parms.put("hall_id", 11);
			parms.put("page", "1");
			parms.put("limit", "10");
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("device", DEVICE);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/lottery/lottery", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
