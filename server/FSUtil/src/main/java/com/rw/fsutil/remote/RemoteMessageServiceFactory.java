package com.rw.fsutil.remote;

import java.util.concurrent.ConcurrentHashMap;

import com.rw.fsutil.remote.parse.FSMessageDecoder;
import com.rw.fsutil.remote.parse.FSMessageEncoder;
import com.rw.fsutil.remote.parse.FSMessageExecutor;

public class RemoteMessageServiceFactory {

	private static ConcurrentHashMap<Integer, RemoteMessageService<?, ?>> serviceMap;

	static {
		serviceMap = new ConcurrentHashMap<Integer, RemoteMessageService<?, ?>>();
	}

	/**
	 * 创建一个远程消息服务，需逻辑自定义类型
	 * @param type
	 * @param host
	 * @param port
	 * @param threadCount
	 * @param maxConnection
	 * @param decoder
	 * @param encoder
	 * @param executor
	 * @return
	 */
	public static <SendMessage, ReceiveMessage> RemoteMessageService<SendMessage, ReceiveMessage> createService(int type, String host, int port, int threadCount, int maxConnection,
			FSMessageDecoder<ReceiveMessage> decoder, FSMessageEncoder<SendMessage> encoder, FSMessageExecutor<ReceiveMessage> executor) {
		RemoteMessageService<?, ?> service = serviceMap.get(type);
		// TODO 验证标准格式
		if (host == null || host.isEmpty() || port <= 0) {
			throw new IllegalArgumentException("illegal host or port,host=" + host + ",port=" + port);
		}
		if (service == null) {
			service = new RemoteMessageService<SendMessage, ReceiveMessage>(1, host, port, threadCount, maxConnection, decoder, encoder, executor);
			RemoteMessageService<?, ?> old = serviceMap.putIfAbsent(type, service);
			if (old != null) {
				service = old;
			}
		}
		if (!service.getRemoteHost().equals(host)) {
			throw new IllegalArgumentException("duplicate type =" + type + ",oldHost=" + service.getRemoteHost() + ",host=" + host);
		}
		if (service.getRemotePort() != port) {
			throw new IllegalArgumentException("duplicate type =" + type + ",oldPort=" + service.getRemotePort() + ",port=" + port);
		}
		return (RemoteMessageService<SendMessage, ReceiveMessage>) service;
	}

	/**
	 * 获取远程消息服务
	 * @param type
	 * @return
	 */
	public static <SendMessage, ReceiveMessage> RemoteMessageService<SendMessage, ReceiveMessage> getService(int type) {
		return (RemoteMessageService<SendMessage, ReceiveMessage>) serviceMap.get(type);
	}

}
