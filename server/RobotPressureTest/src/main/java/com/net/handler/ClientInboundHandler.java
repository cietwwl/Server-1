package com.net.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;

import com.rw.ChannelServer;
import com.rw.Client;
import com.rw.common.MsgLog;
import com.rw.common.RobotLog;
import com.rw.common.push.PushMsgHandlerFactory;
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
		Client client = ChannelServer.getInstance().getClient(ctx.channel());
		Response rsp = (Response) msg;
		if (client == null) {
			RobotLog.testError("receive overdue msg:" + " cmd=" + rsp.getHeader().getCommand() + ",seqId=" + rsp.getHeader().getSeqID());
		} else {
			RobotLog.testInfo("收到的消息, accountId：" + client.getAccountId() + ",cmd" + rsp.getHeader().getCommand() + ",seqId=" + rsp.getHeader().getSeqID());
			MsgLog.info("收到的消息, accountId：" + client.getAccountId() + " cmd:" + rsp.getHeader().getCommand());
			client.getMsgHandler().dataSyn(rsp);
			client.getMsgHandler().setResp(rsp);
			PushMsgHandlerFactory.getFactory().onMsgReceive(client, rsp);// 推送消息到达
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		ctx.channel().closeFuture();
		Attribute<Client> attr = ctx.channel().attr(ChannelServer.ATTR_CLIENT);
		Client client = attr.get();
		if (client == null) {
			RobotLog.testException("channel connection and close but not init:chn=" + ctx.channel(), cause);
			return;
		}
		if (!client.getCloseFlat().get()) {
			RobotLog.testException("channel connection and close:" + client.getAccountId() + "," + client.getCommandInfo() + "," + Thread.currentThread() + ",chn=" + ctx.channel(), cause);
		}
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		Attribute<Client> attr = ctx.channel().attr(ChannelServer.ATTR_CLIENT);
		Client client = attr.get();
		String accountId;
		if (client == null) {
			accountId = "null";
		} else {
			accountId = client.getAccountId();
		}
		RobotLog.info("open connection:" + accountId + "," + Thread.currentThread() + "," + ctx.channel());
		super.channelRegistered(ctx);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		super.channelUnregistered(ctx);
		Attribute<Client> attr = ctx.channel().attr(ChannelServer.ATTR_CLIENT);
		Client client = attr.get();
		if (client == null) {
			RobotLog.testError("close a not init channel:chn=" + ctx.channel());
			return;
		}
		if (!client.getCloseFlat().get()) {
			RobotLog.testError("server close connection:" + client.getAccountId() + "," + client.getCommandInfo() + "," + Thread.currentThread() + ",chn=" + ctx.channel());
		}
	}
}