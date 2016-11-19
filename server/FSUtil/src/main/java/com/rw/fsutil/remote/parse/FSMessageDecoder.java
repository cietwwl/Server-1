package com.rw.fsutil.remote.parse;

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
