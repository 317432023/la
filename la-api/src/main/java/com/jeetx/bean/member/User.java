package com.jeetx.bean.member;

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
 
/**
 * 玩家表
 * @author Administrator
 *
 */
@Entity 
@Table(name="tb_member_user")
public class User implements Serializable {
	private static final long serialVersionUID = -7084393963287332082L;
	
	private Integer id;
	/** 乐观锁版本控制*/
	private Integer version;
	/**登录账号*/
	private String username;
	/**手机号码 */
	private String phone;
	/**登录密码*/
	private String password;
	/**交易密码*/
	private String payPassword;
	/**游戏昵称*/
	private String nickName;
	/**玩家类型(0游客、1玩家、2代理、3推广员、4虚拟号)*/
	private Integer userType;
	/**用户头像*/
	private String headImg;
	/**注册时间 */
	private Date createTime;
	/**当前状态(状态(1在用、0禁用、2冻结))*/
	private Integer status;
	/**余额*/
	private BigDecimal balance;
	/**彩金*/
	private BigDecimal lotteryBalance;
	/**冻结金额*/
	private BigDecimal freezeBalance;
	/**流水要求*/
	private BigDecimal flowRequire;
	/**当前积分*/
	private Integer points;
	/**当前积分*/
	private PointsLevel pointsLevel;
	/**注册来源*/
	private String regSource;
	/**推荐人*/
	private String referee;
	/**上级代理*/
	private User parent;	
	/**子用户 */
	private List<User> childUsers = new ArrayList<User>();
	/**是否被禁言（1是 0否）*/
	private Integer isGag;
	/**是否允许下注（代理跟推广员不允许下注，1允许、0禁止）*/
	private Integer isBet;
	/**登录次数*/
	private Integer loginNum;
	/**上一次登录时间*/
	private Date loginLastDate;
	/**当前登陆时间*/
	private Date loginDate;
	/**登陆Token*/
	private String loginToken;
	/**微信OpenId*/
	private String wechatOpenId;
	/**QQOpenId*/
	private String qqOpenId;
	/**站点*/
	private Station station;
	/**微信号*/
	private String wechat;
	
	
	@Id
	@Column(name="id",columnDefinition="int(10) COMMENT '主键'")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Version
	@Column(name="version",columnDefinition="int(10) COMMENT '乐观锁'")
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	@Column(name="create_time",columnDefinition="datetime COMMENT '注册时间'")
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
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="parent_id",columnDefinition="int(10) COMMENT '上级ID'")
	public User getParent() {
		return parent;
	}
	public void setParent(User parent) {
		this.parent = parent;
	}
	@OneToMany(cascade = { CascadeType.REFRESH, CascadeType.REMOVE,
	CascadeType.PERSIST }, mappedBy = "parent", fetch = FetchType.LAZY)//fetch = FetchType.EAGER 
	public List<User> getChildUsers() {
		return childUsers;
	}
	public void setChildUsers(List<User> childUsers) {
		this.childUsers = childUsers;
	}
	@Column(name="login_last_date",columnDefinition="datetime COMMENT '上一次登陆时间'")
	public Date getLoginLastDate() {
		return loginLastDate;
	}
	public void setLoginLastDate(Date loginLastDate) {
		this.loginLastDate = loginLastDate;
	}
	@Column(name="login_num",columnDefinition="int(10) COMMENT '登陆次数'")
	public Integer getLoginNum() {
		return loginNum;
	}
	public void setLoginNum(Integer loginNum) {
		this.loginNum = loginNum;
	}
	@Column(name="login_date",columnDefinition="datetime COMMENT '当前登陆时间'") 
	public Date getLoginDate() {
		return loginDate;
	}
	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}
	@Column(precision=19,scale=2,name="balance",columnDefinition="decimal(19,2) COMMENT '余额'")
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	@Column(name="user_type",columnDefinition="int(10) COMMENT '玩家类型(0游客、1玩家、2代理、3推广员、4虚拟号)'")
	public Integer getUserType() {
		return userType;
	}
	public void setUserType(Integer userType) {
		this.userType = userType;
	}
	@Column(name="nick_name",columnDefinition="varchar(50) COMMENT '玩家昵称'")
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	@Column(name="head_img",columnDefinition="varchar(50) COMMENT '头像地址'")
	public String getHeadImg() {
		return headImg;
	}
	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}
	@Column(name="is_gag",columnDefinition="int(10) COMMENT '是否被禁言（1是 0否）'")
	public Integer getIsGag() {
		return isGag;
	}
	public void setIsGag(Integer isGag) {
		this.isGag = isGag;
	}
	@Column(name="username",columnDefinition="varchar(50) COMMENT '登陆账号'")
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Column(name="phone",columnDefinition="varchar(50) COMMENT '手机号码'")
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	@Column(name="password",columnDefinition="varchar(50) COMMENT '登陆密码'")
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Column(name="pay_password",columnDefinition="varchar(50) COMMENT '交易密码'")
	public String getPayPassword() {
		return payPassword;
	}
	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
	}
	@Column(precision=19,scale=2,name="lottery_balance",columnDefinition="decimal(19,2) COMMENT '彩金'")
	public BigDecimal getLotteryBalance() {
		return lotteryBalance;
	}
	public void setLotteryBalance(BigDecimal lotteryBalance) {
		this.lotteryBalance = lotteryBalance;
	}
	@Column(precision=19,scale=2,name="freeze_balance",columnDefinition="decimal(19,2) COMMENT '冻结金额'")
	public BigDecimal getFreezeBalance() {
		return freezeBalance;
	}
	public void setFreezeBalance(BigDecimal freezeBalance) {
		this.freezeBalance = freezeBalance;
	}
	@Column(name="login_token",columnDefinition="varchar(50) COMMENT '登陆Token'")
	public String getLoginToken() {
		return loginToken;
	}
	public void setLoginToken(String loginToken) {
		this.loginToken = loginToken;
	}
	@Column(name="reg_source",columnDefinition="varchar(50) COMMENT '注册来源'")
	public String getRegSource() {
		return regSource;
	}
	public void setRegSource(String regSource) {
		this.regSource = regSource;
	}
	@Column(name="is_bet",columnDefinition="int(10) COMMENT '是否允许下注（代理跟推广员不允许下注，1允许、0禁止）'")
	
	public Integer getIsBet() {
		return isBet;
	}
	public void setIsBet(Integer isBet) {
		this.isBet = isBet;
	}
	@Column(name="wechat_open_id",columnDefinition="varchar(50) COMMENT '微信OpenId'")
	public String getWechatOpenId() {
		return wechatOpenId;
	}
	public void setWechatOpenId(String wechatOpenId) {
		this.wechatOpenId = wechatOpenId;
	}
	@Column(name="qq_open_id",columnDefinition="varchar(50) COMMENT 'QQOpenId'")
	public String getQqOpenId() {
		return qqOpenId;
	}
	public void setQqOpenId(String qqOpenId) {
		this.qqOpenId = qqOpenId;
	}
	@JoinColumn(name="points",columnDefinition="int(10) COMMENT '积分'")
	public Integer getPoints() {
		return points;
	}
	public void setPoints(Integer points) {
		this.points = points;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="points_level_id",columnDefinition="int(10) COMMENT '积分等级ID'")
	public PointsLevel getPointsLevel() {
		return pointsLevel;
	}
	public void setPointsLevel(PointsLevel pointsLevel) {
		this.pointsLevel = pointsLevel;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="station_id",columnDefinition="int(10) COMMENT '站点ID'")	
	public Station getStation() {
		return station;
	}
	public void setStation(Station station) {
		this.station = station;
	}
	@Column(precision=19,scale=2,name="flow_require",columnDefinition="decimal(19,2) COMMENT '流水要求'")
	public BigDecimal getFlowRequire() {
		return flowRequire;
	}
	public void setFlowRequire(BigDecimal flowRequire) {
		this.flowRequire = flowRequire;
	}
	@Column(name="referee",columnDefinition="varchar(50) COMMENT '推荐人'")
	public String getReferee() {
		return referee;
	}
	public void setReferee(String referee) {
		this.referee = referee;
	}
	@Column(name="wechat",columnDefinition="varchar(50) COMMENT '微信号'")
	public String getWechat() {
		return wechat;
	}
	public void setWechat(String wechat) {
		this.wechat = wechat;
	}
	
}