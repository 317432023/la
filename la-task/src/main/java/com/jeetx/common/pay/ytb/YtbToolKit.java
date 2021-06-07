package com.jeetx.common.pay.ytb;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YtbToolKit {

	public static String APP_ID = "10885";// 商户号
	public static String APP_SECECT = "9c127c8868d4c9cbe9096e412e9352770a205333";// 签名MD5密钥,24位

	public static String unicodeToString(String str) {
		try {
			Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
			Matcher matcher = pattern.matcher(str);
			char ch;
			while (matcher.find()) {
				String group = matcher.group(2);
				ch = (char) Integer.parseInt(group, 16);
				String group1 = matcher.group(1);
				str = str.replace(group1, ch + "");
			}
			return str;
		}catch (Exception e) {
			// TODO: handle exception
		}
		return str;
	}
}
