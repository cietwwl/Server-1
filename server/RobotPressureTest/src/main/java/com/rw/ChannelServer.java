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
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.Attribute;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.net.handler.ClientInboundHandler;
import com.net.parse.FrameDecoder;
import com.net.parse.FrameEncoder;
import com.rw.common.RobotLog;
import com.rwproto.ResponseProtos.Response;

public class ChannelServer {

	private static ChannelServer instance = new ChannelServer();

	public static ChannelServer getInstance() {
		return instance;
	}

	private Bootstrap bootstrap;

	public static final AttributeKey<Client> ATTR_CLIENT = AttributeKey.valueOf("client");
	// private Map<Channel, Client> ccMap = new ConcurrentHashMap<Channel,
	// Client>() ;

	private Map<Client, Channel> ccReverMap = new ConcurrentHashMap<Client, Channel>();

	public ChannelServer() {
		EventLoopGroup eventGroup = new NioEventLoopGroup(16);// 创建处理事件的线程池
		bootstrap = new Bootstrap();
		bootstrap.group(eventGroup).channel(NioSocketChannel.class)
		// .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 512*1024)
		// .option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 256*1024)
		// .channel(OioSocketChannel.class)
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

	public Channel getChannel(Client client) {
		return ccReverMap.get(client);
	}

	public Client getClient(Channel channel) {
		Attribute<Client> userIdAttr = channel.attr(ATTR_CLIENT);
		return userIdAttr.get();
	}

	public void remove(final Client client) {
		Channel oldChannel = ccReverMap.get(client);
		try {
			// RobotLog.testError("close start1:"+client.getAccountId()+","+client.getLastSeqId());
			ChannelFuture f = oldChannel.close();
			f.addListener(new GenericFutureListener<Future<? super Void>>() {

				@Override
				public void operationComplete(Future<? super Void> future) throws Exception {
					RobotLog.testError("close start2:" + client.getAccountId() + "," + client.getCommandInfo());
				}
			});
			f.get(10, TimeUnit.SECONDS);
		}catch(TimeoutException e){
			RobotLog.testError("close timeout:"+client.getAccountId()+",seqId="+client.getCommandInfo());
		}
		catch (Exception e) {
			// donothing
		}
		// if(oldChannel!=null){
		// ccMap.remove(oldChannel);
		// }
		ccReverMap.remove(client);
	}

	public boolean doConnect(Client client, final String host, final int port) {
		remove(client);
		try {
			ChannelFuture connect = bootstrap.connect(host, port);
			if (connect.await(30, TimeUnit.SECONDS)) {
				Channel newChannel = connect.channel();

				Attribute<Client> userIdAttr = newChannel.attr(ATTR_CLIENT);
				Client oldClient = userIdAttr.get();
				if (oldClient != null) {
					RobotLog.testError("fatal error:exist old client=" + oldClient.getAccountId());
				}
				userIdAttr.set(client);
				ccReverMap.put(client, newChannel);

				RobotLog.testError("connect success:" + client.getAccountId());
				return true;
			} else {
				RobotLog.testError("connect fail:"+client.getAccountId());
				connect.cancel(true);
				return false;
			}
		} catch (Exception e) {
			RobotLog.fail("client doConnect fail", e);
		}
		return false;
	}

}
