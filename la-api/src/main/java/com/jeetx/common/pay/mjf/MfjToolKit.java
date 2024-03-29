package com.jeetx.common.pay.mjf;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.net.util.Base64;

import com.jeetx.util.LogUtil;

public class MfjToolKit {
	static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	
//	public static String MERCH_NO = "MJF201804210000";// 商户号
//	public static String KEY = "7051F630D7D80DC4A41CD25495D34E01";// 签名MD5密钥,24位
//	
//	// 支付公钥
//	public static final String PAY_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCZB0vtpqUIedv3Law11dmx7KAR2qNbfP0MblBNGeuymYmqGFIGNp5HHi7fhIviK7ttuDQsK7hHVxT44S9JZ+sEUgs1+Rl3s8rtkMbArnFN2hPLrFt1XMuAOeMEABfzX4iFoSX13on8vJPoBcfFYXsV2CRD0Vl54nL7E7+Ad0/M/wIDAQAB";
//
//	// 代付公钥
//	public static final String REMIT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCAIyp4FwrgBlUdPR0zBPit6c4vXEIx6fkwE1Jm/IGgNfCSJxvOE32AZPJwwFb42xRc0gPPd6tRmYoaNib29pWpJQMsJn58UKEk+/qjJZXUzUdzivdL/J2p8RZshJW9VpvSHOHL9PssHtFlDrHZ20rB8l39kByo+eZvubzd0fn0cwIDAQAB";
//	// 商户私钥
//	public static final String PRIVATE_KEY = "MIICcwIBADANBgkqhkiG9w0BAQEFAASCAl0wggJZAgEAAoGBAITbaa1yuQw6suUn15cjqmYehMU5\r\n" + 
//			"Pe5A7vrsxefAdsQMOOfFdccbB8+bEQujWhBvj/wavW/IhAOh+tKadHeCMrk+HbeBOPAyDRL5rqbB\r\n" + 
//			"JIhZox8bWsJnBSiXAV0V8IQNSGteuaP8kKW5SDUytXyynoaHKG/esm8hZ5eHZqrYE7QpAgMBAAEC\r\n" + 
//			"f24FRX5dguCdu4gJdn+zCWmH3Gt2+9JR85XYfgttVu0DMhy9aMt5lv5Va8g3fl4eBR7BBh+L7ccU\r\n" + 
//			"XKILRmexak69qyIE1dK389wSjUNBlqLbKgJ8vN6PE0uwwXnhdoqeNLeuR0GbmSL0U5jaVAXocPZp\r\n" + 
//			"91mwGQDgmWsIHtmoz5ECQQD5Y2QkqNH2TmeYTi+aivwCJxgYv0aMTM85RgI/lGMJ8TR5DMMxBVrM\r\n" + 
//			"de2l9nSc92HQ50ivqlBxeEolHegkdBsvAkEAiGEem2TWUcBi5bXL6THUQf1lnq+U2sluLV4sLpWU\r\n" + 
//			"1vdpwD8RPAWQS6iOlBTq0Xg0lbybhqREJsEPCXuevbFwJwJAT05tJT9Obb5nMUcD2miCVYjrH8uL\r\n" + 
//			"+sfeSj9aLa/ZgurinfASDn++bTC/XzytvtDomU9DjSFjLYJsSbtl/Fmz3wJAflWpar4Ao36anrzI\r\n" + 
//			"Bl+4huDb0CJKvFo4jCJev1ClrnVHK2XA618kClI8sgSmeiDmZYdwc0ucLmJNMbWomYTTpQJAeq0s\r\n" + 
//			"FCp41Zp5wVhi1y84vMtSEaeP+j4K5FJYRwRgblYyUbTIXfXUWfvBUdfOLlD68mmQlNm0fj+xi5f5\r\n" + 
//			"29bf9Q==";
	
	public static String MERCH_NO = "MJF201804240205";// 商户号
	public static String KEY = "FC9066CD2889D2C122FA8E166A8E4508";// 签名MD5密钥,24位
	
