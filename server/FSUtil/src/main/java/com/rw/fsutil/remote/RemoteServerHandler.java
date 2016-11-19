package com.rw.fsutil.remote;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class RemoteServerHandler<SendMessage, ReceiveMessage> extends ChannelInboundHandlerAdapter {

	private final RemoteChannelServer<SendMessage, ReceiveMessage> server;

	public RemoteServerHandler(RemoteChannelServer<SendMessage, ReceiveMessage> server) {
		this.server = server;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		this.server.getExecutor().execute((ReceiveMessage)msg);
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		try {
			server.add(ctx);
		} finally {
			ctx.fireChannelRegistered();
		}
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		try {
			server.remove(ctx);
		} finally {
			ctx.fireChannelUnregistered();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
