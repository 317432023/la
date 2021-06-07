package com.jeetx.common.swagger.model.lottery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class LotteryRuleTypeVo implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	@ApiModelProperty(required=true,value="玩法类型") 
	private Integer ruleType;
	@ApiModelProperty(required=true,value="类型名称") 
	private String ruleTypeName;
	@ApiModelProperty(required=true,value="玩法集合") 
	private List<LotteryRuleVo> rules = new ArrayList<LotteryRuleVo>();
	public Integer getRuleType() {
		return ruleType;
	}
	public void setRuleType(Integer ruleType) {
		this.ruleType = ruleType;
	}
	public String getRuleTypeName() {
		return ruleTypeName;
	}
	public void setRuleTypeName(String ruleTypeName) {
		this.ruleTypeName = ruleTypeName;
	}
	public List<LotteryRuleVo> getRules() {
		return rules;
	}
	public void setRules(List<LotteryRuleVo> rules) {
		this.rules = rules;
	}
	
	
}
