package com.rw.fsutil.remote;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import com.rw.fsutil.record.SimpleRecordService;
import com.rw.fsutil.remote.parse.FSMessageDecoder;
import com.rw.fsutil.remote.parse.FSMessageEncoder;
import com.rw.fsutil.remote.parse.FSMessageExecutor;
import com.rw.fsutil.util.DateUtils;


@SuppressWarnings({ "rawtypes", "unchecked" })
public class RemoteMessageServiceFactory {

	private static Logger remoteMsgLogger = Logger.getLogger("remoteMsgLogger");
	private static ConcurrentHashMap<Integer, RemoteMessageService<?, ?>> serviceMap;

	static {
		serviceMap = new ConcurrentHashMap<Integer, RemoteMessageService<?, ?>>();
		SimpleRecordService.getRecrodExectuor().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (serviceMap.isEmpty()) {
					return;
				}
				StringBuilder sb = new StringBuilder();
				sb.append(DateUtils.getddHHmmFormater().format(new Date())).append('\n');
				for (RemoteMessageService service : serviceMap.values()) {
					int type = service.getType();
					List<RemoteServiceSender> senders = service.getAllSenders();
					for (int i = 0, size = senders.size(); i < size; i++) {
						RemoteServiceSender sender = senders.get(i);
						sb.append("type=").append(type).append(",id=").append(sender.getUniqueId()).append(",currentCount=").append(sender.getCount());
						sb.append(",success=").append(sender.getSendSuccessStatCount()).append(",fail=").append(sender.getSendFailStatCount()).append(",reject=").append(sender.getSendRejectStatCount()).append('\n');
					}
				}
				remoteMsgLogger.info(sb.toString());
			}
		}, 5, 5, TimeUnit.MINUTES);
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
