package com.rw.fsutil.remote;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class RemoteMessageDecoderWrap<ReceiveMessage> extends ByteToMessageDecoder {

	private final RemoteMessageDecoder<ReceiveMessage> decoder;

	public RemoteMessageDecoderWrap(RemoteMessageDecoder<ReceiveMessage> decoder) {
		super();
		this.decoder = decoder;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		this.decoder.decode(ctx, in, out);
	}

}
