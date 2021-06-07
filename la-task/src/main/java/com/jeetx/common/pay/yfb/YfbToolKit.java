package com.jeetx.common.pay.yfb;

import com.jeetx.util.MD5Util;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map.Entry;

public class YfbToolKit {

	public static String merchantNo = "7960";
	public static String appSecret = "kEHmhcPhAUygVhR9yDY0Q";
	public static String appKey = "Gbqx43O9MkiWSAnXDqRSTw";
	
	public static List<Entry<String, String>> sortMap(Map<String, String> map) {
		List<Entry<String, String>> infos = new ArrayList<Entry<String, String>>(map.entrySet());

		// 重写集合的排序方法：按字母顺序
		Collections.sort(infos, new Comparator<Entry<String, String>>() {
			@Override
			public int compare(Entry<String, String> o1, Entry<String, String> o2) {
				return (o1.getKey().toString().compareTo(o2.getKey()));
			}
		});

		return infos;
	}

	public static String getSHA256Str(String str) {
		MessageDigest messageDigest;
		String encdeStr = "";
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] hash = messageDigest.digest(str.getBytes("UTF-8"));
			encdeStr = Hex.encodeHexString(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encdeStr;
	}

	/**
	 * 获取签名串
	 * 把所有参数加入配置的secret参数先进行自然排序，然后所有参数用&字符连接 进行md5加密，url不用encode
	 * @param parasMap
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String generateSign(Map<String, String> parasMap) {
		StringBuffer sign = new StringBuffer("");

		List<Entry<String, String>> list = sortMap(parasMap);
		int i = 0;
		for (Entry<String, String> m : list) {
			if(!"userName".equalsIgnoreCase(m.getKey())||!"channelNo".equalsIgnoreCase(m.getKey())||!"payeeName".equalsIgnoreCase(m.getKey())
					||!"appSecret".equalsIgnoreCase(m.getKey())){
				sign.append(m.getKey()).append("=").append(m.getValue());
				if(list.size()!=i+1) {
					sign.append("&");
					//sign.append("&amp;");
				}
				i++;
			}

		}
		sign = sign.append(appKey);
		System.out.println(sign);
		//System.out.println(MD5Util.MD5Encode(sign.toString(), "utf-8").toUpperCase());
		return MD5Util.MD5Encode(getSHA256Str(sign.toString()), "utf-8").toUpperCase();
	}
}
