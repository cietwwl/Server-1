package com.rw.gameRouterServer;


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

import com.log.GameLog;
import com.log.LogModule;

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
					GameLog.error(LogModule.RouterServer, "RouterHttpServer[start]", "重置服务启动失败，请检查配置。chargePort:"+routerPort, e );
				}
				
			}
		});
	}
	
	public void start(String host, int routerPort) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap(); // (2)
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class) // (3)
					.childHandler(new ChannelInitializer<SocketChannel>() { // (4)
//								@Override
//								public void initChannel(SocketChannel ch) throws Exception {
//									// server端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
//									ch.pipeline().addLast(new HttpResponseEncoder());
//									// server端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
//									ch.pipeline().addLast(new HttpRequestDecoder());
//									ch.pipeline().addLast(new RouterInboundHandler());
//								}
								
						@Override
						 public void initChannel(SocketChannel ch) throws Exception {
							 ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
							 ch.pipeline().addLast(new StringDecoder());
							 ch.pipeline().addLast(new RouterInboundHandler());
						 }
					}).option(ChannelOption.SO_BACKLOG, 128) // (5)
					.childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

			ChannelFuture f = b.bind(host, routerPort).sync(); // (7)
			System.out.println("router server started, port:" + routerPort);
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}

