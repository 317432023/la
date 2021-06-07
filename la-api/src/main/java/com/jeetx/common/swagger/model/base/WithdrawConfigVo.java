package com.jeetx.common.swagger.model.base;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;

public class WithdrawConfigVo implements Serializable{
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(required=true,value="受理提现开始时间") 
	private String beginTime; 
	@ApiModelProperty(required=true,value="受理提现结束时间") 
	private String endTime; 
	@ApiModelProperty(required=true,value="提现最低金额") 
	private BigDecimal minApplyAmount; 
	@ApiModelProperty(required=true,value="提现最高金额") 
	private BigDecimal maxApplyAmount; 
	@ApiModelProperty(required=true,value="提现限制每日次数") 
	private Integer applyDailyTimes; //
	@ApiModelProperty(required=true,value="温馨提示") 
	private String reminder;
	public String getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public BigDecimal getMinApplyAmount() {
		return minApplyAmount;
	}
	public void setMinApplyAmount(BigDecimal minApplyAmount) {
		this.minApplyAmount = minApplyAmount;
	}
	public BigDecimal getMaxApplyAmount() {
		return maxApplyAmount;
	}
	public void setMaxApplyAmount(BigDecimal maxApplyAmount) {
		this.maxApplyAmount = maxApplyAmount;
	}
	public Integer getApplyDailyTimes() {
		return applyDailyTimes;
	}
	public void setApplyDailyTimes(Integer applyDailyTimes) {
		this.applyDailyTimes = applyDailyTimes;
	}
	public String getReminder() {
		return reminder;
	}
	public void setReminder(String reminder) {
		this.reminder = reminder;
	} 
	
	
}
