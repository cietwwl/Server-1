package com.rw.handler.chat.data;

import java.util.EnumMap;
import java.util.List;

import com.rwproto.ChatServiceProtos.ChatMessageData;
import com.rwproto.ChatServiceProtos.eChatType;

/**
 * @Author HC
 * @date 2016年8月8日 下午5:13:59
 * @desc
 **/

public class ChatData {

	private EnumMap<eChatType, List<ChatMessageData>> chatMap = new EnumMap<eChatType, List<ChatMessageData>>(eChatType.class);// 所有消息存储的Map

	/**
	 * 当接受到消息修改
	 * 
	 * @param chatType
	 * @param updateList
	 */
	public void onMsgUpdate(eChatType chatType, List<ChatMessageData> updateList) {
		if (updateList == null || updateList.isEmpty()) {
			return;
		}

		if (chatMap.containsKey(chatType)) {// 接受到聊天消息
			chatMap.get(chatType).addAll(updateList);
		} else {
			chatMap.put(chatType, updateList);
		}
	}

	/**
	 * 获取某种消息
	 * 
	 * @param chatType
	 * @return
	 */
	public List<ChatMessageData> getMsgList(eChatType chatType) {
		if (chatMap.containsKey(chatType)) {
			return chatMap.get(chatType);
		}

		return null;
	}
}