package com.rw.fsutil.remote.parse;

/**
 * 发送消息编码器
 * @author Jamaz
 *
 * @param <SendMessage>
 */
public interface FSMessageEncoder<SendMessage> {

	public byte[] encode(SendMessage msg);

}
