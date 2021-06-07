package com.jeetx.timer.lotteryTask.FetchCollector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeetx.timer.lotteryTask.FetchResult;
import com.jeetx.timer.lotteryTask.LotteryDTO;
import com.jeetx.util.DateTimeTool;
import com.jeetx.util.LogUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class KcwCollector {
	private static int timerOut = 3;

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
		
				JSONObject json = JSONObject.fromObject(respone);
				JSONArray data = json.getJSONArray("data");
				section  = data.getJSONObject(0).getString("expect");
				openCode = data.getJSONObject(0).getString("opencode");
				openTime = data.getJSONObject(0).getString("opentime");

				switch (type) {
				case 1:
					openCode = openCode.substring(0, openCode.indexOf("+"));
					String[] number = openCode.split(",");
					Arrays.sort(number);
					int tmp1 = Integer.valueOf(number[0]) + Integer.valueOf(number[1]) + Integer.valueOf(number[2]) + Integer.valueOf(number[3])
							+ Integer.valueOf(number[4]) + Integer.valueOf(number[5]);
					int num1 = tmp1 % 10;
					int tmp2 = Integer.valueOf(number[6]) + Integer.valueOf(number[7]) + Integer.valueOf(number[8]) + Integer.valueOf(number[9])
							+ Integer.valueOf(number[10]) + Integer.valueOf(number[11]);
					int num2 = tmp2 % 10;
					int tmp3 = Integer.valueOf(number[12]) + Integer.valueOf(number[13]) + Integer.valueOf(number[14]) + Integer.valueOf(number[15])
							+ Integer.valueOf(number[16]) + Integer.valueOf(number[17]);
					int num3 = tmp3 % 10;
					openCode = num1 + "," + num2 + "," + num3;
					break;
				case 2:
					String[] numberArr = openCode.split(",");
					num1 = Integer.valueOf(numberArr[1]) + Integer.valueOf(numberArr[4]) + Integer.valueOf(numberArr[7]) + Integer.valueOf(numberArr[10])
							+ Integer.valueOf(numberArr[13]) + Integer.valueOf(numberArr[16]);
					num2 = Integer.valueOf(numberArr[2]) + Integer.valueOf(numberArr[5]) + Integer.valueOf(numberArr[8]) + Integer.valueOf(numberArr[11])
							+ Integer.valueOf(numberArr[14]) + Integer.valueOf(numberArr[17]);
					num3 = Integer.valueOf(numberArr[3]) + Integer.valueOf(numberArr[6]) + Integer.valueOf(numberArr[9]) + Integer.valueOf(numberArr[12])
							+ Integer.valueOf(numberArr[15]) + Integer.valueOf(numberArr[18]);
					String strNum1 = String.valueOf(String.valueOf(num1).charAt(String.valueOf(num1).length() - 1));
					String strNum2 = String.valueOf(String.valueOf(num2).charAt(String.valueOf(num2).length() - 1));
					String strNum3 = String.valueOf(String.valueOf(num3).charAt(String.valueOf(num3).length() - 1));
					openCode = Integer.valueOf(strNum1) + "," + Integer.valueOf(strNum2) + "," + Integer.valueOf(strNum3);
					break;
				}
				
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
