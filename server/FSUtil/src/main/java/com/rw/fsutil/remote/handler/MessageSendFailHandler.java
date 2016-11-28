package com.rw.fsutil.remote.handler;

public interface MessageSendFailHandler<SendMessage> {

	/**
	 * 处理发送失败的消息
	 * @param message
	 */
	public void handle(SendMessage message);

}
