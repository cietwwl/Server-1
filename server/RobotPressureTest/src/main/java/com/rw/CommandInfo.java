package com.rw;

import com.rwproto.MsgDef.Command;

public class CommandInfo {

	private final Command command;
	private final int seqId;

	public CommandInfo(Command command, int seqId) {
		this.command = command;
		this.seqId = seqId;
	}

	public Command getCommand() {
		return command;
	}

	public int getSeqId() {
		return seqId;
	}

	@Override
	public String toString() {
		return "[command=" + command + ", seqId=" + seqId + "]";
	}

}
