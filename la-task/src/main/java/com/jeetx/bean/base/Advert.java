package com.jeetx.bean.base;

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
@Table(name="tb_base_advert")
public class Advert implements Serializable{
	private static final long serialVersionUID = 1L;
	//主键
	private Integer id;
	//轮播图标题
	private String adTitle;
	//轮播图地址
	private String adImg;
	//轮播图跳转地址
	private String adHttp;
	//创建时间
	private Date createTime;
	//排序号(大的在前)
	private Integer sortNum;
	//当前状态(1启用、0禁用)
	private Integer status = 1;
	//类型（1PC、2app）
	private Integer advertType;
	//站点
	private Station station; 
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name="ad_title",columnDefinition="varchar(50) COMMENT '轮播图标题'")
	public String getAdTitle() {
		return adTitle;
	}
	public void setAdTitle(String adTitle) {
		this.adTitle = adTitle;
	}
	@Column(name="ad_img",columnDefinition="varchar(1000) COMMENT '轮播图地址'")
	public String getAdImg() {
		return adImg;
	}
	public void setAdImg(String adImg) {
		this.adImg = adImg;
	}
	@Column(name="ad_http",columnDefinition="varchar(1000) COMMENT '轮播图跳转地址'")
	public String getAdHttp() {
		return adHttp;
	}
	public void setAdHttp(String adHttp) {
		this.adHttp = adHttp;
	}
	@Column(name="create_time",columnDefinition="datetime COMMENT '创建时间'")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name="sort",columnDefinition="int(10) COMMENT '排序号(大的在前)'")
	public Integer getSortNum() {
		return sortNum;
	}
	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}
	@Column(name="status",columnDefinition="int(10) COMMENT '状态(1启用、0禁用)'")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name="advert_type",columnDefinition="int(10) COMMENT '类型（1PC、2app）'")
	public Integer getAdvertType() {
		return advertType;
	}
	public void setAdvertType(Integer advertType) {
		this.advertType = advertType;
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
