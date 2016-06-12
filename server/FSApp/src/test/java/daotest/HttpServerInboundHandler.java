package daotest;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerInboundHandler extends ChannelInboundHandlerAdapter{
	private static Logger log = LoggerFactory.getLogger(HttpServerInboundHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
    	System.out.println("-------------------");
    	ByteBuf buf = Unpooled.copiedBuffer(((String)msg + System.getProperty("line.separator")).getBytes("UTF-8"));
    	Thread.sleep(10);
        ctx.writeAndFlush(buf);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage());
        ctx.close();
    }
}
