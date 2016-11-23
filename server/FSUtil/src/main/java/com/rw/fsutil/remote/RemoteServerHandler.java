package com.rw.fsutil.remote;

import com.rw.fsutil.cacheDao.FSUtilLogger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class RemoteServerHandler<SendMessage, ReceiveMessage> extends ChannelInboundHandlerAdapter {

	private final RemoteMessageService<SendMessage, ReceiveMessage> server;

	public RemoteServerHandler(RemoteMessageService<SendMessage, ReceiveMessage> server) {
		this.server = server;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		this.server.getExecutor().execute((ReceiveMessage) msg);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		try {
			server.add(ctx);
		} finally {
			ctx.fireChannelActive();
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		try {
			server.remove(ctx);
		} finally {
			ctx.fireChannelInactive();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
