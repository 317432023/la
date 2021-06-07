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


@Entity 
@Table(name="tb_lottery_number_config")
public class LotteryNumberConfig implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	
	private Integer id;//主键
	private Integer code;//特码
	private LotteryType lotteryType; //彩票类型
	private String greaterName; //特码对应的单类型(大、小)
	private String singleName; //特码对应的单类型(单、双)
	private String bandName; //特码对应的波段类型（红、绿、蓝）
	private String groupName; //特码对应的组合类型（小单、小双、大单、大双）
	private String primeName; //特码对应的质数合数
	private String extremalName; //特码对应的极值类型（极大、极小）
	private String groupCollection;//特殊组合名称 如:小:双:小双:极小:04:顺子
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name="code",columnDefinition="int(10) COMMENT '特码'")
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
	
	@Column(name="single_name",columnDefinition="varchar(50) COMMENT '特码对应的单类型(单、双)'")
	public String getSingleName() {
		return singleName;
	}

	public void setSingleName(String singleName) {
		this.singleName = singleName;
	}

	@Column(name="band_name",columnDefinition="varchar(50) COMMENT '特码对应的波段类型（红、绿、蓝）'")
	public String getBandName() {
		return bandName;
	}

	public void setBandName(String bandName) {
		this.bandName = bandName;
	}

	@Column(name="group_name",columnDefinition="varchar(50) COMMENT '特码对应的组合类型（小单、小双、大单、大双）'")
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Column(name="prime_name",columnDefinition="varchar(50) COMMENT '特码对应的质数合数'")
	public String getPrimeName() {
		return primeName;
	}

	public void setPrimeName(String primeName) {
		this.primeName = primeName;
	}

	@Column(name="extremal_name",columnDefinition="varchar(50) COMMENT '特码对应的极值类型（极大、极小）'")
	public String getExtremalName() {
		return extremalName;
	}

	public void setExtremalName(String extremalName) {
		this.extremalName = extremalName;
	}

	@Column(name="group_collection",columnDefinition="varchar(50) COMMENT '特殊组合名称 如:小:双:小双:极小:04:顺子'")
	public String getGroupCollection() {
		return groupCollection;
	}

	public void setGroupCollection(String groupCollection) {
		this.groupCollection = groupCollection;
	}
	@Column(name="greater_name",columnDefinition="varchar(50) COMMENT '特码对应的单类型(大、小)'")
	public String getGreaterName() {
		return greaterName;
	}

	public void setGreaterName(String greaterName) {
		this.greaterName = greaterName;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="lottery_type_id",columnDefinition="int(10) COMMENT '彩票类型ID'")
	public LotteryType getLotteryType() {
		return lotteryType;
	}

	public void setLotteryType(LotteryType lotteryType) {
		this.lotteryType = lotteryType;
	}
	
}
