package com.rounter.client.node;

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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rounter.client.config.NodeState;
import com.rounter.client.config.RouterConst;
import com.rounter.client.exception.CannotCreateNodeException;
import com.rounter.client.exception.NodeMsgQueueDisorderException;
import com.rounter.client.exception.ParamInvalidException;
import com.rounter.innerParam.ReqType;
import com.rounter.innerParam.RouterReqestObject;
import com.rounter.param.IResponseData;
import com.rounter.service.IResponseHandler;
import com.rounter.util.JsonUtil;

public final class ChannelNode {
	
	Logger logger = LoggerFactory.getLogger(ChannelNode.class);
	
	private final EventLoopGroup senderGroup;
	private ConcurrentLinkedQueue<NodeData> msgQueue = new ConcurrentLinkedQueue<NodeData>();
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
	
	private final String TARGET_ADDR;
	private final int TARGET_PORT;
	private AtomicBoolean isActive = new AtomicBoolean(false);

	public void setActiveState(boolean isActive){
		if(this.isActive.compareAndSet(!isActive, isActive) && isActive){
			//从不活跃转为活跃，就创建连接
			handleFailedQueue();
		}
	}
	
	public void closeNode(){
		this.isActive.compareAndSet(true, false);
		if(null != cf && null != cf.channel()){
			cf.channel().close();
		}
	}
	
	public ChannelNode(EventLoopGroup senderGroup, String addr, int port){
		this.senderGroup = senderGroup;
		this.TARGET_ADDR = addr;
		this.TARGET_PORT = port;
	}

	public boolean connectOrReconnectChannel() throws InterruptedException, CannotCreateNodeException, ExecutionException, TimeoutException {
		if(System.currentTimeMillis() - lastConnectTime < RouterConst.RECONNECT_DISTANCE){
			//间隔时间重连
			return false;
		}
		if (cf != null) {
			if ((nodeState != NodeState.Ready && nodeState != NodeState.Over)){
				throw new CannotCreateNodeException("当前状态不可以创建ChannelNode");
			}
			cf.channel().close();
			if (!isActive.get()){
				throw new CannotCreateNodeException("目标状态不允许创建连接");
			}
		}
		nodeState = NodeState.Connecting;
		Bootstrap b = new Bootstrap().group(senderGroup)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.handler(new HeartbeatChannelInitializer());
		lastConnectTime = System.currentTimeMillis();
		ChannelFuture connectFuture = b.connect(TARGET_ADDR, TARGET_PORT);
		connectFuture.get(RouterConst.MAX_OVER_TIME, TimeUnit.SECONDS);
		isActive.set(connectFuture.isSuccess());
		return connectFuture.isSuccess();
	}

	public int getMsgQueueCount() {
		return this.queueMemCount.get();
	}

	/**
	 * 检查节点的是否活跃
	 * @return
	 */
	public boolean isChannelActive() {
		logger.info("TARGET_ADDR:{}, TARGET_PORT:{}, nodeState:{}", TARGET_ADDR, TARGET_PORT, nodeState);
		if (nodeState != NodeState.Normal){
			return false;
		}
		return cf != null && cf.channel().isActive();
	}

	public void sendMessage(final RouterReqestObject reqData, IResponseHandler resHandler, IResponseData resData)
			throws UnsupportedEncodingException, InterruptedException, ParamInvalidException {
		if (queueMemCount.incrementAndGet() >= RouterConst.NODE_MAX_QUEUE_SIZE){
			nodeState = NodeState.Busy;
		}
		if(null == cf){
			throw new NullPointerException("链接还未建立");
		}
		final NodeData nodeData = new NodeData(resData, resHandler);
		final String sendeMsg = JsonUtil.writeValue(reqData) + System.getProperty("line.separator");
		logger.debug("send msg to other server,ip:{}, port:{}, msg:{}",TARGET_ADDR, TARGET_PORT, sendeMsg);
		final ByteBuf buf = Unpooled.copiedBuffer(sendeMsg.getBytes("UTF-8"));
		cf.channel().eventLoop().execute(new Runnable() {
			public void run() {
				
				cf.channel().writeAndFlush(buf).addListener(new GenericFutureListener<ChannelFuture>() {
					
					public void operationComplete(ChannelFuture future) throws Exception {
						if(!future.isSuccess() || !addToQueue(nodeData)) {
							if (queueMemCount.decrementAndGet() < RouterConst.NODE_MAX_QUEUE_SIZE && nodeState == NodeState.Busy){
								nodeState = NodeState.Normal;
							}
							if(reqData.getType() != ReqType.HeartBit){
								nodeData.getResHandler().handleSendFailResponse(nodeData.getResData());
								synchronized (nodeData) {
									nodeData.getResData().notify();
								}
							}
						}
					}
					
				});
			}
		});
		if(null != resData && reqData.getType() != ReqType.HeartBit){
			synchronized (resData) {
				resData.wait(200);
			}
		}
	}

