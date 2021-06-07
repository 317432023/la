package com.jeetx.bean.member;

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

@Entity
@Table(name="tb_member_letter")
public class Letter implements Serializable{

	private static final long serialVersionUID = 1L;
	//主键
	private Integer id;
	//标题
	private String title;
	//内容
	private String contents;
	//创建时间
	private Date createTime;
	//已读时间
	private Date readTime;
	//状态 （1未读、2已读、3已删除）
	private Integer status;
	//用户ID
	private User user;
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name="title",columnDefinition="varchar(500) COMMENT '标题'")
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Column(name="contents",columnDefinition="varchar(2000) COMMENT '内容'")
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	@Column(name="create_time",columnDefinition="datetime COMMENT '创建时间'")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name="read_time",columnDefinition="datetime COMMENT '读取时间'")
	public Date getReadTime() {
		return readTime;
	}
	public void setReadTime(Date readTime) {
		this.readTime = readTime;
	}
	@Column(name="status",columnDefinition="int(10) COMMENT '状态 （1未读、2已读、3已删除）'")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="user_id",columnDefinition="int(10) COMMENT '关联会员表ID'")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
