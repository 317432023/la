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
@Table(name = "tb_lottery_rule_default")
public class LotteryRuleDefault implements Serializable {
	private static final long serialVersionUID = 6693965150652975115L;
	  
	private Integer id;//主键
	private LotteryType lotteryType; //彩票类型
	private String paramName;//玩法名称
	private String paramValues;//玩法值（赔率）
	private String remarks;//说明信息
	private Integer ruleType;//玩法类型
	private String ruleRegular;//玩法正则
	
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
	@JoinColumn(name="lottery_type_id",columnDefinition="int(10) COMMENT '彩票类型ID'")
	public LotteryType getLotteryType() {
		return lotteryType;
	}

	public void setLotteryType(LotteryType lotteryType) {
		this.lotteryType = lotteryType;
	}
	@Column(name="param_name",columnDefinition="varchar(50) COMMENT '玩法名称 '")
	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	@Column(name="param_values",columnDefinition="varchar(5000) COMMENT '玩法值（赔率）'")
	public String getParamValues() {
		return paramValues;
	}

	public void setParamValues(String paramValues) {
		this.paramValues = paramValues;
	}
	@Column(name="remarks",columnDefinition="varchar(5000) COMMENT '说明信息'")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	@Column(name="rule_type",columnDefinition="int(10) COMMENT '玩法类型'")
	public Integer getRuleType() {
		return ruleType;
	}

	public void setRuleType(Integer ruleType) {
		this.ruleType = ruleType;
	}
	@Column(name="rule_regular",columnDefinition="varchar(5000) COMMENT '玩法正则'")
	public String getRuleRegular() {
		return ruleRegular;
	}

	public void setRuleRegular(String ruleRegular) {
		this.ruleRegular = ruleRegular;
	}
}
