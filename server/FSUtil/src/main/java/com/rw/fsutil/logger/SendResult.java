package com.rw.fsutil.logger;

public enum SendResult {

	/**
	 * 发送成功
	 */
	SUCCESS("ok"),
	/**
	 * socket不可用
	 */
	SOCKET_NOT_AVAILABLE("not connect"),
	/**
	 * 接收消息超时
	 */
	SOCKET_TIME_OUT("tmieout"),
	/**
	 * reponse收到的不是ok
	 */
	RESPONSE_NOT_OK("send fail")

	;
	private final String desc;

	private SendResult(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}
}
