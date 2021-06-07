package com.jeetx.common.pay.alipays;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.jeetx.util.HttpUtil;

import net.sf.json.JSONObject;

public class PayDemo {

	public static String reqUrl = "https://www.668915.com/Pay_Index.html";
	public static String queryUrl = "https://www.668915.com/Pay_Trade_query.html";

	public static void main(String[] args) {
		//pay();
		query();

	}

	
	/**
	 * 支付方法
	 */
	public static void pay() {
		String pay_orderid = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()); 
		String pay_applydate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); 
		String pay_bankcode = "903";
		String pay_notifyurl = "http://127.0.0.1/";
		String pay_callbackurl = "http://127.0.0.1/";
		String pay_amount = "1000";
		String pay_productname = "productname";
		
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("pay_memberid", AlipaysToolKit.PAY_MEMBERID);
		metaSignMap.put("pay_orderid", pay_orderid);
		metaSignMap.put("pay_applydate", pay_applydate);
		metaSignMap.put("pay_bankcode", pay_bankcode);
		metaSignMap.put("pay_notifyurl", pay_notifyurl);
		metaSignMap.put("pay_callbackurl", pay_callbackurl);
		metaSignMap.put("pay_amount", pay_amount);
		metaSignMap.put("pay_md5sign", AlipaysToolKit.generateSign(metaSignMap));
		metaSignMap.put("pay_productname", pay_productname);
		
		String resultJsonStr = new HttpUtil().postMethod(reqUrl, metaSignMap, "UTF-8");

		if(!resultJsonStr.contains("{\"status\":\"error\"")) {
			System.out.println(resultJsonStr);
		}else {
			JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
			String msg = resultJsonObj.getString("msg");
			System.out.println(msg);
		}
		
	}
	
	public static void query() {
		String pay_orderid = "20190420163720557"; 

		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("pay_memberid", AlipaysToolKit.PAY_MEMBERID);
		metaSignMap.put("pay_orderid", pay_orderid);
		metaSignMap.put("pay_md5sign", AlipaysToolKit.generateSign(metaSignMap));
		
		String resultJsonStr = new HttpUtil().postMethod(queryUrl, metaSignMap, "UTF-8");
		System.out.println(resultJsonStr);
		if(resultJsonStr.contains("orderid")) {
			JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
			String trade_state = resultJsonObj.getString("trade_state");
			//NOTPAY-未支付 SUCCESS已支付
			System.out.println(trade_state);
		}


	}

}
