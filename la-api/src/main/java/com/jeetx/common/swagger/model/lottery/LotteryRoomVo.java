package com.jeetx.common.swagger.model.lottery;

import java.io.Serializable;

import com.jeetx.bean.lottery.LotteryHall;

import io.swagger.annotations.ApiModelProperty;


public class LotteryRoomVo implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	@ApiModelProperty(required=true,value="ID") 
	private Integer id;
	@ApiModelProperty(required=true,value="房间名称") 
	private String title;
	@ApiModelProperty(required=true,value="大厅名称") 
	private String hallTitle;
	@ApiModelProperty(required=true,value="图标链接") 
	private String iconImg;
	@ApiModelProperty(required=true,value="状态(1启用、0关闭、2隐藏)") 
	private Integer status;
	@ApiModelProperty(required=true,value="在线人数") 
	private Integer onLineCount;
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
	public String getHallTitle() {
		return hallTitle;
	}
	public void setHallTitle(String hallTitle) {
		this.hallTitle = hallTitle;
	}
	public Integer getOnLineCount() {
		return onLineCount;
	}
	public void setOnLineCount(Integer onLineCount) {
		this.onLineCount = onLineCount;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getIconImg() {
		return iconImg;
	}
	public void setIconImg(String iconImg) {
		this.iconImg = iconImg;
	}

}
