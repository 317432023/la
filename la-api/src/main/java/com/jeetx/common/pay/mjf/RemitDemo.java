package com.jeetx.common.pay.mjf;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import net.sf.json.JSONObject;


/**
 * 代付
 * 
 * @author
 * 
 */
public class RemitDemo {
	static String merchNo = "MJF201804210000";// 商户号
	static String key = "7051F630D7D80DC4A41CD25495D34E01";// 签名MD5密钥,24位
	static String reqUrl = "http://39.105.8.4:9803/api/remit";
	static String reqQueryUrl = "http://39.105.8.4:9803/api/queryRemitResult";

	public static void main(String[] args) throws Exception {
		 remit();
		 //remitQuery();
	}

	
	/**
	 * 代付方法
	 * 
	 * @throws Exception
	 */
	public static void remit() throws Exception {
		Map<String, String> metaSignMap = new TreeMap<String, String>();

		String orderNum = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()); // 20位
		orderNum += MfjToolKit.randomStr(3);
		metaSignMap.put("orderNum", orderNum);
		metaSignMap.put("version", "V3.0.0.0");// 版本号
		metaSignMap.put("charset", MfjToolKit.CHARSET);// 编码

		// 需要经常修改的参数
		metaSignMap.put("bankCode", "ICBC");// 银行代码 参考对照表
		metaSignMap.put("merchNo", merchNo); // 商户号
		metaSignMap.put("bankAccountName", "户名");// 账户名
		metaSignMap.put("bankAccountNo", "6217002200015935552");// 银行卡号
		metaSignMap.put("amount", "1650");// 金额 单位:分
		metaSignMap.put("callBackUrl", "http://127.0.0.1/");// 支付结果通知地址

		String metaSignJsonStr = MfjToolKit.mapToJson(metaSignMap);
		String sign = MfjToolKit.MD5(metaSignJsonStr + key, MfjToolKit.CHARSET);// 32位
		metaSignMap.put("sign", sign);
		byte[] dataStr = MfjToolKit.encryptByPublicKey(MfjToolKit.mapToJson(metaSignMap).getBytes(MfjToolKit.CHARSET),
				MfjToolKit.REMIT_PUBLIC_KEY);
		String param = Base64.encode(dataStr);
		String reqParam = "data=" + URLEncoder.encode(param, MfjToolKit.CHARSET) + "&merchNo=" + metaSignMap.get("merchNo")
				+ "&version=" + metaSignMap.get("version");
		String resultJsonStr = MfjToolKit.request(reqUrl, reqParam);
		// 检查状态
		JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
		String stateCode = resultJsonObj.getString("stateCode");
		if (!stateCode.equals("00")) {
			return;
		}
		String resultSign = resultJsonObj.getString("sign");
		resultJsonObj.remove("sign");
		String targetString = MfjToolKit.MD5(resultJsonObj.toString() + key, MfjToolKit.CHARSET);
		if (targetString.equals(resultSign)) {
			System.out.println("签名校验成功");
		}
	}

	
	
	/**
	 * 代付查询
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public static void remitQuery() throws UnsupportedEncodingException {
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("orderNum", "20180421194242837J6F");
		metaSignMap.put("remitDate", "2018-04-21");
		metaSignMap.put("merchNo", merchNo);
		metaSignMap.put("amount", "1650");// 单位:分

		String metaSignJsonStr = MfjToolKit.mapToJson(metaSignMap);
		String sign = MfjToolKit.MD5(metaSignJsonStr + key, MfjToolKit.CHARSET);// 32位
		System.out.println("sign=" + sign); // 英文字母大写
		metaSignMap.put("sign", sign);

		byte[] dataStr = MfjToolKit.encryptByPublicKey(MfjToolKit.mapToJson(metaSignMap).getBytes(MfjToolKit.CHARSET),
				MfjToolKit.REMIT_PUBLIC_KEY);
		String param = Base64.encode(dataStr);
		String reqParam = "data=" + URLEncoder.encode(param, MfjToolKit.CHARSET) + "&merchNo=" + metaSignMap.get("merchNo")
				+ "&version=V3.0.0.0";
		String resultJsonStr = MfjToolKit.request(reqQueryUrl, reqParam);
		// 检查状态
		JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
		String stateCode = resultJsonObj.getString("stateCode");
		if (!stateCode.equals("00")) {
			return;
		}
		String resultSign = resultJsonObj.getString("sign");
		resultJsonObj.remove("sign");
		String targetString = MfjToolKit.MD5(resultJsonObj.toString() + key, MfjToolKit.CHARSET);
		if (targetString.equals(resultSign)) {
			System.out.println("签名校验成功");
		}
	}

	/**
	 * 代付结果处理方法
	 * 
	 * @throws Throwable
	 */
	public static void remitResult(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		String data = request.getParameter("data");

		byte[] result = MfjToolKit.decryptByPrivateKey(Base64.decode(data), MfjToolKit.PRIVATE_KEY);
		String resultData = new String(result, MfjToolKit.CHARSET);
		System.out.println("解密数据：" + resultData);

		JSONObject jsonObj = JSONObject.fromObject(resultData);
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("merchNo", jsonObj.getString("merchNo"));
		metaSignMap.put("orderNum", jsonObj.getString("orderNum"));
		metaSignMap.put("amount", jsonObj.getString("amount"));
		metaSignMap.put("remitStateCode", jsonObj.getString("remitStateCode"));
		metaSignMap.put("remitDate", jsonObj.getString("remitDate"));// yyyyMMddHHmmss
		String jsonStr = MfjToolKit.mapToJson(metaSignMap);
		String sign = MfjToolKit.MD5(jsonStr.toString() + key, MfjToolKit.CHARSET);
		if (!sign.equals(jsonObj.getString("sign"))) {
			return;
		}
		System.out.println("签名校验成功");
		response.getOutputStream().write("SUCCESS".getBytes());
	}

	
}
