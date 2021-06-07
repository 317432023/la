package com.jeetx.common.pay.ytb;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.jeetx.common.pay.lyf.LyfToolKit;
import com.jeetx.util.HttpUtil;
import com.jeetx.util.MD5Util;

import net.sf.json.JSONObject;

/**
 * 支付Demo
 * 
 */
public class PayDemo {
	public static String PAY_URL = "http://pay.51haohuo.cn/apisubmit";
	public static String QUERY_URL = "http://pay.51haohuo.cn/apiorderquery";
	
	public static void main(String[] args) throws Throwable {
		pay();
		//query();
	}

	/**
	 * 支付方法
	 */
	public static void pay() throws Exception {
		String appid = YtbToolKit.APP_ID;
		String appsecect = YtbToolKit.APP_SECECT;

		String version = "1.0";
		String sdorderno = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		String total_fee = new BigDecimal(55).setScale(2, BigDecimal.ROUND_UNNECESSARY).toString();
		String paytype = "zhifu";//支付编号
		String bankcode = "";//银行编号
		String remark = "ytb";
		String notify_url = "http://www.baidu.com";
		String return_url = "http://www.baidu.com";
		String is_qrcode = "1";//值1为获取，值0不获取，只对扫码付款有效
		String signStr = "version="+version+"&customerid="+appid+"&total_fee="+total_fee+"&sdorderno="+sdorderno+"&notifyurl="+notify_url+"&returnurl="+return_url+"&"+appsecect;
		//System.out.println(signStr);
		
		Map<String, String> parms = new TreeMap<String, String>();
		parms.put("version", version);
		parms.put("customerid", appid);
		parms.put("sdorderno", sdorderno);
		parms.put("total_fee", total_fee);
		parms.put("paytype", paytype);
		parms.put("bankcode", bankcode);
		parms.put("returnurl",return_url);
		parms.put("notifyurl", notify_url);
		parms.put("remark", remark);
		parms.put("is_qrcode", is_qrcode);
		parms.put("sign", MD5Util.MD5Encode(signStr, "utf-8"));
		
		//String resultJsonStr = new HttpUtil().postMethod(PAY_URL, parms,"UTF-8");
		String resultJsonStr = new HttpUtil().getMethod(PAY_URL.concat("?").concat(new HttpUtil().mapToString(parms)), "utf-8");
		System.out.println(resultJsonStr);
		if(resultJsonStr.contains("payUrl")) {
			JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
			String code = resultJsonObj.getString("code");
			if("0".contentEquals(code)) {
				JSONObject dataJsonObj = JSONObject.fromObject(resultJsonObj.getString("data"));
				String payUrl = dataJsonObj.getString("payUrl");
				System.out.println(payUrl);
			}
		}


	}
	
	
	public static void query() throws Exception {
		String appid = YtbToolKit.APP_ID;
		String appsecect = YtbToolKit.APP_SECECT;

		String sdorderno = "ane2019032519165115302";
		String reqtime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String signStr = "customerid="+appid+"&sdorderno="+sdorderno+"&reqtime="+reqtime+"&"+appsecect;
		System.out.println(signStr);
		
		Map<String, String> parms = new TreeMap<String, String>();
		parms.put("customerid", appid);
		parms.put("sdorderno", sdorderno);
		parms.put("reqtime", reqtime);
		parms.put("sign", MD5Util.MD5Encode(signStr, "utf-8"));
		
		System.out.println(QUERY_URL.concat("?").concat(new HttpUtil().mapToString(parms)));
		String resultJsonStr = new HttpUtil().postMethod(QUERY_URL, parms,"UTF-8");
		System.out.println(resultJsonStr);
		// 检查状态̬
		JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
		String status = resultJsonObj.getString("status");
		String message = LyfToolKit.unicodeToString(resultJsonObj.getString("msg"));
		System.out.println(message);
	}

}
