package com.rw.fsutil.remote;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
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
	private volatile ArrayList<RemoteServiceSender<SendMessage, ReceiveMessage>> channels;
	private final int maxConnection;
	private final RemoteMessageDecoder<ReceiveMessage> decoder;
	private final RemoteMessageEncoder<SendMessage> encoder;
	private final FSMessageExecutor<ReceiveMessage> executor;
	private final NioEventLoopGroup eventGroup;
	private final AtomicLong sendAvailable;
	private final AtomicLong unAvailable;
	private volatile boolean debugLogger;

	public RemoteMessageService(int type, String host, int port, int threadCount, int maxConnection,
			FSMessageDecoder<ReceiveMessage> decoder, FSMessageEncoder<SendMessage> encoder, FSMessageExecutor<ReceiveMessage> executor) {
		this.remoteHost = host;
		this.remotePort = port;
		this.type = type;
		this.maxConnection = maxConnection;
		this.eventGroup = new NioEventLoopGroup(threadCount);
		this.decoder = new RemoteMessageDecoder<ReceiveMessage>(decoder);
		this.encoder = new RemoteMessageEncoder<SendMessage>(encoder);
		this.executor = executor;
		this.sendAvailable = new AtomicLong();
		this.unAvailable = new AtomicLong();
		this.channels = new ArrayList<RemoteServiceSender<SendMessage, ReceiveMessage>>();
		for (int i = 0; i < this.maxConnection; i++) {
			RemoteServiceSender<SendMessage, ReceiveMessage> sender = new RemoteServiceSender<SendMessage, ReceiveMessage>(i + 1, 8192, null, RemoteMessageService.this);
			this.channels.add(sender);
			if (i == 0) {
				sender.checkAndConnect();
			}
		}

		eventGroup.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				for (int i = 0, size = channels.size(); i < size; i++) {
					RemoteServiceSender<SendMessage, ReceiveMessage> sender = channels.get(i);
					if (sender.isWritable()) {
						break;
					}
					if (sender.isActive()) {
						continue;
					}
					sender.checkAndConnect();
					break;
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
	}

	class ChannelInit extends ChannelInitializer<SocketChannel> {

		private RemoteServiceSender<SendMessage, ReceiveMessage> sender;

		public ChannelInit(RemoteServiceSender<SendMessage, ReceiveMessage> sender) {
			this.sender = sender;
		}

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast(new RemoteMessageDecoderWrap<ReceiveMessage>(decoder));
			ch.pipeline().addLast(new RemoteServerHandler<SendMessage, ReceiveMessage>(sender));
			ch.pipeline().addLast(new RemoteMessageEncoderWrap<SendMessage>(encoder));
		}
	};

	protected ChannelInitializer<SocketChannel> createChannelInitializer(RemoteServiceSender<SendMessage, ReceiveMessage> sender) {
		return new ChannelInit(sender);
	}

	public boolean sendMsg(SendMessage content) {
		RemoteServiceSender<SendMessage, ReceiveMessage> sender = getAvailableSender();
		if (sender == null) {
			unAvailable.incrementAndGet();
			return false;
		} else {
			sender.sendMsg(content);
			sendAvailable.incrementAndGet();
			return true;
		}
	}

	public List<RemoteServiceSender<SendMessage, ReceiveMessage>> getAllSenders() {
		return new ArrayList<RemoteServiceSender<SendMessage, ReceiveMessage>>(this.channels);
	}

	public RemoteServiceSender<SendMessage, ReceiveMessage> getAvailableSender() {
		ArrayList<RemoteServiceSender<SendMessage, ReceiveMessage>> current = channels;
		int size = current.size();
		int index = RandomUtil.getRandom().nextInt(size);
		int maxIndex = size - 1;
		// writable
		for (int i = 0; i < size; i++) {
			RemoteServiceSender<SendMessage, ReceiveMessage> channel = current.get(index);
			if (channel.isWritable()) {
				return channel;
			}
			if (index == maxIndex) {
				index = 0;
			} else {
				index++;
			}
		}
		// active
		for (int i = 0; i < size; i++) {
			RemoteServiceSender<SendMessage, ReceiveMessage> channel = current.get(index);
			if (channel.isActive()) {
				return channel;
			}
			if (index == maxIndex) {
				index = 0;
			} else {
				index++;
			}
		}
		return null;
	}

	public FSMessageExecutor<ReceiveMessage> getExecutor() {
		return executor;
	}

	/**
	 * 获取远程连接ip
	 * @return
	 */
	public String getRemoteHost() {
		return remoteHost;
	}

	/**
	 * 获取远程连接端口
	 * @return
	 */
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

	protected NioEventLoopGroup getEventGroup() {
		return eventGroup;
	}

	public boolean isDebugLogger() {
		return debugLogger;
	}

	public void setDebugLogger(boolean debugLogger) {
		this.debugLogger = debugLogger;
	}

}
