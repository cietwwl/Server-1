package com.bm.chat;

import com.rwproto.ChatServiceProtos.ChatMessageData;

/*
 * @author HC
 * @date 2016年1月16日 上午4:42:05
 * @Description 
 */
public class ChatInfo {
	private final int id;
//	private final ChatMessageData.Builder message;
	private final ChatMessageData message;

	public ChatInfo(int id, ChatMessageData.Builder pMessageBuilder) {
		this.id = id;
		this.message = pMessageBuilder.build();
	}
	
	public int getId() {
		return id;
	}

//	public ChatMessageData.Builder getMessage() {
//		return message;
//	}
	
	public ChatMessageData getMessage() {
		return message;
	}
}