package com.rw.fsutil.remote.parse;

/**
 * 接收消息解码器
 * @author Jamaz
 *
 * @param <ReceiveMessage>
 */
public interface FSMessageDecoder<ReceiveMessage> {

	/**
	 * 获取前置处理器
	 * @return
	 */
	public FSMessagePrefix getPrefix();

	/**
	 * 把byte[]转换成消息对象
	 * @param array
	 * @return
	 */
	public ReceiveMessage convertToMessage(byte[] array);

}
