package com.rw.routerServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import com.rw.fsutil.util.jackson.JsonUtil;
import com.rw.routerServer.data.ResultState;
import com.rw.routerServer.data.RouterReqestObject;
import com.rw.routerServer.data.RouterRespObject;

public class RouterInboundHandler extends ChannelInboundHandlerAdapter {
	Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String result = null;
		try{
			RouterReqestObject reqParam = JsonUtil.readValue((String)msg, RouterReqestObject.class);
			switch (reqParam.getType()) {
			case GetSelfRoles:
				result = RouterServiceHandler.getInstance().getSelfAllRoles(reqParam.getContent());
				break;
			case GetAreaInfo:
				result = RouterServiceHandler.getInstance().getAllAreas();
				break;
			case HeartBit:
				RouterRespObject obj2 = new RouterRespObject();
				obj2.setResult(ResultState.SUCCESS);
				result = JsonUtil.writeValue(obj2);
				break;
			default:
				RouterRespObject obj = new RouterRespObject();
				obj.setResult(ResultState.PARAM_ERROR);
				result = JsonUtil.writeValue(obj);
				break;
			}
		}catch(Exception ex){
			logger.error("处理直通车服消息异常，消息内容:{}", result);
			RouterRespObject obj = new RouterRespObject();
			obj.setResult(ResultState.EXCEPTION);
			result = JsonUtil.writeValue(obj);
		}
		ByteBuf buf = Unpooled.copiedBuffer(((String)result + System.getProperty("line.separator")).getBytes("UTF-8"));
        ctx.writeAndFlush(buf);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//		System.out.println("channelReadComplete");
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
//		System.out.println("exceptionCaught");
		super.exceptionCaught(ctx, cause);
		ctx.close();
	}
}
