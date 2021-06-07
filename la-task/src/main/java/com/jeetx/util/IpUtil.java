package com.jeetx.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class IpUtil {
	/**
	 * 获取登录用户IP地址
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip.equals("0:0:0:0:0:0:0:1")) {
			ip = "本地";
		}
		return ip;
	}

   /**
	* 获取本地IP列表（针对多网卡情况）
	*
	* @return
	*/
	public static List<String> getLocalIPList() {
       List<String> ipList = new ArrayList<String>();
       try {
           Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
           NetworkInterface networkInterface;
           Enumeration<InetAddress> inetAddresses;
           InetAddress inetAddress;
           String ip;
           while (networkInterfaces.hasMoreElements()) {
               networkInterface = networkInterfaces.nextElement();
               inetAddresses = networkInterface.getInetAddresses();
               while (inetAddresses.hasMoreElements()) {
                   inetAddress = inetAddresses.nextElement();
                   if (inetAddress != null && inetAddress instanceof Inet4Address) { // IPV4
                       ip = inetAddress.getHostAddress();
                       ipList.add(ip);
                       //System.out.println("获取本机IP："+ip);
                   }
               }
           }
       } catch (SocketException e) {
           e.printStackTrace();
       }
       return ipList;
	}
	
	/**
	 * 通过IP获取地址
	 * @return
	 */
	public static String getAddrByIP(String ip) {
		String address = "";
//		try {
//			if (StringUtils.isNotBlank(ip)) {
//				HttpUtil httpUtil = new HttpUtil();
//				StringBuffer sb = new StringBuffer();
//				sb.append("http://www.ip138.com/ips138.asp?action=2&ip=").append(ip);
//
//				String info = httpUtil.getMethod(sb.toString(), "gbk");
//				if (StringUtils.isNotBlank(info)) {
//					Document doc = Jsoup.parse( info);
//					Elements strongs = doc.getElementsByClass("ul1");
//					doc = Jsoup.parse( strongs.html());
//					Elements list = doc.getElementsByTag("li");
//					int a =0;
//					for (Element element : list) {
//						if (a==0) {
//							String s = element.text();
//							if (s.contains("本站主数据：")) {
//								address = s.replace("本站主数据：", "").replace(" ", "");
//							}else {
//								address = s;
//							}
//						}
//						a++;
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return address;
	} 
}
