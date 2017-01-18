package com.rw.routerServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import com.rw.fsutil.util.jackson.JsonUtil;
import com.rw.routerServer.data.RouterReqestObject;
import com.rw.service.http.request.ResponseObject;

public class RouterInboundHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("channelRead:" + msg);
		RouterReqestObject reqParam = JsonUtil.readValue((String)msg, RouterReqestObject.class);
		String result = null;
		try{
			switch (reqParam.getType()) {
			case GetGift:
				result = RouterServiceHandler.getInstance().getSelfAllRoles(reqParam.getContent());
				break;
			default:
				ResponseObject obj = new ResponseObject();
				obj.setResult("游戏服找不到对应的协议");
				obj.setSuccess(false);
				result = JsonUtil.writeValue(obj);
				break;
			}
		}catch(Exception ex){
			ResponseObject obj = new ResponseObject();
			obj.setResult("游戏服数据异常");
			obj.setSuccess(false);
			result = JsonUtil.writeValue(obj);
		}
		ByteBuf buf = Unpooled.copiedBuffer(((String)result + System.getProperty("line.separator")).getBytes("UTF-8"));
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
