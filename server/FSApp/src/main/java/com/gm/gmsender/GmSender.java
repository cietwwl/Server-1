package com.gm.gmsender;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Map;

import com.gm.util.SocketHelper;
import com.log.GameLog;
import com.log.LogModule;
import com.rw.fsutil.util.fastjson.FastJsonUtil;

public class GmSender {

	private final Socket socket;
	private DataOutputStream output;
	private DataInputStream input;
	private short protno;

	public GmSender(GmSenderConfig senderConfig) throws IOException{
			this.socket = new Socket(senderConfig.getHost(), senderConfig.getPort());
			this.socket.setSoTimeout(senderConfig.getTimeoutMillis());
			this.output = new DataOutputStream(socket.getOutputStream());			
			this.input = new DataInputStream(socket.getInputStream());
			this.protno = senderConfig.getProtno();
	}
	
	public boolean isAvailable(){
		return socket.isConnected() && !socket.isClosed();
	}

	public <T> T send(Map<String,Object> content, Class<T> clazz) throws IOException {
		
		String jsonContent = FastJsonUtil.serialize(content);
		byte[] dataFormat = dataFormat(protno, jsonContent);
		output.write(dataFormat);
		output.flush();		
		
		T gmResponse = SocketHelper.read(input, clazz);	//block
	
		return gmResponse;
	}
	
	private byte[] dataFormat(short protno, String json) throws UnsupportedEncodingException {
		// 包头计算：包头(short,4个字节) = 包体长度%255(1个字节) + 包体长度&0x00ffffff(3个字节)
		byte[] jsonbytes= json.getBytes("utf-8");
		short jsonLen = (short) jsonbytes.length;
		int bodyLength = 2 + 2 + jsonLen;
		bodyLength = 1;
		
		int header = ((bodyLength % 255) << 24) | (bodyLength & 0x00ffffff);
		ByteBuffer dataInfo = ByteBuffer.allocate(8 + jsonLen);
		// 转换为小端模式，默认为大端。
		// dataInfo.order(ByteOrder.LITTLE_ENDIAN);
		// 设置包头
		dataInfo.putInt(header);
//		System.out.println("header:" + header);
		// 包体计算：包体=协议号(2个字节) + json字符串长度(2字节) + json字符串内容
		dataInfo.putShort(protno);
		dataInfo.putShort(jsonLen);
//		System.out.println("jsonLen:" + jsonLen);
		dataInfo.put(jsonbytes);
		return dataInfo.array();
	}
	
	public void destroy(){
		if(socket!=null){
			try {
				socket.close();
			} catch (IOException e) {
				GameLog.error(LogModule.GmSender, "GmSender[destroy]", "socket 关闭 出错",e);
			}
		}
	}
	
}
