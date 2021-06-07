package com.jeetx.common.swagger.model.lottery;

import java.io.Serializable;

public class LotteryOrderItemDTO implements Serializable {
	private static final long serialVersionUID = -6661460923010241475L;
	
	private Integer ruleId;
	private String betContent;
	private String betMoney;
	public Integer getRuleId() {
		return ruleId;
	}
	public void setRuleId(Integer ruleId) {
		this.ruleId = ruleId;
	}
	public String getBetContent() {
		return betContent;
	}
	public void setBetContent(String betContent) {
		this.betContent = betContent;
	}
	public String getBetMoney() {
		return betMoney;
	}
	public void setBetMoney(String betMoney) {
		this.betMoney = betMoney;
	}
}
