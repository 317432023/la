package com.jeetx.bean.member;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity 
@Table(name="tb_member_points_level")
public class PointsLevel implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	
	private Integer id;
	private String title; //等级名称
	private Integer minPoints; //区间最小积分
	private Integer maxPoints;//区间最大积分

	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name="title",columnDefinition="varchar(500) COMMENT '等级名称'")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	@Column(name="min_points",columnDefinition="int(10) COMMENT '区间最小积分'")
	public Integer getMinPoints() {
		return minPoints;
	}

	public void setMinPoints(Integer minPoints) {
		this.minPoints = minPoints;
	}
	@Column(name="max_points",columnDefinition="int(10) COMMENT '区间最大积分'")
	public Integer getMaxPoints() {
		return maxPoints;
	}

	public void setMaxPoints(Integer maxPoints) {
		this.maxPoints = maxPoints;
	}
}
