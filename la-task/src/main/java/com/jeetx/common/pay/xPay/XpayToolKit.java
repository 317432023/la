package com.jeetx.common.pay.xPay;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.jeetx.util.MD5Util;

public class XpayToolKit {

	public static String SHID = "xingsheng";
	public static String SHKEY = "EOEQXG9KPCO7U2HEKVGX0NGYNWHYC9NL";// MD5签名密钥，商户需改为自己的
	
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
		for (Map.Entry<String, String> m : list) {
			if(StringUtils.isNotBlank(m.getValue())) {
				sign.append(m.getKey()).append(m.getValue());
			}
		}
		sign = sign.append(SHKEY);
		System.out.println(sign);
		//System.out.println(MD5Util.MD5Encode(sign.toString(), "utf-8").toUpperCase());
		return MD5Util.MD5Encode(sign.toString(), "utf-8").toUpperCase();
	}
}
