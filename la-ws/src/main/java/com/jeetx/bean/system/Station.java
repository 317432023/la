package com.jeetx.bean.system;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**站点表*/ 
@Entity 
@Table(name="tb_system_station")
public class Station implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	
	private Integer id;

	private String stationName;//站点名称
	
	private String stationDomain;//站点域名

	private Date createTime;//开通时间
	
	private Integer status;//启用状态(1启用、2禁用)
	
	private String shareLinks;//分享链接
	
	private String customerLinks;//客服链接

	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name="station_name",columnDefinition="varchar(50) COMMENT '站点名称'")
	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	@Column(name="create_time",columnDefinition="datetime COMMENT '创建时间'")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name="status",columnDefinition="int(10) COMMENT '1在用、0禁用'")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name="station_domain",columnDefinition="varchar(50) COMMENT '站点域名'")
	public String getStationDomain() {
		return stationDomain;
	}

	public void setStationDomain(String stationDomain) {
		this.stationDomain = stationDomain;
	}

	@Column(name="share_links",columnDefinition="varchar(50) COMMENT '分享链接'")
	public String getShareLinks() {
		return shareLinks;
	}

	public void setShareLinks(String shareLinks) {
		this.shareLinks = shareLinks;
	}

	@Column(name="customer_links",columnDefinition="varchar(50) COMMENT '客服链接'")
	public String getCustomerLinks() {
		return customerLinks;
	}

	public void setCustomerLinks(String customerLinks) {
		this.customerLinks = customerLinks;
	}
}
