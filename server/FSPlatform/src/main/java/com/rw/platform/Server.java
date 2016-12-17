package com.rw.platform;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.HashMap;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.log.PlatformLog;
import com.rw.common.DataService;
import com.rw.common.SynTaskExecutor;
import com.rw.fsutil.cacheDao.DataKVDao;
import com.rw.fsutil.cacheDao.loader.DataExtensionCreator;
import com.rw.fsutil.dao.optimize.DataAccessFactory;
import com.rw.netty.FrameDecoder;
import com.rw.netty.ProtobufFrameEncoder;
import com.rw.netty.ServerHandler;
import com.rw.netty.http.HttpServer;
import com.rw.routerServer.RouterHttpServer;
import com.rwproto.RequestProtos.Request;



public class Server {
	public static final boolean isDebug=true;
//	public static final boolean isDebug=false;
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		PropertyConfigurator.configure(Server.class.getClassLoader().getResource("log4j.properties"));
    	
    	new ClassPathXmlApplicationContext(new String[] { "classpath*:applicationContext*.xml"});
    	
    	
    	PlatformFactory.init();
    	SynTaskExecutor.init();
    	DataService.initDataService();
    	RouterHttpServer.getInstance().init();
		EventLoopGroup bossEventLoopGroup = new NioEventLoopGroup();
		int ioThreads = Runtime.getRuntime().availableProcessors()+1;
		EventLoopGroup ioGroup = new NioEventLoopGroup(128);
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossEventLoopGroup, ioGroup);
			
			
			serverBootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
			serverBootstrap.childOption( ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
			
			serverBootstrap.channel(NioServerSocketChannel.class);
			serverBootstrap.childHandler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast("idle", new IdleStateHandler(0, 0, 180));
					ch.pipeline().addLast("frameDecoder", new FrameDecoder());
					
					// 构造函数传递要解码成的类型
					ch.pipeline().addLast("protobufDecoder", new ProtobufDecoder(Request.getDefaultInstance()));
					ch.pipeline().addLast("serverHandler", new ServerHandler());
					// 编码
					ch.pipeline().addLast("frameEncoder", new ProtobufFrameEncoder());
					ch.pipeline().addLast("protobufEncoder", new ProtobufEncoder());
				};
			});
			
			serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
			System.out.println("---------------------------Platfrom Start Finish-------------------------------");
			ChannelFuture channelFuture = serverBootstrap.bind(new InetSocketAddress(PlatformFactory.getPort())).sync();
			
			channelFuture.channel().closeFuture().sync();
			
		} catch (Exception e) {
			PlatformLog.error(e);
		} finally {
			bossEventLoopGroup.shutdownGracefully();
			ioGroup.shutdownGracefully();
		}
	}
}