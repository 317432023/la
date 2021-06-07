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
@Table(name = "tb_lottery_activity_duration")
public class LotteryActivityDuration implements Serializable {
	private static final long serialVersionUID = 6693965150652975115L;
	  
	private Integer id;//主键
	private Integer minAmount; //最小金额
	private Integer maxAmount;//最大金额
	private BigDecimal ratio;//价格比例
	private LotteryActivity lotteryActivity;
	private Integer giftType;//赠送方式（1定额、0比例）
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="min_amount",columnDefinition="int(10) COMMENT '最小金额'")
	public Integer getMinAmount() {
		return minAmount;
	}
	public void setMinAmount(Integer minAmount) {
		this.minAmount = minAmount;
	}
	
	@Column(name="max_amount",columnDefinition="int(10) COMMENT '最大金额'")
	public Integer getMaxAmount() {
		return maxAmount;
	}
	public void setMaxAmount(Integer maxAmount) {
		this.maxAmount = maxAmount;
	}
	@Column(precision=19,scale=2,name="ratio",columnDefinition="decimal(19,2) COMMENT '价格比例'")
	public BigDecimal getRatio() {
		return ratio;
	}
	public void setRatio(BigDecimal ratio) {
		this.ratio = ratio;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="activity",columnDefinition="int(10) COMMENT '活动ID'")
	public LotteryActivity getLotteryActivity() {
		return lotteryActivity;
	}
	public void setLotteryActivity(LotteryActivity lotteryActivity) {
		this.lotteryActivity = lotteryActivity;
	}
	@Column(name="gift_type",columnDefinition="int(10) COMMENT '赠送方式（1定额、2比例）'")
	public Integer getGiftType() {
		return giftType;
	}

	public void setGiftType(Integer giftType) {
		this.giftType = giftType;
	}
}
