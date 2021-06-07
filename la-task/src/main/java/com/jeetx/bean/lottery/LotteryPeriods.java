package com.jeetx.bean.lottery;

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
import javax.persistence.Version;


@Entity 
@Table(name="tb_lottery_periods")
public class LotteryPeriods implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	
	private Integer id;//主键
	private String lotteryPeriods;//彩票期数
	private LotteryType lotteryType; //彩票类型
	private Integer status;//期数状态(1投注中 、2已封盘中、3已开奖 )
	private Date lotteryBeginTime;//开始时间
	private Date lotteryOpenTime;//开奖时间
	private Date lotteryCollectTime;//采集时间
	private Date lotterySourceCollectTime;//数据源上的开奖时间
	private String lotteryDataSource;//数据来源
	private String lotteryOpenContent;//开奖号码(每个数字“+”隔开)
	private Integer lotteryOpenNumber;//开奖号码和值
	private String lotteryShowContent;//页面显示内容
	private Integer version;//乐观锁

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
	@Column(name="version",columnDefinition="int(10) COMMENT '乐观锁'")
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="lottery_type_id",columnDefinition="int(10) COMMENT '彩票类型ID'")
	public LotteryType getLotteryType() {
		return lotteryType;
	}

	public void setLotteryType(LotteryType lotteryType) {
		this.lotteryType = lotteryType;
	}
	@Column(name="lottery_periods",columnDefinition="varchar(50) COMMENT '彩票期数'")
	public String getLotteryPeriods() {
		return lotteryPeriods;
	}

	public void setLotteryPeriods(String lotteryPeriods) {
		this.lotteryPeriods = lotteryPeriods;
	}
	@Column(name="lottery_begin_time",columnDefinition="datetime COMMENT '开始时间'")
	public Date getLotteryBeginTime() {
		return lotteryBeginTime;
	}

	public void setLotteryBeginTime(Date lotteryBeginTime) {
		this.lotteryBeginTime = lotteryBeginTime;
	}
	@Column(name="lottery_open_time",columnDefinition="datetime COMMENT '开奖时间'")
	public Date getLotteryOpenTime() {
		return lotteryOpenTime;
	}

	public void setLotteryOpenTime(Date lotteryOpenTime) {
		this.lotteryOpenTime = lotteryOpenTime;
	}
	@Column(name="lottery_open_content",columnDefinition="varchar(50) COMMENT '开奖号码(每个数字“+”隔开)'")
	public String getLotteryOpenContent() {
		return lotteryOpenContent;
	}

	public void setLotteryOpenContent(String lotteryOpenContent) {
		this.lotteryOpenContent = lotteryOpenContent;
	}
	@Column(name="lottery_status",columnDefinition="int(10) COMMENT '期数状态(1投注中 、2已封盘中、3已开奖 )'")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name="lottery_data_source",columnDefinition="varchar(50) COMMENT '数据源'")
	public String getLotteryDataSource() {
		return lotteryDataSource;
	}

	public void setLotteryDataSource(String lotteryDataSource) {
		this.lotteryDataSource = lotteryDataSource;
	}
	@Column(name="lottery_collect_time",columnDefinition="datetime COMMENT '采集时间'")
	public Date getLotteryCollectTime() {
		return lotteryCollectTime;
	}

	public void setLotteryCollectTime(Date lotteryCollectTime) {
		this.lotteryCollectTime = lotteryCollectTime;
	}
	@Column(name="lottery_source_collect_time",columnDefinition="datetime COMMENT '数据源上的开奖时间'")
	public Date getLotterySourceCollectTime() {
		return lotterySourceCollectTime;
	}

	public void setLotterySourceCollectTime(Date lotterySourceCollectTime) {
		this.lotterySourceCollectTime = lotterySourceCollectTime;
	}
	@Column(name="lottery_show_content",columnDefinition="varchar(50) COMMENT '页面显示内容'")
	public String getLotteryShowContent() {
		return lotteryShowContent;
	}

	public void setLotteryShowContent(String lotteryShowContent) {
		this.lotteryShowContent = lotteryShowContent;
	}
	@Column(name="lottery_open_number",columnDefinition="varchar(50) COMMENT '开奖号码和值'")
	public Integer getLotteryOpenNumber() {
		return lotteryOpenNumber;
	}

	public void setLotteryOpenNumber(Integer lotteryOpenNumber) {
		this.lotteryOpenNumber = lotteryOpenNumber;
	}
}
