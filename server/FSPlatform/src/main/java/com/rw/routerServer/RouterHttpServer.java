package com.rw.routerServer;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RouterHttpServer {
	
	private ExecutorService service = Executors.newSingleThreadExecutor();
	public void init(){
		
		final int routerPort = 10119;
		final String intranetIp = "localhost";
		
		service.submit(new Runnable() {
			
			@Override
			public void run() {
				try {
					start(intranetIp, routerPort);					
				} catch (Exception e) {
					System.out.println("RouterHttpServer启动失败，请检查配置，routerPort:" + routerPort);
					e.printStackTrace();
				}
			}
			
		});
	}
	
	public void start(String host, int routerPort) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
							ch.pipeline().addLast(new StringDecoder());
							ch.pipeline().addLast(new RouterInboundHandler());
						}
					}).option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture f = b.bind(host, routerPort).sync();
			System.out.println("router server started, port:" + routerPort);
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}