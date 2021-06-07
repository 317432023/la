package com.jeetx.common.swagger.model.base;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

public class SiteDomainVo implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	@ApiModelProperty(required=true,value="id") 
	private Integer id;
	@ApiModelProperty(required=true,value="站点名称") 
	private String siteName; //站点名称
	@ApiModelProperty(required=true,value="站点域名") 
	private String siteDomain; //站点域名

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public String getSiteDomain() {
		return siteDomain;
	}
	public void setSiteDomain(String siteDomain) {
		this.siteDomain = siteDomain;
	}
	
	
	
}
