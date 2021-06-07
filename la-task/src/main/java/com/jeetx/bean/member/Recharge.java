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
import javax.persistence.Version;

@Entity
@Table(name = "tb_member_recharge")
public class Recharge implements Serializable {
	private static final long serialVersionUID = 6693965150652975115L;
	
	private Integer id;
	private Integer version;
	private User user;//用户ID
	private String tradeCode;//流水号
	private BigDecimal rechargeAmount;//充值金额
	private BigDecimal lotteryAmount;//赠送彩金
	private Date createTime;//创建时间
	private Date tradeTime;//交易时间（到账时间）
	private Integer payType;//支付方式（1支付宝、2微信、3网银转账、4后台手动充值、5扫码充值、6明捷支付、7后台充值彩金、8佰富支付）
	private Integer status;//状态（1：初始化 2：交易成功 3：交易失败 4：异常待核实）
	private String remark; //摘要信息

	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Version
	@Column(name="version")
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="user_id",columnDefinition="int(10) COMMENT '用户ID'")
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	@Column(name="trade_code",columnDefinition="varchar(500) COMMENT '流水号'")
	public String getTradeCode() {
		return tradeCode;
	}
	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}
	
	@Column(precision=19,scale=2,name="recharge_amount",columnDefinition="decimal(19,2) COMMENT '充值金额'")
	public BigDecimal getRechargeAmount() {
		return rechargeAmount;
	}
	public void setRechargeAmount(BigDecimal rechargeAmount) {
		this.rechargeAmount = rechargeAmount;
	}
	
	@Column(name="create_time",columnDefinition="datetime COMMENT '创建时间'")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Column(name="trade_time",columnDefinition="datetime COMMENT '交易时间（到账时间）'")
	public Date getTradeTime() {
		return tradeTime;
	}
	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
	}
	
	@Column(name="pay_type",columnDefinition="int(10) COMMENT '支付方式（1支付宝、2微信、3银行卡转账、4扫描充值、5网银充值）'")
	public Integer getPayType() {
		return payType;
	}
	public void setPayType(Integer payType) {
		this.payType = payType;
	}
	
	@Column(name="status",columnDefinition="int(10) COMMENT '状态（1：初始化 2：交易成功 3：交易失败 4：异常待核实）'")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name="remark",columnDefinition="varchar(500) COMMENT '摘要信息'")
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	@Column(precision=19,scale=2,name="lottery_amount",columnDefinition="decimal(19,2) COMMENT '赠送彩金'")
	public BigDecimal getLotteryAmount() {
		return lotteryAmount;
	}
	public void setLotteryAmount(BigDecimal lotteryAmount) {
		this.lotteryAmount = lotteryAmount;
	}
	
}
