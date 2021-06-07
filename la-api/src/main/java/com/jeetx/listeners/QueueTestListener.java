package com.jeetx.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.jeetx.bean.member.User;
import com.rabbitmq.client.Channel;

/**
 * 活动处理listener http://47.99.214.237:15672/#/
 * 
 * @author
 * @date 2017年6月30日
 **/
@Component
public class QueueTestListener implements ChannelAwareMessageListener {
	private static final Logger log = LoggerFactory.getLogger(QueueTestListener.class);

	@Override
	@Transactional
	public void onMessage(Message message, Channel channel) {
//		Gson gson = new Gson();
		log.info("------> DirectConsumer 接收到的消息为 : " + message);
//		try {
//			log.info("===endDat====" + new String(message.getBody(), "UTF-8"));
//			String data = new String(message.getBody(), "UTF-8");
//			User user = gson.fromJson(data, User.class);
//			log.info("===endDat====" + user.getAge() + "=====" + user.getName());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
