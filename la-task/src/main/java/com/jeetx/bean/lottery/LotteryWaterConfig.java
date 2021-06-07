package com.jeetx.bean.lottery;

import java.io.Serializable;
import java.math.BigDecimal;

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
@Table(name="tb_lottery_water_config")
public class LotteryWaterConfig implements Serializable {
	private static final long serialVersionUID = -7084393963287332082L;
	
	private Integer id;
	private BigDecimal upMoney; //区间上限
	private BigDecimal lowMoney; //区间下限
	private BigDecimal backWaterRatio;//比例
	private LotteryHall lotteryHall;//彩票大厅
	private Integer orderCount;//投注次数
	private Integer combinationCount;//组合次数
	private BigDecimal combinationRatio;//组合占总投注金额比例（组合比）
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(precision=19,scale=2,name="up_money",columnDefinition="decimal(19,2) COMMENT '区间上限'")
	public BigDecimal getUpMoney() {
		return upMoney;
	}
	public void setUpMoney(BigDecimal upMoney) {
		this.upMoney = upMoney;
	}
	@Column(precision=19,scale=2,name="low_money",columnDefinition="decimal(19,2) COMMENT '区间下限'")
	public BigDecimal getLowMoney() {
		return lowMoney;
	}
	public void setLowMoney(BigDecimal lowMoney) {
		this.lowMoney = lowMoney;
	}
	@Column(precision=19,scale=2,name="backwater_ratio",columnDefinition="decimal(19,2) COMMENT '比例'")
	public BigDecimal getBackWaterRatio() {
		return backWaterRatio;
	}
	public void setBackWaterRatio(BigDecimal backWaterRatio) {
		this.backWaterRatio = backWaterRatio;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="lottery_hall_id",columnDefinition="int(10) COMMENT '彩票大厅ID，对应彩票大厅ID'")
	public LotteryHall getLotteryHall() {
		return lotteryHall;
	}

	public void setLotteryHall(LotteryHall lotteryHall) {
		this.lotteryHall = lotteryHall;
	}
	@Column(name="order_count",columnDefinition="int(10) COMMENT '投注次数'")
	public Integer getOrderCount() {
		return orderCount;
	}
	public void setOrderCount(Integer orderCount) {
		this.orderCount = orderCount;
	}
	@Column(name="combination_count",columnDefinition="int(10) COMMENT '组合次数'")
	public Integer getCombinationCount() {
		return combinationCount;
	}
	public void setCombinationCount(Integer combinationCount) {
		this.combinationCount = combinationCount;
	}
	@Column(precision=19,scale=2,name="combination_ratio",columnDefinition="decimal(19,2) COMMENT '组合占总投注金额比例（组合比）'")
	public BigDecimal getCombinationRatio() {
		return combinationRatio;
	}
	public void setCombinationRatio(BigDecimal combinationRatio) {
		this.combinationRatio = combinationRatio;
	}
	
	
}