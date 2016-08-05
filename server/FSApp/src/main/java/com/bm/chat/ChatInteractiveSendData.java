package com.bm.chat;

import java.util.List;

import com.rwproto.ChatServiceProtos.MsgChatResponse;

public class ChatInteractiveSendData {

	private ChatInteractiveType _type;
	private MsgChatResponse _msg;
	private List<String> _targetUserIds;
	private boolean _sendToWorld;
	
	public ChatInteractiveSendData(ChatInteractiveType pType, MsgChatResponse pMsg, List<String> pTargetUserIds, boolean pSendToWorld) {
		this._msg = pMsg;
		this._type = pType;
		this._targetUserIds = pTargetUserIds;
		this._sendToWorld = pSendToWorld;
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
	
	public boolean isSendToWorld() {
		return _sendToWorld;
	}
}
