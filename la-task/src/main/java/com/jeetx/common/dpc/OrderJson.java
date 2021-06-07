package com.jeetx.common.dpc;

import java.math.BigDecimal;
import java.util.Date;

public class OrderJson {

	private String orderCode;//订单编号
	private String nickname;//账单昵称
	private String betMoney; //投注金额
	private String betContent; //投注内容
	private String lotteryPeriod;//当前期数
	private Integer lotteryType; //彩票类型
	private String roomKey; //游戏房间
	private Integer toyType;//玩法类型（1:PC、2:FT）
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getBetMoney() {
		return betMoney;
	}
	public void setBetMoney(String betMoney) {
		this.betMoney = betMoney;
	}
	public String getBetContent() {
		return betContent;
	}
	public void setBetContent(String betContent) {
		this.betContent = betContent;
	}
	public String getLotteryPeriod() {
		return lotteryPeriod;
	}
	public void setLotteryPeriod(String lotteryPeriod) {
		this.lotteryPeriod = lotteryPeriod;
	}
	public Integer getLotteryType() {
		return lotteryType;
	}
	public void setLotteryType(Integer lotteryType) {
		this.lotteryType = lotteryType;
	}
	public Integer getToyType() {
		return toyType;
	}
	public void setToyType(Integer toyType) {
		this.toyType = toyType;
	}
	public String getRoomKey() {
		return roomKey;
	}
	public void setRoomKey(String roomKey) {
		this.roomKey = roomKey;
	}
	
	
}
