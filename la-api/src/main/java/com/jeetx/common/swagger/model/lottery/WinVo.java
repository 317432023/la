package com.jeetx.common.swagger.model.lottery;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;


public class WinVo implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	@ApiModelProperty(required=true,value="昵称") 
	private String nickName;
	@ApiModelProperty(required=true,value="大厅") 
	private String hillTitle;
	@ApiModelProperty(required=true,value="奖金") 
	private String bonus;
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getHillTitle() {
		return hillTitle;
	}
	public void setHillTitle(String hillTitle) {
		this.hillTitle = hillTitle;
	}
	public String getBonus() {
		return bonus;
	}
	public void setBonus(String bonus) {
		this.bonus = bonus;
	}
	
	
}
