package com.jeetx.controller.api.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jeetx.controller.api.ApiUtil;
import com.jeetx.util.MD5Util;

import net.sf.json.JSONObject;

public class ConsoleApiTest {
	private static final String VERSION = "1.0";
	//private static final String USERNAME = "虚拟a";
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
//		logout();
//		userInfo();
//		updateNickName();
//		updateHeadImg();
//		addBankCard();
//		delBankCard();
//		bankCardList();
//		updatePwd();
//		letterList();
//		letterDetail();
//		transRecordList();
//		transRecordDetail();
//		withdrawList();
//		withdrawDetail();
//		lotteryOrderList();
//		lotteryOrderDetail();
		
//		withdrawConfig();
//		withdrawApply();
		
//		scanCodeRecharge();
//		transferRecharge();
//		onlineRecharge();
		submitOnlineRecharge();
//		waterRecordList();
		
//		rechargeConfig();
//		profitDailyList();
//		getCustomerLinks();
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
		//System.out.println("token:"+token);
		//System.out.println("secretKey:"+secretKey);
		return responseStr;
	}
	
	 /**注销*/
	public static void logout(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/logout", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**获取玩家信息*/
	public static void userInfo(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/userInfo", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**修改昵称*/
	public static void updateNickName(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("nickname", "悟空");
			parms.put("token",tokenArray[0]);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/updateNickName", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**修改头像*/
	public static void updateHeadImg(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("head_link", "http://www.baidu.com");
			parms.put("token",tokenArray[0]);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/updateHeadImg", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**添加银行卡*/
	public static void addBankCard(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("card_json", "{bankName:\"中国建设银行\",cardholder:\"张三\",cardNo:\"12345678912344\",bankPlace:\"河北石家庄\",bankBranch:\"保定支行\"}");
			parms.put("token",tokenArray[0]);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/addBankCard", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**删除银行卡*/
	public static void delBankCard(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("card_id", "1");
			parms.put("token",tokenArray[0]);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/delBankCard", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**获取银行卡列表*/
	public static void bankCardList(){
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
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/bankCardList", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**修改密码*/
	public static void updatePwd(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			//parms.put("password", MD5Util.MD5Encode(MD5Util.MD5Encode(PASSWORD, "utf-8").toLowerCase(),"utf-8").toLowerCase());
			parms.put("password", "");
			parms.put("new_password",MD5Util.MD5Encode(MD5Util.MD5Encode(PASSWORD, "utf-8").toLowerCase(),"utf-8").toLowerCase());
			parms.put("type", "2");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/updatePwd", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**获取站内信列表*/
	public static void letterList(){
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
			parms.put("page", "1");
			parms.put("limit", "10");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/letterList", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**获取站内信详情*/
	public static void letterDetail(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("letter_id", "1");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/letterDetail", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**获取资金明细列表*/
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
			parms.put("date_begin", "");
			parms.put("date_end", "");
			parms.put("page", "1");
			parms.put("limit", "10");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/transRecordList", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**获取资金明细详情*/
	public static void transRecordDetail(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("record_id", "1");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/transRecordDetail", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**提现记录列表*/
	public static void withdrawList(){
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
			parms.put("date_begin", "");
			parms.put("date_end", "");
			parms.put("page", "1");
			parms.put("limit", "10");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/withdrawList", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**提现记录详情*/
	public static void withdrawDetail(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("record_id", "28");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/withdrawDetail", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**投注记录列表*/
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
			parms.put("lotteryType", "-1");
			parms.put("date_begin", "");
			parms.put("date_end", "");
			parms.put("page", "1");
			parms.put("limit", "10");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/lotteryOrderList", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**投注记录详情*/
	public static void lotteryOrderDetail(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("order_id", "1");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/lotteryOrderDetail", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**获取提现设置信息*/
	public static void withdrawConfig(){
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
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/withdrawConfig", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**申请提现*/
	public static void withdrawApply(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			Map<String,Object> parms = new HashMap<String,Object>(); 
			
			String securityCode = MD5Util.MD5Encode(MD5Util.MD5Encode(MD5Util.MD5Encode(PASSWORD, "utf-8").toLowerCase(),"utf-8").toLowerCase()
					.concat(timestamp),"utf-8").toLowerCase();
			
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			//parms.put("security_code", securityCode);
			//parms.put("bankcard_id",39);
			parms.put("bankcard_id",1);
			parms.put("apply_amount", "100");
			parms.put("token",tokenArray[0]);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/withdrawApply", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**充值(线下扫码)*/
	public static void scanCodeRecharge(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/scanCodeRechargeConfig", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**充值(网银转账)*/
	public static void transferRecharge(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/transferRechargeConfig", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**充值(网上充值)*/
	public static void onlineRecharge(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/onlineRechargeConfig", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**提交网上充值*/
	public static void submitOnlineRecharge(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
//			//金樽
//			parms.put("provider_id", "6");
//			parms.put("netway_code", "4");
			
//			//星支付
//			parms.put("provider_id", "7");
//			parms.put("netway_code", "2");

			//付必达
			parms.put("provider_id", "8");
			parms.put("netway_code", "1");

//			//优付宝
//			parms.put("provider_id", "9");
//			parms.put("netway_code", "2");
			
			parms.put("amount", "30000");
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/submitOnlineRecharge", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**回水列表*/
	public static void waterRecordList(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("lotteryType", "-1");
			parms.put("date_begin", "");
			parms.put("date_end", "");
			parms.put("page", "1");
			parms.put("limit", "10");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/waterRecordList", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void rechargeConfig(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/rechargeConfig", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**每日盈亏列表*/
	public static void profitDailyList(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("date_begin", "2019-05-05");
			parms.put("date_end", "2019-06-05");
			parms.put("page", "1");
			parms.put("limit", "10");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/profitDailyList", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**获取在线客服信息*/
	public static void getCustomerLinks(){
		try {
			String[] tokenArray = getToken();
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);
			parms.put("username", USERNAME);
			parms.put("token",tokenArray[0]);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,tokenArray[1]));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/console/getCustomerLinks", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
