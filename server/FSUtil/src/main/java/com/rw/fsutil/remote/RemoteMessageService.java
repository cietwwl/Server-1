package com.rw.fsutil.remote;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.rw.fsutil.cacheDao.FSUtilLogger;
import com.rw.fsutil.remote.parse.FSMessageDecoder;
import com.rw.fsutil.remote.parse.FSMessageEncoder;
import com.rw.fsutil.remote.parse.FSMessageExecutor;
import com.rw.fsutil.util.RandomUtil;

/**
 * <pre>
 * 远程消息服务的封装
 * 使用者需要定义远程ip、端口号、线程数、最大连接数
 * 需要定义{@link FSMessageEncoder}发送消息编码器、{@link FSMessageDecoder}接收消息解码器
 * {@link FSMessageExecutor}接收消息处理器，注意：消息处理是在io线程中执行
 * </pre>
 * 
 * @author Jamaz
 *
 * @param <SendMessage>
 * @param <ReceiveMessage>
 */
public class RemoteMessageService<SendMessage, ReceiveMessage> {

	private final String remoteHost;
	private final int remotePort;
	private final int type;
	private final String portString;
	private volatile ArrayList<Channel> channels;
	private final Bootstrap bootstrap;
	private final int maxConnection;
	private final RemoteMessageDecoder<ReceiveMessage> decoder;
	private final RemoteMessageEncoder<SendMessage> encoder;
	private final FSMessageExecutor<ReceiveMessage> executor;
	private final AtomicLong sendFailStat;
	private final AtomicLong sendSuccessStat;
	private final AtomicLong sendRejectStat;

	public RemoteMessageService(int type, String host, int port, int threadCount, int maxConnection,
			FSMessageDecoder<ReceiveMessage> decoder, FSMessageEncoder<SendMessage> encoder, FSMessageExecutor<ReceiveMessage> executor) {
		this.remoteHost = host;
		this.remotePort = port;
		this.type = type;
		this.portString = String.valueOf(port);
		this.maxConnection = maxConnection;
		NioEventLoopGroup eventGroup = new NioEventLoopGroup(threadCount);
		this.bootstrap = new Bootstrap();
		this.bootstrap.group(eventGroup).channel(NioSocketChannel.class).handler(new ChannelInit());
		this.channels = new ArrayList<Channel>();
		this.decoder = new RemoteMessageDecoder<ReceiveMessage>(decoder);
		this.encoder = new RemoteMessageEncoder<SendMessage>(encoder);
		this.executor = executor;
		this.sendFailStat = new AtomicLong();
		this.sendSuccessStat = new AtomicLong();
		this.sendRejectStat = new AtomicLong();

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
			ch.pipeline().addLast(new RemoteServerHandler<SendMessage, ReceiveMessage>(RemoteMessageService.this));
			ch.pipeline().addLast(new RemoteMessageEncoderWrap<SendMessage>(encoder));
		}
	};

	public void checkAndCreate() {
		if (channels.size() < RemoteMessageService.this.maxConnection) {
			bootstrap.connect(remoteHost, remotePort).addListener(new GenericFutureListener<Future<? super Void>>() {

				@Override
				public void operationComplete(Future<? super Void> future) throws Exception {
					if (future.isSuccess()) {
						sendSuccessStat.incrementAndGet();
					} else {
						sendFailStat.incrementAndGet();
					}
				}
			});
		}
	}

	protected void add(ChannelHandlerContext ctx) {
		Channel channel = ctx.channel();
		String address = channel.remoteAddress().toString();
		if (!address.contains(remoteHost) || !address.contains(portString)) {
			FSUtilLogger.error("illegal remote address=" + address + ",expect=" + remoteHost + ":" + portString);
			return;
		}
		synchronized (this) {
			ArrayList<Channel> list = new ArrayList<Channel>(this.channels);
			list.add(channel);
			this.channels = list;
		}
		FSUtilLogger.error("remote connection active:" + type + ",address=" + address);
	}

	protected void remove(ChannelHandlerContext ctx) {
		Channel channel = ctx.channel();
		synchronized (this) {
			ArrayList<Channel> list = new ArrayList<Channel>(this.channels);
			list.remove(channel);
			this.channels = list;
		}
		FSUtilLogger.error("remote connection closed:" + type + ",address=" + ctx.channel().remoteAddress());
	}

	public boolean sendMsg(String content) {
		ArrayList<Channel> current = channels;
		int size = current.size();
		if (size == 0) {
			sendRejectStat.incrementAndGet();
			return false;
		}

		int index = RandomUtil.getRandom().nextInt(size);
		int maxIndex = size - 1;
		// writable
		for (int i = 0; i < size; i++) {
			Channel channel = current.get(index);
			if (channel.isWritable()) {
				channel.writeAndFlush(content);
				return true;
			}
			if (index == maxIndex) {
				index = 0;
			} else {
				index++;
			}
		}
		// active
		for (int i = 0; i < size; i++) {
			Channel channel = current.get(index);
			if (channel.isActive()) {
				channel.writeAndFlush(content);
				return true;
			}
			if (index == maxIndex) {
				index = 0;
			} else {
				index++;
			}
		}
		sendRejectStat.incrementAndGet();
		return false;
	}

	public FSMessageExecutor<ReceiveMessage> getExecutor() {
		return executor;
	}

	/**
	 * 获取发送消息被拒绝数
	 * @return
	 */
	public long getSendRejectCount() {
		return this.sendRejectStat.get();
	}

	/**
	 * 获取发送消息成功数
	 * @return
	 */
	public long getSendSuccessCount() {
		return this.sendSuccessStat.get();
	}

	/**
	 * 获取发送消息失败数
	 * @return
	 */
	public long getSendFailCount() {
		return this.sendFailStat.get();
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public int getType() {
		return type;
	}

	/**
	 * 获取当前连接数
	 * @return
	 */
	public int getConnectionCount() {
		return channels.size();
	}
}
