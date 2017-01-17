package com.rounter.client.config;

public enum NodeState {
	/**
	 * 初始等待连接
	 */
	Ready,
	
	/**
	 * 正常状态
	 */
	Normal,
	
	/**
	 * 繁忙
	 */
	Busy,
	
	/**
	 * 连接中
	 */
	Connecting,
	
	/**
	 * 连接失败
	 */
	ConnFail,
	
	/**
	 * 断开连接
	 */
	DisConn,
	
	/**
	 * 正在操作未成功的队列
	 */
	HandleFailedQueue,
	
	/**
	 * 连接出现异常
	 */
	Exception,
	
	/**
	 * 连接非活跃状态
	 */
	InActive,
	
	/**
	 * 结束状态，还没开始新的连接
	 * 一般在连接失败时，队列处理完毕之后的状态
	 */
	Over
}
