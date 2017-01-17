package com.rw.gameRouterServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import com.rw.fsutil.util.jackson.JsonUtil;
import com.rw.gameRouterServer.data.ReqestObject;

public class RouterInboundHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("channelRead:" + msg);
		ReqestObject reqParam = JsonUtil.readValue((String)msg, ReqestObject.class);
		String result = "{}";
		switch (reqParam.getType()) {
		case GetGift:
			result = RouterServiceHandler.getInstance().getGift(reqParam.getContent());
			break;
		default:
			break;
		}
		ByteBuf buf = Unpooled.copiedBuffer(((String)msg + System.getProperty("line.separator")).getBytes("UTF-8"));
        ctx.writeAndFlush(buf);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channelReadComplete");
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		System.out.println("exceptionCaught");
		super.exceptionCaught(ctx, cause);
	}
}
