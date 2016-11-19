package com.rw.fsutil.remote;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.rw.fsutil.remote.parse.FSMessageDecoder;
import com.rw.fsutil.remote.parse.FSMessageEncoder;
import com.rw.fsutil.remote.parse.FSMessageExecutor;
import com.rw.fsutil.util.RandomUtil;

public class RemoteChannelServer<SendMessage, ReceiveMessage> {

	private final String host;
	private final int port;
	private final String portString;
	private volatile ArrayList<Channel> channels;
	private final Bootstrap bootstrap;
	private final int maxConnection;
	private final RemoteMessageDecoder<ReceiveMessage> decoder;
	private final RemoteMessageEncoder<SendMessage> encoder;
	private final FSMessageExecutor<ReceiveMessage> executor;

	public RemoteChannelServer(String host, int port, int threadCount, int maxConnection,
			FSMessageDecoder<ReceiveMessage> decoder, FSMessageEncoder<SendMessage> encoder, FSMessageExecutor<ReceiveMessage> executor) {
		this.host = host;
		this.port = port;
		this.portString = String.valueOf(port);
		this.maxConnection = maxConnection;
		NioEventLoopGroup eventGroup = new NioEventLoopGroup(threadCount);
		this.bootstrap = new Bootstrap();
		this.bootstrap.group(eventGroup).channel(NioSocketChannel.class).handler(new ChannelInit());
		this.channels = new ArrayList<Channel>();
		this.decoder = new RemoteMessageDecoder<ReceiveMessage>(decoder);
		this.encoder = new RemoteMessageEncoder<SendMessage>(encoder);
		this.executor = executor;

		eventGroup.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				checkAndCreate();
			}
		}, 1, 1, TimeUnit.SECONDS);
	}

	private class ChannelInit extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast(new RemoteMessageDecoderWrap<ReceiveMessage>(decoder));
			ch.pipeline().addLast(new RemoteServerHandler(RemoteChannelServer.this));
			ch.pipeline().addLast(new RemoteMessageEncoderWrap<SendMessage>(encoder));
		}
	};

	public void checkAndCreate() {
		if (channels.size() < RemoteChannelServer.this.maxConnection) {
			bootstrap.connect(host, port);
		}
	}

	protected void add(ChannelHandlerContext ctx) {
		Channel channel = ctx.channel();
		synchronized (this) {
			ArrayList<Channel> list = new ArrayList<Channel>(this.channels);
			list.add(channel);
			this.channels = list;
		}
	}

	protected void remove(ChannelHandlerContext ctx) {
		Channel channel = ctx.channel();
		synchronized (this) {
			ArrayList<Channel> list = new ArrayList<Channel>(this.channels);
			list.remove(channel);
			this.channels = list;
		}
	}

	public boolean sendMsg(String content) {
		ArrayList<Channel> current = channels;
		int size = current.size();
		if (size == 0) {
			return false;
		}
		// TODO 后续会继续做优化
		int index = RandomUtil.getRandom().nextInt(size);

		Channel channel = current.get(index);
		if (channel.isActive()) {
			channel.writeAndFlush(content);
		}
		return true;
	}

	public FSMessageExecutor<ReceiveMessage> getExecutor() {
		return executor;
	}

}
