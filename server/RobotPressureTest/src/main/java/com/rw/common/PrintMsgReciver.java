package com.rw.common;

import com.rwproto.MsgDef.Command;

/*
 * @author HC
 * @date 2016年3月15日 下午5:40:36
 * @Description 消息接收
 */
public abstract class PrintMsgReciver implements MsgReciver {

	private final Command command;// 协议类型
	protected final String functionName;// 功能模块名字
	protected final String protoType;// 功能协议类型

	public PrintMsgReciver(Command command, String functionName, String protoType) {
		this.command = command;
		this.functionName = functionName;
		this.protoType = protoType;
	}

	@Override
	public Command getCmd() {
		return this.command;
	}
}