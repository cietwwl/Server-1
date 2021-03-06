package com.rw.handler.chat;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.RandomMethodIF;
import com.rwproto.ChatServiceProtos.ChatMessageData;
import com.rwproto.ChatServiceProtos.MsgChatRequest;
import com.rwproto.ChatServiceProtos.MsgChatResponse;
import com.rwproto.ChatServiceProtos.eChatResultType;
import com.rwproto.ChatServiceProtos.eChatType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class ChatHandler implements RandomMethodIF{

	private static ChatHandler instance = new ChatHandler();

	public static ChatHandler instance() {
		return instance;
	}

	/**
	 * 发送聊天
	 * 
	 * @param serverId
	 * @param accountId
	 */
	public boolean send(Client client, String message) {

		MsgChatRequest.Builder req = MsgChatRequest.newBuilder();
		req.setChatType(eChatType.CHANNEL_WORLD);

		// MessageUserInfo sendUserInfo = MessageUserInfo.newBuilder().setUserId(client.getAccountId()).setUserName(client.getAccountId()).setLevel(30).build();
		ChatMessageData messageData = ChatMessageData.newBuilder().setMessage(message).build();

		req.setChatMessageData(messageData);

		boolean success = client.getMsgHandler().sendMsg(Command.MSG_CHAT, req.build().toByteString(), new MsgReciver() {

			@Override
			public Command getCmd() {
				return Command.MSG_CHAT;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {

					MsgChatResponse rsp = MsgChatResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("ChatHandler[send] 转换响应消息为null");
						return false;
					}

					eChatResultType result = rsp.getChatResultType();
					if (result == eChatResultType.FAIL) {
						RobotLog.fail("ChatHandler[send] 服务器处理消息失败 " + result);
						return false;
					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("ChatHandler[send] 失败", e);
					return false;
				}
				return true;
			}

		});
		return success;

	}
	// public void sendRequestTreasure(Client client) {
	// MsgChatRequest.Builder req = MsgChatRequest.newBuilder();
	// req.setChatType(eChatType.CHAT_TREASURE);
	// client.getMsgHandler().sendMsg(Command.MSG_CHAT, req.build().toByteString(), new MsgReciver() {
	// @Override
	// public Command getCmd() {
	// return Command.MSG_CHAT;
	// }
	//
	// @Override
	// public boolean execute(Client client, Response response) {
	// ByteString serializedContent = response.getSerializedContent();
	// try {
	//
	// MsgChatResponse rsp = MsgChatResponse.parseFrom(serializedContent);
	// if (rsp == null) {
	// RobotLog.fail("ChatHandler[send] 转换响应消息为null");
	// return false;
	// }
	//
	// eChatResultType result = rsp.getChatResultType();
	// if (result == eChatResultType.FAIL) {
	// RobotLog.fail("ChatHandler[send] 服务器处理消息失败 " + result);
	// return false;
	// }
	//
	// List<ChatMessageData> listMessageList = rsp.getListMessageList();
	// GroupSecretInviteDataHolder groupSecretInviteDataHolder = client.getGroupSecretInviteDataHolder();
	// groupSecretInviteDataHolder.setList(listMessageList);
	//
	// } catch (InvalidProtocolBufferException e) {
	// RobotLog.fail("ChatHandler[send] 失败", e);
	// return false;
	// }
	// return true;
	// }
	// });
	// }

	@Override
	public boolean executeMethod(Client client) {
		return send(client, "这里是聊天的测试，hahaha~~~~~");
	}
}