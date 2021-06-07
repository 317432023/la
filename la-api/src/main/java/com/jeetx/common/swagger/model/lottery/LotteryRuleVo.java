package com.jeetx.common.swagger.model.lottery;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;


public class LotteryRuleVo implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	@ApiModelProperty(required=true,value="玩法ID") 
	private Integer ruleId;
	@ApiModelProperty(required=true,value="玩法名称") 
	private String ruleName;
	@ApiModelProperty(required=true,value="玩法赔率") 
	private String ruleOdds;
	@ApiModelProperty(required=true,value="玩法正则") 
	private String ruleRegular;
	@ApiModelProperty(required=true,value="玩法说明") 
	private String remarks;
	@ApiModelProperty(required=true,value="玩法类型") 
	private Integer ruleType;
	@ApiModelProperty(required=true,value="当前状态(1启用、0禁用)") 
	private Integer status;

	public Integer getRuleId() {
		return ruleId;
	}
	public void setRuleId(Integer ruleId) {
		this.ruleId = ruleId;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public String getRuleOdds() {
		return ruleOdds;
	}
	public void setRuleOdds(String ruleOdds) {
		this.ruleOdds = ruleOdds;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Integer getRuleType() {
		return ruleType;
	}
	public void setRuleType(Integer ruleType) {
		this.ruleType = ruleType;
	}
	public String getRuleRegular() {
		return ruleRegular;
	}
	public void setRuleRegular(String ruleRegular) {
		this.ruleRegular = ruleRegular;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
