package com.jeetx.controller.api.test;

import java.util.HashMap;
import java.util.Map;

import com.jeetx.controller.api.ApiUtil;
import com.jeetx.util.HttpUtil;
import com.jeetx.util.MD5Util;
import com.jeetx.util.RandomUtil;

import net.sf.json.JSONObject;

public class AdminApiTest {

	private static final String VERSION = "1.0";
	private static final String STATION_ID = "1";
	private static final String REFERER = ""; 
	//private static final String BASE_URL = "http://47.99.214.237:9090/la-api";
	private static final String BASE_URL = "http://127.0.0.1:8080/la-api";
	//private static final String BASE_URL = "http://api.zhongxingyue.cn";
	//private static final String BASE_URL = "http://api.kingzo.cn";
	
	public static void main(String[] args) {
		//rechargeHandle();
		//cancelOrder();
		//withdrawHandle();
		//lotteryAwardHandle();
		//createStation();
		//subTransfer();
		initLotteryRule();
		//initLotteryType();
		//addVirtualUser();
		//initRobotPlantConfig();
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
	
	 /**手动充值(调账)处理*/
	public static void rechargeHandle(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("username", "玩家1");
			parms.put("type", "2");
			parms.put("remark", "后台充值");
			parms.put("amount", "10000");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/admin/rechargeHandle", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	 /**后台提现处理*/
	public static void withdrawHandle(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("withdraw_id", "14");
			parms.put("type", "1");
			parms.put("remark", "");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/admin/withdrawHandle", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**后台手动派奖处理*/
	public static void lotteryAwardHandle(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("lottery_type", 1);
			parms.put("lottery_period", "941592");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/admin/lotteryAwardHandle", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**取消订单*/
	public static void cancelOrder(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("order_ids", "1175");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/admin/cancelOrder", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**创建站点*/
	public static void createStation(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();

			parms.put("station_name", "测试5");
			parms.put("station_domain", "http://test3.43018.cn");
			parms.put("entry_domain", "http://test3.43018.cn");
			parms.put("image_domain", "http://test3.43018.cn");
			parms.put("mq_domain", "http://test3.43018.cn");
			
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/admin/createStation", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**隶属转移*/
	public static void subTransfer(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("uid", "5");
			parms.put("pid", "3");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/admin/subTransfer", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**初始游戏规则*/
	public static void initLotteryRule(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>();
			ApiUtil client = new ApiUtil();
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/admin/initLotteryRule", parms,REFERER,"UTF-8"));
			System.out.println(BASE_URL+"/api/admin/initLotteryRule?"+ ApiUtil.mapToString(parms));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**初始游戏*/
	public static void initLotteryType(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", 4);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/admin/initLotteryType", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**后台新增虚拟号*/
	public static void addVirtualUser(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("username", "推广abc");
			parms.put("is_init_order", "1");
			parms.put("card_json", "{bankName:\"中国建设银行\",cardholder:\"张三\",cardNo:\"12345678912344\",bankPlace:\"河北石家庄\",bankBranch:\"保定支行\"}");
			parms.put("add_username", "虚拟"+RandomUtil.getRangeRandom(1, 1000));
			//parms.put("add_username", "虚拟123");
			parms.put("add_password", MD5Util.MD5Encode(MD5Util.MD5Encode("123456", "utf-8").toLowerCase(),"utf-8").toLowerCase() );
			
			parms.put("sign", ApiUtil.generateSign(parms,ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/admin/addVirtualUser", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**初始化假人*/
	public static void initRobotPlantConfig(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/admin/initRobotPlantConfig", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
