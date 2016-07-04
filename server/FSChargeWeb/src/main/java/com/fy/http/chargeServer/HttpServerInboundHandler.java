package com.fy.http.chargeServer;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fy.ChargeLog;
import com.fy.ContentPojo;
import com.fy.common.FastJsonUtil;
import com.fy.db.ZoneInfo;
import com.fy.db.ZoneInfoMgr;
import com.fy.http.HttpClientUtil;

public class HttpServerInboundHandler extends ChannelInboundHandlerAdapter {
	private ByteBufToBytes reader;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof HttpRequest) {
			HttpRequest request = (HttpRequest) msg;
			if (HttpHeaders.isContentLengthSet(request)) {
				reader = new ByteBufToBytes((int) HttpHeaders.getContentLength(request));
			}
		}

		if (msg instanceof HttpContent) {
			HttpContent httpContent = (HttpContent) msg;
			ByteBuf content = httpContent.content();
			if(content== null){
				return;
			}
			
			reader.reading(content);
			content.release();

			if (reader.isEnd()) {
				
				String jsonContent = new String(reader.readFull());
				if(StringUtils.isBlank(jsonContent)){
					return;
				}
				ChargeLog.info("charge", "收到 jsonContent:", jsonContent);
				String result = doService(jsonContent);
				ChargeLog.info("charge", "反馈给支付中心信息:", result);
				FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(result.getBytes("UTF-8")));
				response.headers().set(CONTENT_TYPE, "text/plain");
				response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
//				response.headers().set(CONNECTION, Values.KEEP_ALIVE);
				
				ctx.write(response);
				ctx.flush();
			}
		}
	}
	
	public String doService(String jsonContent) {		
			
		ContentPojo contentPojo = FastJsonUtil.fromJson(jsonContent, ContentPojo.class);		
		ChargeLog.info("charge", contentPojo.getCpTradeNo(), jsonContent);
		boolean success  = reqGameServer(jsonContent, contentPojo);
		
		String result = success?contentPojo.getCpTradeNo():"-1";	
		return result;
					

	}
	
	private boolean reqGameServer(String jsonContent,ContentPojo contentPojo){
		
		boolean success = false;
		
		try {
			
			ZoneInfo targetZone = ZoneInfoMgr.getInstance().getZone(contentPojo.getServerId());
			System.out.println("hettpservel" + targetZone.getChargePort());
			Map<String,Object> params = new HashMap<String, Object>();
			params.put("content", jsonContent);
			
			String resp = HttpClientUtil.post(targetZone.getServerIp(),targetZone.getChargePort(), params);
			success = StringUtils.contains(resp, "ok");
			ChargeLog.info("charge", contentPojo.getCpTradeNo(), "游戏服处理结果："+resp);
			
		} catch (Exception e) {			
			ChargeLog.error("charge", contentPojo.getCpTradeNo(), "请求游戏服处理异常",e);
		}
		return success;
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		// TODO Auto-generated method stub
		super.exceptionCaught(ctx, cause);
	}
}
