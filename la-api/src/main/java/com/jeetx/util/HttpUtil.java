package com.jeetx.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

public class HttpUtil {
	
	public static Logger log = Logger.getLogger(HttpUtil.class);
	
	/**
	 * 使用 POST 方式提交数据
	 * @version 2011-11-18 下午01:47:55
	 * @param url
	 * @param parms
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String postMethod(String url,Map parms,String charset){  
		
		String body = null;
		byte[] b = null;
		//构造HttpClient的实例  
		HttpClient httpClient = new HttpClient();
		//创建Post方法的实例  
		PostMethod postMethod = new PostMethod(url);
		//postMethod.setRequestHeader( "Content-type" , "text/xml; charset=utf-8" );
		//填入各个表单域的值  
		NameValuePair[] data = new NameValuePair[parms.keySet().size()]; 
		Iterator it = parms.entrySet().iterator();  
		int i=0;  
		String parmsStr = null;
		while (it.hasNext()) { 
			Map.Entry entry = (Map.Entry) it.next(); 
			Object key = entry.getKey(); 
			Object value = entry.getValue(); 
			data[i]=new NameValuePair(key.toString(),value.toString());  
			parmsStr += "&"+key.toString()+value.toString();
			i++;  
		}  

		postMethod.setRequestBody(data);
		try {  
			int statusCode = httpClient.executeMethod(postMethod); 
			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY
					|| statusCode == HttpStatus.SC_SEE_OTHER || statusCode == HttpStatus.SC_TEMPORARY_REDIRECT) {  
				Header locationHeader = postMethod.getResponseHeader("location");  
				String location = null;  
				if (locationHeader != null) { 
					location = locationHeader.getValue(); 
					System.out .println("The page was redirected to:" + location);  
				} else {  
					System.err.println("Location field value is null.");  
				}  
			}
			
			b = postMethod.getResponseBody();  
			
			if(b != null){
				body = new String(b,charset);
			}
			
		} catch (Exception e) { 
			e.printStackTrace();  
			log.info("处理("+url+"?"+parmsStr+")请求异常："+e.getMessage());
		} finally {
			if(null != postMethod){
				postMethod.releaseConnection();//释放连接
			}
		} 
		return body;  
	}
	
	/**
	 * 使用 POST 方式提交数据
	 * @version 2011-11-18 下午01:47:55
	 * @param url
	 * @param parms
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String postMethod(String url,Map<String, String> parms,Map<String, String> header,String charset){  
		
		String body = null;
		byte[] b = null;
		//构造HttpClient的实例  
		HttpClient httpClient = new HttpClient();
		//创建Post方法的实例  
		PostMethod postMethod = new PostMethod(url);
        for (Map.Entry<String, String> entry : header.entrySet()) {
            //System.out.println(entry.getKey() + ": " + entry.getValue());
            postMethod.setRequestHeader(entry.getKey(),entry.getKey());
        }
		
		//填入各个表单域的值  
		NameValuePair[] data = new NameValuePair[parms.keySet().size()]; 
		Iterator it = parms.entrySet().iterator();  
		int i=0;  
		String parmsStr = null;
		while (it.hasNext()) { 
			Map.Entry entry = (Map.Entry) it.next(); 
			Object key = entry.getKey(); 
			Object value = entry.getValue(); 
			data[i]=new NameValuePair(key.toString(),value.toString());  
			parmsStr += "&"+key.toString()+value.toString();
			i++;  
		}  

		postMethod.setRequestBody(data);
		try {  
			int statusCode = httpClient.executeMethod(postMethod); 
			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY
					|| statusCode == HttpStatus.SC_SEE_OTHER || statusCode == HttpStatus.SC_TEMPORARY_REDIRECT) {  
				Header locationHeader = postMethod.getResponseHeader("location");  
				String location = null;  
				if (locationHeader != null) { 
					location = locationHeader.getValue(); 
					System.out .println("The page was redirected to:" + location);  
				} else {  
					System.err.println("Location field value is null.");  
				}  
			}
			
			b = postMethod.getResponseBody();  
			
			if(b != null){
				body = new String(b,charset);
			}
			
		} catch (Exception e) { 
			e.printStackTrace();  
			log.info("处理("+url+"?"+parmsStr+")请求异常："+e.getMessage());
		} finally {
			if(null != postMethod){
				postMethod.releaseConnection();//释放连接
			}
		} 
		return body;  
	}
	
	/**
	 * 使用 Get 方式提交数据
	 * @version 2011-11-18 下午01:47:55
	 * @param url
	 * @return
	 */
	public String getMethod(String url,String charset){  
		String responseBody = null; 
		//构造HttpClient的实例
		HttpClient httpClient = new HttpClient();       
		//设置连接超时为10秒
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
		//创建GET方法的实例
		GetMethod getMethod = new GetMethod(url);
		getMethod.setRequestHeader("Connection", "close");
		//设置get请求超时为10秒
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,10000);
		
		//使用系统提供的默认的恢复策略
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler());
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: "+ getMethod.getStatusLine());
			}
			StringBuffer temp = new StringBuffer();
			InputStream in = getMethod.getResponseBodyAsStream();
	        BufferedReader buffer = new BufferedReader(new InputStreamReader(in,charset));
	        for(String tempstr = ""; (tempstr = buffer.readLine()) != null;)
	        	temp = temp.append(tempstr);
	        buffer.close();
	        in.close();
	        responseBody = temp.toString().trim();
		} catch (HttpException e) {
			//发生致命的异常，可能是协议不对或者返回的内容有问题
			e.printStackTrace();
			log.info("处理("+url+")请求异常，可能是协议不对或者返回的内容有问题："+e.getMessage());
		} catch (IOException e) {
			//发生网络异常
			e.printStackTrace();
			log.info("处理("+url+")请求异常："+e.getMessage());
		} finally {
			if(null != getMethod){
				getMethod.releaseConnection();//释放连接
			}	
		}
		return responseBody;  
	}
	
	public String mapToString(Map<String,String> parms) {
		StringBuffer sb = new StringBuffer();
		Iterator it = parms.entrySet().iterator();   
        while (it.hasNext()) {   
            Map.Entry e = (Map.Entry) it.next();
            sb.append(e.getKey()).append("=").append(e.getValue()); 
            if(it.hasNext()){
            	 sb.append("&"); 
            }
        } 
		return sb.toString();
	}


	public static void main(String[] args){
//		try {
//			Map parms=new HashMap(); 
//			HttpClientUtil client = new HttpClientUtil();
//			String cpnoderno = "C20120602121644344627";
//			parms.put("method", "querystate");
//			parms.put("cporderno", cpnoderno);
//			parms.put("sign", MD5.getMD5ofStr("method=querystate&cporderno="+cpnoderno+"cuncun8.com").toUpperCase());
//			String response = new String(client.postMethod("http://ec.cuncun8.com/rechargeinterface.sc", parms),"UTF-8");
//			System.out.println(response);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} 
	
	}
}