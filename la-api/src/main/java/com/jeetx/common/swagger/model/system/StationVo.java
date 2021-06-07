package com.jeetx.common.swagger.model.system;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;

public class StationVo implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(required=true,value="ID") 
	private Integer id;
	@ApiModelProperty(required=true,value="站点名称") 
	private String stationName;
	@ApiModelProperty(required=true,value="入口域名") 
	private String entryDomain;
	@ApiModelProperty(required=true,value="图片域名") 
	private String imageDomain;
	@ApiModelProperty(required=true,value="MQ域名") 
	private String mqDomain;
	@ApiModelProperty(required=true,value="过期时间") 
	private String effectiveTime;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public String getEntryDomain() {
		return entryDomain;
	}
	public void setEntryDomain(String entryDomain) {
		this.entryDomain = entryDomain;
	}
	public String getImageDomain() {
		return imageDomain;
	}
	public void setImageDomain(String imageDomain) {
		this.imageDomain = imageDomain;
	}
	public String getMqDomain() {
		return mqDomain;
	}
	public void setMqDomain(String mqDomain) {
		this.mqDomain = mqDomain;
	}
	public String getEffectiveTime() {
		return effectiveTime;
	}
	public void setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
	}
	
}
