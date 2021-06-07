package com.jeetx.webSocket;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.GsonBuilder;
import com.jeetx.bean.lottery.LotteryRoom;
import com.jeetx.bean.member.User;
import com.jeetx.common.constant.Globals;
import com.jeetx.common.exception.BusinessException;
import com.jeetx.service.lottery.LotteryRoomService;
import com.jeetx.service.member.UserService;
import com.jeetx.util.LogUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

@Component
public class MsgScoketHandle implements WebSocketHandler {
	
	@Autowired UserService userService;
	@Autowired LotteryRoomService lotteryRoomService;
	
	@Value("${rabbitHostname}")
	private String rabbitHostname;
	
	@Value("${rabbitUsername}")
	private String rabbitUsername;
	
	@Value("${rabbitPassword}")
	private String rabbitPassword;
	
	/** 已经连接的用户 */
	private static final Map<String,WebSocketSession> userTokens;
	static {
		userTokens = new java.util.concurrent.ConcurrentHashMap<String,WebSocketSession>();
	}

	/**
	 * 建立链接
	 * 
	 * @param webSocketSession
	 * @throws Exception
	 */
	@Override
	public void afterConnectionEstablished(final WebSocketSession webSocketSession) throws Exception {
		String token = (String)webSocketSession.getAttributes().get("token");
		String roomId = (String)webSocketSession.getAttributes().get("roomId");
		//System.out.println("webSocket握手成功：" + token);
		
		User user = userService.findUserByToken(token);
		if(user == null || user.getStatus()!=1) {
			throw new BusinessException("认证失败1，webSocket创建连接失败");
		}
		
		LotteryRoom lotteryRoom = lotteryRoomService.find(Integer.valueOf(roomId));
		if(lotteryRoom == null) {
			throw new BusinessException("认证失败2，webSocket创建连接失败");
		}
		
		String exchange = Globals.RABBIT_EXCHANGE_ROOM.concat(lotteryRoom.getLotteryType().getId().toString()).concat("-").concat(lotteryRoom.getId().toString());
		String queueName = Globals.RABBIT_QUEUE_USER.concat(lotteryRoom.getId().toString()).concat("-").concat(user.getId().toString());

		webSocketSession.getAttributes().put("userId", user.getId());
		webSocketSession.getAttributes().put("queueName", queueName);
		webSocketSession.getAttributes().put("nickName", user.getNickName());

		//===========================================================
		//0、关闭已创建的MQ连接
		if(userTokens.get(token)!=null) {
			try {
				WebSocketSession webSocketSession1 = userTokens.get(token);
				Connection rabbitConn1 = (Connection)webSocketSession1.getAttributes().get("rabbitConn");
				Channel rabbitChannel1 = (Channel)webSocketSession1.getAttributes().get("rabbitChannel");
				
				if(rabbitChannel1 !=null && rabbitChannel1.isOpen()) {
					rabbitChannel1.close();
				}
				if(rabbitConn1 !=null && rabbitConn1.isOpen()) {
					rabbitConn1.close();
				}
				
				System.out.println("webSocket重新创建：" + token + "-" + userTokens.size() + "-"+user.getNickName() + "-"+queueName);
			} catch (Exception e) {
				LogUtil.info("WS重新创建关闭MQ异常："+e.getMessage());
			}
			userTokens.remove(token);
		}
		
		
		//1、创建连接，创建通道
		ConnectionFactory rabbitConnFactory = null;
		Connection rabbitConn = null;
		try {
			rabbitConnFactory = new ConnectionFactory();
			rabbitConnFactory.setHost(rabbitHostname);
			rabbitConnFactory.setPort(5672);
			rabbitConnFactory.setUsername(rabbitUsername);
			rabbitConnFactory.setPassword(rabbitPassword);
			
//			// 关键所在，指定线程池
//			ExecutorService service = Executors.newFixedThreadPool(1000);
//			rabbitConnFactory.setSharedExecutor(service);
			
			rabbitConnFactory.setAutomaticRecoveryEnabled(true);//设置网络异常重连
			rabbitConnFactory.setRequestedHeartbeat(5);
			rabbitConnFactory.setNetworkRecoveryInterval(6000);
			
			rabbitConn = rabbitConnFactory.newConnection();//获取连接
			final Channel rabbitChannel = rabbitConn.createChannel();//创建通道
			
			webSocketSession.getAttributes().put("rabbitConn", rabbitConn);
			webSocketSession.getAttributes().put("rabbitChannel", rabbitChannel);
			webSocketSession.getAttributes().put("createTime", new Date());

			rabbitChannel.queueDelete(queueName);
			rabbitChannel.exchangeDeclare(exchange, "fanout", true, false, null);//声明exchange,路由模式声明direct
			
			Map<String, Object> arguments = new HashMap<String, Object>();
			arguments.put("x-message-ttl", 30000);//message在该queue的存过时间最大为30秒
			arguments.put("x-expires", 600000);//queue如果在10分钟只能未被使用则会被删除
			rabbitChannel.queueDeclare(queueName, true, false, true, arguments);
			rabbitChannel.queueBind(queueName, exchange, "");
			rabbitChannel.basicQos(0,1,false); //RabbitMQ客户端接受消息最大数量
			
			//2、接收MQ消息，发送ws消息
			Consumer consumer = new DefaultConsumer(rabbitChannel) {
				@Override
				public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
					super.handleShutdownSignal(consumerTag, sig);
				}

				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {
					String message = new String(body, "UTF-8");
					try {
						if(webSocketSession.isOpen()) {
							webSocketSession.sendMessage(new TextMessage(new GsonBuilder().create().toJson(message)));
							//System.out.println("webSocket接收消息：" + message);
						}
					} catch (Exception e) {
						LogUtil.info("接收MQ消息且发送WS消息时异常："+message+"-"+e.getMessage());
						e.printStackTrace();
					} finally {
						rabbitChannel.basicAck(envelope.getDeliveryTag(),false);
					}

				}
			};
			rabbitChannel.basicConsume(queueName, false, consumer);
		}catch (Exception e) {
			LogUtil.info("WS建立链接创建MQ通道异常："+e.getMessage());
			try {
				if(rabbitConn !=null && rabbitConn.isOpen()) {
					rabbitConn.close();
				}
			} catch (Exception ex) {
				LogUtil.info("WS建立链接创建MQ通道异常时关闭MQ异常："+ex.getMessage());
			}
		}
		
