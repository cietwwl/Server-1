package com.bm.chat;

import com.rwproto.ChatServiceProtos.ChatMessageData;
import com.rwproto.ChatServiceProtos.ChatMessageData.Builder;

/*
 * @author HC
 * @date 2016年1月16日 上午4:42:05
 * @Description 
 */
public class ChatInfo {
	private final int id;
	private final ChatMessageData.Builder message;

	public ChatInfo(int id, Builder message) {
		this.id = id;
		this.message = message;
	}

	public int getId() {
		return id;
	}

	public ChatMessageData.Builder getMessage() {
		return message;
	}
}