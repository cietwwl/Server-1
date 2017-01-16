package com.rounter.client.sender.config;

public class LogConst {
	public final static String TARGET_ADDR = "localhost";
	public final static int TARGET_PORT = 8844;
	public final static String TARGET_MQ_ADDR_AND_PORT = "localhost:9876";
	
	public final static int MAX_CHANNEL_COUNT = 20;	// 处理的连接数
	public final static int MAX_THREAD_COUNT = 8;	// 处理的线程数
	
	public final static long MAX_OVER_TIME = 1000L;	// 单次最大超时毫秒数
	public final static byte CHECK_CHANNEL_SAMPLE_COUNT = 4;  //检查连接反应时间的样本数
	public final static int NODE_MAX_QUEUE_SIZE = 100;	// node中等待服务端返回的最大等待数
	public final static int NODE_MAX_BUSY_TIMES = 5; // 节点最大允许繁忙次数（超过需要重连）
	public final static long RECONNECT_DISTANCE = 1L * 60 * 1000;	//重连间隔(同时也是测试链接心跳的间隔时间)
	
	public final static int WRITE_DB_ONCE = 1000; // 每次写入数据库的失败日志数量
	
	/**
	 * 从mq中查询消息的有效期
	 */
	public final static long VIEW_MQ_MAX_PERIOD = MAX_OVER_TIME * CHECK_CHANNEL_SAMPLE_COUNT * 4;
	/**
	 * 应用程序自动重新向数据中心发送失败消息的有效期
	 * 超过有效期,由mq的消费者控制重发和错误记录
	 */
	public final static long UNRESEND_MQ_MAX_PERIOD = MAX_OVER_TIME * CHECK_CHANNEL_SAMPLE_COUNT * 5;
	public final static String TOPIC = "PushTopic";
	public final static String SUBEXPRESSION = "push";
	public final static int DEFAULT_DELAY_TIME_LEVEL = 2;
	public final static int MAX_CONSUME_TIMES = 200;
	public final static int CONSUME_MESSAGE_BATCH_MAX_SIZE = 50; //单次MQ消费数量
	
	
	public final static long FAIL_LOG_FILE_SIZE = 1024 * 1024 * 128;
	public final static long MAX_ONE_LOG_SIZE = 512;
	public final static long MAPPED_SIZE = 1024 * 1024;
	public final static String LOG_DIR_PATH = "log/dataCenterLog/";
}