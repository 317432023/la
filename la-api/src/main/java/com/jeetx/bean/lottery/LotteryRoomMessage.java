package com.jeetx.bean.lottery;

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
@Table(name="tb_lottery_room_message")
public class LotteryRoomMessage implements Serializable {

	private static final long serialVersionUID = -6661460923010241475L;
	
	private Integer id;
	private LotteryRoom lotteryRoom; //房间ID
	private String sender;//发送人
	private Date createTime;//发送时间
	private String msgBody;//消息内容文本
	private Integer messageType;//消息类型(1投注消息、2聊天消息、3广播消息)
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name="create_time",columnDefinition="datetime COMMENT '信息发送时间'")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Column(name="msg_body",columnDefinition="varchar(50) COMMENT '消息内容'")
	public String getMsgBody() {
		return msgBody;
	}
	public void setMsgBody(String msgBody) {
		this.msgBody = msgBody;
	}
	@ManyToOne(cascade={CascadeType.REFRESH}, optional=true)
	@JoinColumn(name="lottery_room_id",columnDefinition="int(10) COMMENT '彩票房间ID'")
	public LotteryRoom getLotteryRoom() {
		return lotteryRoom;
	}
	public void setLotteryRoom(LotteryRoom lotteryRoom) {
		this.lotteryRoom = lotteryRoom;
	}
	@Column(name="sender",columnDefinition="varchar(50) COMMENT '发送者'")
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	@Column(name="message_type",columnDefinition="int(10) COMMENT '消息类型(1投注消息、2聊天消息、3广播消息)'")
	public Integer getMessageType() {
		return messageType;
	}
	public void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}	
}
