package com.jeetx.common.swagger.model.lottery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;


public class LotteryOrderVo implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	@ApiModelProperty(required=true,value="ID") 
	private Integer id;
	@ApiModelProperty(required=true,value="用户ID") 
	private Integer userId;
	@ApiModelProperty(required=true,value="用户名") 
	private String username;
	@ApiModelProperty(required=true,value="游戏昵称") 
	private String nickName;
	@ApiModelProperty(required=true,value="订单编号") 
	private String orderCode;
	@ApiModelProperty(required=true,value="下注时间") 
	private String createTime;
	@ApiModelProperty(required=true,value="彩票类型") 
	private Integer lotteryType; 
	@ApiModelProperty(required=true,value="彩票类型名称") 
	private String lotteryName; 
	@ApiModelProperty(required=true,value="大厅ID") 
	private Integer hallId;
	@ApiModelProperty(required=true,value="大厅名称") 
	private String hallName; 
	@ApiModelProperty(required=true,value="游戏房间ID") 
	private Integer roomId;
	@ApiModelProperty(required=true,value="游戏房间名称") 
	private String roomName; 
	@ApiModelProperty(required=true,value="当前期数") 
	private String lotteryPeriod;
	@ApiModelProperty(required=true,value="下注金额") 
	private String betMoney; 
	@ApiModelProperty(value="中奖金额（含本）") 
	private String winMoney;
	@ApiModelProperty(value="盈亏金额") 
	private String profitMoney; 
	@ApiModelProperty(required=true,value="状态(1待开奖、2已中奖、3已取消、4未中奖)") 
	private Integer status=1; 
	@ApiModelProperty(value="开奖号码") 
	private String lotteryOpenContent;
	@ApiModelProperty(required=true,value="明细") 
	private List<LotteryOrderItemVo> items = new ArrayList<LotteryOrderItemVo>();
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public Integer getLotteryType() {
		return lotteryType;
	}
	public void setLotteryType(Integer lotteryType) {
		this.lotteryType = lotteryType;
	}
	public String getLotteryName() {
		return lotteryName;
	}
	public void setLotteryName(String lotteryName) {
		this.lotteryName = lotteryName;
	}
	public Integer getRoomId() {
		return roomId;
	}
	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public String getLotteryPeriod() {
		return lotteryPeriod;
	}
	public void setLotteryPeriod(String lotteryPeriod) {
		this.lotteryPeriod = lotteryPeriod;
	}
	public String getBetMoney() {
		return betMoney;
	}
	public void setBetMoney(String betMoney) {
		this.betMoney = betMoney;
	}
	public String getWinMoney() {
		return winMoney;
	}
	public void setWinMoney(String winMoney) {
		this.winMoney = winMoney;
	}
	public String getProfitMoney() {
		return profitMoney;
	}
	public void setProfitMoney(String profitMoney) {
		this.profitMoney = profitMoney;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getLotteryOpenContent() {
		return lotteryOpenContent;
	}
	public void setLotteryOpenContent(String lotteryOpenContent) {
		this.lotteryOpenContent = lotteryOpenContent;
	}
	public List<LotteryOrderItemVo> getItems() {
		return items;
	}
	public void setItems(List<LotteryOrderItemVo> items) {
		this.items = items;
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
	public Integer getHallId() {
		return hallId;
	}
	public void setHallId(Integer hallId) {
		this.hallId = hallId;
	}
	public String getHallName() {
		return hallName;
	}
	public void setHallName(String hallName) {
		this.hallName = hallName;
	}
	
}
