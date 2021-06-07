package com.jeetx.service.lottery.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jeetx.bean.lottery.LotteryPeriods;
import com.jeetx.bean.lottery.LotteryRoom;
import com.jeetx.bean.lottery.LotteryRoomMessage;
import com.jeetx.common.constant.Globals;
import com.jeetx.common.exception.BusinessException;
import com.jeetx.common.rabbitmq.RabbitMQClientUtil;
import com.jeetx.common.swagger.model.lottery.LotteryOrderItemDTO;
import com.jeetx.service.dao.DaoSupport;
import com.jeetx.service.lottery.LotteryRoomMessageService;
import com.jeetx.util.DateTimeTool;
import com.jeetx.util.JsonUtil;
import com.jeetx.util.LogUtil;
import com.jeetx.util.RandomUtil;

@Service
@Transactional
public class LotteryRoomMessageServiceImpl extends DaoSupport<LotteryRoomMessage> implements LotteryRoomMessageService {
	@Value("${countDownSecond}")
	private Integer countDownSecond;
	
	@Value("${rabbitHostname}")
	private String rabbitHostname;
	
	@Value("${rabbitUsername}")
	private String rabbitUsername;
	
	@Value("${rabbitPassword}")
	private String rabbitPassword;
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void sendBroadcastMessage(LotteryRoom lotteryRoom,LotteryPeriods lotteryPeriods,String configMessage) {
		Integer msgType = 3;//消息类型(1投注消息、2聊天消息、3广播消息)
		String sender = null;
		String userType = null;
		String headImg = null;
		String levelTitle = null;
		
		//生成消息json
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("msgId", RandomUtil.getSeqNumber("","yyyyMMddHHmmss", 4));
		result.put("roomId", lotteryRoom.getId());
		result.put("lotteryPeriod", lotteryPeriods.getLotteryPeriods());
		result.put("sender", sender);
		result.put("userType", userType);
		result.put("headImg", headImg);
		result.put("levelTitle", levelTitle);
		result.put("createTime", DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", new Date()));
		result.put("msgType", msgType);
		if(lotteryPeriods.getStatus()==3) {
			result.put("msgBody", configMessage.replace("[开奖期数]", lotteryPeriods.getLotteryPeriods())
					.replace("[时间]", DateTimeTool.dateFormat("HH:mm:ss", new Date())).replace("[开奖内容]", lotteryPeriods.getLotteryShowContent()));
		}else {
			result.put("msgBody", configMessage.replace("[开奖期数]", lotteryPeriods.getLotteryPeriods()).replace("[剩余秒数]", countDownSecond.toString()));
		}
		String msgBody = JsonUtil.array2json(result);
		//LogUtil.info("广播消息："+msgBody);
		
		LotteryRoomMessage lotteryRoomMessage = new LotteryRoomMessage();
		lotteryRoomMessage.setLotteryRoom(lotteryRoom);
		lotteryRoomMessage.setSender(sender);//发送人
		lotteryRoomMessage.setCreateTime(new Date());//发送时间
		lotteryRoomMessage.setMessageType(msgType);//消息类型(1投注消息、2聊天消息、3广播消息)
		lotteryRoomMessage.setMsgBody(msgBody);//消息内容文本
		this.save(lotteryRoomMessage);
		
		//插入MQ队列
		RabbitMQClientUtil rabbitUtil = null;
		try {
			String exchange = Globals.RABBIT_EXCHANGE_ROOM.concat(lotteryRoom.getLotteryType().getId().toString()).concat("-").concat(lotteryRoom.getId().toString());
			rabbitUtil = new RabbitMQClientUtil(rabbitHostname,rabbitUsername,rabbitPassword);
			rabbitUtil.sendMsg(exchange,msgBody, 10);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException("连接及时通讯服务器失败:"+e);
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void sendOrderMessage(LotteryRoom lotteryRoom,String sender,Integer userType,String headImg,String levelTitle,
			LotteryPeriods lotteryPeriods,List<LotteryOrderItemDTO> orderItemList,String orderCode) {
		Integer msgType = 1;//消息类型(1投注消息、2聊天消息、3广播消息)
		
		Map<String, Object> msgBodyMap = new HashMap<String, Object>();
		msgBodyMap.put("orderCode", orderCode);
		msgBodyMap.put("orderItem", orderItemList);
		
		//生成消息json
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("msgId", RandomUtil.getSeqNumber("","yyyyMMddHHmmss", 4));
		result.put("roomId", lotteryRoom.getId());
		result.put("lotteryPeriod", lotteryPeriods.getLotteryPeriods());
		result.put("sender", sender);
		result.put("userType", userType);
		result.put("headImg", headImg);
		result.put("levelTitle", levelTitle);
		result.put("createTime", DateTimeTool.dateFormat("yyyy-MM-dd HH:mm:ss", new Date()));
		result.put("msgType", msgType);
		result.put("msgBody", msgBodyMap);
		String msgBody = JsonUtil.array2json(result);
		//LogUtil.info("广播消息："+msgBody);

		LotteryRoomMessage lotteryRoomMessage = new LotteryRoomMessage();
		lotteryRoomMessage.setLotteryRoom(lotteryRoom);
		lotteryRoomMessage.setSender(sender);//发送人
		lotteryRoomMessage.setCreateTime(new Date());//发送时间
		lotteryRoomMessage.setMessageType(msgType);//消息类型(1投注消息、2聊天消息、3广播消息)
		lotteryRoomMessage.setMsgBody(msgBody);//消息内容文本
		this.save(lotteryRoomMessage);
		
		//插入MQ队列
		RabbitMQClientUtil rabbitUtil = null;
		try {
			String exchange = Globals.RABBIT_EXCHANGE_ROOM.concat(lotteryRoom.getLotteryType().getId().toString()).concat("-").concat(lotteryRoom.getId().toString());
			rabbitUtil = new RabbitMQClientUtil(rabbitHostname,rabbitUsername,rabbitPassword);
			rabbitUtil.sendMsg(exchange,msgBody, 10);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException("连接及时通讯服务器失败:"+e);
		}
	}
}
