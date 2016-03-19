package com.rw.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import com.log.GameLog;
import com.rw.controler.FsNettyControler;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.platform.PlatformFactory;
import com.rwproto.RequestProtos.Request;



public class ServerHandler extends ChannelInboundHandlerAdapter{
	
//	private ThreadPoolExecutor 
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		try {
			UserChannelMgr.setThreadLocalCTX(ctx);
			FsNettyControler controler = SpringContextUtil.getBean("fsNettyControler");
			
			controler.doMyService( (Request) msg,ctx);
			
			UserChannelMgr.removeThreadLocalCTX();
		} catch (Exception e) {
			GameLog.error("ServerHandler", "ServerHandler[channelRead]", "", e);
			
		}
	}
	

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().flush();
		
	};

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		GameLog.error("ServerHandler", "ServerHandler[exceptionCaught]", "", cause);
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		GameLog.info("channelRegistered", "", "open connection", null);
		super.channelRegistered(ctx);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		PlatformFactory.getPlatformService().removeAccount(ctx);
		super.channelUnregistered(ctx);
		UserChannelMgr.exit(ctx);
	}
	


	
	
}