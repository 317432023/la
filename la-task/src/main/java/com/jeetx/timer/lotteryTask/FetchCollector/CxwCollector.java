package com.jeetx.timer.lotteryTask.FetchCollector;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.jeetx.timer.lotteryTask.FetchResult;
import com.jeetx.timer.lotteryTask.LotteryDTO;
import com.jeetx.util.DateTimeTool;
import com.jeetx.util.LogUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CxwCollector {
	private static int timerOut = 3;

	public static LotteryDTO getOpenCur(Integer type,Integer lotteryType,String sourceUrl,String dataSource) {
		LotteryDTO lotteryDTO = new LotteryDTO();
		lotteryDTO.setBool(false);
		
		String section = null;
		String openTime = null;
		String openCode = null;
		try {
			if(StringUtils.isNotBlank(sourceUrl)) {
				Document doc = Jsoup.connect(sourceUrl).userAgent("Mozilla").ignoreContentType(true).timeout(timerOut*1000).get();
				String body = doc.select("body").first().text();
				JSONObject data = JSONObject.fromObject(body);
				JSONArray history = data.getJSONObject("data").getJSONArray("history");
				JSONObject periodObject = history.getJSONObject(0);
				
				section = periodObject.getString("qishu");
				openTime = periodObject.getString("timeDraw");
				JSONArray number = periodObject.getJSONArray("nums");

				if(type == 5) {
					int num1 = Integer.valueOf(number.getInt(0));
					int num2 = Integer.valueOf(number.getInt(1));
					int num3 = Integer.valueOf(number.getInt(2));
					int num4 = Integer.valueOf(number.getInt(3));
					int num5 = Integer.valueOf(number.getInt(4));
					if(section.length() == 7) {
						section = DateTimeTool.dateFormat("yyyy", new Date()).concat(section);
					}

					openCode =  String.format("%01d", num1) + "," + String.format("%01d", num2) + "," + String.format("%01d", num3) + "," + String.format("%01d", num4) + "," + String.format("%01d", num5);
				}else if(type== 1 || type == 2){
					int num1 = Integer.valueOf(number.getInt(0));
					int num2 = Integer.valueOf(number.getInt(1));
					int num3 = Integer.valueOf(number.getInt(2));
					
					openCode = String.format("%02d", num1) + "," + String.format("%02d", num2) + "," + String.format("%02d", num3);
				}else if(type == 4||type  == 3) {
					int num1 = Integer.valueOf(number.getInt(0));
					int num2 = Integer.valueOf(number.getInt(1));
					int num3 = Integer.valueOf(number.getInt(2));
					int num4 = Integer.valueOf(number.getInt(3));
					int num5 = Integer.valueOf(number.getInt(4));
					int num6 = Integer.valueOf(number.getInt(5));
					int num7 = Integer.valueOf(number.getInt(6));
					int num8 = Integer.valueOf(number.getInt(7));
					int num9 = Integer.valueOf(number.getInt(8));
					int num10 = Integer.valueOf(number.getInt(9));
					if(section.length() == 7 && type == 3) {
						section = DateTimeTool.dateFormat("yyyy", new Date()).concat(section);
					}

					openCode = num1  + "," + num2  + "," + num3  + "," + num4  + "," + num5  + "," + num6  + "," + num7  + "," + num8  + "," + num9  + "," + num10;
				}
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
}
