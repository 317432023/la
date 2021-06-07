package com.jeetx.common.pay.yfb;

import com.jeetx.util.HttpsUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class PayDemo {

	public static String reqUrl = "https://hb.thepay.co.nz/order/create";
	public static String queryUrl = "https://hb.thepay.co.nz/order/status";

	public static void main(String[] args) {
		//pay();
		query();

	}

	
	/**
	 * 支付方法
	 */
	public static void pay() {
		String orderNo = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()); 
		String amount = "300.00";//订单总金额,以元为单位
		String channelNo = "0";
		String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String notifyUrl = "http://127.0.0.1/";
		String time = new Date().getTime()+"";
		String appSecret = YfbToolKit.appSecret;
		
		Map<String, String> metaSignMap = new HashMap<String, String>();
		metaSignMap.put("merchantNo", YfbToolKit.merchantNo);//商户编号 (由隍寶支付平台提供,见:商户信息)
		metaSignMap.put("orderNo", orderNo);//商户单号 (用于对账、查询; 不超过32个字符)
		metaSignMap.put("amount", amount);//订单金额 (单位:元,2位小数; 最小充值金额1元,最大50000元。此金额可能会变动,请与隍寶支付平台确认),
		metaSignMap.put("channelNo", channelNo);//支付通道编号 (纯数字格式; 支付宝转支付宝:0 | 微信:1 | 支付宝转银联卡:2 | 云闪付扫码:3 | 卡转卡网关:4 | 聚合码:5) (不参加加密)
		metaSignMap.put("datetime", datetime);//日期时间 (格式:2018-01-01 23:59:59)
		metaSignMap.put("notifyUrl", notifyUrl);//异步通知地址 (当用户完成付款时,支付平台将向此URL地址,异步发送付款通知。建议使用 https)
		metaSignMap.put("time", time);//
		metaSignMap.put("appSecret", appSecret);//(由隍寶支付平台提供,见:商户信息/appSecret) (不参加加密)
		metaSignMap.put("sign", YfbToolKit.generateSign(metaSignMap));
		
		String resultJsonStr = HttpsUtil.postMethod(reqUrl, metaSignMap, "UTF-8");

		System.out.println(resultJsonStr);
		if(StringUtils.isNotBlank(resultJsonStr)) {
			JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
			String code = resultJsonObj.getString("code");
			if("0".equalsIgnoreCase(code)) {
				String text = resultJsonObj.getString("text");
				System.out.println(text);
			}else {
				String text = resultJsonObj.getString("text");
				System.out.println(text);
			}
		}
		
	}
	
	public static void query() {
		String orderNo = "20200321150353778";

		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("merchantNo", YfbToolKit.merchantNo);//商户编号 (由隍寶支付平台提供,见:商户信息)
		//metaSignMap.put("tradeNo", tradeNo);//交易单号 (隍寶支付平台的交易单号)
		metaSignMap.put("orderNo", orderNo);//商户单号 (用于对账、查询; 不超过32个字符)
		metaSignMap.put("time", new Date().getTime()+"");//
		metaSignMap.put("appSecret", YfbToolKit.appSecret);//(由隍寶支付平台提供,见:商户信息/appSecret) (不参加加密)
		metaSignMap.put("sign", YfbToolKit.generateSign(metaSignMap));

		String resultJsonStr = HttpsUtil.postMethod(queryUrl, metaSignMap, "UTF-8");
		System.out.println(resultJsonStr);

		if(StringUtils.isNotBlank(resultJsonStr)) {
			JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
			String code = resultJsonObj.getString("code");
			if("0".equalsIgnoreCase(code)) {
				String status = resultJsonObj.getString("status");
				System.out.println(status);
			}else {
				String text = resultJsonObj.getString("text");
				System.out.println(text);
			}
		}
	}

}
