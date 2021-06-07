package com.jeetx.timer.lotteryTask.FetchCollector;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.jeetx.timer.lotteryTask.FetchResult;
import com.jeetx.timer.lotteryTask.LotteryDTO;
import com.jeetx.util.DateTimeTool;
import com.jeetx.util.LogUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class T28Collector {
	private static int timerOut = 3;

	public static LotteryDTO getOpenCur(Integer type,Integer lotteryType,String sourceUrl,String dataSource) {
		LotteryDTO lotteryDTO = new LotteryDTO();
		lotteryDTO.setBool(false);
		
		String section = null;
		String openTime = null;
		String openCode = null;
		try {
			Map<String, String> header = new HashMap<String, String>();
	        header.put("Host", sourceUrl);
	        header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:55.0) Gecko/20100101 Firefox/55.0");
	        header.put("Accept", "application/json, text/javascript, */*; q=0.01");
	        header.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
	        header.put("Referer", sourceUrl);
	        header.put("Connection", "keep-alive");
			
	        String url = sourceUrl + "/Mobile/Indexs/myOpens/type/" + type +"/pz/3";
			Document doc = Jsoup.connect(url).headers(header).ignoreContentType(true).validateTLSCertificates(false).timeout(timerOut * 1000).get();
			
			String body = doc.select("body").first().text();
			JSONObject data = JSONObject.fromObject(body);
			JSONArray history = data.getJSONArray("data");
			JSONObject periodObject = history.getJSONObject(0);

			switch (type) {
			case 1:
			case 2:
				section = periodObject.get("section").toString();  
				openTime= DateTimeTool.DateParserT("yyyy-MM-dd HH:mm:ss",Long.valueOf(periodObject.get("openTime").toString()));  
				openCode = periodObject.get("middleCode").toString();  
				break;
			case 3:
			case 4:
			case 5:
				section = periodObject.get("section").toString();  
				openTime= DateTimeTool.DateParserT("yyyy-MM-dd HH:mm:ss",Long.valueOf(periodObject.get("openTime").toString()));  
				openCode = periodObject.get("result").toString();  

				break;
			}

			lotteryDTO = FetchResult.createLotteryDTO(lotteryType, dataSource, section, openTime, openCode);
		}catch (Exception e) {
			//e.printStackTrace();
			lotteryDTO.setBool(false);
			LogUtil.info(dataSource+"采集器执行异常："+e.getMessage());
		}
		return lotteryDTO ;
	}
	
}
