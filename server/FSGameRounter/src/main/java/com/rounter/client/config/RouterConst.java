package com.rounter.client.config;

public class RouterConst {
	
	public final static int MAX_CHANNEL_COUNT = 1;	// 处理的连接数
	public final static int MAX_THREAD_COUNT = 16;	// 处理的线程数
	
	public final static long MAX_OVER_TIME = 1000L;	// 单次最大超时毫秒数
	public final static byte CHECK_CHANNEL_SAMPLE_COUNT = 4;  //检查连接反应时间的样本数
	public final static int NODE_MAX_QUEUE_SIZE = 100;	// node中等待服务端返回的最大等待数
	public final static int NODE_MAX_BUSY_TIMES = 1; // 节点最大允许繁忙次数（超过需要重连）
	public final static long RECONNECT_DISTANCE = 1L * 60 * 1000;	//重连间隔(同时也是测试链接心跳的间隔时间)
}