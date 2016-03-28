package com.rw.netty;



import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import com.log.PlatformLog;


public class ProtobufFrameDecoder extends ByteToMessageDecoder{
	private static final int MAX_SIZE = 2048;

	

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		PlatformLog.info("ProtobufFrameDecoder", "", ctx.channel().toString());
		if (in.readableBytes() < 4) {
			return;
		}
		
//		if(out.size() > 1024 * 10)
//		{
//			GameLog.warning("out.size() > 1024 * 10,close connect now");
//			ctx.channel().close();
//			out.clear();
//			return;
//		}
		in.markReaderIndex();
		byte [] b = new byte[4];
		in.readBytes(b);
		ByteBuffer bf = ByteBuffer.wrap(b);
		
		bf.order(ByteOrder.LITTLE_ENDIAN);
		int size = bf.getInt();
		
		if (size<0 || size>MAX_SIZE) {
			throw new CorruptedFrameException("wrong size of packet");
		}
		
		if (in.readableBytes() < size) {
			in.resetReaderIndex();
			return;
		}
		out.add(in.readBytes(size));
	
	}
}
