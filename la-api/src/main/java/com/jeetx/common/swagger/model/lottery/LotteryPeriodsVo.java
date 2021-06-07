package com.jeetx.common.swagger.model.lottery;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;


public class LotteryPeriodsVo implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	@ApiModelProperty(required=true,value="彩票类型") 
	private Integer lotteryType;
	@ApiModelProperty(required=true,value="彩票名称") 
	private String lotteryName;
	@ApiModelProperty(required=true,value="彩票期数") 
	private String lotteryPeriods;//彩票期数
	@ApiModelProperty(required=true,value="期数状态(1投注中 、2已封盘、3已开奖 )") 
	private Integer status;//期数状态(1投注中 、2已封盘中、3已开奖 )
	@ApiModelProperty(value="开始时间") 
	private String lotteryBeginTime;//开始时间
	@ApiModelProperty(value="开奖时间") 
	private String lotteryOpenTime;//开奖时间
	@ApiModelProperty(required=true,value="开奖内容") 
	private String lotteryOpenContent;//页面显示内容
	@ApiModelProperty(required=true,value="显示内容") 
	private String lotteryShowContent;//页面显示内容
	@ApiModelProperty(required=true,value="走势图数据，每个“|”隔开") 
	private String chartData;//页面显示内容
	
	public String getChartData() {
		return chartData;
	}
	public void setChartData(String chartData) {
		this.chartData = chartData;
	}
	public Integer getLotteryType() {
		return lotteryType;
	}
	public void setLotteryType(Integer lotteryType) {
		this.lotteryType = lotteryType;
	}
	public String getLotteryName() {
		return lotteryName;
	}
	public void setLotteryName(String lotteryName) {
		this.lotteryName = lotteryName;
	}
	public String getLotteryPeriods() {
		return lotteryPeriods;
	}
	public void setLotteryPeriods(String lotteryPeriods) {
		this.lotteryPeriods = lotteryPeriods;
	}
	
	public String getLotteryBeginTime() {
		return lotteryBeginTime;
	}
	public void setLotteryBeginTime(String lotteryBeginTime) {
		this.lotteryBeginTime = lotteryBeginTime;
	}
	public String getLotteryOpenTime() {
		return lotteryOpenTime;
	}
	public void setLotteryOpenTime(String lotteryOpenTime) {
		this.lotteryOpenTime = lotteryOpenTime;
	}
	public String getLotteryOpenContent() {
		return lotteryOpenContent;
	}
	public void setLotteryOpenContent(String lotteryOpenContent) {
		this.lotteryOpenContent = lotteryOpenContent;
	}
	public String getLotteryShowContent() {
		return lotteryShowContent;
	}
	public void setLotteryShowContent(String lotteryShowContent) {
		this.lotteryShowContent = lotteryShowContent;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
