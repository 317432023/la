package com.jeetx.common.pay.bf;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

public class PayDemo {

	static String reqUrl = "http://defray.948pay.com:8188/api/smPay.action";// 支付接口请求地址，无需更改

	public static void main(String[] args) {
		pay();
//		//h5支付
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
//		jsonObject.put("netwayTitle", "QQ钱包");
//		jsonObject.put("netwayIcon", "");
//		jsonArray.add(jsonObject);
//		
//		jsonObject = new JSONObject();
//		jsonObject.put("netwayCode", "JDQB_WAP");
//		jsonObject.put("netwayTitle", "京东钱包");
//		jsonObject.put("netwayIcon", "");
//		jsonArray.add(jsonObject);
//		
//		jsonObject = new JSONObject();
//		jsonObject.put("netwayCode", "YL_WAP");
//		jsonObject.put("netwayTitle", "银联H5");
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
//		jsonObject.put("netwayTitle", "QQ钱包");
//		jsonObject.put("netwayIcon", "");
//		jsonArray.add(jsonObject);
//		
//		jsonObject = new JSONObject();
//		jsonObject.put("netwayCode", "YL");
//		jsonObject.put("netwayTitle", "银联扫码");
//		jsonObject.put("netwayIcon", "");
//		jsonArray.add(jsonObject);
//		
//		jsonObject = new JSONObject();
//		jsonObject.put("netwayCode", "JDQB");
//		jsonObject.put("netwayTitle", "京东钱包");
//		jsonObject.put("netwayIcon", "");
//		jsonArray.add(jsonObject);
//		
//		jsonObject = new JSONObject();
//		jsonObject.put("netwayCode", "KJ");
//		jsonObject.put("netwayTitle", "快捷支付");
//		jsonObject.put("netwayIcon", "");
//		jsonArray.add(jsonObject);
//		
//		jsonObject = new JSONObject();
//		jsonObject.put("netwayCode", "JD");
//		jsonObject.put("netwayTitle", "京东扫码");
//		jsonObject.put("netwayIcon", "");
//		jsonArray.add(jsonObject);
//		
//		System.out.println(jsonArray);

	}
	/**
	 * 支付结果处理
	 * @throws Throwable 
	 */
	public static void result(HttpServletRequest request,
			HttpServletResponse response) throws Throwable {
		String data = request.getParameter("paramData");
		JSONObject jsonObj = JSONObject.fromObject(data);
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("merchantNo", jsonObj.getString("merchantNo"));
		metaSignMap.put("netwayCode", jsonObj.getString("netwayCode"));
		metaSignMap.put("orderNum", jsonObj.getString("orderNum"));
		metaSignMap.put("payAmount", jsonObj.getString("payAmount"));
		metaSignMap.put("goodsName", jsonObj.getString("goodsName"));
		metaSignMap.put("resultCode", jsonObj.getString("resultCode"));// 支付状态
		metaSignMap.put("payDate", jsonObj.getString("payDate"));// yyyy-MM-dd
																	// HH:mm:ss
		String jsonStr = BfToolKit.mapToJson(metaSignMap);
		String sign = BfToolKit.MD5(jsonStr.toString() + BfToolKit.KEY, "UTF-8");
		if(!sign.equals(jsonObj.getString("sign"))){
			System.out.println("签名校验失败");
			return;
		}
		System.out.println("签名校验成功");
		response.getOutputStream().write("000000".getBytes());//强制要求返回000000
	}
	
	/**
	 * 支付方法
	 */
	public static void pay() {
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("merchantNo", BfToolKit.MER_NO);
		metaSignMap.put("netwayCode", "ZFB_WAP");// 网关代码
		metaSignMap.put("randomNum", BfToolKit.randomStr(4));// 4位随机数
		String orderNum = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()); // 20位
		orderNum += BfToolKit.randomStr(3);
		metaSignMap.put("orderNum", orderNum);
		metaSignMap.put("payAmount", "7000");// 单位:分
		metaSignMap.put("goodsName", "goodsName");// 商品名称：20位
		metaSignMap.put("callBackUrl", "http://127.0.0.1/");// 回调地址
		metaSignMap.put("frontBackUrl", "http://localhost/view");// 回显地址
		metaSignMap.put("requestIP","120.42.131.229");// 客户ip地址
		String metaSignJsonStr = BfToolKit.mapToJson(metaSignMap);
		String sign = BfToolKit.MD5(metaSignJsonStr + BfToolKit.KEY, "UTF-8");// 32位
		System.out.println("sign=" + sign); // 英文字母大写
		metaSignMap.put("sign", sign);
		String reqParam = "paramData=" + BfToolKit.mapToJson(metaSignMap);
		String resultJsonStr = BfToolKit.request(reqUrl, reqParam);

		// 检查状态̬
		JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
		String stateCode = resultJsonObj.getString("resultCode");
		if (!stateCode.equals("00")) {
			System.out.println("订单提交失败");
			return;
		}
		String resultSign = resultJsonObj.getString("sign");
		resultJsonObj.remove("sign");
		String targetString = BfToolKit.MD5(resultJsonObj.toString() + BfToolKit.KEY, "UTF-8");
		if (targetString.equals(resultSign)) {
			System.out.println("签名校验成功");
		}else{
			System.out.println("签名校验失败");
		}
	}

}
