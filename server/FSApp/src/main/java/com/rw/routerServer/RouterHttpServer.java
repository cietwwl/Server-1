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

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rw.netty.ServerConfig;

public class RouterHttpServer {
	
	private ExecutorService service = Executors.newSingleThreadExecutor();
	
	public void init(){

		final int routerPort = ServerConfig.getInstance().getServeZoneInfo().getUcGiftRounterPort();
		
		service.submit(new Runnable() {
			
			@Override
			public void run() {
				try {
					start(routerPort);
				} catch (Exception e) {
					System.out.println("RouterHttpServer启动失败，请检查配置，routerPort:" + routerPort);
					e.printStackTrace();
				}
			}
			
		});
	}
	
	public void start(int routerPort) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new LineBasedFrameDecoder(409600));
							ch.pipeline().addLast(new StringDecoder());
							ch.pipeline().addLast(new RouterInboundHandler());
						}
					}).option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			InetSocketAddress address = new InetSocketAddress(routerPort);
			ChannelFuture f = b.bind(address).sync();
			System.out.println("router server started, port:" + routerPort);
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}