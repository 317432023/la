package com.jeetx.bean.lottery;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tb_lottery_rule_plan_item")
public class LotteryRulePlanItem implements Serializable {
	private static final long serialVersionUID = 6693965150652975115L;
	  
	private Integer id;//主键
	private LotteryRulePlan lotteryRulePlan; //计划ID
	private LotteryRule lotteryRule;//赔率规则
	private String paramValues;//玩法值（赔率）
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="lottery_rule_id",columnDefinition="int(10) COMMENT '彩票赔率规则ID'")
	public LotteryRule getLotteryRule() {
		return lotteryRule;
	}

	public void setLotteryRule(LotteryRule lotteryRule) {
		this.lotteryRule = lotteryRule;
	}

	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="lottery_rule_plan_id",columnDefinition="int(10) COMMENT '赔率计划ID'")
	public LotteryRulePlan getLotteryRulePlan() {
		return lotteryRulePlan;
	}

	public void setLotteryRulePlan(LotteryRulePlan lotteryRulePlan) {
		this.lotteryRulePlan = lotteryRulePlan;
	}
	@Column(name="param_values",columnDefinition="varchar(5000) COMMENT '玩法值（赔率）'")
	public String getParamValues() {
		return paramValues;
	}

	public void setParamValues(String paramValues) {
		this.paramValues = paramValues;
	}
}
