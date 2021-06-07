package com.jeetx.bean.lottery;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.jeetx.bean.member.User;


@Entity 
@Table(name="tb_lottery_order")
public class LotteryOrder implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	
	private Integer id;
	private Integer version;
	private String orderCode;//订单编号
	private User user;//玩家
	private Date createTime = new Date();//下注时间
	private LotteryType lotteryType; //彩票类型
	private LotteryRoom lotteryRoom; //游戏房间
	private String lotteryPeriod;//当前期数
	private BigDecimal betMoney; //下注金额
	private BigDecimal combinationMoney; //下注组合金额
	private BigDecimal dxdsMoney; //下注大小单双金额
	private BigDecimal jzMoney; //下注极值金额
	private BigDecimal sbdMoney; //下注顺豹对金额
	private BigDecimal ddMoney; //下注单点金额
	private Integer combinationCount;//组合期数（有组合1、没有组合0）
	private BigDecimal winMoney; //中奖金额（含本）
	private BigDecimal profitMoney; //盈亏金额
	private Integer status=1; //状态(1待开奖、2已中奖、3已取消、4未中奖)
	private String lotteryOpenContent;//开奖号码
	private List<LotteryOrderItem> lotteryOrderItems = new ArrayList<LotteryOrderItem>();
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Version
	@Column(name="version",columnDefinition="int(10) COMMENT '乐观锁'")
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(precision=19,scale=2,name="win_money",columnDefinition="decimal(19,2) COMMENT '中奖金额'")
	public BigDecimal getWinMoney() {
		return winMoney;
	}

	public void setWinMoney(BigDecimal winMoney) {
		this.winMoney = winMoney;
	}
	@Column(name="status",columnDefinition="int(10) COMMENT '状态(1待开奖、2已中奖、3已取消、4未中奖)'")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name="lottery_periods",columnDefinition="varchar(50) COMMENT '游戏期数'")
	public String getLotteryPeriod() {
		return lotteryPeriod;
	}

	public void setLotteryPeriod(String lotteryPeriod) {
		this.lotteryPeriod = lotteryPeriod;
	}

	@Column(name="order_code",columnDefinition="varchar(50) COMMENT '订单编号'")
	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="user_id",columnDefinition="int(10) COMMENT '玩家ID'")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	@Column(name="create_time",columnDefinition="datetime COMMENT '创建时间'")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="lottery_type_id",columnDefinition="int(10) COMMENT '彩票类型ID'")
	public LotteryType getLotteryType() {
		return lotteryType;
	}

	public void setLotteryType(LotteryType lotteryType) {
		this.lotteryType = lotteryType;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="lottery_room_id",columnDefinition="int(10) COMMENT '彩票房间ID'")
	public LotteryRoom getLotteryRoom() {
		return lotteryRoom;
	}

	public void setLotteryRoom(LotteryRoom lotteryRoom) {
		this.lotteryRoom = lotteryRoom;
	}
	@Column(precision=19,scale=2,name="bet_money",columnDefinition="decimal(19,2) COMMENT '下注金额'")
	public BigDecimal getBetMoney() {
		return betMoney;
	}

	public void setBetMoney(BigDecimal betMoney) {
		this.betMoney = betMoney;
	}
	@Column(name="lottery_open_content",columnDefinition="varchar(50) COMMENT '开奖内容'")
	public String getLotteryOpenContent() {
		return lotteryOpenContent;
	}

	public void setLotteryOpenContent(String lotteryOpenContent) {
		this.lotteryOpenContent = lotteryOpenContent;
	}
	@OneToMany(cascade = { CascadeType.REFRESH, CascadeType.REMOVE,
	CascadeType.PERSIST }, mappedBy = "lotteryOrder", fetch = FetchType.LAZY)//fetch = FetchType.EAGER 
	public List<LotteryOrderItem> getLotteryOrderItems() {
		return lotteryOrderItems;
	}

	public void setLotteryOrderItems(List<LotteryOrderItem> lotteryOrderItems) {
		this.lotteryOrderItems = lotteryOrderItems;
	}
	@Column(precision=19,scale=2,name="profit_money",columnDefinition="decimal(19,2) COMMENT '盈亏金额'")
	public BigDecimal getProfitMoney() {
		return profitMoney;
	}

	public void setProfitMoney(BigDecimal profitMoney) {
		this.profitMoney = profitMoney;
	}
	@Column(precision=19,scale=2,name="combination_money",columnDefinition="decimal(19,2) COMMENT '下注组合金额'")
	public BigDecimal getCombinationMoney() {
		return combinationMoney;
	}

	public void setCombinationMoney(BigDecimal combinationMoney) {
		this.combinationMoney = combinationMoney;
	}
	@Column(name="combination_count",columnDefinition="int(10) COMMENT '组合期数（有组合1、没有组合0）'")
	public Integer getCombinationCount() {
		return combinationCount;
	}

	public void setCombinationCount(Integer combinationCount) {
		this.combinationCount = combinationCount;
	}

	@Column(precision=19,scale=2,name="dxds_money",columnDefinition="decimal(19,2) COMMENT '下注大小单双金额'")
	public BigDecimal getDxdsMoney() {
		return dxdsMoney;
	}

	public void setDxdsMoney(BigDecimal dxdsMoney) {
		this.dxdsMoney = dxdsMoney;
	}

	@Column(precision=19,scale=2,name="jz_money",columnDefinition="decimal(19,2) COMMENT '下注极值金额'")
	public BigDecimal getJzMoney() {
		return jzMoney;
	}

	public void setJzMoney(BigDecimal jzMoney) {
		this.jzMoney = jzMoney;
	}

	@Column(precision=19,scale=2,name="sbd_money",columnDefinition="decimal(19,2) COMMENT '下注顺豹对金额'")
	public BigDecimal getSbdMoney() {
		return sbdMoney;
	}

	public void setSbdMoney(BigDecimal sbdMoney) {
		this.sbdMoney = sbdMoney;
	}

	@Column(precision=19,scale=2,name="dd_money",columnDefinition="decimal(19,2) COMMENT '下注单点金额'")
	public BigDecimal getDdMoney() {
		return ddMoney;
	}

	public void setDdMoney(BigDecimal ddMoney) {
		this.ddMoney = ddMoney;
	}
	
	
}
