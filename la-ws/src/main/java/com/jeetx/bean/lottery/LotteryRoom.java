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

import com.jeetx.bean.system.Station;

@Entity 
@Table(name="tb_lottery_room")
public class LotteryRoom implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	
	private Integer id;//id
	private String title;//房间名称
	private String iconImg;//图标链接
	private LotteryType lotteryType; //彩票类型
	private LotteryHall lotteryHall;//彩票大厅
	private Date createTime;//创建时间
	private Integer onLineCount;//在线人数
	private Integer status;//状态(1启用、0关闭、2隐藏)
	private Integer sort;//排序号
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
	@Column(name="online_count",columnDefinition="int(10) COMMENT '在线人数'")
	public Integer getOnLineCount() {
		return onLineCount;
	}

	public void setOnLineCount(Integer onLineCount) {
		this.onLineCount = onLineCount;
	}
	@Column(name="create_time",columnDefinition="datetime COMMENT '创建时间'")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name="title",columnDefinition="varchar(50) COMMENT '房间名称'")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	@Column(name="status",columnDefinition="int(10) COMMENT '状态(1启用、0关闭、2隐藏)'")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="lottery_type_id",columnDefinition="int(10) COMMENT '彩票类型ID，对应彩票类型表ID'")
	public LotteryType getLotteryType() {
		return lotteryType;
	}

	public void setLotteryType(LotteryType lotteryType) {
		this.lotteryType = lotteryType;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="lottery_hall_id",columnDefinition="int(10) COMMENT '彩票大厅ID，对应彩票大厅ID'")
	public LotteryHall getLotteryHall() {
		return lotteryHall;
	}

	public void setLotteryHall(LotteryHall lotteryHall) {
		this.lotteryHall = lotteryHall;
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
	@Column(name="icon_img",columnDefinition="varchar(200) COMMENT '图标链接'")
	public String getIconImg() {
		return iconImg;
	}

	public void setIconImg(String iconImg) {
		this.iconImg = iconImg;
	}
	
}
