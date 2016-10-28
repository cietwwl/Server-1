package com.rw.controler;

import com.rwproto.MsgDef.Command;

public class PlayerMsgTimeRecord {

	private final Command command;
	private final int seqId;
	private final long timeMillis;

	public PlayerMsgTimeRecord(Command command, int seqId, long timeMillis) {
		super();
		this.seqId = seqId;
		this.command = command;
		this.timeMillis = timeMillis;
	}

	public Command getCommand() {
		return command;
	}

	public int getSeqId() {
		return seqId;
	}

	public long getTimeMillis() {
		return timeMillis;
	}

}
