package com.jeetx.common.pay.jdzf;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jeetx.util.MD5Util;

public class JdzfToolKit {

	public static String MERCHANT_ID = "10155";
	public static String MERCHANT_SECRET = "14a04dbc2cb44a76a546027101dc1556";

	public static List<Map.Entry<String, String>> sortMap(Map<String, String> map) {
		List<Map.Entry<String, String>> infos = new ArrayList<Map.Entry<String, String>>(map.entrySet());

		// 重写集合的排序方法：按字母顺序
		Collections.sort(infos, new Comparator<Map.Entry<String, String>>() {
			@Override
			public int compare(Entry<String, String> o1, Entry<String, String> o2) {
				return (o1.getKey().toString().compareTo(o2.getKey()));
			}
		});

		return infos;
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
		
		List<Map.Entry<String, String>> list = sortMap(parasMap);
		int i = 0;
		for (Map.Entry<String, String> m : list) {
			sign.append(m.getKey()).append("=").append(m.getValue());
			if(list.size()!=i+1) {
				sign.append("&");
			}
			i++;
		}
		System.out.println(sign);
		return MD5Util.MD5Encode(sign.toString(), "utf-8").toLowerCase();
	}
}
