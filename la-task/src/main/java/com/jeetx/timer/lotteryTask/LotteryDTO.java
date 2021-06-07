package com.jeetx.timer.lotteryTask;

import java.io.Serializable;

public class LotteryDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	public boolean bool;
	public Integer lotteryType;
    public String period;
    public String openContent;
    public Integer lotteryNumber;
    public String openTime;
    public String dataSource;
	private String groupName;//组合名称 如:豹子；对子;顺子
	public Integer getLotteryType() {
		return lotteryType;
	}
	public void setLotteryType(Integer lotteryType) {
		this.lotteryType = lotteryType;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getOpenContent() {
		return openContent;
	}
	public void setOpenContent(String openContent) {
		this.openContent = openContent;
	}
	public String getOpenTime() {
		return openTime;
	}
	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}
	public String getDataSource() {
		return dataSource;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	public boolean isBool() {
		return bool;
	}
	public void setBool(boolean bool) {
		this.bool = bool;
	}
	
	public Integer getLotteryNumber() {
		return lotteryNumber;
	}
	public void setLotteryNumber(Integer lotteryNumber) {
		this.lotteryNumber = lotteryNumber;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
    
}