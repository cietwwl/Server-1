package com.rw.fsutil.logger;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class LoggerSender {

	private final Socket socket;
	private DataOutputStream output;
//	private InputStream input;
	private BufferedReader reader;

	public LoggerSender(String host, int port,int timeoutMillis){
		try {
			this.socket = new Socket(host, port);
			this.socket.setSoTimeout(timeoutMillis);
			this.output = new DataOutputStream(socket.getOutputStream());
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
		} catch (IOException e) {
			//暂时屏蔽
			//e.printStackTrace();
			throw new RuntimeException("初始化连接失败");
		}
	}
	
	public boolean isAvailable(){
		return socket.isConnected() && !socket.isClosed();
	}

	public SendResult sendLogger(String content) {
		try {
			//TODO 这里需要做超时控制处理，超过一定时间认为发送失败
			content+="\n";
			output.write(content.getBytes("UTF-8"));//阻塞
			output.flush();			 //阻塞	
			System.out.println("发送消息："+content);
		} catch (IOException e) {
			e.printStackTrace();
			return SendResult.SOCKET_NOT_AVAILABLE;
		}
		try {
			//TODO 这里需要做超时控制处理，超过一定时间接收发送失败
//			BufferedReader reader = new BufferedReader(new InputStreamReader(input,"UTF-8"));
			String line = null;
			while((line = reader.readLine()) != null)
			{
			    if (line.toLowerCase().contains("ok")) {    
					return SendResult.SUCCESS;
				}else if(line.contains("maximum")){
					return SendResult.RESPONSE_NOT_OK;
				}
				else {
					return SendResult.RESPONSE_NOT_OK;
				}
			}
			return SendResult.RESPONSE_NOT_OK;
		}catch(SocketTimeoutException e){
			System.out.println("接收消息超时："+content);
			return SendResult.SOCKET_TIME_OUT;
		}
		catch (IOException e) {
			e.printStackTrace();
			return SendResult.SOCKET_NOT_AVAILABLE;
		}
	}
	
}
