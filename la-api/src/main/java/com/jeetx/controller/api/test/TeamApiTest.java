package com.jeetx.controller.api.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jeetx.controller.api.ApiUtil;
import com.jeetx.util.MD5Util;
import com.jeetx.util.RandomUtil;

import net.sf.json.JSONObject;

public class TeamApiTest {
	private static final String VERSION = "1.0";
	
//	private static final String USERNAME = "代理abc";
//	private static final String PASSWORD = "123456";
	
	private static final String USERNAME = "推广abc";
	private static final String PASSWORD = "123456";
	
//	private static final String USERNAME = "玩家1";
//	private static final String PASSWORD = "123456";
//	private static final String STATION_ID = "";
//	private static final Integer DEVICE = 3;
//	private static final String REFERER = "http://www.411027.com/";
	
	private static final String STATION_ID = "1";
	private static final Integer DEVICE = 1;
	private static final String REFERER = "http://www.zz.com/";
	
	private static final String BASE_URL = "http://127.0.0.1:8080/la-api";
	
	public static void main(String[] args) {
//		promoterList();
//		virtualUserList();
//		playerList();
//		transRecordList();
//		lotteryOrderList();
//		profitList();
//		addSalesman();
		addVirtualUser();
//		disableUser();
//		virtualUserRecharge();
//		teamTotal();
//		profitTotal();
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
		
		return responseStr;
	}
	
	
	/**下级推广员列表*/
	public static void promoterList(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("page", "1");
			parms.put("limit", "10");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/team/promoterList", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**下级虚拟号列表*/
	public static void virtualUserList(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("page", "1");
			parms.put("limit", "10");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/team/virtualUserList", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**下级玩家列表*/
	public static void playerList(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("page", "1");
			parms.put("limit", "10");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/team/playerList", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**团队账变列表*/
	public static void transRecordList(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("status", "-1");
			//parms.put("date_begin", "2018-01-01");
			//parms.put("date_end", "2019-01-01");
			parms.put("page", "1");
			parms.put("limit", "10");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/team/transRecordList", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**团队投注列表*/
	public static void lotteryOrderList(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("status", "-1");
			parms.put("lotteryType", "1");
			parms.put("date_begin", "");
			parms.put("date_end", "");
			parms.put("page", "1");
			parms.put("limit", "10");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/team/lotteryOrderList", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**团队盈亏列表*/
	public static void profitList(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("date_begin", "2019-05-01");
			parms.put("date_end", "2019-06-18");
			parms.put("page", "1");
			parms.put("limit", "10");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/team/profitList", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**新增推广号*/
	public static void addSalesman(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("add_username", "推广3");
			parms.put("add_password", MD5Util.MD5Encode(MD5Util.MD5Encode("123456", "utf-8").toLowerCase(),"utf-8").toLowerCase() );
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/team/addSalesman", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**新增虚拟号*/
	public static void addVirtualUser(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("is_init_order", "1");
			parms.put("card_json", "{bankName:\"中国建设银行\",cardholder:\"张三\",cardNo:\"12345678912344\",bankPlace:\"河北石家庄\",bankBranch:\"保定支行\"}");
			parms.put("token",tokenArray[0]);
			parms.put("add_username", "虚拟abc"+RandomUtil.getRangeRandom(1, 1000));
			parms.put("add_password", MD5Util.MD5Encode(MD5Util.MD5Encode("123456", "utf-8").toLowerCase(),"utf-8").toLowerCase() );
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/team/addVirtualUser", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**注销子账号*/
	public static void disableUser(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("child_username", "虚拟21");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/team/disableUser", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**虚拟号充值*/
	public static void virtualUserRecharge(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("type", "1");
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("amount", "1000");
			parms.put("child_username", "虚拟a");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/team/virtualUserRecharge", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**团队信息统计*/
	public static void teamTotal(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("date_begin", "2019-05-01");
			parms.put("date_end", "2019-06-18");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/team/teamTotal", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**团队统计列表*/
	public static void profitTotal(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("date_begin", "2019-05-01");
			parms.put("date_end", "2019-06-18");
			parms.put("pid", "260");
			parms.put("page", "1");
			parms.put("limit", "10");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/team/profitTotal", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
