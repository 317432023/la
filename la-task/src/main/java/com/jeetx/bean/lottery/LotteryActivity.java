package com.jeetx.bean.lottery;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.jeetx.bean.system.Station;

@Entity
@Table(name = "tb_lottery_activity")
public class LotteryActivity implements Serializable {
	private static final long serialVersionUID = 6693965150652975115L;
	  
	private Integer id;//主键
	private String title;//活动名称
	private Date beginTime;//开始时间
	private Date endTime;//结束时间
	private Integer giftUserType;//赠送对象（0全部用户、1新用户、2老用户）
	private Integer giftType;//赠送方式（1定额、0比例）
	private Integer isFirstRecharge;//是否首充（1是、0否）
	private Integer isSuperpose;//是否叠加（1是、0否）
	private Integer isSostenuto;//是否持续（1是、0否）
	private Integer isLimitedMoney;//限制金额（1是、0否）
	private BigDecimal maxMoney; //最高金额
	private Integer activityType;//活动类型（1充值）
	private Integer sortNum;//优先级(数字小在前)
	private Integer status = 1;//当前状态(1启用、0禁用)
	private Station station; //站点
	private Integer userType = 0;//用户类型（0全部、1玩家、2虚拟号）
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name="title",columnDefinition="varchar(50) COMMENT '活动名称'")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	@Column(name="begin_time",columnDefinition="datetime COMMENT '开始时间'")
	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	@Column(name="end_time",columnDefinition="datetime COMMENT '结束时间'")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Column(name="gift_user_type",columnDefinition="int(10) COMMENT '赠送对象（0全部用户、1新用户、2老用户）'")
	public Integer getGiftUserType() {
		return giftUserType;
	}

	public void setGiftUserType(Integer giftUserType) {
		this.giftUserType = giftUserType;
	}

	@Column(name="gift_type",columnDefinition="int(10) COMMENT '赠送方式（1定额、2比例）'")
	public Integer getGiftType() {
		return giftType;
	}

	public void setGiftType(Integer giftType) {
		this.giftType = giftType;
	}

	@Column(name="is_first_recharge",columnDefinition="int(10) COMMENT '是否首充（1是、0否）'")
	public Integer getIsFirstRecharge() {
		return isFirstRecharge;
	}

	public void setIsFirstRecharge(Integer isFirstRecharge) {
		this.isFirstRecharge = isFirstRecharge;
	}

	@Column(name="is_superpose",columnDefinition="int(10) COMMENT '是否叠加（1是、0否）'")
	public Integer getIsSuperpose() {
		return isSuperpose;
	}

	public void setIsSuperpose(Integer isSuperpose) {
		this.isSuperpose = isSuperpose;
	}
	@Column(name="is_sostenuto",columnDefinition="int(10) COMMENT '是否持续（1是、0否）'")
	public Integer getIsSostenuto() {
		return isSostenuto;
	}

	public void setIsSostenuto(Integer isSostenuto) {
		this.isSostenuto = isSostenuto;
	}

	@Column(name="is_limited_money",columnDefinition="int(10) COMMENT '限制金额（1是、0否）'")
	public Integer getIsLimitedMoney() {
		return isLimitedMoney;
	}

	public void setIsLimitedMoney(Integer isLimitedMoney) {
		this.isLimitedMoney = isLimitedMoney;
	}
	@Column(precision=19,scale=2,name="max_money",columnDefinition="decimal(19,2) COMMENT '最高余额'")
	public BigDecimal getMaxMoney() {
		return maxMoney;
	}

	public void setMaxMoney(BigDecimal maxMoney) {
		this.maxMoney = maxMoney;
	}
	@Column(name="activity_type",columnDefinition="int(10) COMMENT '活动类型（1充值、2完善资料）'")
	public Integer getActivityType() {
		return activityType;
	}

	public void setActivityType(Integer activityType) {
		this.activityType = activityType;
	}
	@Column(name="sort",columnDefinition="int(10) COMMENT '排序'")
	public Integer getSortNum() {
		return sortNum;
	}

	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}
	@Column(name="status",columnDefinition="int(10) COMMENT '当前状态(1启用、0禁用)'")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="station_id",columnDefinition="int(10) COMMENT '站点ID'")	
	public Station getStation() {
		return station;
	}
	public void setStation(Station station) {
		this.station = station;
	}
	@Column(name="user_type",columnDefinition="int(10) COMMENT '用户类型（0全部、1玩家、2虚拟号）'")
	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}
	
}
