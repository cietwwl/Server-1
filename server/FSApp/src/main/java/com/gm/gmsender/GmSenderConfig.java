package com.gm.gmsender;

public class GmSenderConfig {

	private String host;
	private int port;
	private int timeoutMillis;
	private short protno = 11;
	
	public GmSenderConfig(String host, int port, int timeoutMillis, short protno) {
		super();
		this.host = host;
		this.port = port;
		this.timeoutMillis = timeoutMillis;
		this.protno = protno;
	}
	
	public String getHost() {
		return host;
	}
	public int getPort() {
		return port;
	}
	public int getTimeoutMillis() {
		return timeoutMillis;
	}

	public short getProtno() {
		return protno;
	}
	
	
	
}
