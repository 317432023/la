package com.jeetx.common.swagger.model.base;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

public class RechargeUnderLineConfigVo implements Serializable{
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(required=true,value="标题") 
	private String title; 
	@ApiModelProperty(required=true,value="图标链接") 
	private String iconImg;
	@ApiModelProperty(required=true,value="二维码") 
	private String qrCodeLink; 
	@ApiModelProperty(required=true,value="最低金额") 
	private String minAmount;
	@ApiModelProperty(required=true,value="最高金额") 
	private String maxAmount;
	@ApiModelProperty(required=true,value="温馨提示") 
	private String reminder;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getIconImg() {
		return iconImg;
	}
	public void setIconImg(String iconImg) {
		this.iconImg = iconImg;
	}
	public String getQrCodeLink() {
		return qrCodeLink;
	}
	public void setQrCodeLink(String qrCodeLink) {
		this.qrCodeLink = qrCodeLink;
	}
	public String getMinAmount() {
		return minAmount;
	}
	public void setMinAmount(String minAmount) {
		this.minAmount = minAmount;
	}
	public String getMaxAmount() {
		return maxAmount;
	}
	public void setMaxAmount(String maxAmount) {
		this.maxAmount = maxAmount;
	}
	public String getReminder() {
		return reminder;
	}
	public void setReminder(String reminder) {
		this.reminder = reminder;
	} 
	
	
}