	private void msgBackSuccess(Object msg) {
		try {
			NodeData ndInfo = pollFromQueue();
			if (onCheckNodeActive > 0) {
				endCheckTime = System.nanoTime();
				onCheckNodeActive--;
			}
			if (ndInfo.getResData() != null && ndInfo.getResHandler() != null){
				ndInfo.getResHandler().handleServerResponse(msg, ndInfo.getResData());
				synchronized (ndInfo.getResData()) {
					ndInfo.getResData().notify();
				}
			}
		} catch (NodeMsgQueueDisorderException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 节点失败（需要重置）
	 * 处理没有收到成功返回的key
	 * 
	 */
	private synchronized void handleFailedQueue() {
		nodeState = NodeState.HandleFailedQueue;
		for (NodeData nd : msgQueue) {
			if (nd.getResData() != null && nd.getResHandler() != null){
				nd.getResHandler().handleSendFailResponse(nd.getResData());
				synchronized (nd.getResData()) {
					nd.getResData().notify();
				}
			}
		}
		msgQueue.clear();
		queueMemCount.set(0);
		nodeState = NodeState.Over;
		try {
			boolean suc = connectOrReconnectChannel();
			logger.debug("contect target, ip:{}, port:{},suc:{}",TARGET_ADDR, TARGET_PORT, suc);
		} catch (Exception ex) {
			logger.error("contect target, ip:{}, port:{},suc:{}", TARGET_ADDR, TARGET_PORT, false, ex);
			nodeState = NodeState.ConnFail;
			isActive.set(false);
		}
	}

	/**
	 * 加入已发送队列
	 * @param log
	 * @return
	 */
	private boolean addToQueue(NodeData nodeData) {	
		return msgQueue.add(nodeData);
	}

	/**
	 * 收到返回，从已发送队列删除
	 * @return
	 * @throws NodeMsgQueueDisorderException
	 */
	private NodeData pollFromQueue() throws NodeMsgQueueDisorderException {
		NodeData nodeData = msgQueue.poll();
		if (nodeData != null) {
			if (queueMemCount.decrementAndGet() < RouterConst.NODE_MAX_QUEUE_SIZE && nodeState == NodeState.Busy)
				nodeState = NodeState.Normal;
			return nodeData;
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
		}

		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
				throws Exception {
			if (evt instanceof IdleStateEvent) {
				startCheckResponseTime();
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

		private static final long READ_IDEL_TIME_OUT = RouterConst.MAX_OVER_TIME
				* RouterConst.CHECK_CHANNEL_SAMPLE_COUNT * 10; // 读超时
		private static final long WRITE_IDEL_TIME_OUT = RouterConst.MAX_OVER_TIME
				* RouterConst.CHECK_CHANNEL_SAMPLE_COUNT * 6; // 写超时
		private static final long ALL_IDEL_TIME_OUT = RouterConst.MAX_OVER_TIME
				* RouterConst.CHECK_CHANNEL_SAMPLE_COUNT * 15; // 所有超时

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast(new LineBasedFrameDecoder(102400));
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
	 */
	public void startCheckResponseTime() {
		if(!isActive.get()){
			//如果目前服务器没开启，就不用检查了
			return;
		}
		synchronized (checkResponseLock) {
			if(System.currentTimeMillis() - lastCheckResponse < RouterConst.RECONNECT_DISTANCE){
				//间隔时间心跳
				return;
			}
			onCheckNodeActive = RouterConst.CHECK_CHANNEL_SAMPLE_COUNT;
			startCheckTime = System.nanoTime();
			endCheckTime = startCheckTime;
			lastCheckResponse = System.currentTimeMillis();
		}
		sendHeartBitMessage();
		putCheckResponseToThread();
	}

	/**
	 * 发送心跳数据
	 * @param heartBitMsg
	 */
	private void sendHeartBitMessage(){
		for (int i = 0; i < RouterConst.CHECK_CHANNEL_SAMPLE_COUNT; i++){
			try {
				RouterReqestObject reqObj = new RouterReqestObject();
				reqObj.setType(ReqType.HeartBit);
				sendMessage(reqObj, null, null);
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
						Thread.sleep(RouterConst.MAX_OVER_TIME
								* RouterConst.CHECK_CHANNEL_SAMPLE_COUNT
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
							/ RouterConst.CHECK_CHANNEL_SAMPLE_COUNT / 1000000l;
				}
				if (responseTime > RouterConst.MAX_OVER_TIME){
					System.out.println("当前平均响应时间(mm)：" + responseTime);
					handleFailedQueue();
				}else{
					nodeState = NodeState.Normal;
				}
			}
		}).start();
	}
}
