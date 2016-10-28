package com.rw.netty;

import com.rwproto.MsgDef.Command;

public class SessionInfo {

	private final Long sessionId; // 建立连接时分配的唯一ID
	private final long createMillis; // 创建的时间戳
	private Command lastCommand; // 最后一次接收的消息
	private volatile long lastRecvMsgMillis; // 上次接收消息的绝对时间

	public SessionInfo(long sessionId) {
		this.sessionId = sessionId;
		this.createMillis = System.currentTimeMillis();
		this.lastRecvMsgMillis = createMillis;
	}

	public Command getLastCommand() {
		return lastCommand;
	}

	public void setLastCommand(Command lastCommand) {
		this.lastCommand = lastCommand;
	}

	public long getLastRecvMsgMillis() {
		return lastRecvMsgMillis;
	}

	public void setLastRecvMsgMillis(long lastRecvMsgMillis) {
		this.lastRecvMsgMillis = lastRecvMsgMillis;
	}

	public long getCreateMillis() {
		return createMillis;
	}

	public Long getSessionId() {
		return sessionId;
	}

}
