package com.jeetx.common.swagger.model;

import org.apache.commons.lang.StringUtils;

import com.jeetx.util.LogUtil;

import io.swagger.annotations.ApiModelProperty;
 
public class JsonResult {
	@ApiModelProperty(value="返回编码，0成功，其它失败")
	private int code=-1;
	@ApiModelProperty(value="返回详细信息")
	private String message;
//	@ApiModelProperty(value="返回新的token，仅对已登录的用户请求有效")
//	private String token;
	@ApiModelProperty(value="返回的结果集")
	private Object result = null;
	
    public JsonResult(Boolean developMode,String methodName,String requestUrl,
    		int code,String message,Object result) {
    	this.code = code;
    	this.message = StringUtils.isNotBlank(message)?message:"";
    	this.result = result!=null?result:"";
    	
		if(developMode && !"获取服务器时间".equalsIgnoreCase(methodName)) {
			StringBuffer sBuffer = new StringBuffer();
			sBuffer.append(methodName).append(":");
			sBuffer.append("接收信息-").append(requestUrl);
			sBuffer.append(", 返回信息-").append(code+"-"+message);
			LogUtil.info(sBuffer.toString());
		}
    }
	
//    public JsonResult(Boolean developMode,String methodName,String requestUrl,
//    		int code,String message,String token,Object result) {
//    	this.code = code;
//    	this.message = StringUtils.isNotBlank(message)?message:"";
//    	this.token = StringUtils.isNotBlank(token)?token:"";
//    	this.result = result!=null?result:"";
//    	
//		if(developMode) {
//			StringBuffer sBuffer = new StringBuffer();
//			sBuffer.append(methodName).append(":");
//			sBuffer.append("接收信息-").append(requestUrl);
//			sBuffer.append(", 返回信息-").append(code+"-"+message);
//			LogUtil.info(sBuffer.toString());
//		}
//    }
      
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
//
//	public String getToken() {
//		return token;
//	}
//
//	public void setToken(String token) {
//		this.token = token;
//	}
}
