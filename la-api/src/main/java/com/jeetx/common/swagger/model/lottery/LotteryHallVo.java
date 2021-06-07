package com.jeetx.common.swagger.model.lottery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;


public class LotteryHallVo implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	@ApiModelProperty(required=true,value="ID") 
	private Integer id;
	@ApiModelProperty(required=true,value="大厅名称") 
	private String title;
	@ApiModelProperty(required=true,value="图标链接") 
	private String iconImg;
	@ApiModelProperty(required=true,value="进入最低金额") 
	private String minimum;
	@ApiModelProperty(required=true,value="赔率说明") 
	private String ruleRemarks;
	@ApiModelProperty(required=true,value="房间") 
	private List<LotteryRoomVo> rooms = new ArrayList<LotteryRoomVo>();
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
	public String getIconImg() {
		return iconImg;
	}
	public void setIconImg(String iconImg) {
		this.iconImg = iconImg;
	}
	public String getMinimum() {
		return minimum;
	}
	public void setMinimum(String minimum) {
		this.minimum = minimum;
	}
	public String getRuleRemarks() {
		return ruleRemarks;
	}
	public void setRuleRemarks(String ruleRemarks) {
		this.ruleRemarks = ruleRemarks;
	}
	public List<LotteryRoomVo> getRooms() {
		return rooms;
	}
	public void setRooms(List<LotteryRoomVo> rooms) {
		this.rooms = rooms;
	}
}
