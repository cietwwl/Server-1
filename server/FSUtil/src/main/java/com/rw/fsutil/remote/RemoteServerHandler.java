package com.rw.fsutil.remote;

import java.util.Arrays;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class RemoteServerHandler extends ChannelInboundHandlerAdapter {

	private final RemoteChannelServer server;

	public RemoteServerHandler(RemoteChannelServer server) {
		this.server = server;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("收到消息：" + msg + Arrays.toString(((String) msg).getBytes()));
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
