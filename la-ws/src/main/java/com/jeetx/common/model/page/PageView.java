package com.jeetx.common.model.page;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.jeetx.common.constant.Globals;

import net.sf.json.JSONArray;

public class PageView {
	private Integer code = 0;// 是否成功
	private String msg = "成功";// 提示信息
	
	private long count;//记录数
	private long pageCount;//总页数
	private int limit = Globals.LIMIT;//每页显示记录
	private int page = 1;//当前页
	private JSONArray data;//分页数据	
	private String pagingHtml;
	
	/** 页码开始索引和结束索引 **/
	private PageIndex pageindex;

	public PageView() {}
	
	public PageIndex getPageindex() {
		return pageindex;
	}

	public void setPageindex(PageIndex pageindex) {
		this.pageindex = pageindex;
	}

	public int getFirstResult() {//要获取记录的开始索引
		return (this.page-1)*this.limit;
	}

	public PageView(int limit, int page) {
		this.limit = limit;
		this.page = page;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
		setPageCount(this.count%this.limit==0? this.count/this.limit : this.count/this.limit+1);
	}
	
	public JSONArray getData() {
		return data;
	}

	public void setData(JSONArray data) {
		this.data = data;
	}

	public long getPageCount() {
		return pageCount;
	}

	public void setPageCount(long pageCount) {
		this.pageCount = pageCount;
		this.pageindex = PageIndex.getPageIndex(pageCount, page, pageCount);
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getPagingHtml() {
	    StringBuilder pageHTML = new StringBuilder();
	    
	    if(pageCount >0){
		    pageHTML.append("<input type=\"hidden\" id=\"page\" name=\"page\" value=\"").append(pageCount >0?page:1).append("\"/>");
		    pageHTML.append("<i class=\"fl\">");
		    pageHTML.append("共<span style=\"margin: 0px 3px;\">").append(count).append("</span>条记录，第<span style=\"margin: 0px 3px;\">").append(pageCount >0?page:1).append("</span>/<span style=\"margin: 0px 3px;\">").append(pageCount).append("</span>页&nbsp;&nbsp;");
		    pageHTML.append("</i>");
		    
		    pageHTML.append("<em class=\"fr\" style=\"color:#333;\">");
    	    
    	    if(page == this.getPageindex().getStartindex()){
    	    	pageHTML.append("<span style=\"margin: 0px 5px;\">首页<span>");
    	    	pageHTML.append("<span style=\"margin: 0px 5px;\">上一页<span>");
    	    }else{
    	    	pageHTML.append("<span style=\"margin: 0px 5px;\"><a href=\"javascript:ajaxPage('"+this.getPageindex().getStartindex()+"')\">首页</a><span>");
    	    	int temp = page-1;
    	    	pageHTML.append("<span style=\"margin: 0px 5px;\"><a href=\"javascript:ajaxPage('"+temp+"')\">上一页</a><span>");
    	    }
    	    if(page == this.getPageindex().getEndindex()){
    	    	pageHTML.append("<span style=\"margin: 0px 5px;\">下一页<span>");
    	    	pageHTML.append("<span style=\"margin: 0px 5px;\">尾页<span>");
    	    }else{
    	    	int temp = page+1;
    	    	pageHTML.append("<span style=\"margin: 0px 5px;\"><a href=\"javascript:ajaxPage('"+temp+"')\">下一页</a><span>");
    	    	pageHTML.append("<span style=\"margin: 0px 5px;\"><a href=\"javascript:ajaxPage('"+this.getPageindex().getEndindex()+"')\">尾页</a><span>");
    	    }
    	    
    	    pageHTML.append("</em>");
    	}

		this.pagingHtml = pageHTML.toString();
		return pagingHtml;
	}

	public void setPagingHtml(String pagingHtml) {
		this.pagingHtml = pagingHtml;
	}

	public void sendJson(HttpServletRequest request,HttpServletResponse response) throws IOException{
		JSONObject obj = new JSONObject();
		obj.put("code", this.getCode());
		obj.put("msg", this.getMsg());
		obj.put("count", this.getCount());
		obj.put("data", this.getData()!=null?this.getData().toArray():null);

		response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write(obj.toJSONString());
        writer.flush();
        writer.close();
	}
	
	/**
	 * 增加是否显示分页HTML
	 * @param request
	 * @param response
	 * @param showPaging 
	 * @throws IOException
	 */
	public void sendJson(HttpServletRequest request,HttpServletResponse response,boolean showPaging) throws IOException{
		JSONObject obj = new JSONObject();
		obj.put("code", this.getCode());
		obj.put("msg", this.getMsg());
		obj.put("count", this.getCount());
		obj.put("data", this.getData()!=null?this.getData().toArray():null);
		if(showPaging) {
			obj.put("paging", this.getPagingHtml());
		}

		response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write(obj.toJSONString());
        writer.flush();
        writer.close();
	}
}
