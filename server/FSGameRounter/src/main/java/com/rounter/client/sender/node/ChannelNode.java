package com.rounter.client.sender.node;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.rounter.client.sender.config.LogConst;
import com.rounter.client.sender.config.NodeState;
import com.rounter.client.sender.exception.CannotCreateNodeException;
import com.rounter.client.sender.exception.NodeMsgQueueDisorderException;
import com.rounter.client.sender.resultHandler.SendMsgResultHandler;
import com.rounter.client.sender.resultHandler.ServerResponseHandler;
import com.sun.jdi.Bootstrap;

public final class ChannelNode {
	private static long HEARTBITKEY = -101;

	private final EventLoopGroup senderGroup;
	private final SendMsgResultHandler resultHandler = new SendMsgResultHandler();
	private final ServerResponseHandler responseHandler = new ServerResponseHandler();

	private ConcurrentLinkedQueue<LogStoreInfo> msgQueue = new ConcurrentLinkedQueue<LogStoreInfo>();
	private AtomicInteger queueMemCount = new AtomicInteger();
	private ChannelHandlerContext cf;
	private volatile NodeState nodeState = NodeState.Normal; // 连接状态
	private volatile long lastConnectTime = 0L; // 上次链接时间
	private volatile long lastCheckResponse = 0L;	//上次测试链接状态的时间

	// 测试响应时间的字段
	private long startCheckTime = 0;
	private long endCheckTime = 0;
	private byte onCheckNodeActive = 0;
	// 测试响应时间的锁
	private byte[] checkResponseLock = new byte[0];

	public ChannelNode(EventLoopGroup senderGroup){
		this.senderGroup = senderGroup;
	}

	public void connectOrReconnectChannel() throws InterruptedException,
			CannotCreateNodeException {
		if(System.currentTimeMillis() - lastConnectTime < LogConst.RECONNECT_DISTANCE){
			//间隔时间重连
			return;
		}
		if (cf != null) {
			if (nodeState != NodeState.Ready && nodeState != NodeState.Over)
				throw new CannotCreateNodeException("当前状态不可以创建ChannelNode");
			cf.channel().close();
		}
		nodeState = NodeState.Connecting;
		Bootstrap b = new Bootstrap().group(senderGroup)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.handler(new HeartbeatChannelInitializer());
		b.connect(LogConst.TARGET_ADDR, LogConst.TARGET_PORT);
		lastConnectTime = System.currentTimeMillis();
	}

	public int getMsgQueueCount() {
		return this.queueMemCount.get();
	}

	/**
	 * 检查节点的是否活跃
	 * @return
	 */
	public boolean isChannelActive() {
		if (nodeState != NodeState.Normal)
			return false;
		return cf != null && cf.channel().isActive();
	}

	public void sendMessage(final LogStoreInfo logInfo)
			throws UnsupportedEncodingException, InterruptedException {
		if (queueMemCount.incrementAndGet() >= LogConst.NODE_MAX_QUEUE_SIZE){
			nodeState = NodeState.Busy;
		}
		if(null == cf){
			throw new NullPointerException("链接还未建立");
		}
		final String sendeMsg = logInfo.getLog_Id() + System.getProperty("line.separator");
		final ByteBuf buf = Unpooled.copiedBuffer(sendeMsg.getBytes("UTF-8"));
		cf.channel().eventLoop().execute(new Runnable() {
			public void run() {
				cf.channel().writeAndFlush(buf).addListener(new GenericFutureListener<ChannelFuture>() {
					public void operationComplete(ChannelFuture future)
							throws Exception {
						if(!future.isSuccess() || !addToQueue(logInfo)) {
							if (queueMemCount.decrementAndGet() < LogConst.NODE_MAX_QUEUE_SIZE && nodeState == NodeState.Busy){
								nodeState = NodeState.Normal;
							}
							if(logInfo.getLog_Id() != HEARTBITKEY) resultHandler.handleResult(false, logInfo);
						}
					}
				});
			}
		});
	}

