package com.jeetx.bean.lottery;

import java.io.Serializable;

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
@Table(name = "tb_lottery_rule_plan")
public class LotteryRulePlan implements Serializable {
	private static final long serialVersionUID = 6693965150652975115L;
	  
	private Integer id;//主键
	private String title;//计划名称
	private Station station; //站点
	private LotteryHall lotteryHall;//彩票大厅
	private Integer status = 1;//当前状态(1启用、0禁用)
	private Integer sortNum;//优先级(数字小在前)
	private String beginTime; ///每天计划开始时间，格式如21:00:00
	private String endTime; //每天计划结束时间，格式如21:00:00

	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name="title",columnDefinition="varchar(50) COMMENT '计划名称'")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="station_id",columnDefinition="int(10) COMMENT '站点ID'")	
	public Station getStation() {
		return station;
	}
	public void setStation(Station station) {
		this.station = station;
	}

	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="lottery_hall_id",columnDefinition="int(10) COMMENT '彩票大厅ID，对应彩票大厅ID'")	
	public LotteryHall getLotteryHall() {
		return lotteryHall;
	}

	public void setLotteryHall(LotteryHall lotteryHall) {
		this.lotteryHall = lotteryHall;
	}

	@Column(name="status",columnDefinition="int(10) COMMENT '状态(1启用、0禁用)'")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name="sort",columnDefinition="int(10) COMMENT '优先级(数字小在前)'")
	public Integer getSortNum() {
		return sortNum;
	}

	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}

	@Column(name="begin_time",columnDefinition="varchar(50) COMMENT '每天计划开始时间，格式如21:00:00'")
	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	@Column(name="end_time",columnDefinition="varchar(50) COMMENT '每天计划结束时间，格式如21:00:00'")
	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
