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

import com.jeetx.bean.system.Station;

@Entity
@Table(name = "tb_lottery_rule")
public class LotteryRule implements Serializable {
	private static final long serialVersionUID = 6693965150652975115L;
	  
	private Integer id;//主键
	private LotteryType lotteryType; //彩票类型
	private String paramName;//玩法名称
	private String paramValues;//玩法值（赔率）
	private String remarks;//说明信息
	private Integer ruleType;//玩法类型
	private Integer status = 1;//当前状态(1启用、0禁用)
	private LotteryHall lotteryHall;//彩票大厅
	private String ruleRegular;//玩法正则
	private Station station; //站点
	
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
	@Column(name="param_name",columnDefinition="varchar(50) COMMENT '玩法名称)'")
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
	@Column(name="status",columnDefinition="int(10) COMMENT '状态(1启用、0禁用)'")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="lottery_hall_id",columnDefinition="int(10) COMMENT '彩票大厅ID，对应彩票大厅ID'")
	public LotteryHall getLotteryHall() {
		return lotteryHall;
	}

	public void setLotteryHall(LotteryHall lotteryHall) {
		this.lotteryHall = lotteryHall;
	}
	@Column(name="rule_regular",columnDefinition="varchar(5000) COMMENT '玩法正则'")
	public String getRuleRegular() {
		return ruleRegular;
	}

	public void setRuleRegular(String ruleRegular) {
		this.ruleRegular = ruleRegular;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="station_id",columnDefinition="int(10) COMMENT '站点ID'")	
	public Station getStation() {
		return station;
	}
	public void setStation(Station station) {
		this.station = station;
	}
}
