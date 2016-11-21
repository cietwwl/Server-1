package com.rw.fsutil.remote;

import com.rw.fsutil.remote.parse.FSMessageEncoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RemoteMessageEncoder<SendMessage> extends MessageToByteEncoder<Object> {

	private final FSMessageEncoder<SendMessage> encoder;

	public RemoteMessageEncoder(FSMessageEncoder<SendMessage> encoder) {
		super();
		this.encoder = encoder;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		byte[] array = encoder.encode((SendMessage)msg);
		out.writeBytes(array);
	}

}
