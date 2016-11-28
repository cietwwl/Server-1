package com.rw.fsutil.remote;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class RemoteServerHandler<SendMessage, ReceiveMessage> extends ChannelInboundHandlerAdapter {

	private final RemoteServiceSender<SendMessage,ReceiveMessage> sender;

	public RemoteServerHandler(RemoteServiceSender<SendMessage,ReceiveMessage> sender) {
		this.sender = sender;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		this.sender.channelRead(ctx, msg);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		this.sender.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		this.sender.channelInactive(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
