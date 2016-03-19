package com.rw.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import com.log.GameLog;

enum CLIENT_PROTOCOL_TYPE{
	NOCOMP,//非加密，旧客户端: protocol v1:前四个字节是长度
	COMP2,
	//更新加密协议（发送包含压缩前大小），protocol v2：
	//加密使用简单的XOR加密，压缩使用GZip
	//客户端发送前四个字节还是长度，但是跟着四个字节，其中前三个是标志位（0XF0，0XF0，0XF1），用于区分旧版协议
	//第四个字节的最低位是是否压缩的标志（1表示压缩，0表示没有压缩），其余7个位是客户端生成的随机数
	//接着的字节数组的长度应该等于一开始读取的长度-4,
	//标志位后的数据都是XOR加密过的，需要解密后再做剩下的操作
	//如果是压缩的，前四个字节是解压后的长度，解压后可以对长度做验证
	
	//服务端发送的时候，保证压缩是有意义的（总字节数更少）。
	//如果压缩，写入四个字节表示总长度，然后写入三个字节标志为(0XF0，0XF0，0XF1)
	//最后写入一个字节表示是否压缩（最后一位为0不压缩，1表示压缩），而去其余7个位是随机数
	//如果是压缩的，必须再额外发送四个字节，表示压缩前的长度，
	//最后发送前把除了标志位的剩余数据（包括压缩前的长度）用XOR加密
}

//ProtobufFrameEncoder and FrameDecoder is a pair
public class FrameDecoder extends ByteToMessageDecoder {
	private static final int MAX_SIZE = 2048;
	public static final AttributeKey<CLIENT_PROTOCOL_TYPE> key = new AttributeKey<CLIENT_PROTOCOL_TYPE>("clientProtocol");

	// 控制变量
	private int length = 0;

	private void reset() {
		this.length = 0;
	}
	
	byte[] orgLenBytes = new byte[4];

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < 8) {
			return;
		}
		in.markReaderIndex();
		int size = EncryCompHelper.GetOrderInt(in);
		if (size<0 || size>MAX_SIZE) {
			throw new CorruptedFrameException("wrong size of packet ,size="+size);
		}

		byte[] trans;
		ByteBuf buf;
		
		byte tag0 = in.readByte();
		byte tag1 = in.readByte();
		byte tag2 = in.readByte();
		
		Attribute<CLIENT_PROTOCOL_TYPE> map = ctx.channel().attr(key);

		if ((tag0&0XFF) == 0XF0 && (tag1&0XFF) == 0XF0 && (tag2&0XFF) == 0XF1){
			//protocal v2 除了标志位和最后纪录版本号不同，其他的地方都一样
			//接收数据解密＋解压
			byte b = in.readByte();
			size =size -4;
			if (in.readableBytes() < size) {
				in.resetReaderIndex();
				return;
			}
			
			byte bb = (byte) (b & 0x01);
			buf = in.readBytes(size);
			byte[] arr = buf.array();
			EncryCompHelper.xorByte(arr);
						 
			if (bb != 0){
				orgLenBytes[0]=arr[0];
				orgLenBytes[1]=arr[1];
				orgLenBytes[2]=arr[2];
				orgLenBytes[3]=arr[3];
				int orgLen = EncryCompHelper.GetOrderInt(orgLenBytes);
				EncryCompHelper.Print(arr,"received compressed data",4);
				trans = EncryCompHelper.GZipDeComp(arr,4);
				if (orgLen != trans.length){
					System.out.println("WARNING: decompressed size not consistent, client say is " + orgLen
							+ ",server found is " + trans.length);
				}
			}else{
				trans = arr;
			}
			
			EncryCompHelper.printDebugInfo("org size:"+size+",decode size:"+trans.length);
			buf.capacity(trans.length);
			buf.clear();
			buf.writeBytes(trans);
			out.add(buf);
			
			map.set(CLIENT_PROTOCOL_TYPE.COMP2);
		}else{
			//old protocal
			
			in.resetReaderIndex();
			size = EncryCompHelper.GetOrderInt(in);
			if (in.readableBytes() < size) {
				in.resetReaderIndex();
				return;
			}

			out.add(in.readBytes(size));
			
			map.set(CLIENT_PROTOCOL_TYPE.NOCOMP);

		}
		
		/*
		if (in.readableBytes() < 4) {
			return;
		}
		
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
		*/
	}
}
