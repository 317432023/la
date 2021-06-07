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
@Table(name="tb_base_app_version")
public class AppVersion implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	
	private Integer id;
	private Integer deviceType;//设备类型（1安卓、2IOS）
	private String versionCode; //版本号
	private String downloadLink; //下载地址
	private Integer isForceupdate;//是否强制更新(1是、0否)
	private String updateLog;//升级日志
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

	@Column(name="device_type",columnDefinition="int(10) COMMENT '设备类型（1安卓、2IOS）'")
	public Integer getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}

	@Column(name="version_code",columnDefinition="varchar(50) COMMENT '版本号'")
	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	@Column(name="download_link",columnDefinition="varchar(50) COMMENT '下载地址'")
	public String getDownloadLink() {
		return downloadLink;
	}

	public void setDownloadLink(String downloadLink) {
		this.downloadLink = downloadLink;
	}

	@Column(name="is_force_update",columnDefinition="int(10) COMMENT '是否强制更新(1是、0否)'")
	public Integer getIsForceupdate() {
		return isForceupdate;
	}

	public void setIsForceupdate(Integer isForceupdate) {
		this.isForceupdate = isForceupdate;
	}
	@Column(name="update_log",columnDefinition="varchar(50) COMMENT '升级日志'")
	public String getUpdateLog() {
		return updateLog;
	}

	public void setUpdateLog(String updateLog) {
		this.updateLog = updateLog;
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
