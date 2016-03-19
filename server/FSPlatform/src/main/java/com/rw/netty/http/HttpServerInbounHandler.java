package com.rw.netty.http;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;
import io.netty.handler.codec.http.HttpMethod;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class HttpServerInbounHandler extends ChannelInboundHandlerAdapter {
	private HttpRequest request;

	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		try {
			if (msg instanceof HttpRequest) {
				request = (HttpRequest) msg;
			}
			
			if (msg instanceof HttpContent) {
				HttpContent content = (HttpContent) msg;
				ByteBuf buf = content.content();
				byte[] bytes = new byte[buf.readableBytes()];
				buf.readBytes(bytes);
				ByteArrayInputStream bi = null;
				ObjectInputStream input = null;
				try{
					bi = new ByteArrayInputStream(bytes);
					input = new ObjectInputStream(bi);
					Object readObject = input.readObject();
					GSResponseMgr.processMsg(request, readObject, ctx);
				}catch(Exception ex){
					throw new Exception(ex.getMessage());
				}finally{
					if(bi!=null){
						bi.close();
					}
					if(input !=null){
						input.close();
					}
				}
				
			}
		} catch (Exception ex) {
			if(ex.getMessage() == null){
				return;
			}
			throw new Exception(ex.getMessage());
		}finally{
			ReferenceCountUtil.release(msg);
		}
	}
}
