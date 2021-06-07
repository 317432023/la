package com.jeetx.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpsUtil {

	public static Logger log = Logger.getLogger(HttpsUtil.class);
	public static final  int TIMEOUT = 60;//这个要弄长点  
	public static final String CONTENT_TYPE_NAME = "Content-Type";  
    public static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";  
    public static final String CONTENT_TYPE_XML = "text/xml;charset=UTF-8";  
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded;charset=UTF-8";  
    public static final String ACCEPT_NAME = "Accept";  
    public static final String ACCEPT = "application/json;charset=UTF-8";  
	
	/**
	 * 向HTTPS地址发送POST请求
	 * @param reqURL 请求地址
	 * @param params 请求参数
	 * @param aCharSet 编码
	 * @return 响应内容
	 */
	@SuppressWarnings("finally")
	public static String postMethod(String reqURL,Map<String, String> params,Map<String, String> header,String aCharSet) {
		long responseLength = 0; // 响应长度
		String responseContent = null; // 响应内容
		HttpClient httpClient = new DefaultHttpClient(); // 创建默认的httpClient实例
		X509TrustManager xtm = new X509TrustManager() { // 创建TrustManager
			public void checkClientTrusted(X509Certificate[] chain,String authType) throws CertificateException {}
			public void checkServerTrusted(X509Certificate[] chain,String authType) throws CertificateException {}
			public X509Certificate[] getAcceptedIssuers() {return null;}
		};
		
		try {
			// TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext
			SSLContext ctx = SSLContext.getInstance("TLS");
			// 使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
			ctx.init(null, new TrustManager[] { xtm }, null);
			// 创建SSLSocketFactory
			SSLSocketFactory socketFactory = new SSLSocketFactory(ctx,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			// 通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));

			HttpPost httpPost = new HttpPost(reqURL); // 创建HttpPost
	        for (Map.Entry<String, String> entry : header.entrySet()) {
	            //System.out.println(entry.getKey() + ": " + entry.getValue());
	        	httpPost.addHeader(entry.getKey(),entry.getKey());
	        }
			List<NameValuePair> formParams = new ArrayList<NameValuePair>(); // 构建POST请求的表单参数
			for (Map.Entry<String, String> entry : params.entrySet()) {
				formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(formParams, aCharSet));

			HttpResponse response = httpClient.execute(httpPost); // 执行POST请求
			HttpEntity entity = response.getEntity(); // 获取响应实体
			if (null != entity) {
				responseLength = entity.getContentLength();
				responseContent = EntityUtils.toString(entity, "UTF-8");
				EntityUtils.consume(entity); // Consume response content
			}
			//System.out.println("请求地址: " + httpPost.getURI());
			//System.out.println("响应状态: " + response.getStatusLine());
			//System.out.println("响应长度: " + responseLength);
			//System.out.println("响应内容: " + responseContent);
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown(); // 关闭连接,释放资源
			return responseContent;
		}
	}

	/**
	 * 向HTTPS地址发送POST请求
	 * @param reqURL 请求地址
	 * @param params 请求参数
	 * @param aCharSet 编码
	 * @return 响应内容
	 */
	@SuppressWarnings("finally")
	public static String postMethod(String reqURL,Map<String, String> params,String aCharSet) {
		long responseLength = 0; // 响应长度
		String responseContent = null; // 响应内容
		HttpClient httpClient = new DefaultHttpClient(); // 创建默认的httpClient实例
		X509TrustManager xtm = new X509TrustManager() { // 创建TrustManager
			public void checkClientTrusted(X509Certificate[] chain,String authType) throws CertificateException {}
			public void checkServerTrusted(X509Certificate[] chain,String authType) throws CertificateException {}
			public X509Certificate[] getAcceptedIssuers() {return null;}
		};
		
		try {
			// TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext
			SSLContext ctx = SSLContext.getInstance("TLS");
			// 使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
			ctx.init(null, new TrustManager[] { xtm }, null);
			// 创建SSLSocketFactory
			//SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
			SSLSocketFactory socketFactory = new SSLSocketFactory(ctx,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			// 通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));

			HttpPost httpPost = new HttpPost(reqURL); // 创建HttpPost
			List<NameValuePair> formParams = new ArrayList<NameValuePair>(); // 构建POST请求的表单参数
			for (Map.Entry<String, String> entry : params.entrySet()) {
				formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(formParams, aCharSet));

			HttpResponse response = httpClient.execute(httpPost); // 执行POST请求
			HttpEntity entity = response.getEntity(); // 获取响应实体
			if (null != entity) {
				responseLength = entity.getContentLength();
				responseContent = EntityUtils.toString(entity, "UTF-8");
				EntityUtils.consume(entity); // Consume response content
			}
			//System.out.println("请求地址: " + httpPost.getURI());
			//System.out.println("响应状态: " + response.getStatusLine());
			//System.out.println("响应长度: " + responseLength);
			//System.out.println("响应内容: " + responseContent);
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown(); // 关闭连接,释放资源
			return responseContent;
		}
	}
	
	/**
	 * 向HTTPS地址发送POST请求
	 * @param reqURL 请求地址
	 * @param josn 请求json或者xml
	 * @param aCharSet 编码
	 * @return 响应内容
	 */
	@SuppressWarnings("finally")
	public static String postMethod(String reqURL,String josn,Map<String, String> header,String aCharSet) {
		long responseLength = 0; // 响应长度
		String responseContent = null; // 响应内容
		HttpClient httpClient = new DefaultHttpClient(); // 创建默认的httpClient实例
		X509TrustManager xtm = new X509TrustManager() { // 创建TrustManager
			public void checkClientTrusted(X509Certificate[] chain,String authType) throws CertificateException {}
			public void checkServerTrusted(X509Certificate[] chain,String authType) throws CertificateException {}
			public X509Certificate[] getAcceptedIssuers() {return null;}
		};
		
		try {
			// TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext
			SSLContext ctx = SSLContext.getInstance("TLS");
			// 使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
			ctx.init(null, new TrustManager[] { xtm }, null);
			// 创建SSLSocketFactory
			SSLSocketFactory socketFactory = new SSLSocketFactory(ctx,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			// 通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));

			RequestConfig defaultRequestConfig = RequestConfig.custom()  
                    .setSocketTimeout(TIMEOUT * 1000)  
                    .setConnectTimeout(TIMEOUT * 1000)  
                    .setConnectionRequestTimeout(TIMEOUT * 1000)  
                    .build(); 
			
			HttpPost httpPost = new HttpPost(reqURL); // 创建HttpPost
			for (Map.Entry<String, String> entry : header.entrySet()) {
				//System.out.println(entry.getKey() + ": " + entry.getValue());
				httpPost.addHeader(entry.getKey(),entry.getKey());
			}
//			httpPost.setProtocolVersion(HttpVersion.HTTP_1_0);  
//			httpPost.addHeader(CONTENT_TYPE_NAME, CONTENT_TYPE_FORM);  
//			httpPost.addHeader(ACCEPT_NAME, ACCEPT);  
//			httpPost.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);  
//			httpPost.setConfig(defaultRequestConfig);  
			httpPost.setEntity(new StringEntity(josn));

			HttpResponse response = httpClient.execute(httpPost); // 执行POST请求
			HttpEntity entity = response.getEntity(); // 获取响应实体
			if (null != entity) {
				responseLength = entity.getContentLength();
				responseContent = EntityUtils.toString(entity, "UTF-8");
				EntityUtils.consume(entity); // Consume response content
			}
			//System.out.println("请求地址: " + httpPost.getURI());
			//System.out.println("响应状态: " + response.getStatusLine());
			//System.out.println("响应长度: " + responseLength);
			//System.out.println("响应内容: " + responseContent);
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown(); // 关闭连接,释放资源
			return responseContent;
		}
	}
	
	/**
	 * 向HTTPS地址发送Get请求
	 * @param reqURL 请求地址
	 * @param aCharSet 编码
	 * @return 响应内容
	 */
	@SuppressWarnings("finally")
	public static String getMethod(String reqURL,String aCharSet) {
		long responseLength = 0; // 响应长度
		String responseContent = null; // 响应内容
		HttpClient httpClient = new DefaultHttpClient(); // 创建默认的httpClient实例
		X509TrustManager xtm = new X509TrustManager() { // 创建TrustManager
			public void checkClientTrusted(X509Certificate[] chain,String authType) throws CertificateException {}
			public void checkServerTrusted(X509Certificate[] chain,String authType) throws CertificateException {}
			public X509Certificate[] getAcceptedIssuers() {return null;}
		};
		
		try {
			// TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext
			SSLContext ctx = SSLContext.getInstance("TLS");
			// 使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
			ctx.init(null, new TrustManager[] { xtm }, null);
			// 创建SSLSocketFactory
			SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
			// 通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));

			HttpGet httpGet = new HttpGet(reqURL); // 创建HttpPost
			HttpResponse response = httpClient.execute(httpGet); // 执行POST请求
			HttpEntity entity = response.getEntity(); // 获取响应实体
			if (null != entity) {
				responseLength = entity.getContentLength();
				responseContent = EntityUtils.toString(entity, "UTF-8");
				EntityUtils.consume(entity); // Consume response content
			}
			//System.out.println("请求地址: " + httpPost.getURI());
			//System.out.println("响应状态: " + response.getStatusLine());
			//System.out.println("响应长度: " + responseLength);
			//System.out.println("响应内容: " + responseContent);
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown(); // 关闭连接,释放资源
			return responseContent;
		}
	}

	public static void main(String[] args) throws Exception {

		HttpsUtil client = new HttpsUtil();
		// 获取access_token
		//System.out.println(client.getMethod("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxba9cf773382dc621&secret=43b6bb0507b47440fc2a0d91de867df8", "UTF-8"));
		
		// 获取用户信息
		//System.out.println(client.getMethod("https://api.weixin.qq.com/cgi-bin/user/info?access_token=KqPrtca1nnyAYhcEnJZ-1aWzWkoc_5HQWlSgCZfn7zo4NEvnFGaJDkjduMZum9i4ho-1jm12wqNJdldrx-LqDK6ZW6mrtwAftBF536NSUKtiNsJxWKeQ1nawxxGxUm-VLwBp0jR7f_hUGn27EMQytg&openid=ozYDCjpTUxyCebYchDUIjfcbjOiI&lang=zh_CN", "UTF-8"));
		
		// 目前自定义菜单
//		try {
//			//Map<String,String> parms=new HashMap<String,String>(); 
//			String json = "{\"button\": [{\"type\": \"click\", \"name\": \"今日歌曲\", \"key\": \"V1001_TODAY_MUSIC\"}]}";
//			String response = client.postMethod("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=KqPrtca1nnyAYhcEnJZ-1aWzWkoc_5HQWlSgCZfn7zo4NEvnFGaJDkjduMZum9i4ho-1jm12wqNJdldrx-LqDK6ZW6mrtwAftBF536NSUKtiNsJxWKeQ1nawxxGxUm-VLwBp0jR7f_hUGn27EMQytg", json,"UTF-8");
//			System.out.println("response:"+response);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		
		// 发送文本消息
//		try {
//			Map<String,String> parms=new HashMap<String,String>();
//			String json = "{\"touser\": \"ozYDCjpTUxyCebYchDUIjfcbjOiI\", \"msgtype\": \"text\", \"text\": {\"content\": \"Hello Woweeeeeeeeeeeeewrld\"}}";
//			String response = client.postMethod("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=KqPrtca1nnyAYhcEnJZ-1aWzWkoc_5HQWlSgCZfn7zo4NEvnFGaJDkjduMZum9i4ho-1jm12wqNJdldrx-LqDK6ZW6mrtwAftBF536NSUKtiNsJxWKeQ1nawxxGxUm-VLwBp0jR7f_hUGn27EMQytg", json,"UTF-8");
//			System.out.println("response:"+response);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

}