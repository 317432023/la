package com.jeetx.common.swagger.model.Member;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

public class LetterVo implements Serializable{
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(required=true,value="ID") 
	private Integer id;
	@ApiModelProperty(required=true,value="标题") 
	private String title;
	@ApiModelProperty(required=true,value="内容") 
	private String contents;
	@ApiModelProperty(required=true,value="创建时间") 
	private String createTime;
	@ApiModelProperty(required=true,value="状态 （1未读、2已读、3已删除）") 
	private Integer status;
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
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
}
