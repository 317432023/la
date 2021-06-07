package com.jeetx.controller.api.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jeetx.controller.api.ApiUtil;
import com.jeetx.util.MD5Util;
import com.jeetx.util.RandomUtil;

import net.sf.json.JSONObject;

public class CloudApiTest {
	private static final String SECRETKEY = "AF241F8CA6E2AC7138F50D713851432F";
	private static final String BASE_URL = "http://127.0.0.1:8080/la-api";
	private static final String REFERER = "";
	//private static final String BASE_URL = "http://api.43018.cn";
	
	public static void main(String[] args) {
		//periodsHistoryList();
		currentPeriods();
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
	
	
	/**获取彩票历史开奖列表*/
	public static void periodsHistoryList(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("lottery_type", "1");
			parms.put("page", "1");
			parms.put("limit", "10");
			parms.put("timestamp", timestamp);
			parms.put("sign", ApiUtil.generateSign(parms,SECRETKEY));
	    	
			String response = new String(client.postMethod(BASE_URL+"/api/cloud/periodsHistoryList", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	 /**获取当前游戏期数*/
	public static void currentPeriods(){
		try {
			String timestamp = JSONObject.fromObject(serverTime()).getJSONObject("result").getString("serverTime");
			
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			parms.put("lottery_type", 2);
			parms.put("timestamp", timestamp);
			parms.put("sign", ApiUtil.generateSign(parms,SECRETKEY));
			//parms.put("periods", "2434157");
			
			String response = new String(client.postMethod(BASE_URL+"/api/cloud/currentPeriods", parms,REFERER,"UTF-8"));
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
