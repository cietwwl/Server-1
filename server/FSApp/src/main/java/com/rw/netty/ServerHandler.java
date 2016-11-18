package com.rw.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

import com.log.GameLog;
import com.rw.controler.FsNettyControler;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwproto.MsgDef.Command;
import com.rwproto.RequestProtos.Request;

public class ServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			FsNettyControler controler = SpringContextUtil.getBean("fsNettyControler");

			Request request = (Request) msg;
			controler.doMyService(request, ctx);

		} catch (Exception e) {
			GameLog.error("ServerHandler", "ServerHandler[channelRead]", "", e);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().flush();

	};

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		GameLog.error("ServerHandler", "ServerHandler[exceptionCaught]", "", cause);
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		UserChannelMgr.createSession(ctx);
		System.out.println("open connection:" + ctx.channel().remoteAddress());
		super.channelRegistered(ctx);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (!(evt instanceof IdleStateEvent)) {
			return;
		}
		//IdleStateEvent idleState = (IdleStateEvent) evt;
		// GameLog.info(idleState.state().name(),
		// ctx.channel().remoteAddress().toString(),
		// UserChannelMgr.getCtxInfo(ctx));
		SessionInfo session = UserChannelMgr.getSession(ctx);
		if (session == null) {
			ctx.close();
			GameLog.error("session idle", ctx.channel().remoteAddress().toString(), "not create session!");
		} else {
			Command lastCommand = session.getLastCommand();
			if (lastCommand == null) {
				ctx.close();
				GameLog.error("session idle", ctx.channel().remoteAddress().toString(), "session has not command:" + UserChannelMgr.getCtxInfo(ctx));
			} else if (lastCommand == Command.MSG_PLATFORMGS) {
				// 平台先不处理
				return;
			} else {
				long current = System.currentTimeMillis();
				if (current - session.getLastRecvMsgMillis() > UserChannelMgr.RECONNECT_TIME) {
					ctx.close();
					GameLog.info("session idle", ctx.channel().remoteAddress().toString(), "session has not command:" + UserChannelMgr.getCtxInfo(ctx));
				}
			}
		}
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("close connection:" + UserChannelMgr.getCtxInfo(ctx));
		super.channelUnregistered(ctx);
		UserChannelMgr.closeSession(ctx);
	}

}