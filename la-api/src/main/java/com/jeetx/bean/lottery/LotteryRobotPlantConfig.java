package com.jeetx.bean.lottery;

import java.io.Serializable;
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
/**
 * 假人
 * @author Administrator
 *
 */
@Entity 
@Table(name="tb_lottery_robotplant_config")
public class LotteryRobotPlantConfig implements Serializable {
	private static final long serialVersionUID = -7084393963287332082L;
	
	private Integer id;
	/**创建时间*/
	private Date createTime;
	/**1开启 0关闭*/
	private Integer status;
	/**人数/分钟*/
	private Integer randomCount;
	/**最小投注金额*/
	private Integer minMoney;
	/**最大投注金额*/
	private Integer maxMoney;
	/**游戏房间*/
	private LotteryRoom lotteryRoom; 
	/**彩票大厅*/
	private LotteryHall lotteryHall;
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name="status",columnDefinition="int(10) COMMENT '1开启 0关闭'")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name="random_count",columnDefinition="int(10) COMMENT '人数/分钟'")
	public Integer getRandomCount() {
		return randomCount;
	}
	public void setRandomCount(Integer randomCount) {
		this.randomCount = randomCount;
	}
	@Column(name="create_time",columnDefinition="datetime COMMENT '创建时间'")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(precision=19,scale=2,name="min_money",columnDefinition="decimal(19,2) COMMENT '最小投注金额'")
	public Integer getMinMoney() {
		return minMoney;
	}
	public void setMinMoney(Integer minMoney) {
		this.minMoney = minMoney;
	}
	@Column(precision=19,scale=2,name="max_money",columnDefinition="decimal(19,2) COMMENT '最大投注金额'")
	public Integer getMaxMoney() {
		return maxMoney;
	}
	public void setMaxMoney(Integer maxMoney) {
		this.maxMoney = maxMoney;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="lottery_room_id",columnDefinition="int(10) COMMENT '彩票房间ID'")
	public LotteryRoom getLotteryRoom() {
		return lotteryRoom;
	}

	public void setLotteryRoom(LotteryRoom lotteryRoom) {
		this.lotteryRoom = lotteryRoom;
	}
	
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="lottery_hall_id",columnDefinition="int(10) COMMENT '彩票大厅ID，对应彩票大厅ID'")
	public LotteryHall getLotteryHall() {
		return lotteryHall;
	}

	public void setLotteryHall(LotteryHall lotteryHall) {
		this.lotteryHall = lotteryHall;
	}
}