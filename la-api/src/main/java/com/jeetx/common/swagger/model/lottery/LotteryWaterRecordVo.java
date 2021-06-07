package com.jeetx.common.swagger.model.lottery;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;


public class LotteryWaterRecordVo implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	@ApiModelProperty(required=true,value="ID") 
	private Integer id;
	@ApiModelProperty(required=true,value="用户ID") 
	private Integer userId;
	@ApiModelProperty(required=true,value="用户名") 
	private String username;
	@ApiModelProperty(required=true,value="游戏昵称") 
	private String nickName;
	@ApiModelProperty(required=true,value="统计日期") 
	private String totalDate;
	@ApiModelProperty(required=true,value="彩票类型") 
	private Integer lotteryType; 
	@ApiModelProperty(required=true,value="彩票类型名称") 
	private String lotteryName; 
	@ApiModelProperty(required=true,value="彩票大厅ID") 
	private Integer lotteryHallId;
	@ApiModelProperty(required=true,value="彩票大厅名称") 
	private String lotteryHallTitle;
	@ApiModelProperty(required=true,value="回水金额") 
	private String backWaterMoney;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getTotalDate() {
		return totalDate;
	}
	public void setTotalDate(String totalDate) {
		this.totalDate = totalDate;
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
	public Integer getLotteryHallId() {
		return lotteryHallId;
	}
	public void setLotteryHallId(Integer lotteryHallId) {
		this.lotteryHallId = lotteryHallId;
	}
	public String getLotteryHallTitle() {
		return lotteryHallTitle;
	}
	public void setLotteryHallTitle(String lotteryHallTitle) {
		this.lotteryHallTitle = lotteryHallTitle;
	}
	public String getBackWaterMoney() {
		return backWaterMoney;
	}
	public void setBackWaterMoney(String backWaterMoney) {
		this.backWaterMoney = backWaterMoney;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
}
