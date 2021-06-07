package com.jeetx.controller.api.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.jeetx.controller.api.ApiUtil;
import com.jeetx.util.MD5Util;

import net.sf.json.JSONObject;

public class CommonApiTest {

	private static final String VERSION = "1.0";
	
//	private static final String STATION_ID = "";
//	private static final Integer DEVICE = 3;
//	private static final String REFERER = "http://www.411027.com/";
	
	private static final String STATION_ID = "1";
	private static final Integer DEVICE = 1;
	private static final String REFERER = "http://www.zz.com/";
	
	private static final String BASE_URL = "http://127.0.0.1:8080/la-api";
	public static void main(String[] args) {
		//serverTime();
//		advertList();
//		infoList();
//		infoDetail();
//		lotteryTypeList();
//		siteDomainList();
//		checkAppVersion();
//		register();
//		login();
//		touristLogin();
//		systemConfig();
//		getWinList();
//		weChatLogin();
//		stationInfo();
		index();
	}

	
	/**获取服务器时间*/
	public static String serverTime(){
		String response = null;
		try {
			Date now = new Date();
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			//System.out.println(BASE_URL+"/api/common/serverTime?time="+now.getTime());
			response = new String(client.postMethod(BASE_URL+"/api/common/serverTime?time="+now.getTime(), parms,REFERER,"UTF-8"));
			//System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	/**获取轮播图列表*/
	public static void advertList(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("device", DEVICE);
			parms.put("station_id", STATION_ID);
			parms.put("advert_type", "2");//轮播图类型(1:PC、2:APP)
			parms.put("page", "1");
			parms.put("limit", "10");	
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,DEVICE==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/common/advertList", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**获取资讯列表*/
	public static void infoList(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("device", DEVICE);
			parms.put("station_id", STATION_ID);
			parms.put("category_id", "4");
			parms.put("page", "1");
			parms.put("limit", "10");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,DEVICE==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/common/infoList", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**获取资讯详情*/
	public static void infoDetail(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("device", DEVICE);
			parms.put("station_id", STATION_ID);
			parms.put("info_id", "1");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,DEVICE==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/common/infoDetail", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 /**获取彩票列表*/
	public static void lotteryTypeList(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("device", DEVICE);
			parms.put("station_id", STATION_ID);
			parms.put("device", "2");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,DEVICE==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/common/lotteryTypeList", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**获取站点线路列表*/
	public static void siteDomainList(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("device", DEVICE);
			parms.put("station_id", STATION_ID);
			parms.put("device", "2");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,DEVICE==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/common/siteDomainList", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**获取APP版本信息*/
	public static void checkAppVersion(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("device", DEVICE);
			parms.put("station_id", STATION_ID);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,DEVICE==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/common/checkAppVersion", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**玩家注册*/
	public static void register(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("device", DEVICE);
			parms.put("station_id", STATION_ID);
			//parms.put("pid", "260");
			
			//parms.put("p_username", "虚拟84");
			//parms.put("phone", "12321321312");
			//parms.put("wechat", "wechat");
			//parms.put("referee", "浮云6");
			
			parms.put("username", "浮云13");
			parms.put("password", MD5Util.MD5Encode(MD5Util.MD5Encode("123456", "utf-8").toLowerCase(),"utf-8").toLowerCase() );
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,DEVICE==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/common/register", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**玩家登陆*/
	public static void login(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("device", DEVICE);
			parms.put("station_id", STATION_ID);
			//parms.put("device", "2");
			parms.put("username", "玩家1");
			parms.put("password", MD5Util.MD5Encode(MD5Util.MD5Encode("123456", "utf-8").toLowerCase(),"utf-8").toLowerCase() );
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,DEVICE==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/common/login", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**游客登陆*/
	public static void touristLogin(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("device", DEVICE);
			parms.put("station_id", STATION_ID);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,DEVICE==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/common/touristLogin", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**系统配置*/
	public static void systemConfig(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("device", DEVICE);
			parms.put("station_id", STATION_ID);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,DEVICE==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/common/systemConfig", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**获取最新中奖名单列表*/
	public static void getWinList(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("device", DEVICE);
			parms.put("station_id", STATION_ID);
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,DEVICE==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/common/getWinList", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**微信授权登陆*/
	public static void weChatLogin(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("device", DEVICE);
			parms.put("station_id", STATION_ID);
			parms.put("device", "1");
			parms.put("openid", "A6E2AC7138F50D71AF241F8C3851432F");
			parms.put("nickname", "浮云");
			parms.put("head_img", "https://s3m.mediav.com/galileo/744741-7e019bca925a9062db279c5d204c9d9b.jpg");
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,DEVICE==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/common/weChatLogin", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**获取站点信息*/
	public static void stationInfo(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);	
			parms.put("timestamp", timestamp);
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,DEVICE==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/common/stationInfo", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**获取站点首页信息*/
	public static void index(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("station_id", STATION_ID);
			parms.put("device", DEVICE);	
			parms.put("timestamp", timestamp);
			parms.put("category_id", "4");
			parms.put("page", "1");
			parms.put("limit", "10");
			parms.put("version", VERSION);
			parms.put("sign", ApiUtil.generateSign(parms,DEVICE==3?ApiUtil.H5_SECRETKEY:ApiUtil.SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/common/index", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
