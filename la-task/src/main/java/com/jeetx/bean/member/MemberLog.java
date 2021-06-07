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
@Table(name = "tb_member_log")
public class MemberLog implements Serializable {
	static final long serialVersionUID = -1924272191136313977L;
	/** 主键 */
	private Integer id;
	/** 登录用户 */
	private User user;
	/** IP地址 */
	private String ip;
	/** 属性集 */
	private String argument;
	/** 描述信息 */
	private String memo;
	/** 设备*/
	private String device;
	/** 记录时间 */
	private Date createTime;//创建时间
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="user_id",columnDefinition="int(10) COMMENT '用户ID'")
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	@Column(name="ip",columnDefinition="varchar(50) COMMENT 'ip'")
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	@Column(name="argument",columnDefinition="varchar(500) COMMENT '属性集'")
	public String getArgument() {
		return argument;
	}
	public void setArgument(String argument) {
		this.argument = argument;
	}
	@Column(name="content",columnDefinition="varchar(500) COMMENT '日志描述'")
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	@Column(name="device",columnDefinition="varchar(500) COMMENT '设备、平台'")
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	@Column(name="create_time",columnDefinition="datetime COMMENT '创建时间'")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
