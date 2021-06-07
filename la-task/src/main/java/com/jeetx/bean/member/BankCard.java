package com.jeetx.bean.member;

import java.io.Serializable;
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
@Table(name="tb_member_bankcard")
public class BankCard implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	
	private Integer id;
	private User user; //用户
	private String cardholder; //持卡人姓名
	private String bankCard;//银行卡号
	private String bankName; // 银行名称
	private String openBankPlace; // 开户地点
	private String openBankBranch;//开户支行

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

	@Column(name="cardholder",columnDefinition="varchar(500) COMMENT '持卡人姓名'")
	public String getCardholder() {
		return cardholder;
	}

	public void setCardholder(String cardholder) {
		this.cardholder = cardholder;
	}

	@Column(name="bank_card",columnDefinition="varchar(500) COMMENT '银行卡号'")
	public String getBankCard() {
		return bankCard;
	}

	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}

	@Column(name="bank_name",columnDefinition="varchar(500) COMMENT '银行名称'")
	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	@Column(name="open_bank_place",columnDefinition="varchar(500) COMMENT '开户地点'")
	public String getOpenBankPlace() {
		return openBankPlace;
	}

	public void setOpenBankPlace(String openBankPlace) {
		this.openBankPlace = openBankPlace;
	}

	@Column(name="open_bank_branch",columnDefinition="varchar(500) COMMENT '开户支行'")
	public String getOpenBankBranch() {
		return openBankBranch;
	}

	public void setOpenBankBranch(String openBankBranch) {
		this.openBankBranch = openBankBranch;
	}
}
