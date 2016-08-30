package com.rw.netty;

import com.rwproto.MsgDef.Command;

public class SessionInfo {

	private Command lastCommand;
	private volatile long lastRecvMsgMillis; // 上次接收消息的绝对时间
	private final long createMillis;

	public SessionInfo() {
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

}
