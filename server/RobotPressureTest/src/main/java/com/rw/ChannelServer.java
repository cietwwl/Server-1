package com.rw;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.net.handler.ClientInboundHandler;
import com.net.parse.FrameDecoder;
import com.net.parse.FrameEncoder;
import com.rw.common.RobotLog;
import com.rwproto.ResponseProtos.Response;

public class ChannelServer {


	private static ChannelServer instance =  new ChannelServer();
	
	public static ChannelServer getInstance(){
		return instance;
	}
	
	private Bootstrap bootstrap;
	
	private Map<Channel, Client> ccMap = new ConcurrentHashMap<Channel, Client>() ;
	
	private Map<Client,Channel> ccReverMap = new ConcurrentHashMap<Client,Channel>() ;
	
	
	public ChannelServer(){
		EventLoopGroup eventGroup = new NioEventLoopGroup(128);// 创建处理事件的线程池
		bootstrap = new Bootstrap();
		bootstrap.group(eventGroup)
		.channel(NioSocketChannel.class)
//		.option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 512*1024)
//		.option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 256*1024)
//		.channel(OioSocketChannel.class)
		.handler(new ChannelInit());
	}
	
	
	private class ChannelInit extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast("frameDecoder", new FrameDecoder());// 消息解码
			ch.pipeline().addLast("protobufDecoder", new ProtobufDecoder(Response.getDefaultInstance()));// protobuf的解码
			ch.pipeline().addLast("clientHandler", new ClientInboundHandler());// 消息对应的处理
			ch.pipeline().addLast("frameEncoder", new FrameEncoder());// 消息编码
			ch.pipeline().addLast("protobufEncoder", new ProtobufEncoder());// protobuf的编码
		}
	}
	
	public Channel getChannel(Client client){
		return ccReverMap.get(client);
	}
	
	public Client getClient(Channel channel){
		return ccMap.get(channel);
	}
	
	public void remove(Client client){
		Channel oldChannel = ccReverMap.get(client);
		try {
			oldChannel.close();
		} catch (Exception e) {
			// donothing
		}
		if(oldChannel!=null){
			ccMap.remove(oldChannel);
		}
		ccReverMap.remove(client);
	}
	
	public boolean doConnect(Client client, final String host, final int port) {
		remove(client);

		try {
			ChannelFuture connect = bootstrap.connect(host, port);
			if (connect.await(30, TimeUnit.SECONDS)) {
				Channel newChannel = connect.channel();
				
				ccMap.put(newChannel, client);				
				ccReverMap.put(client, newChannel);
				
				RobotLog.info("生成新的channel  accountId是：" + client.getAccountId());
				return true;
			} else {
				RobotLog.info("连接超时！网络链路不通！！");
				connect.cancel(true);
				return false;
			}
		} catch (Exception e) {
			RobotLog.fail("client doConnect fail", e);
		}
		return false;
	}

	
}
