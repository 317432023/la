package com.jeetx.common.swagger.model.base;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

public class AppVersionVo implements Serializable{
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(required=true,value="设备类型（1安卓、2IOS）") 
	private Integer deviceType;//设备类型（1安卓、2IOS）
	@ApiModelProperty(required=true,value="版本号") 
	private String versionCode; //版本号
	@ApiModelProperty(required=true,value="下载地址") 
	private String downloadLink; //下载地址
	@ApiModelProperty(required=true,value="是否强制更新(1是、0否)") 
	private Integer isForceupdate;//是否强制更新(1是、0否)
	@ApiModelProperty(value="升级日志") 
	private String updateLog;//升级日志
	public Integer getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}
	public String getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}
	public String getDownloadLink() {
		return downloadLink;
	}
	public void setDownloadLink(String downloadLink) {
		this.downloadLink = downloadLink;
	}
	public Integer getIsForceupdate() {
		return isForceupdate;
	}
	public void setIsForceupdate(Integer isForceupdate) {
		this.isForceupdate = isForceupdate;
	}
	public String getUpdateLog() {
		return updateLog;
	}
	public void setUpdateLog(String updateLog) {
		this.updateLog = updateLog;
	}
	
	
}
