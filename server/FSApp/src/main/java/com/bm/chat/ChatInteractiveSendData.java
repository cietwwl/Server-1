package com.bm.chat;

import java.util.List;

import com.rwproto.ChatServiceProtos.MsgChatResponse;

public class ChatInteractiveSendData {

	private ChatInteractiveType _type;
	private MsgChatResponse _msg;
	private List<String> _targetUserIds;
	
	public ChatInteractiveSendData(ChatInteractiveType pType, MsgChatResponse pMsg, List<String> pTargetUserIds) {
		this._msg = pMsg;
		this._type = pType;
		this._targetUserIds = pTargetUserIds;
	}
	
	public ChatInteractiveType getType() {
		return _type;
	}
	
	public MsgChatResponse getMsg() {
		return _msg;
	}
	
	public List<String> getTargetUserIds() {
		return _targetUserIds;
	}
}
