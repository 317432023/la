package com.jeetx.bean.system;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.jeetx.bean.system.Station;
 

@Entity 
@Table(name="tb_system_user")
public class SystemUser implements Serializable {
	private static final long serialVersionUID = -7084393963287332082L;
	
	private Integer id;
	/**登录账号*/
	private String username;
	/**登录密码*/
	private String password;
	/**权限组*/
	private String authorize;
	/**站点昵称*/
	private String nickName;
	/**当前状态(1启用、0禁用)*/
	private Integer status;
	/**创建时间 */
	private Date createTime;
	/**备注信息*/
//	private String desc;
	/**创建人*/
	private String createBy;
	/**站点*/
	private Station station;
	@Id
	@Column(name="id",columnDefinition="int(10) COMMENT '主键'")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name="create_at",columnDefinition="datetime COMMENT '创建时间'")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name="status",columnDefinition="int(10) COMMENT '状态(1在用、0禁用)'")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	@Column(name="username",columnDefinition="varchar(50) COMMENT '登陆账号'")
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Column(name="password",columnDefinition="varchar(50) COMMENT '登陆密码'")
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="station_id",columnDefinition="int(10) COMMENT '站点ID'")	
	public Station getStation() {
		return station;
	}
	public void setStation(Station station) {
		this.station = station;
	}
	@Column(name="authorize",columnDefinition="varchar(50) COMMENT '权限组'")
	public String getAuthorize() {
		return authorize;
	}
	public void setAuthorize(String authorize) {
		this.authorize = authorize;
	}
	@Column(name="nickname",columnDefinition="varchar(50) COMMENT '昵称'")
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
//	@Column(name="desc",columnDefinition="varchar(50) COMMENT '备注信息'")
//	public String getDesc() {
//		return desc;
//	}
//	public void setDesc(String desc) {
//		this.desc = desc;
//	}
	@Column(name="create_by",columnDefinition="varchar(50) COMMENT '备注信息'")
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
}