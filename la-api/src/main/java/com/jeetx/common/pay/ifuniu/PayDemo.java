package com.jeetx.common.pay.ifuniu;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.jeetx.util.HttpsUtil;
import com.jeetx.util.RandomUtil;

import net.sf.json.JSONObject;

public class PayDemo {

	public static String reqUrl = "https://b.ifuniu.store/v1/order";
	public static String queryUrl = "https://b.ifuniu.store/v1/orderquery";

	public static void main(String[] args) {
		pay();
		//query();

	}

	
	/**
	 * 支付方法
	 */
	public static void pay() {
		String nonce_str = RandomUtil.generateString(32);
		String request_no = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()); 
		String amount = "200.00";//订单总金额,以元为单位
		String pay_channel = "ALH5";
		//String channel_id = "130";
		String request_time = String.valueOf(new Date().getTime()/1000); 
		String notify_url = "http://127.0.0.1/";
		String return_url = "http://127.0.0.1/";
		
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("merchant_no", IfuniuToolKit.PAY_MEMBERID);
		metaSignMap.put("nonce_str", nonce_str);
		metaSignMap.put("request_no", request_no);
		metaSignMap.put("amount", amount);
		metaSignMap.put("pay_channel", pay_channel);
		//metaSignMap.put("channel_id", channel_id);
		metaSignMap.put("request_time", request_time);
		metaSignMap.put("notify_url", notify_url);
		metaSignMap.put("return_url", return_url);

		
		metaSignMap.put("sign", IfuniuToolKit.generateSign(metaSignMap));
		String resultJsonStr = HttpsUtil.postMethod(reqUrl, metaSignMap, "UTF-8");

		System.out.println(resultJsonStr);
		if(StringUtils.isNotBlank(resultJsonStr)) {
			JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
			String isSuccess = resultJsonObj.getString("success");
			if("false".equalsIgnoreCase(isSuccess)) {
				JSONObject dataObject = resultJsonObj.getJSONObject("data");
				String message = dataObject.getString("message");
				System.out.println(message);
			}else {
				JSONObject dataObject = resultJsonObj.getJSONObject("data");
				if(dataObject!=null) {
					String status = dataObject.getString("status");
					String bankUrl = dataObject.getString("bank_url");
					String urlType = dataObject.getString("url_type");
					
					if("0".equalsIgnoreCase(status) || "2".equalsIgnoreCase(status)) {
						System.out.println(bankUrl);
						System.out.println(urlType);
					}
				}
			}
		}
		
	}
	
	public static void query() {
		String nonce_str = RandomUtil.generateString(32);
		String request_no = "20191202162143607"; 
		String request_time = String.valueOf(new Date().getTime()/1000); 
		
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("merchant_no", IfuniuToolKit.PAY_MEMBERID);
		metaSignMap.put("nonce_str", nonce_str);
		metaSignMap.put("request_no", request_no);
		metaSignMap.put("request_time", request_time);
		
		metaSignMap.put("sign", IfuniuToolKit.generateSign(metaSignMap));
		String resultJsonStr = HttpsUtil.postMethod(queryUrl, metaSignMap, "UTF-8");

		System.out.println(resultJsonStr);
		if(StringUtils.isNotBlank(resultJsonStr)) {
			JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
			String isSuccess = resultJsonObj.getString("success");
			if("false".equalsIgnoreCase(isSuccess)) {
				JSONObject dataObject = resultJsonObj.getJSONObject("data");
				String message = dataObject.getString("message");
				System.out.println(message);
			}else {
				JSONObject dataObject = resultJsonObj.getJSONObject("data");
				//String message = dataObject.getString("message");
				if(dataObject!=null) {
					String status = dataObject.getString("status");
					if("3".equalsIgnoreCase(status)) {
						System.out.println(status);
					}
				}
			}
		}
	}

}
