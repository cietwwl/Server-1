package com.rw.fsutil.remote.parse;

public interface FSMessageExecutor<ReceiveMessage> {

	/**
	 * 处理接收到的消息逻辑
	 * @param message
	 */
	public void execute(ReceiveMessage message);
	
}
