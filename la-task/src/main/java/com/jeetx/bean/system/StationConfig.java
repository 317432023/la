package com.jeetx.bean.system;

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

@Entity 
@Table(name="tb_station_config")
public class StationConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**主键*/
	private Integer id;
	/**名称*/
	private String title;
	/**配置编码*/
	private String name;
	/**配置值*/
	private String value;
	/**站点*/
	private Station station; 
	/**摘要信息*/
	private String remark;
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(length = 255,name="title",columnDefinition="varchar(500) COMMENT '名称'")
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Column(length = 255,name="name",columnDefinition="varchar(500) COMMENT '配置编码'")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(length = 255,name="value",columnDefinition="varchar(500) COMMENT '配置值'")
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="station_id",columnDefinition="int(10) COMMENT '站点ID'")	
	public Station getStation() {
		return station;
	}
	public void setStation(Station station) {
		this.station = station;
	}
	@Column(length = 255,name="remark",columnDefinition="varchar(500) COMMENT '配置编码'")
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
