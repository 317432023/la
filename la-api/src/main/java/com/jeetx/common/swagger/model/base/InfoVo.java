package com.jeetx.common.swagger.model.base;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

public class InfoVo implements Serializable{

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(required=true,value="ID") 
	private Integer id;
	@ApiModelProperty(required=true,value="标题") 
	private String title;
	@ApiModelProperty(required=true,value="内容") 
	private String content;
	@ApiModelProperty(required=true,value="创建时间") 
	private String createtime;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	
}
