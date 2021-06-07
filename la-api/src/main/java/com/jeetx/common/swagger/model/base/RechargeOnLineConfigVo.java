package com.jeetx.common.swagger.model.base;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import net.sf.json.JSONArray;

public class RechargeOnLineConfigVo implements Serializable{
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(required=true,value="提供商ID") 
	private Integer providerId;
	@ApiModelProperty(required=true,value="标题") 
	private String title; 
	@ApiModelProperty(required=true,value="图标链接") 
	private String iconImg;
	@ApiModelProperty(required=true,value="最低金额") 
	private String minAmount;
	@ApiModelProperty(required=true,value="最高金额") 
	private String maxAmount;
	@ApiModelProperty(required=true,value="温馨提示") 
	private String reminder;
	/*@ApiModelProperty(required=true,value="接口提供商（6明捷支付、8佰富支付）") 
	private Integer providerType;*/
	@ApiModelProperty(required=true,value="支付方式json") 
	private JSONArray payTypeJson;
	@ApiModelProperty(required=true,value="是否允许金额必填(1必须，0不必须)") 
	private Integer isAmountRequire; 
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
	/*public Integer getProviderType() {
		return providerType;
	}
	public void setProviderType(Integer providerType) {
		this.providerType = providerType;
	}*/
	public JSONArray getPayTypeJson() {
		return payTypeJson;
	}
	public void setPayTypeJson(JSONArray payTypeJson) {
		this.payTypeJson = payTypeJson;
	}
	public Integer getProviderId() {
		return providerId;
	}
	public void setProviderId(Integer providerId) {
		this.providerId = providerId;
	}
	public Integer getIsAmountRequire() {
		return isAmountRequire;
	}
	public void setIsAmountRequire(Integer isAmountRequire) {
		this.isAmountRequire = isAmountRequire;
	}
	
}
