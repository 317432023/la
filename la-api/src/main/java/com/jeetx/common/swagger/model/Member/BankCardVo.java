package com.jeetx.common.swagger.model.Member;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

public class BankCardVo implements Serializable{
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(required=true,value="ID") 
	private Integer id;
	@ApiModelProperty(required=true,value="持卡人姓名") 
	private String cardholder; //持卡人姓名
	@ApiModelProperty(required=true,value="银行卡号") 
	private String cardNo;//银行卡号
	@ApiModelProperty(required=true,value="银行名称") 
	private String bankName; // 银行名称
	@ApiModelProperty(required=true,value="开户地点") 
	private String bankPlace; // 开户地点
	@ApiModelProperty(required=true,value="开户支行") 
	private String bankBranch;//开户支行
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCardholder() {
		return cardholder;
	}
	public void setCardholder(String cardholder) {
		this.cardholder = cardholder;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankPlace() {
		return bankPlace;
	}
	public void setBankPlace(String bankPlace) {
		this.bankPlace = bankPlace;
	}
	public String getBankBranch() {
		return bankBranch;
	}
	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}
	
	
}
