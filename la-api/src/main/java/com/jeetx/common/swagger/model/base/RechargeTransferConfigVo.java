package com.jeetx.common.swagger.model.base;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;

public class RechargeTransferConfigVo implements Serializable{
	private static final long serialVersionUID = 1L;
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
	@ApiModelProperty(required=true,value="持卡人姓名") 
	private String cardholder;
	@ApiModelProperty(required=true,value="银行卡号") 
	private String bankCard;
	@ApiModelProperty(required=true,value="银行名称") 
	private String bankName;
	@ApiModelProperty(required=true,value="开户支行") 
	private String openBankBranch;
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
	public String getCardholder() {
		return cardholder;
	}
	public void setCardholder(String cardholder) {
		this.cardholder = cardholder;
	}
	public String getBankCard() {
		return bankCard;
	}
	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getOpenBankBranch() {
		return openBankBranch;
	}
	public void setOpenBankBranch(String openBankBranch) {
		this.openBankBranch = openBankBranch;
	}
}
