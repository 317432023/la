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

import com.jeetx.bean.member.User;


@Entity 
@Table(name="tb_lottery_water_record")
public class LotteryWaterRecord implements Serializable {
	private static final long serialVersionUID = -7084393963287332082L;
	
	private Integer id;
	private User user;//玩家帐号
	private Date totalDate = new Date();//统计日期
	private BigDecimal backWaterMoney; //回水金额
	private LotteryHall lotteryHall;//彩票大厅
	
	@Id
	@Column(name="ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	@Column(precision=19,scale=2,name="water_money",columnDefinition="decimal(19,2) COMMENT '回水金额'")
	public BigDecimal getBackWaterMoney() {
		return backWaterMoney;
	}

	public void setBackWaterMoney(BigDecimal backWaterMoney) {
		this.backWaterMoney = backWaterMoney;
	}
	
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="lottery_hall_id",columnDefinition="int(10) COMMENT '彩票大厅ID，对应彩票大厅ID'")
	public LotteryHall getLotteryHall() {
		return lotteryHall;
	}

	public void setLotteryHall(LotteryHall lotteryHall) {
		this.lotteryHall = lotteryHall;
	}
}