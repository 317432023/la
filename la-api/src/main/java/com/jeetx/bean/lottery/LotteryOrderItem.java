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
@Table(name="tb_lottery_order_item")
public class LotteryOrderItem implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	
	private Integer id;
	private LotteryOrder lotteryOrder;//订单
	private BigDecimal betMoney; //下注金额
	private BigDecimal winMoney; //中奖金额（含本）
	private String ruleName;//玩法名称
	private LotteryRule lotteryRule;//赔率规则
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(precision=19,scale=2,name="win_money",columnDefinition="decimal(19,2) COMMENT '中奖金额'")
	public BigDecimal getWinMoney() {
		return winMoney;
	}

	public void setWinMoney(BigDecimal winMoney) {
		this.winMoney = winMoney;
	}

	@Column(precision=19,scale=2,name="bet_money",columnDefinition="decimal(19,2) COMMENT '下注金额'")
	public BigDecimal getBetMoney() {
		return betMoney;
	}

	public void setBetMoney(BigDecimal betMoney) {
		this.betMoney = betMoney;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="lottery_order_id",columnDefinition="int(10) COMMENT '彩票订单ID'")
	public LotteryOrder getLotteryOrder() {
		return lotteryOrder;
	}

	public void setLotteryOrder(LotteryOrder lotteryOrder) {
		this.lotteryOrder = lotteryOrder;
	}
	@Column(name="rule_name",columnDefinition="varchar(50) COMMENT '玩法'")
	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="lottery_rule_id",columnDefinition="int(10) COMMENT '彩票赔率规则ID'")
	public LotteryRule getLotteryRule() {
		return lotteryRule;
	}

	public void setLotteryRule(LotteryRule lotteryRule) {
		this.lotteryRule = lotteryRule;
	}
}
