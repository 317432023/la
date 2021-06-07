package com.jeetx.bean.lottery;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.jeetx.bean.member.User;


@Entity 
@Table(name="tb_lottery_daily_order_total")
public class LotteryDailyOrderTotal implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	
	private Integer id;
	private Integer version;
	private User user;//玩家帐号
	private Date totalDate = new Date();//统计日期
	private BigDecimal betMoney; //流水金额
	private BigDecimal profitMoney; //盈亏金额
	private BigDecimal backWaterMoney; //回水金额
	private BigDecimal rechargeMoney; //充值金额
	private BigDecimal withdrawMoney; //提现金额
	private BigDecimal winMoney; //中奖金额
	private Integer expandUserNum; //拓展玩家数
	private BigDecimal lotteryBalance;
	private BigDecimal balance;
	
	@Id
	@Column(name="ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Version
	@Column(name="VERSION",columnDefinition="int(10) COMMENT '乐观锁'")
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="user_id",columnDefinition="int(10) COMMENT '玩家ID'")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	@Column(name="total_date",columnDefinition="datetime COMMENT '统计时间'")
	public Date getTotalDate() {
		return totalDate;
	}

	public void setTotalDate(Date totalDate) {
		this.totalDate = totalDate;
	}
	@Column(precision=19,scale=2,name="bet_money",columnDefinition="decimal(19,2) COMMENT '下注金额'")
	public BigDecimal getBetMoney() {
		return betMoney;
	}

	public void setBetMoney(BigDecimal betMoney) {
		this.betMoney = betMoney;
	}
	@Column(precision=19,scale=2,name="profit_money",columnDefinition="decimal(19,2) COMMENT '盈亏金额'")
	public BigDecimal getProfitMoney() {
		return profitMoney;
	}

	public void setProfitMoney(BigDecimal profitMoney) {
		this.profitMoney = profitMoney;
	}
	@Column(precision=19,scale=2,name="water_money",columnDefinition="decimal(19,2) COMMENT '回水金额'")
	public BigDecimal getBackWaterMoney() {
		return backWaterMoney;
	}

	public void setBackWaterMoney(BigDecimal backWaterMoney) {
		this.backWaterMoney = backWaterMoney;
	}
	@Column(precision=19,scale=2,name="recharge_money",columnDefinition="decimal(19,2) COMMENT '充值金额'")
	public BigDecimal getRechargeMoney() {
		return rechargeMoney;
	}

	public void setRechargeMoney(BigDecimal rechargeMoney) {
		this.rechargeMoney = rechargeMoney;
	}
	@Column(precision=19,scale=2,name="withdraw_money",columnDefinition="decimal(19,2) COMMENT '提现金额'")
	public BigDecimal getWithdrawMoney() {
		return withdrawMoney;
	}

	public void setWithdrawMoney(BigDecimal withdrawMoney) {
		this.withdrawMoney = withdrawMoney;
	}
	@Column(precision=19,scale=2,name="win_money",columnDefinition="decimal(19,2) COMMENT '中奖金额'")
	public BigDecimal getWinMoney() {
		return winMoney;
	}

	public void setWinMoney(BigDecimal winMoney) {
		this.winMoney = winMoney;
	}
	@Column(name="expand_user_num",columnDefinition="int(10) COMMENT '拓展玩家数'")
	public Integer getExpandUserNum() {
		return expandUserNum;
	}

	public void setExpandUserNum(Integer expandUserNum) {
		this.expandUserNum = expandUserNum;
	}
	@Column(precision=19,scale=2,name="balance",columnDefinition="decimal(19,2) COMMENT '余额'")
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	@Column(precision=19,scale=2,name="lottery_balance",columnDefinition="decimal(19,2) COMMENT '彩金'")
	public BigDecimal getLotteryBalance() {
		return lotteryBalance;
	}
	public void setLotteryBalance(BigDecimal lotteryBalance) {
		this.lotteryBalance = lotteryBalance;
	}
}
