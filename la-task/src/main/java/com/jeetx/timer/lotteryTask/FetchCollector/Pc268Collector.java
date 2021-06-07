package com.jeetx.timer.lotteryTask.FetchCollector;

import java.util.ArrayList;
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

public class Pc268Collector {
	private static int timerOut = 8;

	public static LotteryDTO getOpenCur(Integer type,Integer lotteryType,String sourceUrl,String dataSource) {
		LotteryDTO lotteryDTO = new LotteryDTO();
		lotteryDTO.setBool(false);
		
		String section = null;
		String openTime = null;
		String openCode = null;
		try {
			if(StringUtils.isNotBlank(sourceUrl)) {
				Connection conn = Jsoup.connect(sourceUrl).timeout(timerOut*1000);    
				String respone = conn.ignoreContentType(true).get().body().html();
				//System.out.println(respone);
				
				JSONObject json = JSONObject.fromObject(respone);
				JSONArray data = json.getJSONArray("data");
				section = data.getJSONObject(0).getString("periods");
				openCode = data.getJSONObject(0).getString("openContent");
				openTime = data.getJSONObject(0).getString("openTime");
				
				openCode = openCode.replaceAll("\\+", ",");
				lotteryDTO = FetchResult.createLotteryDTO(lotteryType, dataSource, section, openTime, openCode);
			}
		}catch (Exception e) {
			e.printStackTrace();
			lotteryDTO.setBool(false);
			LogUtil.info(dataSource+"采集器执行异常："+e.getMessage());
		}
		return lotteryDTO ;
	}
	
    public static List<LotteryDTO> getOpenHis(Integer type,Integer lotteryType,String sourceUrl,String dataSource) {
    	List<LotteryDTO> historyList = null;
		try {
			if(StringUtils.isNotBlank(sourceUrl)) {
				Connection conn = Jsoup.connect(sourceUrl).timeout(timerOut*1000);    
				String respone = conn.ignoreContentType(true).get().body().html();
				//System.out.println(respone);
				
				JSONObject json = JSONObject.fromObject(respone);
				JSONArray data = json.getJSONArray("data");
		    	JSONArray history = JSONArray.fromObject(data);  
		    	//System.out.println(history);

				historyList = new ArrayList<LotteryDTO>();
				for (int j = 1; j < history.size(); j++) {
					JSONObject periodObject = history.getJSONObject(j);
					String section = periodObject.get("periods").toString();  
					String openTime= periodObject.get("openTime").toString();  
					String openCode = periodObject.get("openContent").toString();  
					openCode = openCode.replaceAll("\\+", ",");
					
					historyList.add(FetchResult.createLotteryDTO(lotteryType, dataSource, section, openTime, openCode));
				}
			}


		}catch (Exception e) {
			//e.printStackTrace();
			LogUtil.info(dataSource+"采集器执行异常："+e.getMessage());
		}
		return historyList ;
    }
	
}
