package com.jeetx.util;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.sf.json.JSONObject;


public class HtmlUtil {

	public static Document docBack(String url) {
		Connection con = Jsoup.connect(url).timeout(3000);
		try {
			con.header("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
			con.header("Content-Type", "text/html; charset=utf-8");
			con.header("Connection", "keep-alive");
			Document doc = con.timeout(5000).get();
			return doc;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}
    public static Document wbBack(String url){ 
    	Document page_html = docBack(url);
    	System.out.println(page_html.toString());
        Elements links=page_html.getElementsByTag("script");  
        String allstring=null;  
        int tag=0;  
        for(Element link:links){  
            String script=link.toString();  
            if (script.length()>tag) {  
                tag=script.length();  
                allstring=script;  
            }  
        }  
        int beginIndex = 0;
        
        int endIndex = 0;

        beginIndex = allstring.indexOf("({")+1;

        endIndex = allstring.lastIndexOf(")</script>");
        
        allstring = allstring.substring(beginIndex,endIndex);
        JSONObject json_test = JSONObject.fromObject(allstring);
        Document doc = Jsoup.parse( json_test.get("html").toString());  



        return doc;  
    }  
    	/*WebClient webClient = new WebClient();
     
    	webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        // 启动JS
    	webClient.getOptions().setJavaScriptEnabled(true);
    	// this.webClient.waitForBackgroundJavaScript(10000);
        // 禁用Css，可避免自动二次请求CSS进行渲染
    	webClient.getOptions().setCssEnabled(false);
        // 启动客户端重定向
    	webClient.getOptions().setRedirectEnabled(true);
        // js运行错误时，是否抛出异常
    	webClient.getOptions().setThrowExceptionOnScriptError(false);
    	
        HtmlPage page = null;
        try {
            page = webClient.getPage(url);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            webClient.close();
        }
        webClient.waitForBackgroundJavaScript(10000);
        String pageXml = page.asXml();
        System.out.println(pageXml);
        Document document = Jsoup.parse(pageXml);
        return document;*/
}