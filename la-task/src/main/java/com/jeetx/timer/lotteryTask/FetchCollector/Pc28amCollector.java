package com.jeetx.timer.lotteryTask.FetchCollector;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.jeetx.timer.lotteryTask.FetchResult;
import com.jeetx.timer.lotteryTask.LotteryDTO;
import com.jeetx.util.DateTimeTool;
import com.jeetx.util.EncodingUtil;
import com.jeetx.util.HttpUtil;
import com.jeetx.util.LogUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Pc28amCollector {
	private static int timerOut = 3;
	private static int unknown = 2;
	private static String portal = "m0";

    public static String createBaseURI(String salt,String skey){
    	// 时间戳加盐
    	BigDecimal bd = new BigDecimal(salt);
    	long newSalt = Long.parseLong(bd.toPlainString());
        String ts= new StringBuffer().append(System.currentTimeMillis() + newSalt).toString();
        try {
            return "?appid=m0&sig="+createToken(ts,skey)+"&se="+ts;
        } catch (UnsupportedEncodingException e) {
            //ig
        }
        return null;
    }
    
    private static String createToken(String ts,String skey) throws UnsupportedEncodingException{
        String str1 = ts+"/"+unknown+"/"+portal+"/"+skey;
        byte[] secretKey = skey.getBytes("UTF8");
        HmacUtils hm1 = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secretKey);
        byte[] digest = hm1.hmac(str1);
        String str2 = Base64.encodeBase64String(digest);
        return EncodingUtil.encodeURIComponent(str2);
    }
    
    public static LotteryDTO getOpenCur(Integer type,Integer lotteryType,String sourceUrl,String dataSource) {
		LotteryDTO lotteryDTO = new LotteryDTO();
		lotteryDTO.setBool(false);
		
		String section = null;
		String openTime = null;
		String openCode = null;
		try {
			//String[] domains = new String[] { "28pc.cc", "k028.com" };
			String[] domains = new String[] { "28pc.cc", "28pc.cc" };
			SecureRandom random = new SecureRandom();
			// 1.取HTML
			String domain = domains[random.nextInt(domains.length)];
			
			if(StringUtils.isNotBlank(sourceUrl)) {
				domain = sourceUrl.replace("https://", "").replace("http://", "");
			}

			String entryUrl = "https://" + domain;
			String redirectUrl = entryUrl + "/pc28";
			
			Document parm = Jsoup.connect(redirectUrl).ignoreContentType(true).referrer(entryUrl).timeout(5 * 1000).get();
			
			String respHtml = parm.toString();
			if (respHtml == null || "".equals(respHtml)) {
				return null;
			}
			
			String strtStr = "<script src=";
			String endStr = "></script>";
			int strtIdx = respHtml.lastIndexOf(strtStr);
			int endIdx = respHtml.lastIndexOf(endStr);
			if (strtIdx == -1 || endIdx == -1 || strtIdx >= endIdx) {
				return null;
			}
			
			String jsUrl = respHtml.substring(strtIdx + strtStr.length(), endIdx).replaceAll("\"", "");
			String fullJsUrl = entryUrl + jsUrl;
			//log.info(fullJsUrl);
			
			// 2.取js内容
			String jsCont = new HttpUtil().getMethod(fullJsUrl, "utf-8");
			
			if (jsCont == null || "".equals(jsCont)) {
				return null;
			}
			
			//log.debug(jsCont);
			
			// 3.解析出skey
			String markedStrtStr = "createBaseURI:function(){";
			strtIdx = jsCont.indexOf(markedStrtStr);
			if (strtIdx == -1) {
				return null;
			}
			endIdx = jsCont.indexOf("}", strtIdx+markedStrtStr.length());
			if (endIdx == -1) {
				return null;
			}
			
			String funcCnt = jsCont.substring(strtIdx+markedStrtStr.length(), endIdx); // 函数内容
			/*String skey = StringUtils.replace(funcCnt, "var e=Date.now()+6e5;return\"?appid=m0&sig=\"+b.createToken(\"m0\",\"", "");
			skey = StringUtils.replace(skey, "\",2,e)+\"&se=\"+e", "");
			log.info(skey);*/
			
			String[] sa = StringUtils.split(funcCnt, '"');
			final String skey = sa.length>5?sa[5]:null;
			if(skey == null) {
				LogUtil.info("截取密钥失败");
			    return null;
			}
			
			// 4.解析出apiDomain
			markedStrtStr = "{domain:\"";
			strtIdx = jsCont.indexOf(markedStrtStr);
			if(strtIdx == -1) {
				return null;
			}
			endIdx = jsCont.indexOf("\"", strtIdx+markedStrtStr.length());
			if (endIdx == -1) {
				return null;
			}
			
			String apiDomain = jsCont.substring(strtIdx+markedStrtStr.length(), endIdx);
			String url = apiDomain+"/API/Web/2/MyOpens"+createBaseURI("6e5",skey)+"&top=10&ps=10&fi=0&lt="+type;
			//log.info(url);
			
			Document doc = Jsoup.connect(url).ignoreContentType(true)
			        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36")
			        .timeout(timerOut * 1000).get();
			//log.info(doc.toString());

			String body = doc.select("body").first().text();
			JSONObject data = JSONObject.fromObject(body);
			JSONArray history = data.getJSONArray("data");
			JSONObject periodObject = history.getJSONObject(0);
			
			section = periodObject.get("section").toString();  
			openTime= DateTimeTool.DateParserT("yyyy-MM-dd HH:mm:ss",Long.valueOf(periodObject.get("openTime").toString()));  
			openCode = periodObject.get("middleCode").toString();  
			
			lotteryDTO = FetchResult.createLotteryDTO(lotteryType, dataSource, section, openTime, openCode);
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
			String[] domains = new String[] { "28pc.cc", "28pc.cc" };
			SecureRandom random = new SecureRandom();
			// 1.取HTML
			String domain = domains[random.nextInt(domains.length)];
			
			if(StringUtils.isNotBlank(sourceUrl)) {
				domain = sourceUrl.replace("https://", "").replace("http://", "");
			}

			String entryUrl = "https://" + domain;
			String redirectUrl = entryUrl + "/pc28";
			
			Document parm = Jsoup.connect(redirectUrl).ignoreContentType(true).referrer(entryUrl).timeout(5 * 1000).get();
			
			String respHtml = parm.toString();
			if (respHtml == null || "".equals(respHtml)) {
				return null;
			}
			
			String strtStr = "<script src=";
			String endStr = "></script>";
			int strtIdx = respHtml.lastIndexOf(strtStr);
			int endIdx = respHtml.lastIndexOf(endStr);
			if (strtIdx == -1 || endIdx == -1 || strtIdx >= endIdx) {
				return null;
			}
			
			String jsUrl = respHtml.substring(strtIdx + strtStr.length(), endIdx).replaceAll("\"", "");
			String fullJsUrl = entryUrl + jsUrl;
			//log.info(fullJsUrl);
			
			// 2.取js内容
			String jsCont = new HttpUtil().getMethod(fullJsUrl, "utf-8");
			
			if (jsCont == null || "".equals(jsCont)) {
				return null;
			}
			
			//log.debug(jsCont);
			
			// 3.解析出skey
			String markedStrtStr = "createBaseURI:function(){";
			strtIdx = jsCont.indexOf(markedStrtStr);
			if (strtIdx == -1) {
				return null;
			}
			endIdx = jsCont.indexOf("}", strtIdx+markedStrtStr.length());
			if (endIdx == -1) {
				return null;
			}
			
			String funcCnt = jsCont.substring(strtIdx+markedStrtStr.length(), endIdx); // 函数内容
			/*String skey = StringUtils.replace(funcCnt, "var e=Date.now()+6e5;return\"?appid=m0&sig=\"+b.createToken(\"m0\",\"", "");
			skey = StringUtils.replace(skey, "\",2,e)+\"&se=\"+e", "");
			log.info(skey);*/
			
			String[] sa = StringUtils.split(funcCnt, '"');
			final String skey = sa.length>5?sa[5]:null;
			if(skey == null) {
				LogUtil.info("截取密钥失败");
			    return null;
			}
			
			// 4.解析出apiDomain
			markedStrtStr = "{domain:\"";
			strtIdx = jsCont.indexOf(markedStrtStr);
			if(strtIdx == -1) {
				return null;
			}
			endIdx = jsCont.indexOf("\"", strtIdx+markedStrtStr.length());
			if (endIdx == -1) {
				return null;
			}
			
			String apiDomain = jsCont.substring(strtIdx+markedStrtStr.length(), endIdx);
			String url = apiDomain+"/API/Web/2/MyOpens"+createBaseURI("6e5",skey)+"&top=10&ps=10&fi=0&lt="+type;
			//log.info(url);
			
			Document doc = Jsoup.connect(url).ignoreContentType(true)
			        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36")
			        .timeout(timerOut * 1000).get();
			//log.info(doc.toString());

			String body = doc.select("body").first().text();
			JSONObject data = JSONObject.fromObject(body);
			JSONArray history = data.getJSONArray("data");

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
}
