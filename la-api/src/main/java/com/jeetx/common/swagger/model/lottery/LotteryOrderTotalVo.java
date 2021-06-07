package com.jeetx.common.swagger.model.lottery;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;


public class LotteryOrderTotalVo implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	@ApiModelProperty(required=true,value="用户ID") 
	private Integer userId;
	@ApiModelProperty(required=true,value="玩家帐号") 
	private String username;
	@ApiModelProperty(required=true,value="游戏昵称") 
	private String nickName;
	@ApiModelProperty(required=true,value="消费汇总") 
	private String betMoney;
	@ApiModelProperty(required=true,value="输赢汇总") 
	private String profitMoney;
	@ApiModelProperty(required=true,value="回水汇总") 
	private String backWaterMoney;
	@ApiModelProperty(required=true,value="充值汇总") 
	private String rechargeMoney;
	@ApiModelProperty(required=true,value="提现汇总") 
	private String withdrawMoney;
	@ApiModelProperty(required=true,value="中奖汇总") 
	private String winMoney;
	@ApiModelProperty(required=true,value="拓展玩家") 
	private Integer expandUserNum;
	@ApiModelProperty(required=false,value="统计日期") 
	private String totalDate;
	@ApiModelProperty(required=false,value="余额汇总") 
	private String balance;
	@ApiModelProperty(required=false,value="彩金汇总") 
	private String lotteryBalance;
	
	public Integer getUserId() {
		return userId;
	}
	public String getTotalDate() {
		return totalDate;
	}
	public void setTotalDate(String totalDate) {
		this.totalDate = totalDate;
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
	public String getBetMoney() {
		return betMoney;
	}
	public void setBetMoney(String betMoney) {
		this.betMoney = betMoney;
	}
	public String getProfitMoney() {
		return profitMoney;
	}
	public void setProfitMoney(String profitMoney) {
		this.profitMoney = profitMoney;
	}
	public String getBackWaterMoney() {
		return backWaterMoney;
	}
	public void setBackWaterMoney(String backWaterMoney) {
		this.backWaterMoney = backWaterMoney;
	}
	public String getRechargeMoney() {
		return rechargeMoney;
	}
	public void setRechargeMoney(String rechargeMoney) {
		this.rechargeMoney = rechargeMoney;
	}
	public String getWithdrawMoney() {
		return withdrawMoney;
	}
	public void setWithdrawMoney(String withdrawMoney) {
		this.withdrawMoney = withdrawMoney;
	}
	public String getWinMoney() {
		return winMoney;
	}
	public void setWinMoney(String winMoney) {
		this.winMoney = winMoney;
	}
	public Integer getExpandUserNum() {
		return expandUserNum;
	}
	public void setExpandUserNum(Integer expandUserNum) {
		this.expandUserNum = expandUserNum;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getLotteryBalance() {
		return lotteryBalance;
	}
	public void setLotteryBalance(String lotteryBalance) {
		this.lotteryBalance = lotteryBalance;
	}
	
}
