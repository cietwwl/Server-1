package com.rw.fsutil.remote.parse;

/**
 * <pre>
 * 消息接收处理器
 * 在消息io线程中处理，需避免耗时操作
 * </pre>
 * @author Jaamz
 *
 * @param <ReceiveMessage>
 */
public interface FSMessageExecutor<ReceiveMessage> {

	/**
	 * 处理接收到的消息逻辑
	 * @param message
	 */
	public void execute(ReceiveMessage message);
	
}
