package com.jeetx.common.rabbitmq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jeetx.common.constant.Globals;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.Queue;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class RabbitMQClientUtil {
	private static final Logger log = Logger.getLogger(RabbitMQClientUtil.class);
//	private RabbitMQClientUtil rabbitMQClientUtil;
	private ConnectionFactory rabbitConnFactory = null;
	private Connection rabbitConn = null;
	private Channel rabbitChannel = null;

	public RabbitMQClientUtil(String rabbitIp,String rabbitUserName,String rabbitPassword) {
		rabbitConnFactory = new ConnectionFactory();
		initMQConfig(rabbitIp,rabbitUserName,rabbitPassword);// 初始化设置rabbitmq配置
	}

//	private void initRabbitMQUtil(String rabbitIp,String rabbitUserName,String rabbitPassword) {
//		if (rabbitMQClientUtil == null) {
//			rabbitMQClientUtil = new RabbitMQClientUtil(rabbitIp,rabbitUserName,rabbitPassword);
//		}
//	}
//
//	/**
//	 * 【单例入口】
//	 * @param rabbitIp
//	 * @param rabbitUserName
//	 * @param rabbitPassword
//	 * @return
//	 */
//	public RabbitMQClientUtil getInstance(String rabbitIp,String rabbitUserName,String rabbitPassword) {
//		if (rabbitMQClientUtil == null) {
//			initRabbitMQUtil(rabbitIp,rabbitUserName,rabbitPassword);
//		}
//		return rabbitMQClientUtil;
//	}

	/**
	 * 【Rabbitmq连接配置】
	 * @param rabbitIp
	 * @param rabbitUserName
	 * @param rabbitPassword
	 */
	protected void initMQConfig(String rabbitIp,String rabbitUserName,String rabbitPassword) {
//		log.info("初始化RabbitMQ配置文件信息..........");
//		log.info("RABBIT_MQ_IP：" + rabbitIp);
//		log.info("RABBIT_MQ_PORT：" + 5672);
//		log.info("RABBIT_USERNAME：" + rabbitUserName);
//		log.info("RABBIT_PASSWORD：" + rabbitPassword);
		rabbitConnFactory.setHost(rabbitIp);
		rabbitConnFactory.setPort(5672);
		rabbitConnFactory.setUsername(rabbitUserName);
		rabbitConnFactory.setPassword(rabbitPassword);
		rabbitConnFactory.setAutomaticRecoveryEnabled(true);//设置网络异常重连
		rabbitConnFactory.setRequestedHeartbeat(5);
		rabbitConnFactory.setNetworkRecoveryInterval(6000);
//		log.info("初始化完成：" + rabbitConnFactory);
	}
	
	/**
	 * 【声明fanout类型交换机】
	 * @param exchange
	 * @param queueName
	 * @return
	 */
	public boolean createExchange(String exchange) {
		try {
			rabbitConn = rabbitConnFactory.newConnection();//获取连接
			rabbitChannel = rabbitConn.createChannel();//创建通道

			rabbitChannel.exchangeDeclare(exchange, "fanout", true, false, null);//声明exchange,路由模式声明direct
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("声明fanout类型交换机错误：" + e.getMessage(), e);
		}finally {
			try {
				rabbitChannel.close();
				rabbitConn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * 【fanout类型交换机绑定队列】
	 * @param exchange
	 * @param queueName
	 * @return
	 */
	public boolean bindQueue(String exchange, String queueName) {
		try {
			rabbitConn = rabbitConnFactory.newConnection();//获取连接
			rabbitChannel = rabbitConn.createChannel();//创建通道

			rabbitChannel.queueDelete(queueName);
			rabbitChannel.exchangeDeclare(exchange, "fanout", true, false, null);//声明exchange,路由模式声明direct
			
			Map<String, Object> arguments = new HashMap<String, Object>();
			arguments.put("x-message-ttl", 30000);//message在该queue的存过时间最大为30秒
			arguments.put("x-expires", 600000);//queue如果在10分钟只能未被使用则会被删除
			rabbitChannel.queueDeclare(queueName, true, false, true, arguments);
			rabbitChannel.queueBind(queueName, exchange, "");
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("交换机绑定队列错误：" + e.getMessage(), e);
		}finally {
			try {
				rabbitChannel.close();
				rabbitConn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * 发布信息【fanout模式】消息发送到交换机exchange上，exchange发到相应已绑定关系的Queue上
	 * @param exchange 交换器名称
	 * @param msg 发送内容
	 * @param priority 优先级【优先级,最大10,最小为0,数值越大优先级越高】
	 * @return 
	 */
	public boolean sendMsg(String exchange, String msg, int priority) {
		try {
			rabbitConn = rabbitConnFactory.newConnection();//获取连接
			rabbitChannel = rabbitConn.createChannel();//创建通道
			BasicProperties.Builder properties = new BasicProperties.Builder();//优先级
			properties.priority(priority);//设置优先级

			rabbitChannel.exchangeDeclare(exchange, "fanout", true, false, null);//声明exchange,路由模式声明direct
			rabbitChannel.basicPublish(exchange, "", properties.build(), msg.getBytes("UTF-8"));
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("发布信息【fanout模式】错误：" + e.getMessage(), e);
		}finally {
			try {
				rabbitChannel.close();
				rabbitConn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 发布信息【direct模式】：根据指定的路由键发送到对应的消息队列中
	 * @param exchange 交换器名称
	 * @param queueName 队列名称
	 * @param msg 发送内容
	 * @param priority 优先级【优先级,最大10,最小为0,数值越大优先级越高】
	 * @return 
	 */
	public boolean sendMsg(String exchange, String queueName, String msg, int priority) {
		try {
			rabbitConn = rabbitConnFactory.newConnection();//获取连接
			rabbitChannel = rabbitConn.createChannel();//创建通道
			BasicProperties.Builder properties = new BasicProperties.Builder();//优先级
			properties.priority(priority);//设置优先级

			rabbitChannel.exchangeDeclare(exchange, "direct", true, false, null);//声明exchange,路由模式声明direct
			
			/**
			* 声明（创建）队列
			* 参数1：队列名称
			* 参数2：为true时server重启队列不会消失
			* 参数3：队列是否是独占的，如果为true只能被一个connection使用，其他连接建立时会抛出异常
			* 参数4：队列不再使用时是否自动删除（没有连接，并且没有未处理的消息)
			* 参数5：建立队列时的其他参数
			*/
			Map<String, Object> arguments = new HashMap<String, Object>();
			arguments.put("x-message-ttl", 30000);//message在该queue的存过时间最大为30秒
			arguments.put("x-expires", 600000);//queue如果在10分钟只能未被使用则会被删除
			
			rabbitChannel.queueDeclare(queueName, true, false, false, arguments);
			rabbitChannel.queueBind(queueName, exchange, "");
			rabbitChannel.basicPublish("", queueName, properties.build(), msg.getBytes("UTF-8"));
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("发布信息【direct模式】错误：" + e.getMessage(), e);
		}finally {
			try {
				rabbitChannel.close();
				rabbitConn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 【消费者批量消费】
	 * @param queueName 队列名称
	 * @param listObject
	 * @param limit
	 * @return
	 */
	public List<Object> consumeQueue(String queueName, List<Object> listObject, int limit) {
		try {
			if (listObject == null) {
				listObject = new ArrayList<Object>();
			}
			rabbitConn = rabbitConnFactory.newConnection();
			rabbitChannel = rabbitConn.createChannel();
			Map<String, Object> priority = new HashMap<String, Object>();
			priority.put("x-max-priority", 10);
			priority.put("x-message-ttl", 30000);//message在该queue的存过时间最大为30秒
			priority.put("x-expires", 600000);//queue如果在10分钟只能未被使用则会被删除
			rabbitChannel.queueDeclare(queueName, true, false, true, priority);
			rabbitChannel.basicQos(1);// 同一时间取1条
			QueueingConsumer consumer = new QueueingConsumer(rabbitChannel);
			rabbitChannel.basicConsume(queueName, false, consumer);
			for (int i = 0; i < limit; i++) {
				QueueingConsumer.Delivery delivery = consumer.nextDelivery();
				String message = new String (delivery.getBody());
				if (message != null) {
					rabbitChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
				}
				log.info(message);
				listObject.add(message);
			}
			return listObject;
		} catch (Exception e) {
			log.info("批量消费错误：" + e.getMessage(), e);
		}finally {
			try {
				rabbitChannel.close();
				rabbitConn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 【消费者单个消费】
	 * @param queueName
	 * @return
	 */
	public Object consumeQueue(String queueName) {
		try {
			rabbitConn = rabbitConnFactory.newConnection();
			rabbitChannel = rabbitConn.createChannel();
			
			QueueingConsumer consumer = new QueueingConsumer(rabbitChannel);
			rabbitChannel.basicConsume(queueName, false, consumer);
			while (true) {
				QueueingConsumer.Delivery delivery = consumer.nextDelivery();
				String message = new String (delivery.getBody());
				if (message != null) {
					rabbitChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
				}
				log.info(message);
				//return message;
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("单个消费错误：" + e.getMessage(), e);
		}finally {
			try {
				rabbitChannel.close();
				rabbitConn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 【获取队列内容数量】
	 * @param queueName
	 * @return
	 */
	public int getQueueMessageCount(String queueName) {
		try {
			rabbitConn = rabbitConnFactory.newConnection();
			rabbitChannel = rabbitConn.createChannel();
			Queue.DeclareOk declareOk = rabbitChannel.queueDeclarePassive(queueName);
			if (declareOk != null) {
				int msgCount = declareOk.getMessageCount();
				return msgCount;
			}
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				rabbitChannel.close();
				rabbitConn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	/**
	 * 【删除队列】
	 * @param queueName 队列名称
	 * @return
	 */
	public boolean deleteQueue(String queueName) {
		try {
			rabbitConn = rabbitConnFactory.newConnection();
			rabbitChannel = rabbitConn.createChannel();
			rabbitChannel.queueDelete(queueName);
			//Queue.DeleteOk deleteOk = rabbitChannel.queueDelete(queueName);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				rabbitChannel.close();
				rabbitConn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 【清空队列】
	 * @param queueName 队列名称
	 * @return
	 */
	public boolean clearQueueData(String queueName) {
		try {
			rabbitConn = rabbitConnFactory.newConnection();
			rabbitChannel = rabbitConn.createChannel();
			rabbitChannel.queuePurge(queueName);
			//Queue.PurgeOk purgeOK = rabbitChannel.queuePurge(queueName);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				rabbitChannel.close();
				rabbitConn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

//	/*** 关闭通道及连接*/
//	public void close() {
//		try {
//			rabbitChannel.close();
//			rabbitConn.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	/*** 关闭通道*/
//	public void closeChannel() {
//		try {
//			rabbitConn.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	/*** 关闭连接*/
//	public void closeConn() {
//		try {
//			rabbitConn.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public static void main(String[] args) {
		//String exchange1 = "exchange.lottery.room.1-1";
//		String exchange2 = "exchange.lottery.room.2-13";
//		//String queueName = "exchange.lottery.user.2-1";
//		
//		RabbitMQClientUtil rabbitUtil = new RabbitMQClientUtil("47.244.150.71","admin","admin");
//		for (int i = 0; i < 500; i++) {
//			//rabbitUtil.sendMsg(exchange1, "1发送信息"+i, 10);
//			rabbitUtil.sendMsg(exchange2, "2发送信息"+i, 10);
//		}
//		rabbitUtil.bindQueue(exchange, queueName);
//		
//		//rabbitUtil.consumeQueue(queueName,null,20);
//		rabbitUtil.consumeQueue(queueName);
//		
		
//		String exchange = "exchangeTest";
//		rabbitUtil.createExchange(exchange);
//		rabbitUtil.bindQueue(exchange, "queueName1");
//		rabbitUtil.bindQueue(exchange, "queueName2");
//		rabbitUtil.bindQueue(exchange, "queueName3");
//		
//		for (int i = 1; i <= 20; i++) {
//			rabbitUtil.sendMsg(exchange, "我是测试数据"+ new Date().getTime(), 10);
//		}
		//rabbitUtil.clearQueueData(Globals.RABBIT_QUEUE_USER.concat("17-12"));
		//rabbitUtil.consumeQueue(Globals.RABBIT_QUEUE_USER.concat("17-12"),null,1000);
		
		//rabbitUtil.sendMsg("exchangeTest2019","queueTest2019", "我是测试数据"+ new Date().getTime(), 10);
		//rabbitUtil.sendMsg("exchangeTest1", "我是测试数据"+ new Date().getTime(), 10);
		
//		for (int i = 1; i <= 20; i++) {
//			//rabbitUtil.sendMsg("exchangeTest1","queueTest2", "我是测试数据"+ i, 10);
//		}

//		rabbitUtil.consumeQueue("queueTest2019",null,20);
//		rabbitUtil.consumeQueue("queueTest2");
		
//		for (int i = 1; i <= 33; i++) {
//			for(int j = 1; j <= 123; j++) {
//				rabbitUtil.deleteQueue("exchange.lottery.room."+i+"-"+j);
//			}
//		}
		
		//rabbitUtil.deleteQueue("queueTest1");
		//rabbitUtil.clearQueueData("queueTest2019");
		//System.out.println(rabbitUtil.getQueueMessageCount("exchange.lottery.user.14-12"));
		
		
//		//创建队列
//		String exchangeName = Globals.RABBIT_EXCHANGE_ROOM.concat("2").concat("-").concat("13");
//		String queueName = Globals.RABBIT_QUEUE_USER.concat("13").concat("-").concat("254");
//		//rabbitUtil.deleteQueue(queueName);
//		//rabbitUtil.bindQueue(exchangeName, queueName);
//		
//		for (int i = 0; i < 50000; i++) {
//			System.out.println("1发送信息"+i);
//			rabbitUtil.sendMsg(exchangeName, "1发送信息"+i, 10);
//		}
		
//		rabbitUtil.consumeQueue("exchange.lottery.user.14-34",null,200);
		
		//rabbitUtil.close();

		System.out.println("=============");
		for (int i = 0; i < 10; i++) {
			new Thread(){
				public void run(){
					RabbitMQClientUtil rabbitUtil = new RabbitMQClientUtil("47.244.150.71","admin","admin");
					
					String exchangeName = Globals.RABBIT_EXCHANGE_ROOM.concat("2").concat("-").concat("13");
					String queueName = Globals.RABBIT_QUEUE_USER.concat("13").concat("-").concat("254");

					rabbitUtil.deleteQueue(queueName);
					rabbitUtil.bindQueue(exchangeName, queueName);
					for (int j = 0; j < 50000; j++) {
						System.out.println(Thread.currentThread().getId()+"发送信息"+j);
						rabbitUtil.sendMsg(exchangeName, Thread.currentThread().getId()+"发送信息"+j, 10);
					}
				}
			}.start();
		}
	}
}
