package com.rw.common.push;

import com.rw.Client;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public interface IReceivePushMsg {
	/**
	 * 当接受到推送消息之后做什么处理
	 * 
	 * @param resp
	 */
	public void onReceivePushMsg(Client client, Response resp);

	/**
	 * 获取推送消息的类型
	 * 
	 * @return
	 */
	public Command getCommand();
}