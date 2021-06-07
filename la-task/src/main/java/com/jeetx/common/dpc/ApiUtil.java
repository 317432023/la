package com.jeetx.common.dpc;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.jeetx.common.exception.BusinessException;
import com.jeetx.util.DateTimeTool;
import com.jeetx.util.MD5Util;

import net.sf.json.JSONObject;

public class ApiUtil {
	  
	private static final int second = 30;
	public static final String SECRETKEY = "A6E2AC7138F50D71AF241F8C3851432F";
	public static final String BASE_URL = "http://dpc.13070.cn";//http://dpc.cezhhef.cn";
	//public static final String BASE_URL = "http://127.0.0.1:8080/lott-dpc";
	
	/**获取服务器时间*/
	public static String serverTime(){
		String response = null;
		try {
			Map<String,Object> parms = new HashMap<String,Object>(); 
			ApiUtil client = new ApiUtil();
			response = new String(client.postMethod(BASE_URL+"/api/common/serverTime", parms,"UTF-8"));
			//System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	/**
	 * 参数验证
	 * @param parasMap
	 */
	public static Map<String, Object> checkParameter(Map<String, Object> parasMap){
		//验证参数是否为空
		Date timestamp = null;
		String sign = null;
		Map<String, Object> newParasMap = new HashMap<String, Object>();
		for (Map.Entry<String, Object> paras : parasMap.entrySet()) { 
			//System.out.println(paras.getKey() + "-" + paras.getValue());
			if(paras.getValue()!=null && !"".equals(paras.getValue())) {
				try {	
					if("timestamp".equalsIgnoreCase(paras.getKey())) {
						timestamp = DateTimeTool.dateFormat("yyyyMMddHHmmss", (String)paras.getValue());
					}
				}catch (Exception e) {
					e.printStackTrace();
					throw new BusinessException(ApiUtil.getErrorCode("102").concat(",时间戳格式有误"));
				}
				
				if("sign".equalsIgnoreCase(paras.getKey())) {
					sign = (String)paras.getValue();
				}
				
				try {
					newParasMap.put(paras.getKey(), URLDecoder.decode(String.valueOf(paras.getValue()), "utf-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					throw new BusinessException(ApiUtil.getErrorCode("102"));
				}
			}
		}

		long t_ = Math.abs( new Date().getTime()-timestamp.getTime() );
		if(t_> second*1000) {
			throw new BusinessException(ApiUtil.getErrorCode("103"));
		}
		
		if(!sign.equalsIgnoreCase(ApiUtil.generateSign(parasMap))) {
			throw new BusinessException(ApiUtil.getErrorCode("104"));
		}
		
		return newParasMap;
	}
	
	/**
	 * 参数验证
	 * @param parasMap
	 */
	public static Map<String, Object> checkParameter(Map<String, Object> parasMap,String secretKey){
		//验证参数是否为空
		Date timestamp = null;
		String sign = null;
		Map<String, Object> newParasMap = new HashMap<String, Object>();
		for (Map.Entry<String, Object> paras : parasMap.entrySet()) { 
			//System.out.println(paras.getKey() + "-" + paras.getValue());
			if(paras.getValue()!=null && !"".equals(paras.getValue())) {
				try {	
					if("timestamp".equalsIgnoreCase(paras.getKey())) {
						timestamp = DateTimeTool.dateFormat("yyyyMMddHHmmss", (String)paras.getValue());
					}
				}catch (Exception e) {
					e.printStackTrace();
					throw new BusinessException(ApiUtil.getErrorCode("102").concat(",时间戳格式有误"));
				}
				
				if("sign".equalsIgnoreCase(paras.getKey())) {
					sign = (String)paras.getValue();
				}
				
				try {
					newParasMap.put(paras.getKey(), URLDecoder.decode(String.valueOf(paras.getValue()), "utf-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					throw new BusinessException(ApiUtil.getErrorCode("102"));
				}
			}
		}

		long t_ = Math.abs( new Date().getTime()-timestamp.getTime() );
		if(t_> second*1000) {
			throw new BusinessException(ApiUtil.getErrorCode("103"));
		}
		
		//System.out.println(sign);
		//System.out.println(ApiUtil.generateSign(parasMap,secretKey));
		if(!sign.equalsIgnoreCase(ApiUtil.generateSign(parasMap,secretKey))) {
			throw new BusinessException(ApiUtil.getErrorCode("104"));
		}
		
		return newParasMap;
	}
	
	/**
	 * 使用 POST 方式提交数据
	 * @version 2011-11-18 下午01:47:55
	 * @param url
	 * @param parms
	 * @return
	 * @throws Exception 
	 */
	public String postMethod(String url,Map<String, Object> parms,String charset) throws Exception{  
		
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
			Object value = URLEncoder.encode(String.valueOf(entry.getValue()),charset); 
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
		} finally {
			if(null != postMethod){
				postMethod.releaseConnection();//释放连接
			}
		} 
		return body;  
	}
	
	public static List<Map.Entry<String, Object>> sortMap(Map<String, Object> map) {
        List<Map.Entry<String, Object>> infos = new ArrayList<Map.Entry<String, Object>>(map.entrySet());

        // 重写集合的排序方法：按字母顺序
        Collections.sort(infos, new Comparator<Map.Entry<String, Object>>() {
            public int compare(Entry<String, Object> o1,Entry<String, Object> o2) {
                return (o1.getKey().toString().compareTo(o2.getKey()));
            }
        });

        return infos;
    }
	
	/**
	 * 获取签名串
	 * @param parasMap
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String generateSign(Map<String, Object> parasMap){
		StringBuffer sign = new StringBuffer("");
		
		List<Map.Entry<String, Object>> list = sortMap(parasMap);
		for (Map.Entry<String, Object> m : list) {
			//System.out.println(m.getKey() + ":" + m.getValue());
			if(m.getValue()!=null && !"".equals(m.getValue())&&!"sign".equalsIgnoreCase(m.getKey())) {
				try {
					sign.append(URLDecoder.decode(String.valueOf(m.getValue()), "utf-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					throw new BusinessException(ApiUtil.getErrorCode("102"));
				}
			}
		}
		
		//System.out.println(sign.append(SECRETKEY).toString());
		return MD5Util.MD5Encode(sign.append(SECRETKEY).toString(),"UTF-8").toLowerCase() ;
	}
	
	/**
	 * 获取签名串
	 * @param parasMap
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String generateSign(Map<String, Object> parasMap,String secretKey){
		StringBuffer sign = new StringBuffer("");
		
		List<Map.Entry<String, Object>> list = sortMap(parasMap);
		for (Map.Entry<String, Object> m : list) {
			//System.out.println(m.getKey() + ":" + m.getValue());
			if(m.getValue()!=null && !"".equals(m.getValue())&&!"sign".equalsIgnoreCase(m.getKey())) {
				try {
					sign.append(URLDecoder.decode(String.valueOf(m.getValue()), "utf-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					throw new BusinessException(ApiUtil.getErrorCode("102"));
				}
			}
		}
		
		//System.out.println(sign.append(SECRETKEY).toString());
		return MD5Util.MD5Encode(sign.append(secretKey).toString(),"UTF-8").toLowerCase() ;
	}

	/**
	 * post提交json对象
	 * @param url
	 * @param json
	 * @return
	 */
	public static JSONObject doJsonPost(String url, JSONObject json){
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
		JSONObject response = null;
		try {
			StringEntity s = new StringEntity(json.toString(), ContentType.APPLICATION_JSON);
//			s.setContentEncoding("UTF-8");
//			s.setContentType("application/json;charset=UTF-8");// 发送json数据需要设置contentType
			post.setEntity(s);
			HttpResponse res = httpclient.execute(post);
			//System.out.println(res.getStatusLine().getStatusCode());
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String result = EntityUtils.toString(res.getEntity());// 返回json格式：
				response = JSONObject.fromObject(result);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		//System.out.println("response:"+response);
		return response;
    }
	
	public static String getErrorCode(String errorCode){
		Map<String, String> map = new HashMap<String, String>();
		map.put("200", "系统错误");
		
		map.put("101", "缺少参数");
		map.put("102", "提交参数格式有误");
		map.put("103", "链接无效，请求超时");
		map.put("104", "签名错误");
		map.put("105", "无相关记录");
		map.put("106", "app_id对应的站点不存在");
		map.put("107", "json格式有误，解析错误");
		map.put("108", "lotteryType对应的彩票类型不存在");
		map.put("109", "toyType对应的玩法类型不存在");
		map.put("111", "提交json参数不完整");
		map.put("112", "roomKey对应的房间未初始");
		map.put("113", "对应的期数不存在或已开奖");
		map.put("114", "下注格式有误");

		return errorCode.concat("-").concat(map.get(errorCode));
	}
}
