package com.jeetx.util;

import org.apache.commons.codec.binary.Base64;

import com.jeetx.common.constant.Globals;

/**
 * AES安全编码组件校验
 * 
 * @author 梁栋
 * @version 1.0
 */
public class AESCoderTest {
	public static final String AESKEY = "4B35CFB9092DFD6F3DC23B0DE026FFA1";
	public static void main(String[] args) {
		try{
			/*String inputStr = "AES_CBC加密    123456789 abcdef  By:hmlyn";
			byte[] inputData = inputStr.getBytes("gbk");
			System.err.println("原文:\t" + inputStr);

			// 初始化密钥
			//byte[] key = AESCoder.initKey();
			byte[] key = "31323334353637383132333435363738".getBytes();
			// 初始化偏移量
			byte[] iv = new byte[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
			//byte[] iv = "3838383838383838".getBytes();
			
			System.err.println("密钥:\t" + Base64.encodeBase64String(key));
			System.err.println("密钥:\t" + Hex.encodeHexString(key));
			
			// 加密
			inputData = AESCoder.encrypt(inputData, key, iv);
			
			
			System.err.println("加密后:\t" + Base64.encodeBase64String(inputData));
			System.err.println("加密后:\t" + Hex.encodeHexString(inputData));
			//7n2yIA7o34VfaipWq//3bMMZhBJ7VMuof6JNJ9FK/NX3SWhmgWRY5wO6n+4NfgfQ

			// 解密
			byte[] outputData = AESCoder.decrypt(inputData, key, iv);

			String outputStr = new String(outputData,"gbk");
			System.err.println("解密后:\t" + outputStr);

			// 校验
			assertEquals(inputStr, outputStr);*/
			
			
			String inputStr = "AES_CBC加密    123456789 abcdef  By:hmlyn";
			byte[] inputData = inputStr.getBytes("gbk");
			System.err.println("原文:" + inputStr);

			byte[] key = AESKEY.getBytes();
			//byte[] iv = new byte[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};// 初始化偏移量
			byte[] iv = "0000000000000000".getBytes();// 初始化偏移量
			
			// 加密
			inputData = AESCoder.encrypt(inputData, key, iv);
			System.err.println("加密后:" + Base64.encodeBase64String(inputData));

			// 解密
			byte[] outputData = AESCoder.decrypt(inputData, key, iv);
			String outputStr = new String(outputData,"gbk");
			System.err.println("解密后:" + outputStr);
		}catch (Exception e) {
			e.printStackTrace();
		}

	}
}
