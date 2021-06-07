package com.jeetx.common.pay.jdzf;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONObject;

import com.jeetx.util.HttpUtil;

/**
 * 支付Demo
 * 
 */
public class PayDemo {
	public static String PAY_URL = "http://api.jdzf.net/pay/createOrder";
	public static String QUERY_URL = "http://api.jdzf.net/pay/queryOrder";
	
	public static void main(String[] args) throws Throwable {
		//pay();
		query();
	}

	/**
	 * 支付方法
	 */
	public static void pay() throws Exception {
		String businessId = JdzfToolKit.MERCHANT_ID;
		String outTradeNo = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());//商户订单号，保证唯一性 
		String amount = "300.00";//订单金额，单位：元，保留2 位小数 
		String payMethodId = "4";//支付方式ID
		String random = new Date().getTime()+"";//随机字符串，不长于32位
		String notifyUrl = "http://www.baidu.com";
		String returnUrl = "http://www.baidu.com";

		Map<String, String> parms = new TreeMap<String, String>();
		parms.put("businessId", businessId);
		parms.put("amount", amount);
		parms.put("outTradeNo", outTradeNo);
		parms.put("payMethodId", payMethodId);
		parms.put("notifyUrl", notifyUrl);
		parms.put("returnUrl", returnUrl);
		parms.put("random", random);

		Map<String, String> signMap = parms;
		signMap.put("secret", JdzfToolKit.MERCHANT_SECRET);
		
		parms.put("sign", JdzfToolKit.generateSign(signMap));

		//System.out.println(new HttpUtil().mapToString(parms));
		String resultJsonStr = new HttpUtil().postMethod(PAY_URL, parms,"UTF-8");
		//String resultJsonStr = new HttpUtil().getMethod(PAY_URL.concat("?").concat(new HttpUtil().mapToString(parms)), "utf-8");
		System.out.println(resultJsonStr);
	
		if(resultJsonStr.contains("successed")) {
			JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
			String errorDesc = resultJsonObj.getString("errorDesc");
			System.out.println(errorDesc);
		}else{
			System.out.println("成功");
		}



	}
	
	
	public static void query() throws Exception {
		String businessId = JdzfToolKit.MERCHANT_ID;
		String outTradeNo = "JZ1015520200217061448832535296";//商户订单号，保证唯一性 
		String random = new Date().getTime()+"";//随机字符串，不长于32位
		
		Map<String, String> parms = new TreeMap<String, String>();
		parms.put("businessId", businessId);
		parms.put("outTradeNo", outTradeNo);
		parms.put("random", random);

		Map<String, String> signMap = parms;
		signMap.put("secret", JdzfToolKit.MERCHANT_SECRET);
		
		parms.put("sign", JdzfToolKit.generateSign(signMap));

		String resultJsonStr = new HttpUtil().postMethod(QUERY_URL, parms,"UTF-8");
		System.out.println(resultJsonStr);
		
		if(resultJsonStr.contains("successed")) {
			JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
			String successed = resultJsonObj.getString("successed");
			if("true".contentEquals(successed)) {
				JSONObject dataJsonObj = JSONObject.fromObject(resultJsonObj.getString("returnValue"));
				System.out.println(dataJsonObj.getString("outTradeNo"));
			}else{
				System.out.println(resultJsonObj.getString("errorDesc"));
			}
		}
	}

}
