package com.jeetx.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.ParseException;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jeetx.common.model.dto.Parm;

import net.sf.json.JSONObject;

public class OriginalUntil  implements Runnable{
	int x;
	int fragmentsNum ;
	String content;
	String newContent;
	JSONObject ajaxJson;
	static double red ;
	int redCount ;
	String url = null;
	String className = null;
	Map<String, String> map = null;

	static Map<String, Object> tempMap = new HashMap<String, Object>();
	static List<Parm> tempList = new ArrayList<Parm>();
	
	public OriginalUntil(int x, int fragmentsNum,String content,String newContent,JSONObject ajaxJson) {
		this.x = x;
		this.fragmentsNum = fragmentsNum;
		this.content = content;
		this.newContent = newContent;
		this.ajaxJson = ajaxJson;
	}
	@Override
	public void run() {
		Connection con = null;
		Parm parm = new Parm();
		redCount = 0;// 飘红次数
		if (x == content.length() / fragmentsNum) {
			newContent = content.substring(x * fragmentsNum, content.length());
		} else {			
			String newContent2 = content.substring(x * fragmentsNum, fragmentsNum + x * fragmentsNum);	
			if(x==0) 
				newContent = newContent2.indexOf("\r\n")!=-1?content.substring( x * fragmentsNum, fragmentsNum + x * fragmentsNum+1):newContent2;
			else 
				newContent = newContent2.indexOf("\r\n")!=-1?content.substring(x * fragmentsNum+1, fragmentsNum + x * fragmentsNum+1):newContent2;
		}
		try {
			switch (ajaxJson.getString("website")) {
			case "baidu":
				url = "http://www.baidu.com/s?f=8&rsv_bp=1&rsv_idx=2&tn=baidu&wd=word&oq=op&rqlang=cn&rsv_enter=0&bs=word".replaceAll("word", URLEncoder.encode(newContent, "utf-8")).replace("op", URLEncoder.encode(URLEncoder.encode(newContent, "utf-8"),"utf-8"));
				con = Jsoup.connect(url);
				className="c-abstract";
				break;
			case "360":
				url = "https://www.so.com/s?q=word&pn=1".replace("word", URLEncoder.encode(newContent, "utf-8"));
				con = Jsoup.connect(url);
				className="res-list";
				break;
			case "sougou":
				url="http://www.sogou.com/web?query=word&num=20".replace("word",URLEncoder.encode(newContent, "utf-8"));
				con = Jsoup.connect(url);
				className="results";
				break;
			}
			Document doc = null;
			doc = con.get();
			Response response = con.execute();
	        if(ajaxJson.getString("website").equals("baidu")) {
	        	if(map==null)map = response.cookies();
	        	doc = Jsoup.connect(url).cookies(map).headers(response.headers()).timeout(50000).get();
		        Elements links = doc.getElementsByAttributeValue("class",className);
		        System.out.println("=======================");
		        for (Element element : links) {
					if (element.text().indexOf("更多关于"+newContent+"的问题")<0 && compare1(element.text(),newContent)>= Integer.valueOf(ajaxJson.getString("redNum"))) {
						redCount++;
					}
//					Elements ems = element.getElementsByTag("em");
//					for (Element em : ems) {
//						if (compare(em.text(),newContent)>= Integer.valueOf(ajaxJson.getString("redNum"))||(newContent.indexOf(em.text().replaceAll(" +", "")) != -1&& em.text().length() >= Integer.valueOf(ajaxJson.getString("redNum")))) {
//							redCount++;
//						}	
//					}
				}		
			}else {
				Elements links = doc.getElementsByAttributeValue("class",className);
				for (Element element : links) {
					if (compare1(element.text(),newContent)>= Integer.valueOf(ajaxJson.getString("redNum"))) {
						redCount++;
					}
//					Elements ems = element.getElementsByTag("em");
//					for (Element em : ems) {
//						if (compare(em.text(),newContent)>= Integer.valueOf(ajaxJson.getString("redNum"))||(newContent.indexOf(em.text()/* .replaceAll( "[\\pP+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]" , "") */) != -1&& em.text().length() >= Integer.valueOf(ajaxJson.getString("redNum")))) {
//							redCount++;
//						}	
//					}
				}
			}
	        
			if (redCount == 0&&newContent.length() > Integer.valueOf(ajaxJson.getString("lastNum"))) { 
				red += 1;
				//System.out.println(red);
			}
			parm.setFragmentsNum(fragmentsNum);
			parm.setText(newContent);
			parm.setRedNum(redCount);
			parm.setUrl(url);
			parm.setId(x);
			
			tempList.add(parm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static JSONObject originalCheck(JSONObject ajaxJson) throws URISyntaxException, ParseException, IOException {
		try {	
			red = 0;
	        ExecutorService threadPool = Executors.newFixedThreadPool(10);
			String content =ajaxJson.getString("content");
			JSONObject json = ajaxJson;
			int fragmentsNum=Integer.valueOf(ajaxJson.getString("contentNum"));
			content = ToDBC(content).replaceAll("~", "").replaceAll("—", "");//过滤字母
			Pattern p = Pattern.compile("\\s* |\t|\r|\n");// 去空格，回车，换行符，制表符
			Matcher m = p.matcher(content);
			content = m.replaceAll("");
			String newContent = null;
			int length = content.length() % fragmentsNum != 0 ? content.length() / fragmentsNum + 1 : content.length() / fragmentsNum;
			DecimalFormat df = new DecimalFormat("0.00%");
			
			for (int i = 0; i < length; i++) {
				OriginalUntil t = new OriginalUntil(i,fragmentsNum,content,newContent,json);
				threadPool.execute(new Thread(t));
			}
			threadPool.shutdown();  
			if(!threadPool.awaitTermination(40,TimeUnit.SECONDS)){//20S
				   System.out.println(" 到达指定时间，还有线程没执行完，不再等待，关闭线程池!");
				   threadPool.shutdownNow();  
			}
			
			Collections.sort(tempList);
			tempMap.put("website", tempList);
			tempMap.put("wordCount", content.length());
			tempList  = new ArrayList<Parm>();
			if (red == 0)
				tempMap.put("originalNum", "0%");
			else
				tempMap.put("originalNum", df.format((red / length)));
		    JSONObject jsonObject = JSONObject.fromObject(tempMap);
		    return jsonObject;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	//全角转半角
	public static String ToDBC(String input){
		char[] c=input.toCharArray();
		for (int i = 0; i < c.length; i++){
			if (c[i]==12288){
				c[i]= (char)32; 
				continue;
			}
			if (c[i]>65280 && c[i]<65375)c[i]=(char)(c[i]-65248);
		}
		return new String(c);
	}
	
	
	//两个字符串连续位置相同字数
//	public static int compare(String str1,String str2){
//		//System.out.println(str1+">>>"+str2);
//		if(str1==null || str2==null)return 0;
//		int len=str1.length()>str2.length()?str2.length():str1.length();
//		char c1,c2;
//		int okLen=0;
//		for(int i=0;i<len;i++){
//			c1=str1.charAt(i);
//			c2=str2.charAt(i);
//			if(c1==c2){
//			   okLen++;
//			}else{
//			    okLen=0;
//			}
//		}
//		return okLen;
//	}
	
	public static int compare1(String str1, String str2) {
		int length = 0;
		str1 = delHTMLTag(str1);
		str2 = delHTMLTag(str2);
	
		if (str1.length() > str2.length())
			length = deal(str2, str1).length();
		else
			length = deal(str1, str2).length();
		
		if(length<=12) {
			System.out.println(str1+">>>"+str2+">>>"+length);
		}
		return length;
	}

	public static String deal(String str1, String str2) {
		String[] arr = new String[str2.length()*str1.length()];
		boolean flag = false;
		String max = "";
		int k = 0;
		for (int j = 0; j < str1.length(); j++) {
			for (int i = j + 1; i <= str1.length(); i++)
				arr[k++] = str1.substring(j, i);
		}

		for (int i = 0; i < k; i++) {
			if (str2.contains(arr[i])) {
				flag = true;
				if (max.length() < arr[i].length()) {
					max = arr[i];
				}
			}
		}
		return flag ? max : null;
	}
	
	public static String delHTMLTag(String htmlStr){ 
        String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式 
        String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式 
        String regEx_html="<[^>]+>"; //定义HTML标签的正则表达式 
         
        Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE); 
        Matcher m_script=p_script.matcher(htmlStr); 
        htmlStr=m_script.replaceAll(""); //过滤script标签 
         
        Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE); 
        Matcher m_style=p_style.matcher(htmlStr); 
        htmlStr=m_style.replaceAll(""); //过滤style标签 
         
        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE); 
        Matcher m_html=p_html.matcher(htmlStr); 
        htmlStr=m_html.replaceAll(""); //过滤html标签 

        return htmlStr.trim(); //返回文本字符串 
    } 
}
