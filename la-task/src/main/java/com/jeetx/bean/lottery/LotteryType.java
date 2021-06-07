package com.jeetx.bean.lottery;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity 
@Table(name="tb_lottery_type")
public class LotteryType implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	
	private Integer id;
	private String lotteryName;//彩票名称
	private Integer sortNumPc;//网页端排序
	private Integer sortNumApp;//APP端排序
	private String picLink;//图标地址
	private String remarks;//短描述
	private Integer status;//状态(1启用0禁用)

	@Id
	@Column(name="id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name="lottery_name",columnDefinition="varchar(50) COMMENT '彩票名称'")
	public String getLotteryName() {
		return lotteryName;
	}
	public void setLotteryName(String lotteryName) {
		this.lotteryName = lotteryName;
	}
	@Column(name="sort_pc",columnDefinition="int(10) COMMENT 'pc排序号(大的在前)'")
	public Integer getSortNumPc() {
		return sortNumPc;
	}
	public void setSortNumPc(Integer sortNumPc) {
		this.sortNumPc = sortNumPc;
	}
	@Column(name="sort_app",columnDefinition="int(10) COMMENT 'app排序号(大的在前)'")
	public Integer getSortNumApp() {
		return sortNumApp;
	}
	public void setSortNumApp(Integer sortNumApp) {
		this.sortNumApp = sortNumApp;
	}
	@Column(name="remarks",columnDefinition="varchar(50) COMMENT '描述信息'")
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	@Column(name="status",columnDefinition="int(10) COMMENT '状态(1启用、0禁用)'")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name="pic_link",columnDefinition="varchar(200) COMMENT '图标地址'")
	public String getPicLink() {
		return picLink;
	}
	public void setPicLink(String picLink) {
		this.picLink = picLink;
	}
	
	
}