	// 支付公钥
	public static final String PAY_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDc83bL7UaqWY5KNt8JWyrv/70mLoEOdYZo3/9l1/JKg53h9BRenv+LroGJXHpx/pjMoP2TEwzRMBHKu5z4RVe338ljDbInOtFaAG4Z9352QMfiZHLpn4V0NXD05DYEl5XVYN0RdMwNkrfL7R3KxSfzoORpMNNJDlJqvMNU1yUbuwIDAQAB";

	// 代付公钥
	public static final String REMIT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDIL2fJflOwni/wmbrXdWixjmZ+oRi5iTm8eSuqeQZ3IuPhvW+xdfG69q9M4IkDiJmEexTNsuGL+cdLgpEDi3RgyBLyf9EEFf+bDKqqxmLJ6z/ytWphFbDzdpKB4kQ2UVvGhM7d1MdyWtpba8HcG0oO47pzFDMYZP9ZBjy/pPTZyQIDAQAB";
	// 商户私钥
	public static final String PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJN9eBtg/RjvmadD+wuUyk+H81P7\r\n" + 
			"FbQveW1jIP7DFcEzEBFTQPqRZJf3OeMIT52DG1NuP5Jl5LfDIZGbj2nN+2Nj4uIQXIMzQS6VjD9h\r\n" + 
			"MN/uZL6mOv95dTz9TZ0Y+WX5B+Td3iXSuHfORvLlaN0FIIp5eYmcvz39+YNiaS6rpEyTAgMBAAEC\r\n" + 
			"gYAe9b6JmMFoQ5twdh+znaceLL6SSWmT6kdGBAIAGHlvlcVwrZY9Qtnn4HihRLRZrxTEXacwlvz8\r\n" + 
			"6hQY2ZuwLRHwxm6K65BFCEdYNa0LTxc+e93l4X2aI9zOR9fE8KY6qu4kvqToinBIuLdskAII8SBd\r\n" + 
			"rQ9xIWwui/RQvVIhADqOsQJBAPPGjWfE/XG93q1NKIsDTNfaIM2jIScifM/1Juw98GFkm2Heenqg\r\n" + 
			"L4EMAM7Xt5YH3FdQJbABzQVqIpGqVrFBbEsCQQCa4txG38AaRPuELsUHvcgHG5VHYWhaMhu9D0cR\r\n" + 
			"fGg6cu3XJo7dyHd5iO33s976eEuhLro8BqFCDGWt1obM2+PZAkAuJWnjOOEZRO4ANFj2Z7uGKN6I\r\n" + 
			"ztaZx+eEnk2t7tOwPBFF542b5PLZJOKKRT+VgQtu49ceiFi9K6g7ltkuWModAkB+wV5IWmEano3A\r\n" + 
			"FY6F0VjcCETYz+zYs91jkhc1Rjz+aIQg6ZtOAftU6Szm5Lt8++nZFSawbTSXmxGzfLeA/AuxAkBh\r\n" + 
			"TUxAik6oaK6Qfc/Ky5YxwcB/bXt7wRBWdrDGhyKyUfql8HfsRaduJGkx6aQnV12Lj26HqaHQN+RH\r\n" + 
			"2/2CCf8l";

	//public static int blockSize = 128;
	// 非对称密钥算法
	public static final String KEY_ALGORITHM = "RSA";
	public final static String CHARSET = "UTF-8";

