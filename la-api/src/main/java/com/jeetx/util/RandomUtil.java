package com.jeetx.util;

import java.util.Date;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
/**
 * 
 * 随机数、随即字符串工具
 */
public class RandomUtil {

	public static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String letterChar = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String numberChar = "0123456789";

	/**
	 * 返回一个定长的随机字符串(只包含大小写字母、数字)
	 * @param length  随机字符串长度
	 * @return 随机字符串
	 */
	public static String generateString(int length) {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(allChar.charAt(random.nextInt(allChar.length())));
		}
		return sb.toString();

	}
	
	/**
	 * 返回一个定长的随机字符串(只数字)
	 * @param length  随机字符串长度
	 * @return 随机字符串
	 */
	public static String generateInt(int length) {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(numberChar.charAt(random.nextInt(numberChar.length())));
		}
		return sb.toString();
	}

	/**
	 * 返回一个定长的随机纯字母字符串(只包含大小写字母)
	 * @param length 随机字符串长度
	 * @return 随机字符串
	 */
	public static String generateMixString(int length) {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(allChar.charAt(random.nextInt(letterChar.length())));
		}
		return sb.toString();
	}

	/**
	 * 
	 * 返回一个定长的随机纯大写字母字符串(只包含大小写字母)
	 * @param length 随机字符串长度
	 * @return 随机字符串
	 */
	public static String generateLowerString(int length) {
		return generateMixString(length).toLowerCase();
	}

	/**
	 * 
	 * 返回一个定长的随机纯小写字母字符串(只包含大小写字母)
	 * @param length 随机字符串长度
	 * @return 随机字符串
	 */
	public static String generateUpperString(int length) {
		return generateMixString(length).toUpperCase();
	}

	/**
	 * 生成一个定长的纯0字符串
	 * @param length 字符串长度 
	 * @return 纯0字符串
	 */
	public static String generateZeroString(int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append('0');
		}
		return sb.toString();
	}

	/**
	 * 根据数字生成一个定长的字符串，长度不够前面补0
	 * @param num 数字
	 * @param fixdlenth  字符串长度
	 * @return 定长的字符串
	 */
	public static String toFixdLengthString(long num, int fixdlenth) {
		StringBuffer sb = new StringBuffer();
		String strNum = String.valueOf(num);
		if (fixdlenth - strNum.length() >= 0) {
			sb.append(generateZeroString(fixdlenth - strNum.length()));
		} else {
			throw new RuntimeException("将数字" + num + "转化为长度为" + fixdlenth+ "的字符串发生异常!");
		}
		sb.append(strNum);
		return sb.toString();
	}

	/**
	 * 
	 * 根据数字生成一个定长的字符串，长度不够前面补0
	 * @param num 数字
	 * @param fixdlenth 字符串长度
	 * @return 定长的字符串
	 */
	public static String toFixdLengthString(int num, int fixdlenth) {
		StringBuffer sb = new StringBuffer();
		String strNum = String.valueOf(num);
		if (fixdlenth - strNum.length() >= 0) {
			sb.append(generateZeroString(fixdlenth - strNum.length()));
		} else {
			throw new RuntimeException("将数字" + num + "转化为长度为" + fixdlenth + "的字符串发生异常!");
		}
		sb.append(strNum);
		return sb.toString();

	}
	
	
	/**
	 * 返回一个定长的随机字符串(只数字) 长度不够前面补0
	 * @param length  随机字符串长度
	 * @return 随机字符串
	 */
	public static String getRandomInt(int length) {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(numberChar.charAt(random.nextInt(numberChar.length())));
		}
		String strNum = sb.toString();
		
		StringBuffer sBuffer = new StringBuffer();
		if (length - strNum.length() >= 0) {
			sBuffer.append(generateZeroString(length - strNum.length()));
		} else {
			throw new RuntimeException("将数字" + strNum + "转化为长度为" + length + "的字符串发生异常!");
		}
		sBuffer.append(strNum);
		return sBuffer.toString();

	}
	
	/**
	 * 返回一个区间随机字数
	 */
	public static int getRangeRandom(int min,int max) {
		return new Random().nextInt(max) % (max - min + 1) + min; 
	}
	
	/**
	 * 生产编号信息
	 * @param type
	 * @param pattern
	 * @param length
	 * @return
	 */
	public static String getSeqNumber(String type,String pattern,int length) {
		StringBuffer sbBuffer = new StringBuffer();
		if(StringUtils.isNotBlank(type))
			sbBuffer.append(type);
		if(StringUtils.isNotBlank(pattern))
			sbBuffer.append(DateTimeTool.dateFormat(pattern, new Date()));
		sbBuffer.append(RandomUtil.getRandomInt(length));
		return sbBuffer.toString();
	}

	public static void main(String[] args) {
//		//System.out.println(RandomUtils.toFixdLengthString(45,2));
//		int max = 49;
//	    int min = 1;
//	    String ruleCode = "04";
//	    StringBuffer openContent = new StringBuffer();
//	    int i = 1;
//	    while (i<7) {
//		    int code = new Random().nextInt(max)%(max-min+1) + min;
//		    String codeStr = RandomUtil.toFixdLengthString(code,2);
//		    if(!openContent.toString().contains(codeStr) && !codeStr.equals(ruleCode)){
//		    	openContent.append(codeStr).append("+");
//		    	i++;
//		    }
//		}
//		openContent.append(ruleCode);
//	    System.out.println(openContent);
//		//openContent.append(ruleCode);
	    
	    System.out.println(RandomUtil.getRangeRandom(0,2));
	}

}