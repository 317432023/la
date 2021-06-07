package com.jeetx.common.swagger.model.lottery;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;


public class LotteryOrderItemVo implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	@ApiModelProperty(required=true,value="ID") 
	private Integer id;
	@ApiModelProperty(required=true,value="下注金额") 
	private String betMoney;
	@ApiModelProperty(value="中奖金额（含本）") 
	private String winMoney;
	@ApiModelProperty(required=true,value="玩法名称") 
	private String ruleName;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getBetMoney() {
		return betMoney;
	}
	public void setBetMoney(String betMoney) {
		this.betMoney = betMoney;
	}
	public String getWinMoney() {
		return winMoney;
	}
	public void setWinMoney(String winMoney) {
		this.winMoney = winMoney;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
}
