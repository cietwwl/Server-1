package com.rw.netty.client;

import com.rwproto.RequestProtos.Request;

/**
 * 封装发送游戏服务器信息的对象
 * @author lida
 *
 */
public class GSMessage {
	private String host;
	private int port;
	private Request request;
	private String accountId;
	
	public GSMessage(String host, int port, Request request){
		this.host = host;
		this.port = port;
		this.request = request;
	}
	
	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}
}
