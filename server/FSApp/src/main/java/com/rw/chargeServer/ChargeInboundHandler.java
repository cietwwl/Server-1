package com.rw.chargeServer;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.net.URLDecoder;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.charge.ChargeMgr;
import com.rw.fsutil.util.fastjson.FastJsonUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpRequest;

public class ChargeInboundHandler extends ChannelInboundHandlerAdapter {
	private ByteBufToBytes reader;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof HttpRequest) {
			HttpRequest request = (HttpRequest) msg;
			System.out.println("messageType:" + request.headers().get("messageType"));
			System.out.println("businessType:" + request.headers().get("businessType"));
			if (HttpHeaders.isContentLengthSet(request)) {
				reader = new ByteBufToBytes((int) HttpHeaders.getContentLength(request));
			}
		}

		if (msg instanceof HttpContent) {
			HttpContent httpContent = (HttpContent) msg;
			ByteBuf content = httpContent.content();
			reader.reading(content);
			content.release();

			if (reader.isEnd()) {
				byte[] readFull = reader.readFull();
				String resultStr = new String(readFull);
				String decode = URLDecoder.decode(resultStr, "UTF-8");
				String jsonContent = StringUtils.substringAfter(decode, "content=");
				ChargeContentPojo contenPojo = FastJsonUtil.deserialize(jsonContent, ChargeContentPojo.class );
				
				boolean success = ChargeMgr.getInstance().charge(contenPojo);
				String resp = "ok";
				if(!success){
					resp = "fail";
				}

				FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(resp.getBytes()));
				response.headers().set(CONTENT_TYPE, "text/plain");
				response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
				response.headers().set(CONNECTION, Values.KEEP_ALIVE);
				ctx.write(response);
				ctx.flush();
			}
		}
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
