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
		RouterReqestObject reqParam = null;
		String result = null;
		try{
			reqParam = JsonUtil.readValue((String)msg, RouterReqestObject.class);
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
			logger.info("Rounter server recv except msg:" + msg);
			RouterRespObject obj = new RouterRespObject();
			obj.setResult(ResultState.EXCEPTION);
			result = JsonUtil.writeValue(obj);
		}
		if(reqParam != null && reqParam.getType() != ReqType.HeartBit){
			logger.info("Rounter server recv:" + msg + ", response:" + result);
		}
		ByteBuf buf = Unpooled.copiedBuffer(((String)result + System.getProperty("line.separator")).getBytes("UTF-8"));
        ctx.writeAndFlush(buf);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		super.exceptionCaught(ctx, cause);
		ctx.close();
	}
}
