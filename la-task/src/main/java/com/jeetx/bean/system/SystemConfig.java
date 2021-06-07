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
	private String id;
	/**参数名称*/
	private String name;
	/**参数值*/
	private String value;
	
	@Id
	@Column(length = 255,name="id",columnDefinition="varchar(255) COMMENT 'id'")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(length = 255,name="name",columnDefinition="varchar(50) COMMENT '配置编码'")
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
