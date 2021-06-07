package com.jeetx.bean.base;

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
@Table(name="tb_base_recharge_online_config")
public class RechargeOnLineConfig  implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	
	private Integer id;
	private String title; //标题
	private String iconImg;//图标链接
	private Integer sortNum;//排序号（大的前）
	private Integer providerType;//接口提供商（6明捷支付）
	private BigDecimal minAmount; //最低金额
	private BigDecimal maxAmount; //最高金额
	private Integer isShowPC; //电脑显示（1是、0否）
	private Integer isShowApp; //手机显示（1是、0否）
	private String payUrl; //提交地址
	private String noticeURL; //回调地址
	private String queryUrl; //查询地址
	private String reminder; //温馨提示
	private String payTypeJson; //支付方式json
	private Integer urlType; //返回地址类型：（1wap跳转、2二维码地址）
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

	@Column(name="provider_type",columnDefinition="int(10) COMMENT '接口提供商（6明捷支付）'")
	public Integer getProviderType() {
		return providerType;
	}

	public void setProviderType(Integer providerType) {
		this.providerType = providerType;
	}

	@Column(name="pay_type_json",columnDefinition="varchar(1000) COMMENT '支付方式json'")
	public String getPayTypeJson() {
		return payTypeJson;
	}

	public void setPayTypeJson(String payTypeJson) {
		this.payTypeJson = payTypeJson;
	}

	@Column(name="notice_url",columnDefinition="varchar(50) COMMENT '回调地址'")
	public String getNoticeURL() {
		return noticeURL;
	}

	public void setNoticeURL(String noticeURL) {
		this.noticeURL = noticeURL;
	}

	@Column(name="url_type",columnDefinition="int(10) COMMENT '返回地址类型：（1wap跳转、2二维码地址）'")
	public Integer getUrlType() {
		return urlType;
	}

	public void setUrlType(Integer urlType) {
		this.urlType = urlType;
	}

	@Column(name="pay_url",columnDefinition="varchar(50) COMMENT '提交地址'")
	public String getPayUrl() {
		return payUrl;
	}

	public void setPayUrl(String payUrl) {
		this.payUrl = payUrl;
	}

	@Column(name="query_url",columnDefinition="varchar(50) COMMENT '查询地址'")
	public String getQueryUrl() {
		return queryUrl;
	}

	public void setQueryUrl(String queryUrl) {
		this.queryUrl = queryUrl;
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
