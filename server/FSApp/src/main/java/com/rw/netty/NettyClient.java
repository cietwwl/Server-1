package com.rw.netty;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.rwproto.MsgDef.Command;
import com.rwproto.RequestProtos.Request;
import com.rwproto.RequestProtos.RequestBody;
import com.rwproto.RequestProtos.RequestHeader;



public class NettyClient {
	//


	public static void main(String args[]) throws Exception {
		//RequestHeader header = RequestHeader.newBuilder().setToken("token").setCommand(Command.MSG_LOGIN).setUserId("userId").build();

		Request request = Request.newBuilder().setBody(RequestBody.newBuilder().build()).build();

		// 为了简单起见，所有的异常都直接往外抛
		String host = "127.0.0.1"; // 要连接的服务端IP地址
		int port = 8080; // 要连接的服务端对应的监听端口
		// 与服务端建立连接
		Socket client = new Socket(host, port);
		OutputStream outputStream = client.getOutputStream();
		outputStream.write(request.toByteArray());
		outputStream.flush();
		// 写完以后进行读操作
		InputStream inputStream = client.getInputStream();
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inputStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
			byte[] byteArray = outSteam.toByteArray();
//			Response parseFrom = Response.parseFrom(byteArray);
//			System.out.println(parseFrom);
			System.out.println(byteArray);
		}
		outSteam.close();
		inputStream.close();
	}

}
