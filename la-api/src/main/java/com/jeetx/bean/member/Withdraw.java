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
/**
 * 提现表
 * @author Administrator
 *
 */
@Entity 
@Table(name="tb_member_withdraw")
public class Withdraw  implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	
	private Integer id;
	private Integer version;
	private User user; //用户
	private String tradeCode;//流水号
	private BigDecimal applyAmount; //申请金额
	private Date createTime; //申请时间
	private String applyRemark; //申请摘要信息
	private Integer status; //状态（1申请中、2提现成功、3打款失败）
	private String bankCardInfo; //提现账号信息
	private Date confirmTime; //处理时间
	private String confirmRemark; //处理摘要信息
	
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
	@Column(precision=19,scale=2,name="apply_amount",columnDefinition="decimal(19,2) COMMENT '申请提现金额'")
	public BigDecimal getApplyAmount() {
		return applyAmount;
	}

	public void setApplyAmount(BigDecimal applyAmount) {
		this.applyAmount = applyAmount;
	}
	@Column(name="create_time",columnDefinition="datetime COMMENT '申请时间'")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name="apply_remark",columnDefinition="varchar(500) COMMENT '申请摘要信息'")
	public String getApplyRemark() {
		return applyRemark;
	}

	public void setApplyRemark(String applyRemark) {
		this.applyRemark = applyRemark;
	}
	@Column(name="status",columnDefinition="int(10) COMMENT '状态（1申请中、2提现成功、3打款失败）'")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name="bankcard_info",columnDefinition="varchar(100) COMMENT '提现账号信息'")
	public String getBankCardInfo() {
		return bankCardInfo;
	}

	public void setBankCardInfo(String bankCardInfo) {
		this.bankCardInfo = bankCardInfo;
	}

	@Column(name="confirm_time",columnDefinition="datetime COMMENT '处理时间'")
	public Date getConfirmTime() {
		return confirmTime;
	}

	public void setConfirmTime(Date confirmTime) {
		this.confirmTime = confirmTime;
	}
	@Column(name="confirm_remark",columnDefinition="varchar(500) COMMENT '处理摘要信息'")
	public String getConfirmRemark() {
		return confirmRemark;
	}

	public void setConfirmRemark(String confirmRemark) {
		this.confirmRemark = confirmRemark;
	}
	@Column(name="trade_code",columnDefinition="varchar(500) COMMENT '流水号'")
	public String getTradeCode() {
		return tradeCode;
	}

	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}
	
}
