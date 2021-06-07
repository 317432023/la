package com.jeetx.common.pay.xl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.jeetx.common.exception.BusinessException;
import com.jeetx.util.HttpUtil;

import net.sf.json.JSONObject;

/**
 * 支付Demo
 * 
 */
public class PayDemo {
	public static String PAY_URL = "http://api.cfpay188.com:8083/gateway/payapi/1.0/doPay";
	public static String QUERY_URL = "http://api.cfpay188.com:8083/gateway/payapi/1.0/doQuery";
	
	public static void main(String[] args) throws Throwable {
		//pay();
		query();
	}

	/**
	 * 支付方法
	 */
	public static void pay() throws Exception {
		String appid = XlToolKit.APP_ID;
		String appsecect = XlToolKit.APP_SECRET;
		String merId = XlToolKit.MERCHANT_ID;

		String version = "100";//接口版本[参与签名]
		String bizCode = "H0001";//业务编号[参与签名]
		String serviceType = "ALIPAY_H5PAY";//服务类别[参与签名]
		//String bizCode = "S0001";//业务编号[参与签名]
		//String serviceType = "ALIPAY_SCANPAY";//服务类别[参与签名]
		String orderNo = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());//订单号[参与签名]
		String orderPrice = new BigDecimal(100).toString();//交易金额(元)
		String orderTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//订单时间
		String terminalIp = "120.36.46.178";
		String notify_url = "http://www.baidu.com";
		//String return_url = "http://www.baidu.com";
		
		if("ALIPAY_SCANPAY".equalsIgnoreCase(serviceType) || "WEIXIN_SCANPAY".equalsIgnoreCase(serviceType) ) {
			bizCode = "S0001";
		}else if("WEIXIN_H5PAY".equalsIgnoreCase(serviceType) || "ALIPAY_H5PAY".equalsIgnoreCase(serviceType) ) {
			bizCode = "H0001";
		}
		
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("version", version);
		metaSignMap.put("appId", appid);
		metaSignMap.put("appSecret", appsecect);
		metaSignMap.put("merId", merId);
		metaSignMap.put("bizCode", bizCode);
		metaSignMap.put("serviceType", serviceType);
		metaSignMap.put("orderPrice", orderPrice);
		metaSignMap.put("orderNo", orderNo);

		Map<String, String> parms = new TreeMap<String, String>();
		parms.put("version", version);
		parms.put("appId", appid);
		parms.put("appSecret", appsecect);
		parms.put("merId", merId);
		parms.put("bizCode", bizCode);
		parms.put("serviceType", serviceType);
		parms.put("orderNo", orderNo);
		parms.put("orderPrice", orderPrice);
		parms.put("goodsName", "充值卡");
		parms.put("goodsTag", "TAG");
		parms.put("orderTime", orderTime);
		parms.put("terminalIp", terminalIp);
		//parms.put("returnUrl",return_url);
		parms.put("notifyUrl", notify_url);
		parms.put("settleCycle", "D0");
		parms.put("sign", XlToolKit.generateSign(metaSignMap));

		System.out.println(new HttpUtil().mapToString(parms));
		String resultJsonStr = new HttpUtil().postMethod(PAY_URL, parms,"UTF-8");
		//String resultJsonStr = new HttpUtil().getMethod(PAY_URL.concat("?").concat(new HttpUtil().mapToString(parms)), "utf-8");
		System.out.println(resultJsonStr);
	
		if(resultJsonStr.contains("pay_url")) {
			JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
			String status = resultJsonObj.getString("status");
			if("01".contentEquals(status)) {
				JSONObject dataJsonObj = JSONObject.fromObject(resultJsonObj.getString("bodyMap"));
				System.out.println(dataJsonObj.getString("pay_url"));
			}
		}



	}
	
	
	public static void query() throws Exception {
		String appid = XlToolKit.APP_ID;
		String appsecect = XlToolKit.APP_SECRET;
		String merId = XlToolKit.MERCHANT_ID;

		String version = "100";//接口版本[参与签名]
		String bizCode = "H0001";//业务编号[参与签名]
		String orderNo = "20190326143649783";
		
		Map<String, String> parms = new TreeMap<String, String>();
		parms.put("version", version);
		parms.put("appId", appid);
		parms.put("appSecret", appsecect);
		parms.put("merId", merId);
		parms.put("orderNo", orderNo);
		parms.put("bizCode", bizCode);
		parms.put("sign", XlToolKit.generateSign(parms));

		//System.out.println(new HttpUtil().mapToString(parms));
		String resultJsonStr = new HttpUtil().postMethod(QUERY_URL, parms,"UTF-8");
		//String resultJsonStr = new HttpUtil().getMethod(PAY_URL.concat("?").concat(new HttpUtil().mapToString(parms)), "utf-8");
		System.out.println(resultJsonStr);
		JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
		String status = resultJsonObj.getString("status");
		String retCode = resultJsonObj.getString("retCode");
		if("01".contentEquals(status) && "200".contentEquals(retCode)) {

		}
	}

}
