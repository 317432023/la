package com.jeetx.common.pay.msf;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.jeetx.util.HttpUtil;

import net.sf.json.JSONObject;

/**
 * 支付Demo
 * 
 */
public class PayDemo {
	public static String REQ_URL = "http://pay.huitongworld.cn";
	//public static String REQ_URL = "https://pay.adw0755.com/DoSubmit/Payment?format=json";
	public static void main(String[] args) throws Throwable {
		//pay();
		query();
	}

	/**
	 * 支付方法
	 */
	public static void pay() throws Exception {
		String appid = MsfToolKit.APP_ID;
		String appsecect = MsfToolKit.APP_SECECT;

		String order_id = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		String pay_type = "f2f";
		String price = new BigDecimal(55).setScale(2, BigDecimal.ROUND_UNNECESSARY).toString();
		String name = "product_name";
		String prod_desc = "product_desc";
		String notify_url = "http://www.baidu.com";
		String return_url = "";
		SimpleDateFormat foramrt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String order_time = foramrt.format(new Date(System.currentTimeMillis()));
		String extend = "extend";
		
		String signStr = appid + name + pay_type + price + order_id + extend + notify_url + order_time + appsecect;
		
		Map<String, String> parms = new TreeMap<String, String>();
		parms.put("order_id", order_id);
		parms.put("appid", appid);
		parms.put("pay_type", pay_type);//支付方式（可选wechat/f2f）
		parms.put("price", price);// 4位随机数
		parms.put("name", name);
		parms.put("prod_desc", prod_desc);
		parms.put("return_url",return_url);
		parms.put("notify_url", notify_url);
		parms.put("extend", extend);
		parms.put("order_time", order_time);
		parms.put("sign", MsfToolKit.getMD5(signStr).toLowerCase());

		String resultJsonStr = new HttpUtil().postMethod(REQ_URL, parms,"UTF-8");
		if(StringUtils.isNotBlank(resultJsonStr)) {
			resultJsonStr = resultJsonStr.substring(resultJsonStr.indexOf("window.location.href"), resultJsonStr.length());
			resultJsonStr = resultJsonStr.substring(resultJsonStr.indexOf("/"), resultJsonStr.indexOf(";")-1);
		}
		System.out.println(REQ_URL.concat(resultJsonStr));

	}
	
	
	public static void query() throws Exception {
        String orderNo = "20190228135659745";
		String appid = MsfToolKit.APP_ID;
		String appsecect = MsfToolKit.APP_SECECT;
        String sign = MsfToolKit.getMD5(appid+appsecect).toLowerCase();

        String content = new HttpUtil().getMethod("http://merchant.huitongworld.cn/query/FindOrder?orderNo=" + orderNo+ "&appid=" + appid + "&sign=" + sign, "UTF-8");
        
        JSONObject resultJsonObj = JSONObject.fromObject(content);
		String status = resultJsonObj.getString("status");
		if (status.equals("order_payed")) {
			System.out.println("222222222");
		}
        System.out.println("Query result:"+content);
		System.out.println("quer end");
	}

}
