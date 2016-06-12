package com.log.logToDataCenter.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.log.logToDataCenter.config.NodeState;
import com.log.logToDataCenter.config.LogConst;
import com.log.logToDataCenter.exception.CannotCreateNodeException;
import com.log.logToDataCenter.exception.NodeMsgQueueDisorderException;
import com.log.logToDataCenter.resultHandler.SendMsgResultHandler;
import com.log.logToDataCenter.resultHandler.ServerResponseHandler;

public final class ChannelNode {
	private static long HEARTBITKEY = -101;

	private final EventLoopGroup senderGroup;
	private final SendMsgResultHandler resultHandler = new SendMsgResultHandler();
	private final ServerResponseHandler responseHandler = new ServerResponseHandler();

	private ConcurrentLinkedQueue<Long> msgQueue = new ConcurrentLinkedQueue<Long>();
	private AtomicInteger queueMemCount = new AtomicInteger();
	private ChannelHandlerContext cf;
	private volatile NodeState nodeState = NodeState.Normal; // 连接状态

	// 测试响应时间的字段
	private long startCheckTime = 0;
	private long endCheckTime = 0;
	private byte onCheckNodeActive = 0;

	// 测试响应时间的锁
	private byte[] checkResponseLock = new byte[0];

	public ChannelNode(EventLoopGroup senderGroup) throws InterruptedException,
			CannotCreateNodeException {
		this.senderGroup = senderGroup;
		connectOrReconnectChannel();
	}

	public void connectOrReconnectChannel() throws InterruptedException,
			CannotCreateNodeException {
		if (cf != null) {
			if (nodeState != NodeState.Ready || nodeState != NodeState.Over)
				throw new CannotCreateNodeException("当前状态不可以创建ChannelNode");
			cf.channel().close();
		}
		nodeState = NodeState.Connecting;
		Bootstrap b = new Bootstrap().group(senderGroup)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.handler(new HeartbeatChannelInitializer());
		b.connect(LogConst.TARGET_ADDR, LogConst.TARGET_PORT).sync();
	}

	public int getMsgQueueCount() {
		return this.queueMemCount.get();
	}

	public boolean isChannelActive() {
		if (nodeState != NodeState.Normal)
			return false;
		return cf.channel().isActive();
	}

	public void sendMessage(final long key, String log)
			throws UnsupportedEncodingException, InterruptedException {
		final String sendeMsg = key + System.getProperty("line.separator");
		final ByteBuf buf = Unpooled.copiedBuffer(sendeMsg.getBytes("UTF-8"));
		cf.channel().eventLoop().execute(new Runnable() {
			public void run() {
				
				cf.channel().writeAndFlush(buf).addListener(new GenericFutureListener<ChannelFuture>() {
					public void operationComplete(ChannelFuture future)
							throws Exception {
						if(future.isSuccess()) addToQueue(key);
						else {
							if(key != HEARTBITKEY) resultHandler.handleResult(false, key);
						}
					}
				});
			}
		});
	}

