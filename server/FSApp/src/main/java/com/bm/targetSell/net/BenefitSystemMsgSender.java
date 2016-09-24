package com.bm.targetSell.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

/**
 * 与精准服通讯
 * @author Alex
 * 2016年9月23日 下午2:35:32
 */
public class BenefitSystemMsgSender {

	
	private final Socket socket;
	
	private DataOutputStream output;
	
	private BenefitSystemMsgReciver reciver;
	

	private final SocketAddress remoteAddress;
	
	private int timeoutMillis;

	public BenefitSystemMsgSender(String host, int port, int timeoutMillis) {
		remoteAddress = new InetSocketAddress(host, port);
		this.timeoutMillis = timeoutMillis;
		try {
			this.socket = new Socket();
			this.socket.setKeepAlive(false);//不监视连接是否有效，这样会不主动断开连接
			this.socket.setTcpNoDelay(true);
			this.output = new DataOutputStream(socket.getOutputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
			reciver = new BenefitSystemMsgReciver(reader);
			
			//连接
			connect();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new RuntimeException("无法连接到目标主机");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("初始化连接失败");
		}
		
		
	}
	
	public boolean isAvaliable(){
		return socket.isConnected() && !socket.isClosed();
	}
	
	// 网络重连
	public boolean connect(){
		try {
			socket.connect(remoteAddress , timeoutMillis);//这个会阻塞,到超时或连接成功
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void sendMsg(String content){
		try {
			if(!isAvaliable()){
				return;
			}
			content += "\n";
			output.write(content.getBytes("UTF-8"));
			output.flush();
			System.out.println("发送消息到精准服：" + content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void shutdown(){
		try {
			reciver.setStop();
			output.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("关闭与精准服连接时出异常");
		}
	}
	
}
