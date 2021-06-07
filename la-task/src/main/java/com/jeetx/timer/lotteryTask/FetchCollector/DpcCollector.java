package com.jeetx.timer.lotteryTask.FetchCollector;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.jeetx.common.dpc.ApiUtil;
import com.jeetx.timer.lotteryTask.FetchResult;
import com.jeetx.timer.lotteryTask.LotteryDTO;
import com.jeetx.util.DateTimeTool;
import com.jeetx.util.HttpUtil;
import com.jeetx.util.LogUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DpcCollector {
	private static final String SECRETKEY = "AF241F8CA6E2AC7138F50D713851432F";
	private static int timerOut = 3;

	public static LotteryDTO getOpenCur(Integer type,Integer lotteryType,String sourceUrl,String dataSource) {
		LotteryDTO lotteryDTO = new LotteryDTO();
		lotteryDTO.setBool(false);

		String section = null;
		String openTime = null;
		String openCode = null;
		try {
			Map<String,Object> parms = new HashMap<String,Object>(); 
			parms.put("lottery_type", type);
			parms.put("page", "1");
			parms.put("limit", "1");
			parms.put("timestamp", DateTimeTool.dateFormat("yyyyMMddHHmmss", new Date()));
			parms.put("sign", generateSign(parms,SECRETKEY));
	    	
			String response = new HttpUtil().postMethod(ApiUtil.BASE_URL+"/api/common/periodsHistoryList", parms,"UTF-8");
			if(StringUtils.isNotBlank(response)) {
				//System.out.println(response);
				JSONObject resultJsonObj = JSONObject.fromObject(response);
				String stateCode = resultJsonObj.getString("code");
				if (stateCode.equals("0")) {
					JSONObject result = resultJsonObj.getJSONObject("result");
					JSONArray data = result.getJSONArray("data");
					if(data != null) {
						section = data.getJSONObject(0).getString("lotteryPeriods");
						openCode = data.getJSONObject(0).getString("lotteryOpenContent");
						openTime = data.getJSONObject(0).getString("lotteryOpenTime");

						openCode = openCode.replaceAll("\\+", ",");
						lotteryDTO = FetchResult.createLotteryDTO(lotteryType, dataSource, section, openTime, openCode);
					}
				}
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
			Map<String,Object> parms = new HashMap<String,Object>(); 
			parms.put("lottery_type", type);
			parms.put("page", "1");
			parms.put("limit", "10");
			parms.put("timestamp", DateTimeTool.dateFormat("yyyyMMddHHmmss", new Date()));
			parms.put("sign", generateSign(parms,SECRETKEY));
	    	
			String response = new HttpUtil().postMethod(ApiUtil.BASE_URL+"/api/common/periodsHistoryList", parms,"UTF-8");
			if(StringUtils.isNotBlank(response)) {
				//System.out.println(response);
				JSONObject resultJsonObj = JSONObject.fromObject(response);
				String stateCode = resultJsonObj.getString("code");
				if (stateCode.equals("0")) {
					JSONObject result = resultJsonObj.getJSONObject("result");
					JSONArray data = result.getJSONArray("data");
					if(data != null) {
						historyList = new ArrayList<LotteryDTO>();
						for (int j = 1; j < data.size(); j++) {
							String section = data.getJSONObject(j).getString("lotteryPeriods");
							String openCode = data.getJSONObject(j).getString("lotteryOpenContent");
							String openTime = data.getJSONObject(j).getString("lotteryOpenTime");
							openCode = openCode.replaceAll("\\+", ",");
					    	
							historyList.add(FetchResult.createLotteryDTO(lotteryType, dataSource, section, openTime, openCode));
						}
					}
					
				}
			}
		}catch (Exception e) {
			//e.printStackTrace();
			LogUtil.info(dataSource+"采集器执行异常："+e.getMessage());
		}
		return historyList ;
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
				}
			}
		}
		
		//System.out.println(sign.append(SECRETKEY).toString());
		return com.jeetx.util.MD5Util.MD5Encode(sign.append(secretKey).toString(),"UTF-8").toLowerCase() ;
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
	
}
