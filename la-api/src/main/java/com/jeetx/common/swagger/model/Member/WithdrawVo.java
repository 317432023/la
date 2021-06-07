package com.jeetx.common.swagger.model.Member;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

public class WithdrawVo implements Serializable{
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(required=true,value="ID") 
	private Integer id;
	@ApiModelProperty(required=true,value="流水号") 
	private String tradeCode;
	@ApiModelProperty(required=true,value="申请金额") 
	private String applyAmount; 
	@ApiModelProperty(required=true,value="申请时间") 
	private String createTime;
	@ApiModelProperty(value="申请摘要信息") 
	private String applyRemark;
	@ApiModelProperty(required=true,value="状态（1申请中、2提现成功、3打款失败）") 
	private Integer status; 
	@ApiModelProperty(required=true,value="提现账号信息") 
	private String bankCardInfo;
	@ApiModelProperty(value="处理时间") 
	private String confirmTime;
	@ApiModelProperty(value="处理摘要信息") 
	private String confirmRemark;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTradeCode() {
		return tradeCode;
	}
	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}
	public String getApplyAmount() {
		return applyAmount;
	}
	public void setApplyAmount(String applyAmount) {
		this.applyAmount = applyAmount;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getApplyRemark() {
		return applyRemark;
	}
	public void setApplyRemark(String applyRemark) {
		this.applyRemark = applyRemark;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getBankCardInfo() {
		return bankCardInfo;
	}
	public void setBankCardInfo(String bankCardInfo) {
		this.bankCardInfo = bankCardInfo;
	}
	public String getConfirmTime() {
		return confirmTime;
	}
	public void setConfirmTime(String confirmTime) {
		this.confirmTime = confirmTime;
	}
	public String getConfirmRemark() {
		return confirmRemark;
	}
	public void setConfirmRemark(String confirmRemark) {
		this.confirmRemark = confirmRemark;
	}
}
