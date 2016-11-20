package com.rw.fsutil.remote;

import java.util.Arrays;
import java.util.List;

import com.rw.fsutil.remote.parse.FSMessageDecoder;
import com.rw.fsutil.remote.parse.FSMessagePrefix;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

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
		String content = new String(data);
		System.out.println("normal:"+content);
		if(content.charAt(content.length()-1) != '}'){

			int readSize = in.readableBytes();
			System.out.println("exception:"+readSize+",dataSize="+dataSize);
			if(readSize > 0){
				in.markReaderIndex();
				byte[] remainArray = new byte[readSize];
				in.readBytes(remainArray);
				in.resetReaderIndex();
				byte[] total = new byte[dataSize+readSize];
				System.arraycopy(data, 0, total, 0, data.length);
				System.arraycopy(remainArray, 0, total, data.length, remainArray.length);
				System.out.println("exception:"+new String(total));
			}
		}
		ReceiveMessage message = decoder.convertToMessage(data);
		out.add(message);
		int remainSize = in.readableBytes();
		if(remainSize == 0){
			return;
		}
		in.markReaderIndex();
		byte[] remain = new byte[remainSize];
		System.out.println("remain:"+new String(remain));
		in.resetReaderIndex();
	}
}
