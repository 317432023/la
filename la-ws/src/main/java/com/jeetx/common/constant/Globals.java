package com.jeetx.common.constant;

public class Globals {
	public static final int LIMIT = 10;//每页显示记录数
	public static final String SYSENTIEY_PRIX = "TB_";//表前缀
	
	//=============REIDS参数配置=============
	public static final String REIDS_TOKEN_PREFIX = "TOKEN"; // 令牌前缀键
	public static final String REIDS_CONFIG_MAP = "CONFIG";// 配置表存储键
	
	//=============Rabbit参数配置=============
	public static final String RABBIT_EXCHANGE_ROOM = "exchange.lottery.room."; //交换机（房间）前缀
	public static final String RABBIT_QUEUE_USER = "exchange.lottery.user."; //队列（用户）前缀
	
	public static final Integer GLOBALS_STATION_ID = 1;
}
