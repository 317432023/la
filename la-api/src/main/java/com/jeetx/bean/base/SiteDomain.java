package com.jeetx.bean.base;

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
@Table(name="tb_base_site_domain")
public class SiteDomain implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	
	private Integer id;
	private Integer sortNum;//排序等级
	private String siteName; //站点名称
	private String siteDomain; //站点域名
	private Integer isOpenPC;//PC端是否开启(1启用、0禁用)
	private Integer isOpenApp;//APP端是否开启(1启用、0禁用)
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

	@Column(name="sort",columnDefinition="int(10) COMMENT '排序等级'")
	public Integer getSortNum() {
		return sortNum;
	}

	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}
	@Column(name="site_name",columnDefinition="varchar(100) COMMENT '站点名称'")
	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	@Column(name="site_domain",columnDefinition="varchar(100) COMMENT '站点域名'")
	public String getSiteDomain() {
		return siteDomain;
	}

	public void setSiteDomain(String siteDomain) {
		this.siteDomain = siteDomain;
	}
	
	@Column(name="is_open_pc",columnDefinition="int(10) COMMENT 'PC端是否开启'")
	public Integer getIsOpenPC() {
		return isOpenPC;
	}

	public void setIsOpenPC(Integer isOpenPC) {
		this.isOpenPC = isOpenPC;
	}
	
	@Column(name="is_open_app",columnDefinition="int(10) COMMENT 'APP端是否开启'")
	public Integer getIsOpenApp() {
		return isOpenApp;
	}

	public void setIsOpenApp(Integer isOpenApp) {
		this.isOpenApp = isOpenApp;
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
