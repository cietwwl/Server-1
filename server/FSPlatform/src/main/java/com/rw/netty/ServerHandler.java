package com.rw.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import com.log.PlatformLog;
import com.rw.controler.FsNettyControler;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.platform.PlatformFactory;
import com.rwproto.RequestProtos.Request;
import com.rwproto.RequestProtos.RequestHeader;



public class ServerHandler extends ChannelInboundHandlerAdapter{
	
//	private ThreadPoolExecutor 
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		FsNettyControler controler = SpringContextUtil.getBean("fsNettyControler");
		Request req = (Request) msg;
		try {
			UserChannelMgr.setThreadLocalCTX(ctx);
			controler.doMyService(req, ctx);
			UserChannelMgr.removeThreadLocalCTX();
		} catch (Exception e) {
			PlatformLog.error("ServerHandler", "ServerHandler[channelRead]", "", e);
			controler.sendErrorResponse(req, 500, ctx);
		}
	}
	

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().flush();
		
	};

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		PlatformLog.error("ServerHandler", "ServerHandler[exceptionCaught]", "", cause);
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		PlatformLog.info("channelRegistered", "", "open connection", null);
		super.channelRegistered(ctx);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		PlatformFactory.getPlatformService().removeAccount(ctx);
		super.channelUnregistered(ctx);
		UserChannelMgr.exit(ctx);
	}
	


	
	
}