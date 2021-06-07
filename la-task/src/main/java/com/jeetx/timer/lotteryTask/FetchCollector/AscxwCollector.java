package com.jeetx.timer.lotteryTask.FetchCollector;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.jeetx.timer.lotteryTask.FetchResult;
import com.jeetx.timer.lotteryTask.LotteryDTO;
import com.jeetx.util.DateTimeTool;
import com.jeetx.util.LogUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class AscxwCollector {
	private static int timerOut = 3;

	public static LotteryDTO getOpenCur(Integer type,Integer lotteryType,String sourceUrl,String dataSource) {
		LotteryDTO lotteryDTO = new LotteryDTO();
		lotteryDTO.setBool(false);
		
		String section = null;
		String openTime = null;
		String openCode = null;
		try {
  			if(StringUtils.isNotBlank(sourceUrl)){
  				System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,SSLv3");
  	  			Document parm = Jsoup.connect(sourceUrl.replace("http:", "https:")).ignoreContentType(true).timeout(timerOut*1000).get();
  				String str=parm.toString().substring(parm.toString().lastIndexOf("{app"), parm.toString().lastIndexOf(";"));
  				str="["+str+"]";
  				
  				//System.out.println(str);
  				
  				//获取主要参数sig（签名）和se（毫秒时间）
  				JSONArray jsonArr = JSONArray.fromObject(str);  
  			    String sig=jsonArr.getJSONObject(0).get("sig").toString();
  			    long l = Long.valueOf((String) jsonArr.getJSONObject(0).get("se")).longValue();
  			    
  			    //取得算出来后的se
  			    l = strTo16(sig,l);
  			    
  		    	String url="https://www.z28.me/API/Web/2/Lot/"+type+"/Latest?callback=angular.callbacks._0&appid=mweb&sig="+sig+"&se="+l+"";
  		    	Connection conn = Jsoup.connect(url).ignoreContentType(true).timeout(timerOut*1000);
  		    	conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
	  			conn.header("Accept-Encoding", "gzip, deflate, sdch");
	  			conn.header("Accept-Language", "zh-CN,zh;q=0.8");
	  			conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
	  			Document doc = conn.get();
	  			
  		    	str=doc.toString().replace("({", "[{").replace("})", "}]");
  		    	str=str.substring(str.lastIndexOf("[{"), str.lastIndexOf("}]")+2);
  		    	JSONArray jsonArr1 = JSONArray.fromObject(str);  
  		    	
  		    	//获取期数，开奖时间，开奖号码
  		    	section = jsonArr1.getJSONObject(0).get("section").toString();  
  		    	openTime = DateTimeTool.DateParserT("yyyy-MM-dd HH:mm:ss",Long.valueOf(jsonArr1.getJSONObject(0).get("openTime").toString()));  
  		    	openCode = jsonArr1.getJSONObject(0).get("openNums").toString(); 
  		    	//System.out.println(openCode);
  				lotteryDTO = FetchResult.createLotteryDTO(lotteryType, dataSource, section, openTime, openCode);
  			}
		}catch (Exception e) {
			//e.printStackTrace();
			lotteryDTO.setBool(false);
			LogUtil.info(dataSource+"采集器执行异常："+e.getMessage());
		}
		return lotteryDTO ;
	}
	
    public static List<LotteryDTO> getOpenHis(Integer type,Integer lotteryType,String sourceUrl,String dataSource) {
    	List<LotteryDTO> historyList = null;
		try {
			//System.out.println(sourceUrl);
  			Document parm = Jsoup.connect(sourceUrl.replace("http:", "https:")).ignoreContentType(true).timeout(timerOut*1000).get();
			String str=parm.toString().substring(parm.toString().lastIndexOf("{app"), parm.toString().lastIndexOf(";"));
			str="["+str+"]";
			
			//System.out.println(str);
			
			//获取主要参数sig（签名）和se（毫秒时间）
			JSONArray jsonArr = JSONArray.fromObject(str);  
		    String sig=jsonArr.getJSONObject(0).get("sig").toString();
		    long l = Long.valueOf((String) jsonArr.getJSONObject(0).get("se")).longValue();
		    
		    //取得算出来后的se
		    l = strTo16(sig,l);

			String url = "https://www.z28.me/API/Web/2/MyOpens?callback=angular.callbacks._2&appid=mweb&sig=" + sig+ "&se=" + l + "&fi=0&lt=" + type + "&ps=30&top=10";
	    	Document doc = Jsoup.connect(url).ignoreContentType(true)
	    			.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0").timeout(timerOut*1000).get();
	    	str=doc.toString().replace("({", "[{").replace("})", "}]");
	    	str=str.substring(str.lastIndexOf("[{"), str.lastIndexOf("}]")+2);
	    	JSONArray history = JSONArray.fromObject(str);  
	    	//System.out.println(history);

			historyList = new ArrayList<LotteryDTO>();
			for (int j = 1; j < history.size(); j++) {
				JSONObject periodObject = history.getJSONObject(j);
				String section = periodObject.get("section").toString();  
				String openTime= DateTimeTool.DateParserT("yyyy-MM-dd HH:mm:ss",Long.valueOf(periodObject.get("openTime").toString()));  
				String openCode = periodObject.get("middleCode").toString();  

				historyList.add(FetchResult.createLotteryDTO(lotteryType, dataSource, section, openTime, openCode));
			}
		}catch (Exception e) {
			//e.printStackTrace();
			LogUtil.info(dataSource+"采集器执行异常："+e.getMessage());
		}
		return historyList ;
    }
	
    public static Date getDateByDate2Minute(Integer minute,Date date) {
    	Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minute);
    	Date previousdate=cal.getTime();
    	return previousdate;
	}
    
	//se参数js加密规律
	public static Long strTo16(String s,long l) {
   		//移除非数字的所有字符
	    String b = s.replaceAll("[^\\d]", "");
	    if(b.length() > 3) 
	    	b = b.substring(b.length()-4, b.length()-1);
	    if(b.length()==0)
	    	b="0";
	    return  Integer.valueOf(b)+l;
	}
}
