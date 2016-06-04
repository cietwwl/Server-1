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
import com.rwbase.common.attribute.AttributeBM;
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
		System.out.println("test");
		ServerSwitch.initProperty();

		// 初始化属性的映射关系
		AttributeBM.initAttributeMap();

		// GameManager.initBeforeLoading();
		new ClassPathXmlApplicationContext(new String[] { "classpath:applicationContext.xml" });

		EventLoopGroup bossEventLoopGroup = new NioEventLoopGroup();
		int ioThreads = Runtime.getRuntime().availableProcessors() * 2;
		// new PrintServerState().startPrintState();
		EventLoopGroup workerEventLoopGroup = new NioEventLoopGroup(ioThreads);
		try {
			// 检查所有配置文件，如果配置有问题，请打印日志报告错误，并抛异常中断启动过程
			GameManager.CheckAllConfig();

			// 初始化所有后台服务
			GameManager.initServiceAndCrontab();

//			// lida 2015-08-21 启动http通信端口
//			int httpPort = GameManager.getHttpPort();
//			HttpServer.httpServerStart(httpPort);

			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
			serverBootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
			serverBootstrap.group(bossEventLoopGroup, workerEventLoopGroup);
			serverBootstrap.channel(NioServerSocketChannel.class);
			serverBootstrap.childHandler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast("idle", new IdleStateHandler(0, 0, 180));
					ch.pipeline().addLast("frameDecoder", new FrameDecoder());
					// 构造函数传递要解码成的类型
					ch.pipeline().addLast("protobufDecoder", new ProtobufDecoder(Request.getDefaultInstance()));
					ch.pipeline().addLast("serverHandler", new ServerHandler());
					ch.pipeline().addLast("frameEncoder", new ProtobufFrameEncoder());
					ch.pipeline().addLast("protobufEncoder", new ProtobufEncoder());
				};
			});

			serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
			int port = Integer.valueOf(ServerConfig.getInstance().getServeZoneInfo().getPort());
			ChannelFuture channelFuture = serverBootstrap.bind(new InetSocketAddress(port)).sync();
			channelFuture.channel().closeFuture().sync();
		} catch (Throwable e) {
			e.printStackTrace();
			GameLog.error("Server", "Server[]main", "", e);
			System.exit(0);
		} finally {
			bossEventLoopGroup.shutdownGracefully();
			workerEventLoopGroup.shutdownGracefully();
		}
	}
}