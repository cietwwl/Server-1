package com.bm.targetSell.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import com.log.GameLog;
import com.rwbase.gameworld.GameWorldFactory;

/**
 * <pre>
 * 与精准服通讯
 * 这里使用单socket进行多路复用，发送消息时阻塞，要进行优化
 * </pre> 
 * @author Alex
 * 2016年9月23日 下午2:35:32
 */
public class BenefitSystemMsgAdapter {

	//用于计算包头的key
	private final static int MSG_KEY = 13542;
	
	private Socket socket;
	
	private DataOutputStream output;
	
	private DataInputStream reader;

	private final SocketAddress remoteAddress;
	
	private final SocketAddress localAddress;
	
	private int timeoutMillis;

	private BenefitSystemMsgService msgService = BenefitSystemMsgService.getHandler();
	
	//停服标记
	private AtomicBoolean shutDown = new AtomicBoolean(false);
	
	//连接成功标记
	private AtomicBoolean connectComplete = new AtomicBoolean(false);
	
	public BenefitSystemMsgAdapter(String host, int port, int localPort, int timeoutMillis) {
		remoteAddress = new InetSocketAddress(host, port);
		localAddress = new InetSocketAddress(localPort);
		this.timeoutMillis = timeoutMillis;
		connect();
		startReciver();
	}
	
	private Socket createSocket(){
		Socket socket = new Socket();
		try {
			//不监视连接是否有效，这样不会监听主动重连
			socket.setKeepAlive(false);
			socket.setTcpNoDelay(true);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return socket;
	}
	
	/**
	 * 检查连接是否可用
	 * @return
	 */
	public boolean isAvaliable(){
		return socket != null && socket.isConnected() && !socket.isClosed() && connectComplete.get();
	}
	
	// 网络重连
	public boolean connect(){
//		System.out.println("================try to connet target sell server~");
		try {
			this.socket = createSocket();//重新创建一个
			this.socket.bind(localAddress);//绑定本地端口
			socket.connect(remoteAddress , timeoutMillis);//这个会阻塞,到超时或连接成功
			this.output = new DataOutputStream(socket.getOutputStream());
			this.reader = new DataInputStream(socket.getInputStream());
			System.out.println("BenefitSystemMsgAdapter.connect() sucess~~");
			connectComplete.compareAndSet(false, true);
			
		} catch (Exception e) {
			GameLog.error("TargetSell", "BenefitSystemMsgAdapter[connect]", "连接精准营销服失败,msg:" + e.getMessage(), null);
			closeSocket();
		}
		return connectComplete.get();
	}
	
	/**
	 * 向精准服发送消息 
	 * @param content 消息内容
	 */
	public void sendMsg(String content){
		try {
			if(!isAvaliable()){
				return;
			}
			
			output.write(dataFormat(content));
			output.flush();
			System.out.println("发送消息到精准服：" + content);
		} catch (Exception e) {
			e.printStackTrace();
			closeSocket();
		}
	}
	
	/**
	 * <pre>
	 * 格式化数据，添加包头
	 * 包头计算：包头(4字节)int=content长度^13542(4字节)
	 * </pre>
	 * @param content
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private byte[] dataFormat(String content) throws UnsupportedEncodingException{
		byte[] contentBytes = content.getBytes("utf-8");
		int contentLenght = contentBytes.length;
		ByteBuffer dataBuffer = ByteBuffer.allocate(4 + contentLenght);//创建数据包，大小为包头长度+包体长度
		//添加包头
		int header = contentLenght ^ MSG_KEY;
		dataBuffer.putInt(header);
		dataBuffer.put(contentBytes);
		return dataBuffer.array();
	}
	
	/**
	 * 拆包获取包体内容
	 * @param headerContent 包头内容
	 * @return
	 * @throws IOException 
	 */
	private String decodeData(int headerContent) throws IOException{
		int bodyLen = headerContent ^ MSG_KEY;
		System.out.println("----------------recv msg, msg lenght:" + bodyLen);
		byte[] temp = new byte[bodyLen];
		reader.read(temp);
		return new String(temp,"utf-8");
	}
	
	/**
	 * 停服通知
	 */
	public void shutdown(){
		shutDown.compareAndSet(false, true);
		closeSocket();
	}
	
	
	//启动消息接收线程
	private void startReciver(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (!shutDown.get()) {
					if(isAvaliable()){
//						System.out.println("~~~~~~~~~~~~~~~~check remote msg");
						try {
							
							String reString = null;
							
							int len = 0;//收到的数据包头内容
							
							while ((len = reader.readInt()) != -1) {
								
								reString = decodeData(len);
								System.out.println("recv response :" + reString);
								GameWorldFactory.getGameWorld().asynExecute(new ResponseTask(reString));
							}
							
							
						} catch (IOException e) {
							GameLog.error("TargetSell", "BenefitSystemMsgReciver[startReciver]", "读取精准营销消息异常", e);
							closeSocket();
						}
					}
				}
			}
		}).start();
	}
	
	
	/**
	 * 关闭旧连接  
	 */
	private void closeSocket(){
		try {
			reader.close();
			output.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private class ResponseTask implements Runnable{

		private String content;
		
		
		
		public ResponseTask(String content) {
			this.content = content;
		}



		@Override
		public void run() {
			msgService.doTask(content);
		}
		
	}
		
}