	/**
	 * 获取响应报文
	 * 
	 * @param in
	 * @return
	 */
	private static String getResponseBodyAsString(InputStream in) {
		try {
			BufferedInputStream buf = new BufferedInputStream(in);
			byte[] buffer = new byte[1024];
			StringBuffer data = new StringBuffer();
			int readDataLen;
			while ((readDataLen = buf.read(buffer)) != -1) {
				data.append(new String(buffer, 0, readDataLen, CHARSET));
			}
			LogUtil.info("明捷支付响应报文=" + data);
			return data.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 提交请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String request(String url, String params) {
		try {
			LogUtil.info("明捷支付请求报文:" + params);
			URL urlObj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setConnectTimeout(1000 * 5);
			//conn.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
			//conn.setRequestProperty("Accept","*/*");
			conn.setRequestProperty("Charset", CHARSET);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(params.length()));
			OutputStream outStream = conn.getOutputStream();
			outStream.write(params.toString().getBytes(CHARSET));
			outStream.flush();
			outStream.close();
			return getResponseBodyAsString(conn.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * MD5加密
	 * 
	 * @param s
	 * @param encoding
	 * @return
	 */
	public final static String MD5(String s, String encoding) {
		try {
			byte[] btInput = s.getBytes(encoding);
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
				str[k++] = HEX_DIGITS[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 转换成Json格式
	 * 
	 * @param map
	 * @return
	 */
	public static String mapToJson(Map<String, String> map) {
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		StringBuffer json = new StringBuffer();
		json.append("{");
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			String key = entry.getKey();
			String value = entry.getValue();
			json.append("\"").append(key).append("\"");
			json.append(":");
			json.append("\"").append(value).append("\"");
			if (it.hasNext()) {
				json.append(",");
			}
		}
		json.append("}");
		//LogUtil.info("mapToJson=" + json.toString());
		return json.toString();
	}

	/**
	 * 生成随机字符
	 * 
	 * @param num
	 * @return
	 */
	public static String randomStr(int num) {
		char[] randomMetaData = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
				'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
				'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4',
				'5', '6', '7', '8', '9' };
		Random random = new Random();
		String tNonceStr = "";
		for (int i = 0; i < num; i++) {
			tNonceStr += (randomMetaData[random.nextInt(randomMetaData.length - 1)]);
		}
		return tNonceStr;
	}

	/**
	 * 公钥加密
	 * 
	 * @param data待加密数据
	 * @param key
	 *            密钥
	 * @return byte[] 加密数据
	 */
	public static byte[] encryptByPublicKey(byte[] data, String publicKey) {
		//byte[] key = Base64.getDecoder().decode(publicKey);
		byte[] key = Base64.decodeBase64(publicKey);
		// 实例化密钥工厂
		KeyFactory keyFactory;
		try {
			keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			// 密钥材料转换
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
			// 产生公钥
			PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
			// 数据加密
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			int blockSize = cipher.getOutputSize(data.length) - 11;
			return doFinal(data, cipher,blockSize);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 私钥解密
	 * 
	 * @param data
	 *            待解密数据
	 * @param key
	 *            密钥
	 * @return byte[] 解密数据
	 */
	public static byte[] decryptByPrivateKey(byte[] data, String privateKeyValue) {
		//byte[] key = Base64.getDecoder().decode(privateKeyValue);
		byte[] key = Base64.decodeBase64(privateKeyValue);
		try {
			// 取得私钥
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			// 生成私钥
			PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
			// 数据解密
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			int blockSize = cipher.getOutputSize(data.length);
			return doFinal(data, cipher,blockSize);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 加密解密共用核心代码，分段加密解密
	 * 
	 * @param decryptData
	 * @param cipher
	 * @return
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws IOException
	 */
	public static byte[] doFinal(byte[] decryptData, Cipher cipher,int blockSize)
			throws IllegalBlockSizeException, BadPaddingException, IOException {
		int offSet = 0;
		byte[] cache = null;
		int i = 0;
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		while (decryptData.length - offSet > 0) {
			if (decryptData.length - offSet > blockSize) {
				cache = cipher.doFinal(decryptData, offSet, blockSize);
			} else {
				cache = cipher.doFinal(decryptData, offSet, decryptData.length - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * blockSize;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return encryptedData;
	}

	public static void main(String[] args) throws Exception {
	}
}
