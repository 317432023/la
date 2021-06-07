package com.jeetx.common.swagger.model.Member;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

public class TransRecordVo implements Serializable{
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(required=true,value="ID") 
	private Integer id;
	@ApiModelProperty(required=true,value="用户ID") 
	private Integer userId;
	@ApiModelProperty(required=true,value="用户名") 
	private String username;
	@ApiModelProperty(required=true,value="游戏昵称") 
	private String nickName;
	@ApiModelProperty(required=true,value="交易时间") 
	private String createTime;
	@ApiModelProperty(required=true,value="交易类型（1充值、2提现、3抽奖、4投注、5撤单、6赠送、7中奖、8回水、9调账）") 
	private Integer transCategory;
	@ApiModelProperty(required=true,value="交易账户金额") 
	private String transAmount;
	@ApiModelProperty(required=true,value="剩余账户金额") 
	private String endBalance;
	@ApiModelProperty(required=true,value="交易彩金") 
	private String transLotteryAmount;
	@ApiModelProperty(required=true,value="剩余彩金") 
	private String endLotteryBalance;
	@ApiModelProperty(value="备注信息") 
	private String remark;//
	@ApiModelProperty(required=true,value="1加,0扣") 
	private Integer flag;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getTransAmount() {
		return transAmount;
	}
	public void setTransAmount(String transAmount) {
		this.transAmount = transAmount;
	}
	public String getEndBalance() {
		return endBalance;
	}
	public void setEndBalance(String endBalance) {
		this.endBalance = endBalance;
	}
	public String getTransLotteryAmount() {
		return transLotteryAmount;
	}
	public void setTransLotteryAmount(String transLotteryAmount) {
		this.transLotteryAmount = transLotteryAmount;
	}
	public String getEndLotteryBalance() {
		return endLotteryBalance;
	}
	public void setEndLotteryBalance(String endLotteryBalance) {
		this.endLotteryBalance = endLotteryBalance;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getTransCategory() {
		return transCategory;
	}
	public void setTransCategory(Integer transCategory) {
		this.transCategory = transCategory;
	}
	public Integer getFlag() {
		return flag;
	}
	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
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
}
