package com.rw.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.logging.LoggingHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.log.GameLog;
import com.rw.netty.FrameDecoder;
import com.rw.netty.ProtobufFrameEncoder;
import com.rwproto.ResponseProtos.Response;

public class Client  implements Runnable{
	
	private int id;
	private long lastUseTime;   //上次使用时间
	private String host;        //host
	private int port;           //端口
	private String lastHost;    //最后要处理的host
	private int lastPort;       //最后要处理的port
	private int status;         //客户端连接的状态
	private Channel channel;
	private ConcurrentLinkedQueue<GSMessage> ProcessMsgList = new ConcurrentLinkedQueue<GSMessage>();
	
	private HashMap<String, List<GSMessage>> ProcessMap = new HashMap<String, List<GSMessage>>();
	
	private ClientMsg clientMsg = new ClientMsg();
	
	public Client(int id){
		this.id = id;
		this.status = ClientManager.CLIENT_STATUS_FREE;
	}
	
	private void Connect(String host, int port){
		this.lastUseTime = System.currentTimeMillis();
		this.host = host;
		this.port = port;
		initClient();
	}
	
	@SuppressWarnings("deprecation")
	private void initClient(){
		
		try {
			EventLoopGroup group = new NioEventLoopGroup();
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class);
			bootstrap.handler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel ch) throws Exception {
					// TODO Auto-generated method stub
					ChannelPipeline pipeline = ch.pipeline();
					pipeline.addLast("frameDecoder", new FrameDecoder());
					pipeline.addLast("log1", new LoggingHandler());
					ch.pipeline().addLast("protobufDecoder", new ProtobufDecoder(Response.getDefaultInstance()));
					ch.pipeline().addLast("log2", new LoggingHandler());
					ch.pipeline().addLast("clientHandler", new ClientHandler());

					ch.pipeline().addLast("frameEncoder", new ProtobufFrameEncoder());
					ch.pipeline().addLast("log3", new LoggingHandler());
					ch.pipeline().addLast("protobufEncoder", new ProtobufEncoder());
					ch.pipeline().addLast("log4", new LoggingHandler());
				}
			});
			bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
			channel = bootstrap.connect(host, port).sync().channel();
		} catch (Exception ex) {
			GameLog.error("创建游戏服客户端失败(host:" + host + ",port:" + port + ")"
					+ ex.getMessage());
		}
	}
	
	public void addProcessMsg(GSMessage gsMsg){
		synchronized (this) {
			clientMsg.addProcessMsg(gsMsg);
			this.lastHost = gsMsg.getHost();
			this.lastPort = gsMsg.getPort();
			notify();
		}
	}
	
	/**
	 * 消息处理的长度
	 * @return
	 */
	public int getProcessLength(){
		return this.ProcessMsgList.size();
	}

	public long getLastUseTime() {
		return lastUseTime;
	}

	public void setLastUseTime(long lastUseTime) {
		this.lastUseTime = lastUseTime;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getId() {
		return id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getLastHost() {
		return lastHost;
	}

	public void setLastHost(String lastHost) {
		this.lastHost = lastHost;
	}

	public int getLastPort() {
		return lastPort;
	}

	public void setLastPort(int lastPort) {
		this.lastPort = lastPort;
	}
	
	public void removeProcessMsg(String accountId) {
		synchronized (this) {
			clientMsg.removeProcessMsgByAccountId(accountId);
		}
	}

	public void run() {
		// TODO Auto-generated method stub
		GSMessage gsMsg = null;
		try {
			for (;;) {
				
				synchronized (this) {
					if (clientMsg.getMsgListSize() <= 0) {
						wait();
					}
					gsMsg = clientMsg.pollMsg();
				}
				if (gsMsg != null) {
					if (channel == null || !channel.isActive()) {
						Connect(gsMsg.getHost(), gsMsg.getPort());
					}
					String currentAddress = channel.remoteAddress().toString();
					if (currentAddress.indexOf(gsMsg.getHost()) != -1
							&& currentAddress.indexOf(gsMsg.getPort()) != -1 && !channel.isOpen()) {
						Connect(gsMsg.getHost(), gsMsg.getPort());
					}
					setLastUseTime(System.currentTimeMillis());
					channel.writeAndFlush(gsMsg.getRequest());
					gsMsg = null;
				}
			}
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
}
