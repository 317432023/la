package com.jeetx.common.swagger.model.system;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

public class SystemConfigVo implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(required=true,value="名称") 
	private String title;
	@ApiModelProperty(required=true,value="配置编码") 
	private String name;
	@ApiModelProperty(required=true,value="配置值") 
	private String value;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