	private void msgBackSuccess(Object msg) {
		try {
			long key = pollFromQueue();
//			System.out.println((System.currentTimeMillis() - LogToDataCenterSender.startTime)
//					+ ">Data poll: " + key + ">>" + (String)msg);
			assert(Long.valueOf((String) msg) == key);
			if (onCheckNodeActive > 0) {
				endCheckTime = System.nanoTime();
				onCheckNodeActive--;
			}
			if (key != HEARTBITKEY)
				responseHandler.handleResponse(true, key);
		} catch (NodeMsgQueueDisorderException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 处理没有收到成功返回的key
	 * 
	 * @throws CannotCreateNodeException
	 * @throws InterruptedException
	 */
	private synchronized void handleFailedQueue() {
		nodeState = NodeState.HandleFailedQueue;
		for (long key : msgQueue) {
			if (key != HEARTBITKEY)
				resultHandler.handleResult(false, key);
		}
		msgQueue.clear();
		queueMemCount.set(0);
		nodeState = NodeState.Over;
		try {
			connectOrReconnectChannel();
		} catch (Exception ex) {
			System.out.println("Node建立连接失败..." + ex.toString());
			nodeState = NodeState.ConnFail;
		}
	}

	private boolean addToQueue(long id) {
		boolean isAdd = msgQueue.add(id);
		if (isAdd) {
			if (queueMemCount.incrementAndGet() >= LogConst.NODE_MAX_QUEUE_SIZE)
				nodeState = NodeState.Busy;
		}
		return isAdd;
	}

	private long pollFromQueue() throws NodeMsgQueueDisorderException {
		Long id = msgQueue.poll();
		if (id != null) {
			if (queueMemCount.decrementAndGet() < LogConst.NODE_MAX_QUEUE_SIZE && nodeState == NodeState.Busy)
				nodeState = NodeState.Normal;
			return id;
		}
		throw new NodeMsgQueueDisorderException("Msg Queue Disorder");
	}

	private class GameLogClientInboundHandler extends
			SimpleChannelInboundHandler<Object> {
		
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, Object msg)
				throws Exception {
			msgBackSuccess(msg);
			// TODO
//			switch(Integer.valueOf((String) msg)){
//			case 1: //成功
//				break;
//			case 2:  //格式不对
//				break;
//			case 3:  //长度不符合
//				break;
//			default:
//				break;
//			}
		}

		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
				throws Exception {
			if (evt instanceof IdleStateEvent) {
				// TODO
//				IdleStateEvent event = (IdleStateEvent) evt;
//				startCheckResponseTime(event.state().name());
//				System.out.println(ctx.channel().remoteAddress() + "超时类型：" + event.state().name());
			} else {
				super.userEventTriggered(ctx, evt);
			}
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			super.channelActive(ctx);
			nodeState = NodeState.Normal;
			cf = ctx;
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			super.channelInactive(ctx);
			nodeState = NodeState.InActive;
			handleFailedQueue();
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception {
			super.exceptionCaught(ctx, cause);
			cause.printStackTrace();
			nodeState = NodeState.Exception;
			handleFailedQueue();
		}

		@Override
		public void channelUnregistered(ChannelHandlerContext ctx)
				throws Exception {
			super.channelUnregistered(ctx);
			nodeState = NodeState.DisConn;
			handleFailedQueue();
		}
	}

	private class HeartbeatChannelInitializer extends
			ChannelInitializer<SocketChannel> {

		private static final long READ_IDEL_TIME_OUT = LogConst.MAX_OVER_TIME
				* LogConst.CHECK_CHANNEL_SAMPLE_COUNT * 3; // 读超时
		private static final long WRITE_IDEL_TIME_OUT = LogConst.MAX_OVER_TIME
				* LogConst.CHECK_CHANNEL_SAMPLE_COUNT * 2; // 写超时
		private static final long ALL_IDEL_TIME_OUT = LogConst.MAX_OVER_TIME
				* LogConst.CHECK_CHANNEL_SAMPLE_COUNT * 4; // 所有超时

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
			ch.pipeline().addLast(new StringDecoder());
			ch.pipeline().addLast(
					new IdleStateHandler(READ_IDEL_TIME_OUT,
							WRITE_IDEL_TIME_OUT, ALL_IDEL_TIME_OUT,
							TimeUnit.MILLISECONDS));
			ch.pipeline().addLast(new GameLogClientInboundHandler());
		}
	}

	private void sendHeartBitMessage(String heartBitMsg)
			throws URISyntaxException, UnsupportedEncodingException,
			InterruptedException {
		for (int i = 0; i < LogConst.CHECK_CHANNEL_SAMPLE_COUNT; i++)
			sendMessage(HEARTBITKEY, heartBitMsg);
	}

	/**
	 * 该方法不会返回响应时间 因为它被放入了另外一条线程中去检测时间
	 * 
	 * @return
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 * @throws InterruptedException
	 */
	public void startCheckResponseTime(String heartBitContent)
			throws UnsupportedEncodingException, URISyntaxException,
			InterruptedException {
		synchronized (checkResponseLock) {
			onCheckNodeActive = LogConst.CHECK_CHANNEL_SAMPLE_COUNT;
			startCheckTime = System.nanoTime();
			endCheckTime = 0l;
		}
		sendHeartBitMessage(heartBitContent);
		putCheckResponseToThread();
	}

	/**
	 * 测试心跳后，并不能立即得到结果，只能得到上一次的心跳结果
	 * 
	 * @return
	 */
	private void putCheckResponseToThread() {
		new Thread(new Runnable() {
			public void run() {
				int SLEEPTIMES = 500;
				long responseTime = 0;
				try {
					for (int i = 0; i < SLEEPTIMES; i++) {
						Thread.sleep(LogConst.MAX_OVER_TIME
								* LogConst.CHECK_CHANNEL_SAMPLE_COUNT
								/ SLEEPTIMES);
						if (onCheckNodeActive == 0)
							break;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (onCheckNodeActive > 0)
					responseTime = Long.MAX_VALUE; // 表示超时
				responseTime = (endCheckTime - startCheckTime)
						/ LogConst.CHECK_CHANNEL_SAMPLE_COUNT / 1000000l;
				System.out.println("当前平均响应时间(mm)：" + responseTime);
				if (responseTime > LogConst.MAX_OVER_TIME)
					handleFailedQueue();
			}
		}).start();
	}
}
