package com.rw.fsutil.remote;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;

import com.rw.fsutil.remote.handler.MessageSendFailHandler;

public class RemoteServiceSender<SendMessage, ReceiveMessage> {

	private static Logger remoteMsgLogger = Logger.getLogger("remoteMsgLogger");
	private final MessageSendFailHandler<SendMessage> sendFailHandler;
	private final int maxCapacity;
	private volatile Channel channel;
	private final AtomicInteger count;
	private final int uniqueId;
	private final AtomicLong sendFailStat;
	private final AtomicLong sendSuccessStat;
	private final AtomicLong sendRejectStat;
	private final Bootstrap bootstrap;
	private final RemoteMessageService<SendMessage, ReceiveMessage> service;
	private final int halfCapacity;
	private final AtomicReference<Runnable> connectTask;

	public RemoteServiceSender(int uniqueId, int maxCapacity, MessageSendFailHandler<SendMessage> sendFailHandler, RemoteMessageService<SendMessage, ReceiveMessage> service) {
		this.maxCapacity = maxCapacity;
		this.uniqueId = uniqueId;
		this.sendFailHandler = sendFailHandler;
		this.halfCapacity = maxCapacity >> 1;
		this.count = new AtomicInteger();
		this.sendFailStat = new AtomicLong();
		this.sendSuccessStat = new AtomicLong();
		this.sendRejectStat = new AtomicLong();
		this.bootstrap = new Bootstrap();
		this.service = service;
		this.connectTask = new AtomicReference<Runnable>();
		this.bootstrap.group(service.getEventGroup()).channel(NioSocketChannel.class).handler(service.createChannelInitializer(this));
	}

	public boolean sendMsg(SendMessage content) {
		return sendMsg(content, false);
	}

	public boolean sendMsg(SendMessage content, boolean ignoreCapacity) {
		if (!ignoreCapacity && count.get() >= maxCapacity) {
			sendRejectStat.incrementAndGet();
			if (service.isDebugLogger()) {
				remoteMsgLogger.debug("send fail by count=" + count.get() + ",type=" + service.getType() + "," + content);
			}
			return false;
		}
		Channel channel = this.channel;
		if (channel == null || !channel.isActive()) {
			sendRejectStat.incrementAndGet();
			if (service.isDebugLogger()) {
				remoteMsgLogger.debug("send fail by channel=" + channel + ",type=" + service.getType() + "," + content);
			}
			return false;
		}
		count.incrementAndGet();
		boolean isDebugLogger = service.isDebugLogger();
		final SendMessage sendMessage;
		final long msgId;
		if (sendFailHandler != null || isDebugLogger) {
			msgId = service.generateMsgId();
			sendMessage = content;
		} else {
			sendMessage = null;
			msgId = 0;
		}
		if (isDebugLogger) {
			remoteMsgLogger.debug("send current=" + count.get() + ",type=" + service.getType() + ",id=" + uniqueId + ",msgId=" + msgId);
		}
		channel.writeAndFlush(content).addListener(new GenericFutureListener<Future<? super Void>>() {

			@Override
			public void operationComplete(Future<? super Void> future) throws Exception {
				count.decrementAndGet();
				boolean sendSuccess = future.isSuccess();
				if (!sendSuccess) {
					sendFailStat.incrementAndGet();
					if (sendFailHandler != null && sendMessage != null) {
						sendFailHandler.handle(sendMessage);
					}
				} else {
					sendSuccessStat.incrementAndGet();
				}
				if (sendMessage != null) {
					remoteMsgLogger.debug("send " + (sendSuccess ? "success" : "fail") + ",type=" + service.getType() + ",msgId=" + msgId + ",msg=" + sendMessage);
				}
			}
		});
		return true;
	}

	protected void checkAndConnect() {
		if (channel == null || !channel.isActive() && connectTask.get() == null) {
			Runnable task = new Runnable() {

				@Override
				public void run() {
					final ChannelFuture channelFuture = bootstrap.connect(service.getRemoteHost(), service.getRemotePort());
					remoteMsgLogger.info("start connect:type=" + service.getType() + ",host=" + service.getRemoteHost() + ",port=" + service.getRemotePort());
					channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {

						@Override
						public void operationComplete(Future<? super Void> future) throws Exception {
							try {
								boolean success = future.isSuccess();
								if (success) {
									channel = channelFuture.channel();
								}
								remoteMsgLogger.info("connect remote service finish:type=" + service.getType() + channelFuture.channel() + ",result=" + success);
							} finally {
								connectTask.set(null);
							}
						}
					});
				}
			};
			if (connectTask.compareAndSet(null, task)) {
				task.run();
			}
		}
	}

	protected void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.fireChannelActive();
	}

	protected void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ctx.fireChannelInactive();
		this.channel = null;
		remoteMsgLogger.error("remote connection closed:,address=" + ctx.channel().remoteAddress() + ",type" + service.getType() + ",uniqueId=" + uniqueId);
	}

	protected void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			service.getExecutor().execute((ReceiveMessage) msg);
		} finally {
			if (service.isDebugLogger()) {
				remoteMsgLogger.debug("receive type=" + service.getType() + ",id=" + uniqueId + ",msg=" + msg);
			}
		}
	}

	public int getUniqueId() {
		return uniqueId;
	}

	public boolean isActive() {
		Channel channel = this.channel;
		return channel != null && channel.isActive();
	}

	public boolean isWritable() {
		Channel channel = this.channel;
		boolean isWritable = channel != null && channel.isWritable();
		return isWritable && count.get() < halfCapacity;
	}

	public int getCount() {
		return count.get();
	}

	/**
	 * 获取发送消息被拒绝数
	 * @return
	 */
	public long getSendRejectStatCount() {
		return sendRejectStat.get();
	}

	/**
	 * 获取发送消息成功数
	 * @return
	 */
	public long getSendSuccessStatCount() {
		return sendSuccessStat.get();
	}

	/**
	 * 获取发送消息成功数
	 * @return
	 */
	public long getSendFailStatCount() {
		return sendFailStat.get();
	}

}
