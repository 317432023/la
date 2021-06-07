package com.jeetx.bean.base;

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

import com.jeetx.bean.system.Station;

@Entity 
@Table(name="tb_base_withdraw_config")
public class WithdrawConfig  implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	
	private Integer id;
	private BigDecimal minApplyAmount; //提现最低金额
	private BigDecimal maxApplyAmount; //提现最高金额
	private String beginTime; //提现时间限制-开始
	private String endTime; //提现时间限制-结束
	private Integer applyDailyTimes; //提现限制次数
	private String reminder; //温馨提示
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
	@Column(precision=19,scale=2,name="min_apply_amount",columnDefinition="decimal(19,2) COMMENT '提现最低金额'")
	public BigDecimal getMinApplyAmount() {
		return minApplyAmount;
	}

	public void setMinApplyAmount(BigDecimal minApplyAmount) {
		this.minApplyAmount = minApplyAmount;
	}
	@Column(precision=19,scale=2,name="max_apply_amount",columnDefinition="decimal(19,2) COMMENT '提现最高金额'")
	public BigDecimal getMaxApplyAmount() {
		return maxApplyAmount;
	}

	public void setMaxApplyAmount(BigDecimal maxApplyAmount) {
		this.maxApplyAmount = maxApplyAmount;
	}

	@Column(name="apply_daily_times",columnDefinition="int(10) COMMENT '提现限制次数'")
	public Integer getApplyDailyTimes() {
		return applyDailyTimes;
	}

	public void setApplyDailyTimes(Integer applyDailyTimes) {
		this.applyDailyTimes = applyDailyTimes;
	}
	@Column(name="reminder",columnDefinition="varchar(100) COMMENT '温馨提示'")
	public String getReminder() {
		return reminder;
	}

	public void setReminder(String reminder) {
		this.reminder = reminder;
	}
	@Column(name="begin_time",columnDefinition="varchar(50) COMMENT '提现时间限制-开始'")
	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}
	@Column(name="end_time",columnDefinition="varchar(50) COMMENT '提现时间限制-结束'")
	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
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
