package com.rw.handler.groupsecret;

import java.util.ArrayList;
import java.util.List;

import com.rwproto.ChatServiceProtos.ChatMessageData;

public class GroupSecretInviteDataHolder {
	private List<ChatMessageData> list = new ArrayList<ChatMessageData>();

	public List<ChatMessageData> getList() {
		return list;
	}

	public void setList(List<ChatMessageData> list) {
		this.list = list;
	}
	
	
	
}
