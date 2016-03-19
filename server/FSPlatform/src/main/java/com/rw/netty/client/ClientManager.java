package com.rw.netty.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.protobuf.ByteString;
import com.rw.platform.PlatformFactory;
import com.rwproto.MsgDef.Command;
import com.rwproto.RequestProtos.Request;
import com.rwproto.RequestProtos.RequestBody;
import com.rwproto.RequestProtos.RequestHeader;

public class ClientManager {

	public final static int CLIENT_STATUS_WORKING = 1;             //模拟的客户端状态 working
	public final static int CLIENT_STATUS_FREE = 2;				   //模拟的客户端状态 free
	public final static int PLATFORM_CONNECT_NUM = 5;
	public final static int DEFAULT_CLIENT_ID = 1;                 //默认的客户端id

	private final HashMap<Integer, Client> ClientPool;

	public ClientManager(){
		ClientPool = new HashMap<Integer, Client>(PlatformFactory.getPlatform_connect_num());
		for (int i = 0; i < PLATFORM_CONNECT_NUM; i++) {
			Client client = new Client(i + 1);
			ClientPool.put(client.getId(), client);
			PlatformFactory.getPlatformService().submitClientTask(client);
		}
	}
	
	public static void init() {
		
	}

	/**
	 * 平台处理与游戏服务器通信的入口
	 * 
	 * @param account
	 * @param msg
	 * @param command
	 */
	public void submitReqeust(String host, int port, ByteString msg, Command command, String accountId) {
		Request.Builder builder = Request.newBuilder().setHeader(
				getSimpleRequestHeader(command));
		if (msg != null) {
			builder.setBody(getRequestBody(msg));
		}
		GSMessage gsMsg = new GSMessage(host, port, builder.build());
		gsMsg.setAccountId(accountId);
		arrangeClient(gsMsg);
	}

	private RequestHeader getSimpleRequestHeader(Command command) {
		return RequestHeader.newBuilder().setCommand(command).build();
	}

	private RequestBody getRequestBody(ByteString msg) {
		return RequestBody.newBuilder().setSerializedContent(msg).build();
	}
	
	public void removeUnSendMsgWhenDisconnect(String accountId){
		for (Iterator<Entry<Integer, Client>> iterator = ClientPool.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, Client> entry = iterator.next();
			Client client = entry.getValue();
			client.removeProcessMsg(accountId);
		}
	}

	/**
	 * 选择客户端连接 
	 * 
	 * 筛选连接的机制:
	 * 有空连接找空连接 
	 * 没空连接找同ip并等待队列最短的连接 
	 * 没有相同ip找长度最短的连接
	 * 
	 * @param request
	 * @param account
	 */
	private void arrangeClient(GSMessage gsMsg) {

		Client bestClient = null;

		for (Iterator<Entry<Integer, Client>> iterator = ClientPool.entrySet()
				.iterator(); iterator.hasNext();) {
			Entry<Integer, Client> next = iterator.next();
			Client client = next.getValue();
			if (client.getStatus() == CLIENT_STATUS_FREE) {
				client.addProcessMsg(gsMsg);
				return;
			}
			if (bestClient == null) {
				bestClient = client;
			} else {
				if (!bestClient.getLastHost().equals(client.getHost())
						&& client.getLastHost().equals(gsMsg.getHost())) {
					bestClient = client;
					continue;
				}
				if (bestClient.getHost().equals(client.getHost())
						&& client.getProcessLength() < bestClient
								.getProcessLength()) {
					bestClient = client;
					continue;
				}
			}
		}
		if(bestClient == null){
			Client client = ClientPool.get(DEFAULT_CLIENT_ID);			
			client.addProcessMsg(gsMsg);
		}else{
			bestClient.addProcessMsg(gsMsg);
		}
	}
}
