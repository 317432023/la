package com.jeetx.common.pay.fbd;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.jeetx.util.HttpsUtil;
import com.jeetx.util.JsonUtil;

public class PayDemo {

	public static String reqUrl = "https://fullbitpay.co/api/wallet/v1/provideOrder";
	public static String queryUrl = "https://fullbitpay.co/api/wallet/v1/orderStatus";

	public static void main(String[] args) {
		pay();
		//query();
	}

	
	/**
	 * 支付方法
	 */
	public static void pay() {
		String orderId = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()); //商家订单编号 
		String amount = "300";//充值⾦额（单位：⼈民币，限制：正整数） 
		String userId = "1";//玩家编号（参阅重要通知-⿊名单功能）
		String gameCode = "2VE36FD4LT";//游戏参数（参阅附注-商家资讯）
		String type = "1";//⽀付渠道 （1：⽀付宝，2：微信⽀付，3：银⾏卡，4: 云闪付，5:⽀转银 扫码） 
		String exp = new Date().getTime()+"";//时间戳（单位：毫秒，⼗三位数） 
		String notifyUrl = "https://async.notify.url";
		
		Map<String, String> header = new TreeMap<String, String>();
		header.put("Content-type", "application/json");
		header.put("X-Notify-URL", notifyUrl);

		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("partnerId", FbdToolKit.PARTNERID);//商家编号（参阅附注-商家资讯）
		metaSignMap.put("userId", userId);
		metaSignMap.put("amounts", amount);
		metaSignMap.put("gameCode", gameCode);
		metaSignMap.put("orderId", orderId);
		metaSignMap.put("type", type);
		metaSignMap.put("exp", exp);
		metaSignMap.put("sign", FbdToolKit.generateSign(metaSignMap));//签名（参阅附注-加密⽅式）
		
		String resultJsonStr = HttpsUtil.postMethod(reqUrl, JsonUtil.map2json(metaSignMap),header, "UTF-8");
		//String resultJsonStr = HttpsUtil.postMethod(reqUrl, metaSignMap,header, "UTF-8");
	
		System.out.println("resultJsonStr:"+resultJsonStr);
		if(StringUtils.isNotBlank(resultJsonStr)) {
			JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
			String status = resultJsonObj.getString("status");
			if("1".equalsIgnoreCase(status)) {
				String msg = resultJsonObj.getString("msg");
				String data = resultJsonObj.getString("data");
				JSONObject dataObj = resultJsonObj.fromObject(data);
				String tokenId = dataObj.getString("tokenId");
				String orderCode = dataObj.getString("orderCode");
				String url = "https://fullbitpay.co/api/wallet/provide/"+orderCode+"/"+tokenId;
				
				System.out.println(msg);
				System.out.println(tokenId);
				System.out.println(orderCode);
				System.out.println(url);
			}else {
				String url = resultJsonObj.getString("msg");
				System.out.println(url);
			}
		}
		
	}

	public static void query() {
		String orderId = "2131232321";
		String exp = new Date().getTime()+"";//时间戳（单位：毫秒，⼗三位数）

		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("partnerId", FbdToolKit.PARTNERID);//商家编号（参阅附注-商家资讯）
		metaSignMap.put("orderId", orderId);
		metaSignMap.put("exp", exp);
		metaSignMap.put("sign", FbdToolKit.generateSign(metaSignMap));//签名（参阅附注-加密⽅式）

		Map<String, String> header = new TreeMap<String, String>();
		header.put("Content-type", "application/json");

		String resultJsonStr = HttpsUtil.postMethod(queryUrl, JsonUtil.map2json(metaSignMap),header, "UTF-8");

		System.out.println("resultJsonStr:"+resultJsonStr);
		if(StringUtils.isNotBlank(resultJsonStr)) {
			JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
			String status = resultJsonObj.getString("status");
			if("1".equalsIgnoreCase(status)) {
				String msg = resultJsonObj.getString("msg");
				String data = resultJsonObj.getString("data");
				JSONObject dataObj = resultJsonObj.fromObject(data);
				String _status = dataObj.getString("status");
				System.out.println(msg);
				System.out.println(_status);
			}
		}

	}
	
}
