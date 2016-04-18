package com.rw.netty;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.Attribute;

//ProtobufFrameEncoder and FrameDecoder is a pair
public class ProtobufFrameEncoder extends MessageToByteEncoder<ByteBuf>{
	static Random r = new Random();
	private static final int ExtraByteCountWhenCompressed = 3 + 1 + 4;//3(marked bytes),1(compressed tag),4(original,uncompressed message size)
	private static final int ExtraByteCountWhenNoCompressed = 3 + 1;//3(marked bytes),1(uncompressed tag)
	//鍗忚瀹氫箟锛岃CLIENT_PROTOCOL_TYPE绫诲瀷鐨勬敞閲�
	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out)
			throws Exception {

		//鍙戦�鏁版嵁鍘嬬缉锛嬪姞瀵�
		try {
			Attribute<CLIENT_PROTOCOL_TYPE> map = ctx.channel().attr(FrameDecoder.key);
			CLIENT_PROTOCOL_TYPE clientType = map.get();
			EncryCompHelper.printDebugInfo("encoder channel:" + ctx.channel().toString() + " ,protocol:" + map.get());

			if (clientType == null) {
				// 鏈夐敊璇椂鍋囪鏄渶楂樺姞瀵嗙増鏈�
				// 鏇夸唬鏂规锛氫笉鍙戦�锛�
				clientType = CLIENT_PROTOCOL_TYPE.COMP2;
			}

			switch (clientType) {
			case COMP2: {
				// 鍙戦�鏁版嵁鍘嬬缉锛嬪姞瀵�
				int t = r.nextInt();
				byte tag;
				boolean compressed = false;
				byte[] dataarr;
				byte[] org = msg.array();
				if (EncryCompHelper.shouldCompressed(org)){
					byte[] comp = EncryCompHelper.GZipComp(org);
					if ((ExtraByteCountWhenCompressed - ExtraByteCountWhenNoCompressed) + comp.length < org.length) {
						tag = (byte) ((byte) t | 0x01);
						dataarr = comp;
						compressed = true;
						EncryCompHelper.Print(org,"org data");
						EncryCompHelper.Print(dataarr,"sending compressed data");
					} else {
						tag = (byte) ((byte) t & 0xFE);
						dataarr = org;
						//EncryCompHelper.Print(dataarr,"sending uncompressed data");
					}
					EncryCompHelper.printDebugInfo("protocol v2, org size:" 
							+ org.length + ",compressed size:" + comp.length+"compressed:"+compressed);
				}else{
					tag = (byte) ((byte) t & 0xFE);
					dataarr = org;
					//EncryCompHelper.Print(dataarr,"sending uncompressed data");
				}

				int size = dataarr.length;
				EncryCompHelper.xorByte(dataarr);

				if (compressed) {
					out.ensureWritable(4 + ExtraByteCountWhenCompressed + size);
					EncryCompHelper.PutOrderInt(out, ExtraByteCountWhenCompressed+size);

					out.writeByte((byte) 0XF0);
					out.writeByte((byte) 0XF0);
					out.writeByte((byte) 0XF1);

					out.writeByte(tag);
					EncryCompHelper.PutOrderInt(out, EncryCompHelper.XorInt(org.length));
					out.writeBytes(dataarr);
				} else {
					out.ensureWritable(4 + ExtraByteCountWhenNoCompressed + size);
					EncryCompHelper.PutOrderInt(out, ExtraByteCountWhenNoCompressed+size);

					out.writeByte((byte) 0XF0);
					out.writeByte((byte) 0XF0);
					out.writeByte((byte) 0XF1);

					out.writeByte(tag);
					out.writeBytes(dataarr);
				}

			}
				break;

			case NOCOMP: {
				int size = msg.readableBytes();
				out.ensureWritable(size + 4);
				EncryCompHelper.PutOrderInt(out, size);
				out.writeBytes(msg, msg.readerIndex(), size);
				EncryCompHelper.printDebugInfo("protocol v0 , size:" + size);
				
				if (size == 10031){
					byte[] org = msg.array();
					EncryCompHelper.Print(msg.array(),"sending uncompressed data");
				}
			}
				break;
			default:
				break;
			}

		} catch (Throwable t) {
			{
				EncryCompHelper.printDebugInfo("msg.capacity()=" + msg.capacity());
				EncryCompHelper.printDebugInfo("msg.readableBytes()=" + msg.readableBytes());
				EncryCompHelper.printDebugInfo("msg.maxCapacity()=" + msg.maxCapacity());
				EncryCompHelper.printDebugInfo("msg.maxWritableBytes()=" + msg.maxWritableBytes());
				EncryCompHelper.printDebugInfo("msg.writableBytes()=" + msg.writableBytes());
				EncryCompHelper.printDebugInfo("msg.isWritable()=" + msg.isWritable());
				EncryCompHelper.printDebugInfo("msg.arrayOffset()=" + msg.arrayOffset());
				EncryCompHelper.printDebugInfo("msg.toString()=" + msg.toString());
			}
			t.printStackTrace();
			int readSize = msg.readableBytes();
			StringBuilder sb = new StringBuilder();
			sb.append("address  = " + ctx.pipeline().channel().remoteAddress());
			sb.append("\r\n");
			sb.append("size = " + readSize);
			sb.append("\r\n");
			Attribute<CLIENT_PROTOCOL_TYPE> map = ctx.channel().attr(FrameDecoder.key);
			CLIENT_PROTOCOL_TYPE clientType = map.get();
			sb.append("clienttype=" + clientType);
			sb.append("\r\n");
			sb.append(msg.toString());
			String current = sb.toString();
			System.err.println("" + current);
			// DevelopLogger.error("缂栫爜瑙ｆ瀽閿欒锛� + sb.toString(), t);
			throw new CorruptedFrameException("encode error");
		}
		
		/*
		int size =  msg.readableBytes();
		out.ensureWritable(size+4);		
		
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(size);	
		
		out.writeBytes(buffer.array());
		out.writeBytes(msg, msg.readerIndex(), size);
		*/
	}
}