		userTokens.put(token,webSocketSession);// 将用户信息添加到list中
		System.out.println("webSocket创建连接：" + token + "-" + userTokens.size() + "-"+user.getNickName() + "-"+queueName);
	}

	/**
	 * 接收消息
	 * @param webSocketSession
	 * @param webSocketMessage
	 * @throws Exception
	 */
	@Override
	public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage)
			throws Exception {
	}

	/**
	 * 异常处理
	 * @param webSocketSession
	 * @param throwable
	 * @throws Exception
	 */
	@Override
	public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) {
		String token = (String)webSocketSession.getAttributes().get("token");
		
		try {
			Connection rabbitConn = (Connection)webSocketSession.getAttributes().get("rabbitConn");
			Channel rabbitChannel = (Channel)webSocketSession.getAttributes().get("rabbitChannel");
			
			if(rabbitChannel !=null && rabbitChannel.isOpen()) {
				rabbitChannel.close();
			}
			if(rabbitConn !=null && rabbitConn.isOpen()) {
				rabbitConn.close();
			}
		} catch (Exception e) {
			LogUtil.info("WS异常处理关闭MQ异常："+e.getMessage());
		}

		if(token != null && userTokens.get(token)!=null) {
			userTokens.remove(token);
		}
	}

	/**
	 * 断开链接
	 * @param webSocketSession
	 * @param closeStatus
	 * @throws Exception
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
		String token = (String)webSocketSession.getAttributes().get("token");

		try {
			Connection rabbitConn = (Connection)webSocketSession.getAttributes().get("rabbitConn");
			Channel rabbitChannel = (Channel)webSocketSession.getAttributes().get("rabbitChannel");
			
			if(rabbitChannel !=null && rabbitChannel.isOpen()) {
				rabbitChannel.close();
			}
			if(rabbitConn !=null && rabbitConn.isOpen()) {
				rabbitConn.close();
			}
		} catch (Exception e) {
			LogUtil.info("WS异常处理关闭MQ异常："+e.getMessage());
		}
		
		if(token != null && userTokens.get(token)!=null) {
			userTokens.remove(token);
		}
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}
	
	public static void main(String[] args) {
		for (int i = 0; i < 20; i++) {
			new Thread(){
				public void run(){
					System.out.println("-----------");
					String rabbitHostname = "47.244.150.71";
					String rabbitUsername = "admin";
					String rabbitPassword = "admin";
					
					String exchange = Globals.RABBIT_EXCHANGE_ROOM.concat("2").concat("-").concat("13");
					String queueName = Globals.RABBIT_QUEUE_USER.concat("13").concat("-").concat("254");
					//1、创建连接，创建通道
					ConnectionFactory rabbitConnFactory = null;
					Connection rabbitConn = null;
					try {
						rabbitConnFactory = new ConnectionFactory();
						rabbitConnFactory.setHost(rabbitHostname);
						rabbitConnFactory.setPort(5672);
						rabbitConnFactory.setUsername(rabbitUsername);
						rabbitConnFactory.setPassword(rabbitPassword);
						
//						// 关键所在，指定线程池
//						ExecutorService service = Executors.newFixedThreadPool(1000);
//						rabbitConnFactory.setSharedExecutor(service);
						
						rabbitConnFactory.setAutomaticRecoveryEnabled(true);//设置网络异常重连
						rabbitConnFactory.setRequestedHeartbeat(5);
						rabbitConnFactory.setNetworkRecoveryInterval(6000);
						
						rabbitConn = rabbitConnFactory.newConnection();//获取连接
						final Channel rabbitChannel = rabbitConn.createChannel();//创建通道

						rabbitChannel.queueDelete(queueName);
						rabbitChannel.exchangeDeclare(exchange, "fanout", true, false, null);//声明exchange,路由模式声明direct
						
						Map<String, Object> arguments = new HashMap<String, Object>();
						arguments.put("x-message-ttl", 30000);//message在该queue的存过时间最大为30秒
						arguments.put("x-expires", 600000);//queue如果在10分钟只能未被使用则会被删除
						rabbitChannel.queueDeclare(queueName, true, false, true, arguments);
						rabbitChannel.queueBind(queueName, exchange, "");
						rabbitChannel.basicQos(0,1,false); //RabbitMQ客户端接受消息最大数量
						
						//2、接收MQ消息，发送ws消息
						Consumer consumer = new DefaultConsumer(rabbitChannel) {
							@Override
							public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
								super.handleShutdownSignal(consumerTag, sig);
							}

							@Override
							public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
									byte[] body) throws IOException {
								String message = new String(body, "UTF-8");
								try {
									System.out.println(Thread.currentThread().getId()+"webSocket接收消息：" + message);
								} catch (Exception e) {
									LogUtil.info("接收MQ消息且发送WS消息时异常："+message+"-"+e.getMessage());
									e.printStackTrace();
								} finally {
									rabbitChannel.basicAck(envelope.getDeliveryTag(),false);
								}
							}
						};
						rabbitChannel.basicConsume(queueName, false, consumer);
					}catch (Exception e) {
						LogUtil.info("WS建立链接创建MQ通道异常："+e.getMessage());
						try {
							if(rabbitConn !=null && rabbitConn.isOpen()) {
								rabbitConn.close();
							}
						} catch (Exception ex) {
							LogUtil.info("WS建立链接创建MQ通道异常时关闭MQ异常："+ex.getMessage());
						}
					}
				}
			}.start();
		}
	}
}
