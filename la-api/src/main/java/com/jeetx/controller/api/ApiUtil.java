package com.jeetx.controller.api;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;

import com.jeetx.bean.lottery.LotteryPeriods;
import com.jeetx.bean.member.User;
import com.jeetx.common.exception.BusinessException;
import com.jeetx.util.DateTimeTool;
import com.jeetx.util.MD5Util;

public class ApiUtil {
	  
	private static final int second = 15;
	public static final String SECRETKEY = "A6E2AC7138F50D71AF241F8C3851432F";
	public static final String H5_SECRETKEY = "F241F8C38A6E2AC7138F50D71A51432F";
	
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
		
		if(!sign.equalsIgnoreCase(ApiUtil.generateSign(parasMap,secretKey))) {
			throw new BusinessException(ApiUtil.getErrorCode("104"));
		}
		
		return newParasMap;
	}
	
	/**
	 * 生成玩家返回json
	 * @param user
	 * @return
	 */
	public static Map<String, Object> toUserMap(User user,String resServerLink) {	
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("userId", user.getId());
		result.put("username", user.getUsername());
		result.put("nickName", user.getNickName());
		result.put("headImg", StringUtils.isNotBlank(user.getHeadImg())?
				user.getHeadImg().contains("http://")?user.getHeadImg():resServerLink+user.getHeadImg()
				:"");
		result.put("balance", user.getBalance().toString());
		result.put("freezeBalance", user.getFreezeBalance().toString());
		result.put("lotteryBalance", user.getLotteryBalance().toString());
		result.put("userType", user.getUserType());		
		result.put("points", user.getPoints());
		result.put("pointsLevel", user.getPointsLevel()!=null?user.getPointsLevel().getTitle():"");
		result.put("minPointsLevel", user.getPointsLevel()!=null?user.getPointsLevel().getMinPoints():"");
		result.put("maxPointsLevel", user.getPointsLevel()!=null?user.getPointsLevel().getMaxPoints():"");
		result.put("token", user.getLoginToken());
		result.put("securityCodeLabel", StringUtils.isNotBlank(user.getPayPassword())?"1":"0");
        return result;
    }
	
	/**
	 * 获取设备名称
	 * @param device
	 * @return
	 */
	public static String getDeviceName(Integer device) {	
		String deviceName = "未知";
		switch (device) {
		case 1:
			deviceName = "安卓";
			break;
		case 2:
			deviceName = "IOS";
			break;
		case 3:
			deviceName = "H5";
			break;
		}
		return deviceName;
    }

	public static List<Map.Entry<String, Object>> sortMap(Map<String, Object> map) {
        List<Map.Entry<String, Object>> infos = new ArrayList<Map.Entry<String, Object>>(map.entrySet());

        // 重写集合的排序方法：按字母顺序
        Collections.sort(infos, new Comparator<Map.Entry<String, Object>>() {
            @Override
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
		return MD5Util.MD5Encode(sign.append(secretKey).toString(), "utf-8").toLowerCase() ;
	}
	
	public static String getParameterValues(HttpServletRequest request,String parameterKey){
		try{
			StringBuilder tourl = new StringBuilder();
			tourl.append(request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()).append(request.getRequestURI()).append("?");
			
			Enumeration ens = request.getParameterNames();
			while(ens.hasMoreElements()){
				String pName = (String)ens.nextElement();
				String[] pValue = request.getParameterValues(pName);
				if(pValue!=null) {
					for(String s : pValue){
						if(pName.equalsIgnoreCase(parameterKey))
							return new String(s.getBytes("ISO-8859-1"),"UTF-8");
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getResServerLink(String defaultLink,String entryDomain){
		if(StringUtils.isNotBlank(entryDomain)) {
			return entryDomain;
		}
		return defaultLink;
	}

	public static Map getParameterMap(HttpServletRequest request){
		Map<String, String> parameterMap = new TreeMap<String, String>();
		try{
			Enumeration ens = request.getParameterNames();
			boolean todel = false;
			while(ens.hasMoreElements()){
				String pName = (String)ens.nextElement();
				String[] pValue = request.getParameterValues(pName);
				if(pValue!=null) {
					for(String s : pValue){
						parameterMap.put(pName,new String(s.getBytes("ISO-8859-1"),"UTF-8"));
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return parameterMap;
	}
	
	public static String getRequestUrl(HttpServletRequest request){
		String requestUrl = null;
		try{
			StringBuilder tourl = new StringBuilder();
			tourl.append(request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()).append(request.getRequestURI()).append("?");
			
			Enumeration ens = request.getParameterNames();
			boolean todel = false;
			while(ens.hasMoreElements()){
				String pName = (String)ens.nextElement();
				String[] pValue = request.getParameterValues(pName);
				if(pValue!=null) {
					for(String s : pValue){
						tourl.append(pName).append("=").append(new String(s.getBytes("ISO-8859-1"),"UTF-8")).append("&");
						todel = true;
					}
				}
			}
			if(todel) 
				tourl.deleteCharAt(tourl.length()-1);
			requestUrl = tourl.toString();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return requestUrl;
	}
	
	/**
	 * 使用 POST 方式提交数据
	 * @version 2011-11-18 下午01:47:55
	 * @param url
	 * @param parms
	 * @return
	 * @throws Exception 
	 */
	public String postMethod(String url,Map<String, Object> parms,String referer,String charset) throws Exception{  
		
		String body = null;
		byte[] b = null;
		//构造HttpClient的实例  
		HttpClient httpClient = new HttpClient();
		//创建Post方法的实例  
		PostMethod postMethod = new PostMethod(url);
		if(StringUtils.isNotBlank(referer)) {
			postMethod.setRequestHeader("Referer",referer);
		}
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
	
	/**
	 * 1、北京28及加拿大28(1猜双面、3猜数字、2特殊玩法);
	 *  2、幸运飞艇及北京赛车(1猜双面、2猜号码、3龙虎斗、4猜庄闲、5猜冠亚、6冠亚和<猜双面>、7冠亚和<猜数字>、8冠亚和<猜区段>; 
	 *  3、重庆时时彩(1猜双面、2猜数字、3猜和值、4龙虎斗;
	 * @param lotteryType
	 * @param ruleType
	 * @return
	 */
	public static String getRuleTypeName(Integer lotteryType,Integer ruleType) {
		String typeName = "";
		switch (lotteryType) {
		case 1:		
		case 2:
		case 6:
			switch (ruleType) {
			case 1:
				typeName = "猜双面";
				break;
			case 2:
				typeName = "特殊玩法";			
				break;
			case 3:
				typeName = "猜数字";	
				break;
			}
			break;
		case 3:		
		case 4:
			switch (ruleType) {
			case 1:
				typeName = "猜双面";
				break;
			case 2:
				typeName = "猜号码";			
				break;
			case 3:
				typeName = "龙虎斗";	
				break;
			case 4:
				typeName = "猜庄闲";	
				break;
			case 5:
				typeName = "猜冠亚";	
				break;
			case 6:
				typeName = "冠亚和<猜双面>";	
				break;
			case 7:
				typeName = "冠亚和<猜数字>";	
				break;
			case 8:
				typeName = "冠亚和<猜区段>";	
				break;
			}
			break;
		case 5:		
			switch (ruleType) {
			case 1:
				typeName = "猜双面";
				break;
			case 2:
				typeName = "猜数字";			
				break;
			case 3:
				typeName = "猜和值";	
				break;
			case 4:
				typeName = "龙虎斗";	
				break;
			}
			break;
		}
        return typeName;
    }

	public static void main(final String[] args) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("charSet", "utf-8");
//		map.put("merId", "5412");
//		map.put("acqId", "321");
//
//		List<Map.Entry<String, Object>> list = sortMap(map);
//
//		for (Map.Entry<String, Object> m : list) {
//			System.out.println(m.getKey() + ":" + m.getValue());
//		}

		List<String> munberList = Arrays.asList("1+6+5+9+2+7+10+4+8+3".split("\\+"));
		System.out.println(munberList.size());
	}
	
	public static String getErrorCode(String errorCode){
		Map<String, String> map = new HashMap<String, String>();
		map.put("200", "系统错误");
		
		map.put("101", "缺少参数");
		map.put("102", "提交参数格式有误");
		map.put("103", "链接无效，请求超时");
		map.put("104", "签名错误");
		map.put("105", "无相关记录");
		map.put("106", "ip未绑定，无获取权限");
		map.put("107", "连接及时通讯服务器失败");
		map.put("108", "站点不存在");
		map.put("109", "名称对应站点已存在");
		
		map.put("110", "用户名已经被注册");
		map.put("111", "用户名或者密码错误");
		map.put("112", "TOKEN验证失败，请重新登录");
		map.put("113", "游客登陆失败");
		map.put("114", "记录不存在，无法删除");
		map.put("115", "银行卡JSON格式有误");
		map.put("116", "原密码错误");
		map.put("117", "用户不存在");
		map.put("118", "充减金额不得大于账号余额");
		map.put("119", "pid对应的用户不存在或权限不足");
		map.put("120", "权限不足，无获取权限");
		map.put("121", "记录不存在或已处理");
		map.put("122", "提现金额大于实际冻结金额");
		map.put("123", "提现金额大于实际可提现金额");
		map.put("124", "彩票大厅不存在");
		map.put("125", "订单金额格式有误");
		map.put("126", "订单JOSN格式有误");
		map.put("127", "房间不存在或不允许投注");
		map.put("128", "已封盘，不允许投注");
		map.put("129", "彩票期数不存在");
		map.put("130", "用户账户被禁用");
		map.put("131", "代理及推广员类型用户不允许下注");
		map.put("132", "订单总金额与明细总金额不符");
		map.put("133", "ruleId对应的玩法不存在");
		map.put("134", "投注内容格式有误");
		map.put("135", "订单已存在");
		map.put("136", "下注无效，订单扣除异常");
		map.put("137", "下注无效，余额不足");
		map.put("138", "ruleId对应的玩法信息有误");
		map.put("139", "彩票房间不存在");
		map.put("140", "彩票房间对应的彩票类型不一致");
		map.put("141", "彩票期数未开奖，不允许手动派奖");
		map.put("142", "安全码错误");
		map.put("143", "对应的银行卡信息不存在");
		map.put("144", "申请提现失败");
		map.put("145", "提交金额的格式有误");
		map.put("146", "对应的支付渠道不存在");
		map.put("147", "充值失败");
		map.put("148", "充值失败,无此充值通道");
		map.put("149", "充值失败,提交渠道商服务异常");
		map.put("150", "非代理用户不能进行此操作");
		//map.put("151", "非推广员或代理不能进行此操作");
		map.put("151", "非推广员不能进行此操作");
		map.put("152", "child_username对应用户不存在");
		map.put("153", "非子账号，无权限操作");
		map.put("154", "仅可对虚拟号充值");
		map.put("155", "代理只能注销下级推广员");
		map.put("156", "推广员只能注销下级虚拟员");
		map.put("157", "昵称已存在");
		map.put("158", "安全码未设置，请先设置");
		map.put("159", "不允许提现");
		map.put("160", "订单不存在或已完成");
		map.put("161", "推广员未授权为虚拟号充值");
		map.put("162", "用户账户被冻结");
		map.put("163", "站点授权已到期");
		map.put("164", "推荐人信息不存在");
		map.put("165", "只能绑定本人银行卡，当前持卡人[姓名]");
		map.put("166", "推荐人账号不能为空");
		map.put("167", "推荐人账号有误");
		
		map.put("201", "下注无效，系统限制'[下注类型]'下注范围为：最小值[最小值]，最大值[最大值]");
		map.put("202", "下注无效,禁止反组合下注");
		map.put("203", "下注无效,禁止杀组合下注");
		map.put("204", "订单不存在");
		map.put("205", "已封盘，不允许取消");
		map.put("206", "订单已取消，不能重复取消");
		map.put("207", "同类型彩种期数，只能在一个房间下注");
		map.put("208", "订单已开奖，不允许取消");

		return errorCode.concat("-").concat(map.get(errorCode));
	}
	
	public static String getChartData(Integer lotteryType,LotteryPeriods lotteryPeriods) {
		StringBuffer chartData = new StringBuffer("");
		switch (lotteryType) {
		case 1:
		case 2:
		case 6:
			//值|大单|小单|大双|小双|大|小|单|双
			chartData.append(lotteryPeriods.getLotteryOpenNumber()).append("|");//1值
			chartData.append(lotteryPeriods.getLotteryOpenNumber()>13 && lotteryPeriods.getLotteryOpenNumber()%2!=0 ?"大单":"-").append("|");//2大单
			chartData.append(lotteryPeriods.getLotteryOpenNumber()<=13 && lotteryPeriods.getLotteryOpenNumber()%2!=0 ?"小单":"-").append("|");//3小单
			chartData.append(lotteryPeriods.getLotteryOpenNumber()>13 && lotteryPeriods.getLotteryOpenNumber()%2==0 ?"大双":"-").append("|");//4大双
			chartData.append(lotteryPeriods.getLotteryOpenNumber()<=13 && lotteryPeriods.getLotteryOpenNumber()%2==0 ?"小双":"-").append("|");//5小双
			chartData.append(lotteryPeriods.getLotteryOpenNumber() >13 ?"大":"-").append("|");//6大
			chartData.append(lotteryPeriods.getLotteryOpenNumber()<=13 ?"小":"-").append("|");//7小
			chartData.append(lotteryPeriods.getLotteryOpenNumber()%2!=0 ?"单":"-").append("|");//8单
			chartData.append(lotteryPeriods.getLotteryOpenNumber()%2==0 ?"双":"-");//9双
			break;
		case 3:
		case 4:
			//冠亚和|庄闲|大小|单双|区间|龙虎
			List<String> munberList = Arrays.asList(lotteryPeriods.getLotteryOpenContent().split("\\+"));
			
			//1冠亚和
			Integer number1 = Integer.valueOf(munberList.get(0));//对应位数的开奖号码1
			Integer number2 = Integer.valueOf(munberList.get(1));//对应位数的开奖号码2
			Integer sumNumber = number1 + number2;
			chartData.append(sumNumber).append("|");//1冠亚和
			
			//2冠亚庄闲
			if(number1>number2) {
				chartData.append("庄").append("|");//2庄闲
			}else if(number1<number2) {
				chartData.append("闲").append("|");//2庄闲
			}
			
			//3冠亚大小
			chartData.append(sumNumber >11 ?"大":"小").append("|");//3大小
			
			//4冠亚单双
			chartData.append(sumNumber%2==0 ?"双":"单").append("|");//4单双
			
			//5区间
			if(Arrays.asList("3,4,5,6,7".split(",")).contains(sumNumber.toString())) {
				chartData.append("A").append("|");//5区间
			}else if(Arrays.asList("8,9,10,11,12,13,14".split(",")).contains(sumNumber.toString())) {
				chartData.append("B").append("|");//5区间
			}else if(Arrays.asList("15,16,17,18,19".split(",")).contains(sumNumber.toString())) {
				chartData.append("C").append("|");//5区间
			}
			
			//6龙虎
			for (int i = 0; i < 5; i++) {
				number1 = Integer.valueOf(munberList.get(i));//对应位数的开奖号码1
				number2 = Integer.valueOf(munberList.get(9-i));//对应位数的开奖号码2
				if(number1>number2) {
					chartData.append("龙");
				}else if(number1<number2) {
					chartData.append("虎");
				}
				
				if(i != 4) {
					chartData.append("-");
				}
			}
			break;
		case 5:
			//开奖号码|后二和|龙|虎|和
			munberList = Arrays.asList(lotteryPeriods.getLotteryOpenContent().split("\\+"));

			//1开奖号码
			chartData.append(lotteryPeriods.getLotteryOpenContent().replace("\\+", ",")).append("|");//开奖号码
			
			//2后二和
			number1 = Integer.valueOf(munberList.get(3));//对应位数的开奖号码1
			number2 = Integer.valueOf(munberList.get(4));//对应位数的开奖号码2
			sumNumber = number1 + number2;
			chartData.append(sumNumber).append("|");//后二和

			number1 = Integer.valueOf(munberList.get(0));//对应位数的开奖号码1
			number2 = Integer.valueOf(munberList.get(4));//对应位数的开奖号码2
			
			//3龙
			chartData.append(number1>number2 ?"龙":"-").append("|");
			//4虎
			chartData.append(number1<number2 ?"虎":"-").append("|");
			//5和
			chartData.append(number1==number2 ?"和":"-");
			break;
		default:
			break;
		}
		return chartData.toString();
	}
	
	
	public static String sortNumber(Integer a,Integer b,Integer c) throws Exception{
		if (a < b) {
			int t = a;
			a = b;
			b = t;
		}
		if (a < c) {
			int t = a;
			a = c;
			c = t;
		}
		if (b < c) {
			int t = b;
			b = c;
			c = t;
		}
		
		return a+""+b+""+c;
	}

	public static String mapToString(Map<String,Object> parms) {
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
}
