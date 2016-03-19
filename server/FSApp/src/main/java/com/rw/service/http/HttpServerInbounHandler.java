package com.rw.service.http;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;

public class HttpServerInbounHandler extends ChannelInboundHandlerAdapter {
	private HttpRequest request;

	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		try {
			if (msg instanceof HttpRequest) {
				request = (HttpRequest) msg;
			}
			if (msg instanceof HttpContent) {
				HttpContent content = (HttpContent) msg;
				ByteBuf buf = content.content();
				byte[] bytes = new byte[buf.readableBytes()];
				buf.readBytes(bytes);
				ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
				ObjectInputStream input = new ObjectInputStream(bi);
				Object readObject = input.readObject();
				GSResponseMgr.processMsg(request, readObject, ctx);
			}
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}
}
