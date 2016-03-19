package com.net.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import com.rw.ChannelServer;
import com.rw.Client;
import com.rw.common.MsgLog;
import com.rwproto.ResponseProtos.Response;

/*
 * 客户端接受到消息的处理
 * @author HC
 * @date 2015年12月14日 下午2:10:23
 * @Description 
 */
public class ClientInboundHandler extends SimpleChannelInboundHandler<Object> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		Client clinet = ChannelServer.getInstance().getClient(ctx.channel());

		Response rsp = (Response) msg;
		
		MsgLog.info("收到的消息, accountId：" + clinet.getAccountId()+" cmd:"+rsp.getHeader().getCommand());
		clinet.getMsgHandler().dataSyn(rsp);
		clinet.getMsgHandler().setResp(rsp);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		ctx.channel().closeFuture();
	}
}