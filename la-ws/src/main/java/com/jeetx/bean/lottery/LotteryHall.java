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

import com.jeetx.bean.system.Station;

@Entity 
@Table(name="tb_lottery_hall")
public class LotteryHall implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	
	private Integer id;//ID
	private String title;//大厅名称
	private String iconImg;//图标链接
	private LotteryType lotteryType; //彩票类型
	private BigDecimal minimum;//进入最低金额
	private Integer isMoreRoom = 1;//是否允许多房间(1是、0否)
	private Integer status = 1;//当前状态(1启用、0禁用)
	private String ruleRemarks;;//赔率说明
	private Integer sort;//排序号(大的在前)
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
	@Column(name="title",columnDefinition="varchar(50) COMMENT '大厅名称'")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	@Column(precision=19,scale=2,name="minimum",columnDefinition="decimal(19,2) COMMENT '进入最低金额'")
	public BigDecimal getMinimum() {
		return minimum;
	}

	public void setMinimum(BigDecimal minimum) {
		this.minimum = minimum;
	}
	@Column(name="status",columnDefinition="int(10) COMMENT '当前状态(1启用、0禁用)'")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name="rule_remarks",columnDefinition="varchar(5000) COMMENT '赔率说明'")
	public String getRuleRemarks() {
		return ruleRemarks;
	}

	public void setRuleRemarks(String ruleRemarks) {
		this.ruleRemarks = ruleRemarks;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="lottery_type_id",columnDefinition="int(10) COMMENT '彩票类型ID'")
	public LotteryType getLotteryType() {
		return lotteryType;
	}

	public void setLotteryType(LotteryType lotteryType) {
		this.lotteryType = lotteryType;
	}

	@Column(name="is_more_room",columnDefinition="int(10) COMMENT '是否允许多房间(1是、0否)'")
	public Integer getIsMoreRoom() {
		return isMoreRoom;
	}

	public void setIsMoreRoom(Integer isMoreRoom) {
		this.isMoreRoom = isMoreRoom;
	}
	@Column(name="icon_img",columnDefinition="varchar(200) COMMENT '图标链接'")
	public String getIconImg() {
		return iconImg;
	}

	public void setIconImg(String iconImg) {
		this.iconImg = iconImg;
	}
	@Column(name="sort",columnDefinition="int(10) COMMENT '排序号(大的在前)'")
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
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
