package com.jeetx.common.swagger.model.lottery;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;


public class LotteryTypeVo implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	@ApiModelProperty(required=true,value="ID") 
	private Integer id;
	@ApiModelProperty(required=true,value="彩票名称") 
	private String title;
	@ApiModelProperty(value="图标地址") 
	private String picLink;
	@ApiModelProperty(value="彩票描述") 
	private String remarks;
	@ApiModelProperty(required=true,value="状态(1启用、0禁用、2隐藏)") 
	private Integer status;
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
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getPicLink() {
		return picLink;
	}
	public void setPicLink(String picLink) {
		this.picLink = picLink;
	}
	
}
