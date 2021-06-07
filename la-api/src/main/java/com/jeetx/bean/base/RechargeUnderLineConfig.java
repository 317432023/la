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
@Table(name="tb_base_recharge_underline_config")
public class RechargeUnderLineConfig  implements Serializable {

private static final long serialVersionUID = -6661460923010241475L;
	
	private Integer id;
	private String title; //标题
	private String iconImg;//图标链接
	private Integer sortNum;//排序号（大的前）
	private String qrCodeLink; //二维码
	private BigDecimal minAmount; //最低金额
	private BigDecimal maxAmount; //最高金额
	private Integer isShowPC; //电脑显示（1是、0否）
	private Integer isShowApp; //手机显示（1是、0否）
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
	@Column(name="reminder",columnDefinition="varchar(100) COMMENT '温馨提示'")
	public String getReminder() {
		return reminder;
	}

	public void setReminder(String reminder) {
		this.reminder = reminder;
	}
	
	@Column(name="title",columnDefinition="varchar(50) COMMENT '标题'")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	@Column(name="icon_img",columnDefinition="varchar(200) COMMENT '图标链接'")
	public String getIconImg() {
		return iconImg;
	}

	public void setIconImg(String iconImg) {
		this.iconImg = iconImg;
	}
	@Column(name="sort",columnDefinition="int(10) COMMENT 'pc排序号(大的在前)'")
	public Integer getSortNum() {
		return sortNum;
	}

	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}

	@Column(precision=19,scale=2,name="min_amount",columnDefinition="decimal(19,2) COMMENT '最低金额'")
	public BigDecimal getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}

	@Column(precision=19,scale=2,name="max_amount",columnDefinition="decimal(19,2) COMMENT '最高金额'")
	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}

	@Column(name="is_show_pc",columnDefinition="int(10) COMMENT 'PC显示(1是、0否)'")
	public Integer getIsShowPC() {
		return isShowPC;
	}

	public void setIsShowPC(Integer isShowPC) {
		this.isShowPC = isShowPC;
	}

	@Column(name="is_show_app",columnDefinition="int(10) COMMENT 'APP显示(1是、0否)'")
	public Integer getIsShowApp() {
		return isShowApp;
	}

	public void setIsShowApp(Integer isShowApp) {
		this.isShowApp = isShowApp;
	}
	@Column(name="qrcode_link",columnDefinition="varchar(50) COMMENT '二维码'")
	public String getQrCodeLink() {
		return qrCodeLink;
	}

	public void setQrCodeLink(String qrCodeLink) {
		this.qrCodeLink = qrCodeLink;
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
