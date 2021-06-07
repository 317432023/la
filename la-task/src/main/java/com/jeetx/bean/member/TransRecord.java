package com.jeetx.bean.member;

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

@Entity 
@Table(name="tb_member_transrecord")
public class TransRecord implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;

	private Integer id;
	private User user;//用户
	private Date createTime;//交易时间
	private Integer transCategory;//交易类型（1充值、2提现、3抽奖、4投注、5撤单、6赠送、7中奖、8回水、9调账）
	private BigDecimal transAmount;//交易账户金额
	private BigDecimal endBalance;//剩余账户金额
	private BigDecimal transLotteryAmount;//交易彩金
	private BigDecimal endLotteryBalance;//剩余彩金
	private String remark;//备注
	private Integer flag;//1加,0扣


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
	@JoinColumn(name="user_id",columnDefinition="int(10) COMMENT '用户ID'")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(name="create_time",columnDefinition="datetime COMMENT '交易时间'")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name="trans_category",columnDefinition="int(10) COMMENT '交易类型（1充值、2提现、3抽奖、4投注、5撤单、6赠送、7中奖、8回水）'")
	public Integer getTransCategory() {
		return transCategory;
	}

	public void setTransCategory(Integer transCategory) {
		this.transCategory = transCategory;
	}
	@Column(precision=19,scale=2,name="trans_amount",columnDefinition="decimal(19,2) COMMENT '交易金额'")
	public BigDecimal getTransAmount() {
		return transAmount;
	}

	public void setTransAmount(BigDecimal transAmount) {
		this.transAmount = transAmount;
	}
	@Column(name="remark",columnDefinition="varchar(50) COMMENT '摘要信息'")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Column(name="flag",columnDefinition="int(10) COMMENT '标示(1加,0扣)'")
	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	@Column(precision=19,scale=2,name="end_balance",columnDefinition="decimal(19,2) COMMENT '剩余金额'")
	public BigDecimal getEndBalance() {
		return endBalance;
	}

	public void setEndBalance(BigDecimal endBalance) {
		this.endBalance = endBalance;
	}

	@Column(precision=19,scale=2,name="trans_lottery_amount",columnDefinition="decimal(19,2) COMMENT '交易彩金'")
	public BigDecimal getTransLotteryAmount() {
		return transLotteryAmount;
	}

	public void setTransLotteryAmount(BigDecimal transLotteryAmount) {
		this.transLotteryAmount = transLotteryAmount;
	}

	@Column(precision=19,scale=2,name="end_lottery_balance",columnDefinition="decimal(19,2) COMMENT '剩余彩金'")
	public BigDecimal getEndLotteryBalance() {
		return endLotteryBalance;
	}

	public void setEndLotteryBalance(BigDecimal endLotteryBalance) {
		this.endLotteryBalance = endLotteryBalance;
	}
	
}
