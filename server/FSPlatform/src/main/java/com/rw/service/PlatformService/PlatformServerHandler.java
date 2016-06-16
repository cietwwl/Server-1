package com.rw.service.PlatformService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import com.log.PlatformLog;
import com.rw.fsutil.util.fastjson.FastJsonUtil;
import com.rw.netty.http.GSResponseMgr;
import com.rw.service.http.request.RequestObject;

public class PlatformServerHandler {
	public PlatformServerHandler(){
		
	}
	
	public void handler(Socket socket){
		try {
			// 读取客户端数据
			DataOutputStream output = new DataOutputStream(socket.getOutputStream());
			DataInputStream input = new DataInputStream(socket.getInputStream());

			int jsonLength = input.readInt();
			byte[] jsonBody = new byte[jsonLength];
			input.read(jsonBody);
			String json = new String(jsonBody, "utf-8");
			RequestObject requestObject = FastJsonUtil.deserialize(json, RequestObject.class);
			byte[] result = GSResponseMgr.processMsg(requestObject);
			
			ByteBuffer dataInfo = ByteBuffer.allocate(4 + result.length);
			dataInfo.putInt(result.length);
			dataInfo.put(result);
			byte[] array = dataInfo.array();
			output.write(array);
			output.flush();

			output.close();
			input.close();
		} catch (Exception ex) {
			PlatformLog.error("platformService", "PlatformServerHandler.handler", "handler处理异常"+ex.getMessage());
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (Exception ex) {
					socket = null;
					PlatformLog.error("platformService", "PlatformServerHandler.handler", "finally socket关闭异常"+ex.getMessage());
				}
			}
		}
	}
}
