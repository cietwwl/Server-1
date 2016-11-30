package com.rw.fsutil.remote;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

import com.rw.fsutil.remote.parse.FSMessageDecoder;
import com.rw.fsutil.remote.parse.FSMessagePrefix;

public class RemoteMessageDecoder<ReceiveMessage> extends ByteToMessageDecoder {

	private final FSMessageDecoder<ReceiveMessage> decoder;
	private final FSMessagePrefix prefix;
	private final int prefixSize;
	private final int maxDataSize;

	public RemoteMessageDecoder(FSMessageDecoder<ReceiveMessage> decoder) {
		this.decoder = decoder;
		this.prefix = decoder.getPrefix();
		this.prefixSize = prefix.getPrefixReadSize();
		this.maxDataSize = prefix.getMaxDataReadSize();
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < prefixSize) {
			return;
		}
		in.markReaderIndex();
		byte[] prefixDatas = new byte[prefixSize];
		in.readBytes(prefixDatas);
		int dataSize = prefix.getDataSize(prefixDatas);
		if (dataSize < 0 || dataSize > maxDataSize) {
			byte[] data = new byte[in.readableBytes()];
			in.readBytes(data);
			System.out.println("error:"+new String(data));
			throw new CorruptedFrameException("wrong size of packet ,size=" + dataSize);
		}
		if (in.readableBytes() < dataSize) {
			in.resetReaderIndex();
			return;
		}
		byte[] data = new byte[dataSize];
		in.readBytes(data);
		ReceiveMessage message = decoder.convertToMessage(data);
		out.add(message);
	}
}
