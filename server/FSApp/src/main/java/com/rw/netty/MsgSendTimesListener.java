package com.rw.netty;

import com.rw.trace.stat.MsgStatFactory;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class MsgSendTimesListener implements GenericFutureListener<Future<Void>> {

	private final Command command;
	private final Object subCommand;
	private final int headSize;
	private final int bodySize;

	public MsgSendTimesListener(Command command, Object subCommand, Response response) {
		this.command = command;
		this.subCommand = subCommand;
		this.headSize = response.getHeader().getSerializedSize();
		this.bodySize = response.getSerializedContent().size();
	}

	@Override
	public void operationComplete(Future<Void> future) throws Exception {
		if (future.isSuccess()) {
			MsgStatFactory.getCollector().recordSendSuccessMsg(command, subCommand, headSize, bodySize);
		} else {
			MsgStatFactory.getCollector().recordSendFailMsg(command, subCommand);
		}
	}

}
