package com.jeetx.common.dpc;

import java.util.List;

public class RoomJson {

	private String roomKey;
	private String roomTitle;
	private Integer lotteryType;
	private Integer toyType;
	private List<RuleJson> rule;
	public String getRoomKey() {
		return roomKey;
	}
	public void setRoomKey(String roomKey) {
		this.roomKey = roomKey;
	}
	public String getRoomTitle() {
		return roomTitle;
	}
	public void setRoomTitle(String roomTitle) {
		this.roomTitle = roomTitle;
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
	public List<RuleJson> getRule() {
		return rule;
	}
	public void setRule(List<RuleJson> rule) {
		this.rule = rule;
	}
	
}
