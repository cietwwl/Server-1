package com.rw.netty;

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

import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.log.GameLog;
import com.rw.manager.GameManager;
import com.rw.manager.ServerSwitch;
import com.rw.service.http.HttpServer;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwproto.RequestProtos.Request;

public class Server {
	public static final boolean isDebug = true;

	// public static final boolean isDebug=false;
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		GameWorldFactory.init(64, 16);
		PropertyConfigurator.configure(Server.class.getClassLoader().getResource("log4j.properties"));

		GameManager.initServerProperties();

		ServerSwitch.initProperty();

		new ClassPathXmlApplicationContext(new String[] { "classpath:applicationContext.xml" });

		EventLoopGroup bossEventLoopGroup = new NioEventLoopGroup();
		int ioThreads = Runtime.getRuntime().availableProcessors() * 4;
		// new PrintServerState().startPrintState();
		EventLoopGroup workerEventLoopGroup = new NioEventLoopGroup(128);
		// final EventExecutorGroup pool = new DefaultEventExecutorGroup(512);
		try {
			//检查所有配置文件，如果配置有问题，请打印日志报告错误，并抛异常中断启动过程
			GameManager.CheckAllConfig();
			
			// 初始化所有后台服务
			GameManager.initServiceAndCrontab();

			// lida 2015-08-21 启动http通信端口
			int httpPort = GameManager.getHttpPort();
			HttpServer.httpServerStart(httpPort);

			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
			serverBootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
			serverBootstrap.group(bossEventLoopGroup, workerEventLoopGroup);
			serverBootstrap.channel(NioServerSocketChannel.class);
			serverBootstrap.childHandler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast("idle", new IdleStateHandler(0, 0, 180));
					// ch.pipeline().addLast("log1", new LoggingHandler());
					// ch.pipeline().addLast("frameDecoder", new ProtobufFrameDecoder());
					ch.pipeline().addLast("frameDecoder", new FrameDecoder());

					// ch.pipeline().addLast("log2", new LoggingHandler());
					// ch.pipeline().addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
					// 构造函数传递要解码成的类型
					ch.pipeline().addLast("protobufDecoder", new ProtobufDecoder(Request.getDefaultInstance()));
					// handle in thread pool
					// ch.pipeline().addLast(pool, new ServerHandler());
					ch.pipeline().addLast("serverHandler", new ServerHandler());
					// 编码
					// ch.pipeline().addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
					ch.pipeline().addLast("frameEncoder", new ProtobufFrameEncoder());
					// ch.pipeline().addLast("log3", new LoggingHandler());
					ch.pipeline().addLast("protobufEncoder", new ProtobufEncoder());
					// ch.pipeline().addLast("log4", new LoggingHandler());
				};
			});

			serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
			int port = Integer.valueOf(ServerConfig.getInstance().getServeZoneInfo().getPort());
			ChannelFuture channelFuture = serverBootstrap.bind(new InetSocketAddress(port)).sync();
			channelFuture.channel().closeFuture().sync();
		} catch (Exception e) {
			// e.printStackTrace();
			GameLog.error("Server", "Server[]main", "", e);
		} finally {
			bossEventLoopGroup.shutdownGracefully();
			workerEventLoopGroup.shutdownGracefully();
		}
	}
}