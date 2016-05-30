package com.fy.http.chargeServer;


import java.io.IOException;
import java.util.Properties;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;


public class NettyHttpServer {
	public void start(int port) throws Exception {
		PropertyConfigurator.configure(NettyHttpServer.class.getClassLoader().getResource("log4j.properties"));

		new ClassPathXmlApplicationContext(new String[] { "classpath:applicationContext.xml" });
		
		
		EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap(); // (2)
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class) // (3)
					.childHandler(new ChannelInitializer<SocketChannel>() { // (4)
								@Override
								public void initChannel(SocketChannel ch) throws Exception {
									// server端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
									ch.pipeline().addLast(new HttpResponseEncoder());
									// server端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
									ch.pipeline().addLast(new HttpRequestDecoder());
									ch.pipeline().addLast(new HttpServerInboundHandler());
								}
							}).option(ChannelOption.SO_BACKLOG, 128) // (5)
					.childOption(ChannelOption.SO_KEEPALIVE, false); // (6)

			ChannelFuture f = b.bind(port).sync(); // (7)

			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		NettyHttpServer server = new NettyHttpServer();
		Resource resource = new ClassPathResource("charge.properties");
		int serverPort = 10000;
		try {
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			serverPort = Integer.parseInt(props.getProperty("serverPort"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		server.start(serverPort);
	}
}

