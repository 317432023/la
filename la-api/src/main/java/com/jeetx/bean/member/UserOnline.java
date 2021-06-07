package com.jeetx.bean.member;

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
 
/**
 * 玩家表
 * @author Administrator
 *
 */
@Entity 
@Table(name="tb_member_user_online")
public class UserOnline implements Serializable {
	private static final long serialVersionUID = -7084393963287332082L;
	
	private Integer id;
	/**第三方在线客服地址*/
	private String customerLinks;
	/**是否开启在线客服*/
	private Integer isOpen;
	/**在线客服参数配置*/
	private String onlineInfo;
	/**用户*/
	private User user; 

	
	@Id
	@Column(name="id",columnDefinition="int(10) COMMENT '主键'")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name="customer_links",columnDefinition="varchar(500) COMMENT '第三方在线客服地址'")
	public String getCustomerLinks() {
		return customerLinks;
	}
	public void setCustomerLinks(String customerLinks) {
		this.customerLinks = customerLinks;
	}
	@Column(name="is_open",columnDefinition="int(10) COMMENT '是否开启在线客服'")
	public Integer getIsOpen() {
		return isOpen;
	}
	public void setIsOpen(Integer isOpen) {
		this.isOpen = isOpen;
	}
	@Column(name="online_info",columnDefinition="varchar(500) COMMENT '在线客服参数配置'")
	public String getOnlineInfo() {
		return onlineInfo;
	}
	public void setOnlineInfo(String onlineInfo) {
		this.onlineInfo = onlineInfo;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="user_id",columnDefinition="int(10) COMMENT '代理ID'")
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
}