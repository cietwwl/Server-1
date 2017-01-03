package com.rw.routerServer;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import com.rw.fsutil.util.jackson.JsonUtil;
import com.rw.routerServer.data.ReqType;
import com.rw.routerServer.data.ResultState;
import com.rw.routerServer.data.RouterReqestObject;
import com.rw.routerServer.data.RouterRespObject;

public class RouterInboundHandler extends ChannelInboundHandlerAdapter {
	private static Logger logger = Logger.getLogger("rounterMsgLogger");

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		//long startTime = System.currentTimeMillis();
		//System.out.println("channelRead:" + msg);
		RouterReqestObject reqParam = JsonUtil.readValue((String)msg, RouterReqestObject.class);
		String result = null;
		try{
			switch (reqParam.getType()) {
			case GetGift:
				result = RouterServiceHandler.getInstance().getGift(reqParam.getContent());
				break;
			case GetRoleDataFromGS:
				result = RouterServiceHandler.getInstance().getRoleInfo(reqParam.getContent());
				break;
			case HeartBit:
				RouterRespObject obj1 = new RouterRespObject();
				obj1.setResult(ResultState.SUCCESS);
				result = JsonUtil.writeValue(obj1);
				break;
			default:
				RouterRespObject obj = new RouterRespObject();
				obj.setResult(ResultState.PARAM_ERROR);
				result = JsonUtil.writeValue(obj);
				break;
			}
		}catch(Exception ex){
			RouterRespObject obj = new RouterRespObject();
			obj.setResult(ResultState.EXCEPTION);
			result = JsonUtil.writeValue(obj);
		}
		if(reqParam.getType() != ReqType.HeartBit){
			logger.info("Rounter server recv:" + msg + ", response:" + result);
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
