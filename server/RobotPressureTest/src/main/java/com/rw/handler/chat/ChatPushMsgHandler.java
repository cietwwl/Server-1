package com.rw.handler.chat;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.RobotLog;
import com.rw.common.push.IReceivePushMsg;
import com.rwproto.ChatServiceProtos.MsgChatResponse;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

/**
 * @Author HC
 * @date 2016年8月8日 下午5:30:17
 * @desc
 **/

public class ChatPushMsgHandler implements IReceivePushMsg {

	@Override
	public void onReceivePushMsg(Client client, Response resp) {
		try {
			MsgChatResponse rsp = MsgChatResponse.parseFrom(resp.getSerializedContent());
			client.getChatData().onMsgUpdate(rsp.getChatType(), rsp.getListMessageList());
		} catch (InvalidProtocolBufferException e) {
			RobotLog.fail("接受聊天信息出现了异常", e);
		}
	}

	@Override
	public Command getCommand() {
		return Command.MSG_CHAT;
	}
}