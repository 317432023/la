package com.jeetx.bean.base;

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

import com.jeetx.bean.system.Station;

/**
 * 
 * @describe 资讯内容
 * @version
 * @author 
 * @date 2014-9-1
 */
@Entity
@Table(name="tb_base_info")
public class Info implements Serializable{

	private static final long serialVersionUID = 1L;
	//主键
	private Integer id;
	//标题
	private String title;
	//内容
	private String content;
	//资讯分类
	private InfoCategory infoCategory;
	//排序
	private Integer sortNum;
	//创建时间
	private Date createtime;
	//更新时间
	private Date updatetime;
	private Station station; //站点
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="title",columnDefinition="varchar(50) COMMENT '标题'")
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Column(name="content",columnDefinition="varchar(5000) COMMENT '内容'")
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@ManyToOne
	@JoinColumn(name="info_category_id",columnDefinition="int(10) COMMENT '资讯分类ID'")
	public InfoCategory getInfoCategory() {
		return infoCategory;
	}
	public void setInfoCategory(InfoCategory infoCategory) {
		this.infoCategory = infoCategory;
	}
	@Column(name="sort",columnDefinition="int(10) COMMENT '排序号(大的在前)'")
	public Integer getSortNum() {
		return sortNum;
	}
	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}
	@Column(name="create_time",columnDefinition="datetime COMMENT '创建时间'")
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	@Column(name="update_time",columnDefinition="datetime COMMENT '修改时间'")
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="station_id",columnDefinition="int(10) COMMENT '站点ID'")	
	public Station getStation() {
		return station;
	}
	public void setStation(Station station) {
		this.station = station;
	}
}
