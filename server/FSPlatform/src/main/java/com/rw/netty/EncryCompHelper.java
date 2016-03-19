package com.rw.netty;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import io.netty.buffer.ByteBuf;

public class EncryCompHelper {
	private static final byte XOR_MASK = (byte) 0xAA;
	private static final int XOR_INT_MASK = 0xAAAAAAAA;//= 0xAAaaAAaa;
	private static final int minCompPackSize = 1024;
	
	public static void printDebugInfo(Object x){
        String info = String.valueOf(x);
		System.out.println(info);
//		if (GameWorldAttribute.isOpen(GameWorldAttributeType.PRINT_MSG_COMPRESS)){
//	        String info = String.valueOf(x);
//			System.out.println(info);
//			DevelopLogger.info(info+GameWorldExecutor.LINE_SEPARATOR);
//		}
	}
	
	public static int GetOrderInt(ByteBuf in){
		byte [] b = new byte[4];
		in.readBytes(b);
		return GetOrderInt(b);
	}

	public static int GetOrderInt(byte[] b) {
		if (b == null) return 0;
		ByteBuffer bf = ByteBuffer.wrap(b);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		int size = bf.getInt();
		return size;
	}
	
	public static void PutOrderInt(ByteBuf out,int size){
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(size);	
		out.writeBytes(buffer.array());
	}
	
	public static int XorInt(int insize){
		return (insize ^ XOR_INT_MASK);
	}
	
	public static byte[] GZipComp(final byte[] originalBytes) throws IOException{
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();		    
	    GZIPOutputStream gos = new GZIPOutputStream(baos);
	    gos.write(originalBytes);
	    gos.close();
	    byte[] compressedBytes = baos.toByteArray();
	    baos.close();
	    return compressedBytes;
	}

	public static byte[] GZipDeComp(final byte[] originalBytes,int offset) throws IOException{
	    ByteArrayInputStream bain = new ByteArrayInputStream(originalBytes,offset,originalBytes.length-offset);		    
	    GZIPInputStream zis = new GZIPInputStream(bain);
	    
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();		    
	    byte[] tmpBuffer = new byte[256];
	    int n;
	    while ((n = zis.read(tmpBuffer,0,256)) >= 0){
	      baos.write(tmpBuffer, 0, n);
	      }
	    
	    zis.close();
	    
	    byte[] decompressedBytes = baos.toByteArray();
	    baos.close();
	    return decompressedBytes;
	}
	
	public static void xorByte(byte[] orginals)
	{
		for (int i=0; i<orginals.length; ++i){
			orginals[i] ^= XOR_MASK;
		}
		
	}
	
	public static boolean shouldCompressed(byte[] org){
		return (org.length > minCompPackSize);
	}

	public static void Print(byte[] comp, String msg) {
		//Print(comp,msg,0);		
	}
	
	public static void Print(byte[] comp, String msg,int offset) {
		/*
		StringBuilder b = new StringBuilder();
		b.append(msg);
		b.append(":"+comp.length);
		b.append("[");
		for(int i = offset; i < comp.length;i++){
			byte item = comp[i];
			String str = String.valueOf(item < 0 ? item + 256 : item);
			b.append(str+",");
		}
		b.append("]");
		System.out.println(b.toString());
		*/
	}
}
