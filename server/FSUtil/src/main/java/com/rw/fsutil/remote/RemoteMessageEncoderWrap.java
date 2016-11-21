package com.rw.fsutil.remote;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RemoteMessageEncoderWrap<SendMessage> extends MessageToByteEncoder<Object> {

	private final RemoteMessageEncoder<SendMessage> encoder;

	public RemoteMessageEncoderWrap(RemoteMessageEncoder<SendMessage> encoder) {
		super();
		this.encoder = encoder;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		encoder.encode(ctx, msg, out);
	}

}
