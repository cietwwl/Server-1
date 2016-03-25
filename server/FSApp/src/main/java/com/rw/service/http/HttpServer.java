package com.rw.service.http;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rw.manager.GameManager;
import com.rw.service.http.platformResponse.PlatformNoticeBaseDataResponse;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpServer {
	
	public static void httpServerStart(final int port){
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				start(port);
			}
		});
		thread.start();
	}
	
	public static void start(int port) {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch)
								throws Exception {
							// TODO Auto-generated method stub
							ch.pipeline().addLast(new HttpResponseEncoder());
							ch.pipeline().addLast(new HttpRequestDecoder());
							ch.pipeline().addLast(new HttpServerInbounHandler());
						}

					}).option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture future = b.bind(port).sync();
			future.channel().closeFuture().sync();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
	
	/**
	 * 打包response
	 * @param request
	 * @param responseMsg
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static FullHttpResponse packResponse(HttpRequest request, String responseMsg) throws UnsupportedEncodingException{
		if(responseMsg == null){
			responseMsg = "SUCCESS";
		}
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(responseMsg.getBytes("UTF-8")));
		response.headers().set(CONTENT_TYPE, "text/plain");
		response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
		if(HttpHeaders.isKeepAlive(request)){
			response.headers().set(CONNECTION, Values.KEEP_ALIVE);
		}
		return response;
	}
	
	public static boolean checkConnectOpen(String strUrl){
		ObjectOutputStream output = null;
		try {
			URL url = new URL(strUrl);
			URLConnection connection = url.openConnection();
			connection.setReadTimeout(200);
			connection.setDoOutput(true);
			output = new ObjectOutputStream(connection.getOutputStream());
			return true;
		} catch (Exception ex) {
			return false;
		}finally{

			try {
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static boolean checkConnectOpen(List<String> urls) {
		ObjectOutputStream output = null;
		boolean success = false;
		try {
			for (String url : urls) {
				output = null;
				URL tempUrl = new URL(url);
				URLConnection connection = tempUrl.openConnection();
				connection.setDoOutput(true);
				output = new ObjectOutputStream(connection.getOutputStream());
				success = true;
			}
		} catch (Exception ex) {

		} finally {
			try {
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return success;
	}
}
