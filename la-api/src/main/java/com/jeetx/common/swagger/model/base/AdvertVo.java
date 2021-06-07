package com.jeetx.common.swagger.model.base;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

public class AdvertVo implements Serializable{
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(required=true,value="ID") 
	private Integer id;
	@ApiModelProperty(required=true,value="标题") 
	private String title;
	@ApiModelProperty(required=true,value="图片链接") 
	private String picLink;
	@ApiModelProperty(value="跳转地址") 
	private String httpLink;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getHttpLink() {
		return httpLink;
	}
	public void setHttpLink(String httpLink) {
		this.httpLink = httpLink;
	}
	public String getPicLink() {
		return picLink;
	}
	public void setPicLink(String picLink) {
		this.picLink = picLink;
	}
}
