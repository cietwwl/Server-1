package com.rw.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import com.log.PlatformLog;
import com.rw.controler.ResponseNettyControler;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.netty.UserChannelMgr;
import com.rwproto.ResponseProtos.Response;

public class ClientHandler extends ChannelInboundHandlerAdapter{
	
	public void channelRead(ChannelHandlerContext ctx, Object msg){
		try {
			UserChannelMgr.setThreadLocalCTX(ctx);
			ResponseNettyControler controler = SpringContextUtil.getBean("responseNettyControler");
			//Response response = controler.doService( (Request) msg );
			controler.doService((Response) msg,ctx);
			UserChannelMgr.removeThreadLocalCTX();
		} catch (Exception e) {
			PlatformLog.error(e);
			
			
		}finally{
			ReferenceCountUtil.release(msg);
		}
	}
	

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("open connection:" + ctx);
		super.channelRegistered(ctx);
	}
}
