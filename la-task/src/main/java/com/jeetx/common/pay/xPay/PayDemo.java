package com.jeetx.common.pay.xPay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.jeetx.util.HttpsUtil;
import com.jeetx.util.RandomUtil;

import net.sf.json.JSONObject;

public class PayDemo {

	public static String reqUrl = "http://new.p8kajr.cn/Gk/jk";
	public static String queryUrl = "http://new.p8kajr.cn/Gk/shorder";

	public static void main(String[] args) {
		//pay();
		query();

	}

	
	/**
	 * 支付方法
	 */
	public static void pay() {
		String orderid = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()); 
		String amount = "200.00";//订单总金额,以元为单位
		String pay = "3";
		String ip = "127.0.0.1";
		String urlht = "http://127.0.0.1/";
		String urltz = "http://127.0.0.1/";
		
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("shid", XpayToolKit.SHID);//商户账号
		metaSignMap.put("orderid", orderid);//充值订单号
		metaSignMap.put("amount", amount);//充值金额
		metaSignMap.put("sign", XpayToolKit.generateSign(metaSignMap));
		
		metaSignMap.put("pay", pay);//支付通道1(微信)2(支付宝)3（H5支付宝）
		metaSignMap.put("ip", ip);
		metaSignMap.put("urlht", urlht);//回调通知 
		metaSignMap.put("urltz", urltz);//支付完跳转 

	
		String resultJsonStr = HttpsUtil.postMethod(reqUrl, metaSignMap, "UTF-8");

		System.out.println(resultJsonStr);
		if(StringUtils.isNotBlank(resultJsonStr)) {
			JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
			String status = resultJsonObj.getString("status");
			if(!"1".equalsIgnoreCase(status)) {
				String msg = resultJsonObj.getString("msg");
				System.out.println(msg);
			}else {
				String url = resultJsonObj.getString("url");
				System.out.println(url);
			}
		}
		
	}
	
	public static void query() {
		String orderid = "20200321150353778"; 
		
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("shid", XpayToolKit.SHID);//商户账号
		metaSignMap.put("orderid", orderid);//充值订单号
		metaSignMap.put("sign", XpayToolKit.generateSign(metaSignMap));
		metaSignMap.put("pay", "1");

		String resultJsonStr = HttpsUtil.postMethod(queryUrl, metaSignMap, "UTF-8");

		System.out.println(resultJsonStr);
		if(StringUtils.isNotBlank(resultJsonStr)) {
			JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
			String status = resultJsonObj.getString("status");
			if("3".equalsIgnoreCase(status)) {
				System.out.println("付款确认完成");
			}else {
				System.out.println("其他情况");
			}
		}
	}

}
