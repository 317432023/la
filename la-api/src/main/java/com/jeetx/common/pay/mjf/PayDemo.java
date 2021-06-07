package com.jeetx.common.pay.mjf;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 支付Demo
 * 
 */
public class PayDemo {
	public static String REQ_URL = "http://www.mjzfpay.com:90/api/pay";
	//public static String REQ_URL = "http://wx.mjzfpay.com:90/api/pay";
	//public static String QUERY_URL="http://39.105.8.4:9803/api/queryPayResult";
	
	public static void main(String[] args) throws Throwable {
		pay();
		//payQuery();
		
		
		//h5支付
//		JSONArray jsonArray = new JSONArray();
//		
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.put("netwayCode", "WX_WAP");
//		jsonObject.put("netwayTitle", "微信H5");
//		jsonObject.put("netwayIcon", "");
//		jsonArray.add(jsonObject);
//		
//		jsonObject = new JSONObject();
//		jsonObject.put("netwayCode", "ZFB_WAP");
//		jsonObject.put("netwayTitle", "支付宝H5");
//		jsonObject.put("netwayIcon", "");
//		jsonArray.add(jsonObject);
//		
//		jsonObject = new JSONObject();
//		jsonObject.put("netwayCode", "QQ_WAP");
//		jsonObject.put("netwayTitle", "QQH5");
//		jsonObject.put("netwayIcon", "");
//		jsonArray.add(jsonObject);
//		
//		jsonObject = new JSONObject();
//		jsonObject.put("netwayCode", "JD_WAP");
//		jsonObject.put("netwayTitle", "京东H5");
//		jsonObject.put("netwayIcon", "");
//		jsonArray.add(jsonObject);
//		
//		System.out.println(jsonArray);
//		
//		
//		//扫码支付
//		jsonArray = new JSONArray();
//		
//		jsonObject = new JSONObject();
//		jsonObject.put("netwayCode", "WX");
//		jsonObject.put("netwayTitle", "微信扫码");
//		jsonObject.put("netwayIcon", "");
//		jsonArray.add(jsonObject);
//		
//		jsonObject = new JSONObject();
//		jsonObject.put("netwayCode", "ZFB");
//		jsonObject.put("netwayTitle", "支付宝扫码");
//		jsonObject.put("netwayIcon", "");
//		jsonArray.add(jsonObject);
//		
//		
//		jsonObject = new JSONObject();
//		jsonObject.put("netwayCode", "QQ");
//		jsonObject.put("netwayTitle", "京东钱包");
//		jsonObject.put("netwayIcon", "");
//		jsonArray.add(jsonObject);
//		
////		jsonObject = new JSONObject();
////		jsonObject.put("netwayCode", "UNION_WALLET");
////		jsonObject.put("netwayTitle", "银联扫码");
////		jsonObject.put("netwayIcon", "");
////		jsonArray.add(jsonObject);
//		
//		jsonObject = new JSONObject();
//		jsonObject.put("netwayCode", "JD");
//		jsonObject.put("netwayTitle", "京东钱包");
//		jsonObject.put("netwayIcon", "");
//		jsonArray.add(jsonObject);
//		
//		System.out.println(jsonArray);
	}
	
	
	/**
	 * 支付方法
	 */
	public static void pay() throws Exception {
		String netwayCode = "ZFB";
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		String orderNum = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()); // 20位
		orderNum += MfjToolKit.randomStr(3);
		metaSignMap.put("orderNum", orderNum);
		metaSignMap.put("version", "V3.0.0.0");
		metaSignMap.put("charset", MfjToolKit.CHARSET);//
		metaSignMap.put("randomNum", MfjToolKit.randomStr(4));// 4位随机数

		metaSignMap.put("merchNo", MfjToolKit.MERCH_NO);
		metaSignMap.put("netwayCode", netwayCode);// WX:微信支付,ZFB:支付宝支付
		metaSignMap.put("amount", "5000");// 单位:分
		metaSignMap.put("goodsName", "笔");// 商品名称：20位
		metaSignMap.put("callBackUrl", "http://127.0.0.1/");// 回调地址
		metaSignMap.put("callBackViewUrl", "http://127.0.0.1/view");// 回显地址

		String metaSignJsonStr = MfjToolKit.mapToJson(metaSignMap);
		String sign = MfjToolKit.MD5(metaSignJsonStr + MfjToolKit.KEY, MfjToolKit.CHARSET);// 32位
		System.out.println("sign=" + sign); // 英文字母大写
		metaSignMap.put("sign", sign);

