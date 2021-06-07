package com.jeetx.bean.lottery;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.jeetx.bean.member.PointsLevel;

@Entity 
@Table(name="tb_lottery_robotplant")
public class LotteryRobotPlant implements Serializable {
	private static final long serialVersionUID = -7084393963287332082L;
	
	private Integer id;
	private String nickName;
	private String headImg;
	private PointsLevel pointsLevel;
	private Integer points;
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="nick_name",columnDefinition="varchar(50) COMMENT '玩家昵称'")
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	@Column(name="head_img",columnDefinition="varchar(50) COMMENT '头像地址'")
	public String getHeadImg() {
		return headImg;
	}
	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="points_level_id",columnDefinition="int(10) COMMENT '积分等级ID'")
	public PointsLevel getPointsLevel() {
		return pointsLevel;
	}
	public void setPointsLevel(PointsLevel pointsLevel) {
		this.pointsLevel = pointsLevel;
	}
	@JoinColumn(name="points",columnDefinition="int(10) COMMENT '积分'")
	public Integer getPoints() {
		return points;
	}
	public void setPoints(Integer points) {
		this.points = points;
	}
}