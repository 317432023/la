package com.jeetx.common.pay.bf;


import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.util.Base64;

import net.sf.json.JSONObject;

public class RemitDemo {

	static String merNo = "DR180305164013272";// 商户号，商户需改为自己的
	static String key = "1431FE2BAB92E614441A7A0485";// MD5签名密钥，32位，商户需改为自己的
	static String desKey = "PP3GWZ6RR1MOeB5frsqbWQr8";// 3DES加密密钥，24位，商户需改为自己的
	static String reqUrl = "http://defray.948pay.com:8188/api/remit.action";// 代付接口请求地址，无需更改

	/**
	 * 代付结果处理方法
	 * @throws Throwable 
	 */
	public static void result(HttpServletRequest request,
			HttpServletResponse response) throws Throwable {
		String data = request.getParameter("data");
		System.out.println("data=" + data);
		JSONObject jsonObj = JSONObject.fromObject(data);
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("merchantNo", jsonObj.getString("merchantNo"));
		metaSignMap.put("orderNum", jsonObj.getString("orderNum"));
		metaSignMap.put("payAmount", jsonObj.getString("payAmount"));
		metaSignMap.put("remitResult", jsonObj.getString("remitResult"));
		metaSignMap.put("remitDate", jsonObj.getString("remitDate"));// yyyy-MM-dd HH:mm:ss
		String jsonStr = BfToolKit.mapToJson(metaSignMap);
		String sign = BfToolKit.MD5(jsonStr.toString() + key, "UTF-8");
		if(!sign.equals(jsonObj.getString("sign"))){
			System.out.println("签名校验失败");
			return;
		}
		System.out.println("签名校验成功");
		response.getOutputStream().write("000000".getBytes());//强制要求返回000000
	}
	
	/**
	 * 代付方法
	 * @throws Exception
	 */
	public static void remit(HttpServletRequest request) throws Exception {
		Map<String, String> metaSignMap = new TreeMap<String, String>();
		metaSignMap.put("merchantNo", merNo);
		String orderNum = new SimpleDateFormat("yyyyMMddHHmmssSSS")
				.format(new Date()); 
		orderNum += BfToolKit.randomStr(3);
		metaSignMap.put("orderNum", orderNum);
		metaSignMap.put("merchantNo", merNo);
		metaSignMap.put("bankCode", "ICBC");// 银行代码 参考对照表
		metaSignMap.put("bankAcctName", Encrypt3DES("户名", desKey));// 开户名
		metaSignMap.put("bankAcctNo", Encrypt3DES("银行卡号", desKey));// 银行卡号
		metaSignMap.put("payAmount", Encrypt3DES("100", desKey));// 金额 单位:分
		metaSignMap.put("callBackUrl", "http://127.0.0.1/");// 代付结果通知地址
		metaSignMap.put("requestIP", BfToolKit.getRemoteIP(request) );// 客户ip地址
		
		String metaSignJsonStr = BfToolKit.mapToJson(metaSignMap);
		String sign = BfToolKit.MD5(metaSignJsonStr + key, "UTF-8");// 32位
		System.out.println("sign=" + sign); // 英文字母大写
		metaSignMap.put("sign", sign);
		String reqParam = "paramData=" + URLEncoder.encode(BfToolKit.mapToJson(metaSignMap),"UTF-8");
		String resultJsonStr = BfToolKit.request(reqUrl, reqParam);

		// 检查状态
		JSONObject resultJsonObj = JSONObject.fromObject(resultJsonStr);
		String stateCode = resultJsonObj.getString("resultCode");
		if (!stateCode.equals("00")) {
			System.out.println("代付提交失败");
			return;
		}
		String resultSign = resultJsonObj.getString("sign");
		resultJsonObj.remove("sign");
		String targetString = BfToolKit.MD5(resultJsonObj.toString() + key, "UTF-8");
		if (targetString.equals(resultSign)) {
			System.out.println("签名校验成功");
		}else{
			System.out.println("签名校验失败");
		}
	}
	
	private static final String Algorithm = "DESede"; // 定义 加密算法,可用

	// / <summary>
	// / 3des解码
	// / </summary>
	// / <param name="value">待解密字符串</param>
	// / <param name="key">原始密钥字符串</param>
	// / <returns></returns>
	public static String Decrypt3DES(String value, String key) throws Exception {
		//byte[] b = decryptMode(key.getBytes(), Base64.getDecoder().decode(value));
		byte[] b = decryptMode(key.getBytes(), Base64.decodeBase64(value));
		return new String(b,"UTF-8");
	}

	// / <summary>
	// / 3des加密
	// / </summary>
	// / <param name="value">待加密字符串</param>
	// / <param name="strKey">原始密钥字符串</param>
	// / <returns></returns>
	public static String Encrypt3DES(String value, String key) throws Exception {
		String str = byte2Base64(encryptMode(key.getBytes(), value.getBytes("UTF-8")));
		return str;
	}

	// keybyte为加密密钥，长度为24字节
	// src为被加密的数据缓冲区（源）
	public static byte[] encryptMode(byte[] keybyte, byte[] src) {
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm); // 加密
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	// keybyte为加密密钥，长度为24字节
	// src为加密后的缓冲区
	private static byte[] decryptMode(byte[] keybyte, byte[] src) {
		try { // 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
			// 解密
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.DECRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	// 转换成base64编码
	public static String byte2Base64(byte[] b) {
		//return Base64.getEncoder().encodeToString(b);
		return Base64.encodeBase64String(b);
	}
}