		byte[] dataStr = MfjToolKit.encryptByPublicKey(MfjToolKit.mapToJson(metaSignMap).getBytes(MfjToolKit.CHARSET),
				MfjToolKit.PAY_PUBLIC_KEY);
		String param = new BASE64Encoder().encode(dataStr);
		String reqParam = "data=" + URLEncoder.encode(param, MfjToolKit.CHARSET) + "&merchNo=" + metaSignMap.get("merchNo")
				+ "&version=" + metaSignMap.get("version");
		String resultJsonStr = MfjToolKit.request(REQ_URL.replace("www", netwayCode.toLowerCase().replace("_", "")), reqParam);
		// 检查状态
		JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
		String stateCode = resultJsonObj.getString("stateCode");
		if (!stateCode.equals("00")) {
			return;
		}
		String resultSign = resultJsonObj.getString("sign");
		resultJsonObj.remove("sign");
		String targetString = MfjToolKit.MD5(resultJsonObj.toString() + MfjToolKit.KEY, MfjToolKit.CHARSET);
		if (targetString.equals(resultSign)) {
			System.out.println("签名校验成功");
		}
	}
	
	
//	/**
//	 * 支付查询
//	 * 
//	 * @throws UnsupportedEncodingException
//	 */
//	public static void payQuery() throws UnsupportedEncodingException {
//		Map<String, String> metaSignMap = new TreeMap<String, String>();
//		metaSignMap.put("orderNum", "MJF201804210000");
//		metaSignMap.put("payDate", "2017-08-03");
//		metaSignMap.put("merchNo", MfjToolKit.MERCH_NO);
//		metaSignMap.put("netwayCode", "ZFB");// WX:微信支付,ZFB:支付宝支付
//		metaSignMap.put("amount", "500");// 单位:分
//		metaSignMap.put("goodsName", "笔");// 商品名称：20位
//
//		String metaSignJsonStr = MfjToolKit.mapToJson(metaSignMap);
//		String sign = MfjToolKit.MD5(metaSignJsonStr + MfjToolKit.KEY, MfjToolKit.CHARSET);// 32位
//		System.out.println("sign=" + sign); // 英文字母大写
//		metaSignMap.put("sign", sign);
//
//		byte[] dataStr = MfjToolKit.encryptByPublicKey(MfjToolKit.mapToJson(metaSignMap).getBytes(MfjToolKit.CHARSET),
//				MfjToolKit.PAY_PUBLIC_KEY);
//		String param = new BASE64Encoder().encode(dataStr);
//		String reqParam = "data=" + URLEncoder.encode(param, MfjToolKit.CHARSET) + "&merchNo=" + metaSignMap.get("merchNo")
//				+ "&version=V3.0.0.0";
//		String resultJsonStr = MfjToolKit.request(QUERY_URL, reqParam);
//		// 检查状态
//		JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
//		String stateCode = resultJsonObj.getString("stateCode");
//		if (!stateCode.equals("00")) {
//			return;
//		}
//		String resultSign = resultJsonObj.getString("sign");
//		resultJsonObj.remove("sign");
//		String targetString = MfjToolKit.MD5(resultJsonObj.toString() + MfjToolKit.KEY, MfjToolKit.CHARSET);
//		if (targetString.equals(resultSign)) {
//			System.out.println("签名校验成功");
//		}
//	}

	/**
	 * 支付结果处理
	 * 
	 * @throws Throwable
	 */
	public static void result(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		String data = request.getParameter("data");
		byte[] result = MfjToolKit.decryptByPrivateKey(new BASE64Decoder().decodeBuffer(data), MfjToolKit.PRIVATE_KEY);
		String resultData = new String(result, MfjToolKit.CHARSET);// 解密数据

		JSONObject jsonObj = JSONObject.fromObject(resultData);
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("merchNo", jsonObj.getString("merchNo"));
		metaSignMap.put("netwayCode", jsonObj.getString("netwayCode"));
		metaSignMap.put("orderNum", jsonObj.getString("orderNum"));
		metaSignMap.put("amount", jsonObj.getString("amount"));
		metaSignMap.put("goodsName", jsonObj.getString("goodsName"));
		metaSignMap.put("payStateCode", jsonObj.getString("payStateCode"));// 支付状态
		metaSignMap.put("payDate", jsonObj.getString("payDate"));// yyyyMMddHHmmss
		String jsonStr = MfjToolKit.mapToJson(metaSignMap);
		String sign = MfjToolKit.MD5(jsonStr.toString() + MfjToolKit.KEY, MfjToolKit.CHARSET);
		if (!sign.equals(jsonObj.getString("sign"))) {
			return;
		}
		System.out.println("签名校验成功");
		response.getOutputStream().write("SUCCESS".getBytes());
	}

}
