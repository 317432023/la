package com.jeetx.common.pay.lyf;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.jeetx.common.exception.BusinessException;
import com.jeetx.util.DateTimeTool;
import com.jeetx.util.HttpUtil;
import com.jeetx.util.LogUtil;
import com.jeetx.util.RandomUtil;
import com.microsoft.schemas.office.x2006.encryption.CTKeyEncryptor.Uri;

import net.sf.json.JSONObject;

public class PayDemo {

	static String reqUrl = "http://api.525dt.xyz/api/order/unified";
	static String queryUrl = "http://api.525dt.xyz/api/order/check_order";
	
	public static void main(String[] args) {
		//pay();
		query();
	}


	public static void pay() {
		String orderNum = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()); // 20位
		
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("appid", LyfToolKit.APPID);
		metaSignMap.put("order_no", orderNum);// 网关代码
		metaSignMap.put("amount", "20000");// 单位:分
		metaSignMap.put("openid", RandomUtil.generateLowerString(32));
		metaSignMap.put("product_name", "goodsName");// 商品名称：20位
		metaSignMap.put("bank_code", "911");
		metaSignMap.put("attach", "attach");
		metaSignMap.put("notify_url", "http://localhost/view");// 异步通知地址
		metaSignMap.put("return_url", "");// 同步通知地址
		metaSignMap.put("sign", LyfToolKit.generateSign(metaSignMap));

		String resultJsonStr = new HttpUtil().postMethod(reqUrl, metaSignMap, "utf-8");
		System.out.println(resultJsonStr);
		// 检查状态̬
		JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
		String status = resultJsonObj.getString("status");
		String message = LyfToolKit.unicodeToString(resultJsonObj.getString("message"));
		System.out.println(status);
		System.out.println(message);
		JSONObject dataJson = resultJsonObj.getJSONObject("data");
		System.out.println(dataJson.getString("redirect_url"));
	}
	

	public static void query() {
		String orderNum = "20190314151032218"; // 20位
		
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("appid", LyfToolKit.APPID);
		metaSignMap.put("order_no", orderNum);// 网关代码
		metaSignMap.put("start_time", DateTimeTool.dateFormat("yyyy-MM-dd", new Date()));// 单位:分
		metaSignMap.put("end_time", DateTimeTool.dateFormat("yyyy-MM-dd", new Date()));
		metaSignMap.put("sign", LyfToolKit.generateSign(metaSignMap));

		String resultJsonStr = new HttpUtil().getMethod(queryUrl.concat("?").concat(new HttpUtil().mapToString(metaSignMap)), "utf-8");
		System.out.println(resultJsonStr);
		
		JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
		String status = resultJsonObj.getString("status");
		if (status.equals("1")) {
			JSONObject dataJson = resultJsonObj.getJSONObject("data");
			if (orderNum.equals(dataJson.getString("order_no")) && "1".equalsIgnoreCase(dataJson.getString("pay_status"))) {
				System.out.println(resultJsonStr);

			}
		}

	}

}
