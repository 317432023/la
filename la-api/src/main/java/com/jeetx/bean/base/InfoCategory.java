package com.jeetx.bean.base;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @describe 资讯分类
 * @version
 * @author 
 * @date 2014-8-31
 */
@Entity
@Table(name="tb_base_info_category")
public class InfoCategory implements Serializable{

	private static final long serialVersionUID = 1L;
	//主键
	private Integer id;
	//分类名称
	private String title;
	//状态(1启用0禁用)
	private Integer status;
	//分类父类ID
	private Integer parentId=0;
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="title",columnDefinition="varchar(50) COMMENT '分类名称'")
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Column(name="status",columnDefinition="int(10) COMMENT '状态(1启用、0禁用)'")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name="parent_id",columnDefinition="int(10) COMMENT '分类父类ID'")
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
}
