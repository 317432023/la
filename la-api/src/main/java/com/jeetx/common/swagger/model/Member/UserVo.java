package com.jeetx.common.swagger.model.Member;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

public class UserVo implements Serializable{
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(required=true,value="ID") 
	private Integer id;
	@ApiModelProperty(required=true,value="用户名(登陆账号)") 
	private String username;
	@ApiModelProperty(required=true,value="游戏昵称") 
	private String nickName;
	@ApiModelProperty(required=true,value="上级账号") 
	private String parent;
	@ApiModelProperty(required=true,value="玩家类型(0游客、1玩家、2代理、3推广员、4虚拟号)") 
	private Integer userType;
	@ApiModelProperty(required=true,value="注册时间") 
	private String createTime;
	@ApiModelProperty(required=true,value="余额") 
	private String balance;
	@ApiModelProperty(required=true,value="彩金") 
	private String lotteryBalance;
	@ApiModelProperty(required=true,value="推广用户数") 
	private Integer expandUserNum;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public Integer getUserType() {
		return userType;
	}
	public void setUserType(Integer userType) {
		this.userType = userType;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getLotteryBalance() {
		return lotteryBalance;
	}
	public void setLotteryBalance(String lotteryBalance) {
		this.lotteryBalance = lotteryBalance;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public Integer getExpandUserNum() {
		return expandUserNum;
	}
	public void setExpandUserNum(Integer expandUserNum) {
		this.expandUserNum = expandUserNum;
	}
	
}