	private void msgBackSuccess(Object msg) {
		try {
			LogStoreInfo longInfo = pollFromQueue();
			if(Long.valueOf((String) msg) != longInfo.getLog_Id()){
				(new Exception("Queue Disorder...")).printStackTrace();
			}
			if (onCheckNodeActive > 0) {
				endCheckTime = System.nanoTime();
				onCheckNodeActive--;
			}
			if (longInfo.getLog_Id() != HEARTBITKEY)
				responseHandler.handleResponse(true, longInfo);
		} catch (NodeMsgQueueDisorderException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 节点失败（需要重置）
	 * 处理没有收到成功返回的key
	 * 
	 * @throws CannotCreateNodeException
	 * @throws InterruptedException
	 */
	private synchronized void handleFailedQueue() {
		nodeState = NodeState.HandleFailedQueue;
		for (LogStoreInfo log : msgQueue) {
			if (log.getLog_Id() != HEARTBITKEY)
				resultHandler.handleResult(false, log);
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

	/**
	 * 加入已发送队列
	 * @param log
	 * @return
	 */
	private boolean addToQueue(LogStoreInfo log) {	
		return msgQueue.add(log);
	}

	/**
	 * 收到返回，从已发送队列删除
	 * @return
	 * @throws NodeMsgQueueDisorderException
	 */
	private LogStoreInfo pollFromQueue() throws NodeMsgQueueDisorderException {
		LogStoreInfo logInfo = msgQueue.poll();
		if (logInfo != null) {
			if (queueMemCount.decrementAndGet() < LogConst.NODE_MAX_QUEUE_SIZE && nodeState == NodeState.Busy)
				nodeState = NodeState.Normal;
			return logInfo;
		}
		throw new NodeMsgQueueDisorderException("Msg Queue Disorder");
	}

	/**
	 * 接收消息的handler
	 * @author aken
	 */
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
				startCheckResponseTime("NODE_MAX_IDLE_TIMES");
				// TODO
				IdleStateEvent event = (IdleStateEvent) evt;
				System.out.println(ctx.channel().remoteAddress() + "超时类型：" + event.state().name());
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

	/**
	 * 节点空闲检测
	 * @author aken
	 */
	private class HeartbeatChannelInitializer extends
			ChannelInitializer<SocketChannel> {

		private static final long READ_IDEL_TIME_OUT = LogConst.MAX_OVER_TIME
				* LogConst.CHECK_CHANNEL_SAMPLE_COUNT * 4; // 读超时
		private static final long WRITE_IDEL_TIME_OUT = LogConst.MAX_OVER_TIME
				* LogConst.CHECK_CHANNEL_SAMPLE_COUNT * 3; // 写超时
		private static final long ALL_IDEL_TIME_OUT = LogConst.MAX_OVER_TIME
				* LogConst.CHECK_CHANNEL_SAMPLE_COUNT * 5; // 所有超时

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

	/**
	 * 该方法不会立即返回响应时间，它被放入了另外一条线程中去等待检测时间
	 * 
	 * @return
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 * @throws InterruptedException
	 */
	public void startCheckResponseTime(String heartBitContent) {
		synchronized (checkResponseLock) {
			if(System.currentTimeMillis() - lastCheckResponse < LogConst.RECONNECT_DISTANCE){
				//间隔时间心跳
				return;
			}
			onCheckNodeActive = LogConst.CHECK_CHANNEL_SAMPLE_COUNT;
			startCheckTime = System.nanoTime();
			endCheckTime = startCheckTime;
			lastCheckResponse = System.currentTimeMillis();
		}
		sendHeartBitMessage(heartBitContent);
		putCheckResponseToThread();
	}

	/**
	 * 发送心跳数据
	 * @param heartBitMsg
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 * @throws InterruptedException
	 */
	private void sendHeartBitMessage(String heartBitMsg){
		for (int i = 0; i < LogConst.CHECK_CHANNEL_SAMPLE_COUNT; i++){
			try {
				sendMessage(new LogStoreInfo(HEARTBITKEY, heartBitMsg, 0, false));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
						if (onCheckNodeActive <= 0) break;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (onCheckNodeActive > 0){
					responseTime = Long.MAX_VALUE; // 表示超时
				}else{
					responseTime = (endCheckTime - startCheckTime)
							/ LogConst.CHECK_CHANNEL_SAMPLE_COUNT / 1000000l;
				}
				System.out.println("当前平均响应时间(mm)：" + responseTime);
				if (responseTime > LogConst.MAX_OVER_TIME){
					handleFailedQueue();
				}
			}
		}).start();
	}
}
