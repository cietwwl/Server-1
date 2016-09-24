package com.bm.targetSell.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.log.GameLog;
import com.rwbase.gameworld.GameWorldFactory;

/**
 * 与精准服通讯
 * @author Alex
 * 2016年9月23日 下午2:35:32
 */
public class BenefitSystemMsgAdapter {

	
	private final Socket socket;
	
	private DataOutputStream output;
	
	private BufferedReader reader;

	private final SocketAddress remoteAddress;
	
	private int timeoutMillis;

	private BenefitSystemMsgService msgService = BenefitSystemMsgService.getHandler();
	private AtomicBoolean shutDown = new AtomicBoolean(false);
	private AtomicBoolean connectComplete = new AtomicBoolean(false);
	public BenefitSystemMsgAdapter(String host, int port, int timeoutMillis) {
		remoteAddress = new InetSocketAddress(host, port);
		this.timeoutMillis = timeoutMillis;
		this.socket = new Socket();
		try {
			//不监视连接是否有效，这样会不主动断开连接
			this.socket.setKeepAlive(false);
			this.socket.setTcpNoDelay(true);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		startReciver();
	}
	
	public boolean isAvaliable(){
		return socket.isConnected() && !socket.isClosed() && connectComplete.get();
	}
	
	// 网络重连
	public boolean connect(){
		System.out.println("================try to connet target sell server~");
		try {
			socket.connect(remoteAddress , timeoutMillis);//这个会阻塞,到超时或连接成功
			this.output = new DataOutputStream(socket.getOutputStream());
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
			System.out.println("BenefitSystemMsgAdapter.connect() sucess~~");
			connectComplete.compareAndSet(false, true);
			return true;
		} catch (Exception e) {
			GameLog.error("TargetSell", "BenefitSystemMsgAdapter[connect]", "连接精准营销服失败", e);
			connectComplete.compareAndSet(true, false);
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
			shutDown.compareAndSet(false, true);
			reader.close();
			output.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("关闭与精准服连接时出异常");
		}
	}
	
	
	//启动消息接收线程
	private void startReciver(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (!shutDown.get()) {
					if(isAvaliable()){
						System.out.println("~~~~~~~~~~~~~~~~check remote msg");
						try {
							
							final String reString = reader.readLine();//这个会阻塞
							
							GameWorldFactory.getGameWorld().asynExecute(new Runnable() {
								
								@Override
								public void run() {
									msgService.doTask(reString);
								}
							});
							
						} catch (IOException e) {
							GameLog.error("TargetSell", "BenefitSystemMsgReciver[startReciver]", "读取精准营销消息异常", e);
						}
					}
				}
			}
		}).start();
	}


		
}
