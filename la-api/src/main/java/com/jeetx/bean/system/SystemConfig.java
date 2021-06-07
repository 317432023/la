package com.jeetx.bean.system;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity 
@Table(name="tb_system_config")
public class SystemConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**主键*/
	private Integer id;
	/**类型*/
	private Integer type;
	/**名称*/
	private String title;
	/**配置编码*/
	private String name;
	/**配置值*/
	private String value;
	
	@Id
	@Column(name="id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name="type",columnDefinition="int(10) COMMENT '类型'")
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
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
}
