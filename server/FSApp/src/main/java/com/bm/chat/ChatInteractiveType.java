package com.bm.chat;

import com.rwproto.ChatServiceProtos.eAttachItemType;
import com.rwproto.ChatServiceProtos.eChatType;

/**
 * 
 * 聊天互動頻道的類型配置
 * 
 * @author CHEN.P
 *
 */
public enum ChatInteractiveType {

	/**
	 * 互動類型：幫派秘境
	 */
	TREASURE(eChatType.CHAT_TREASURE,  eAttachItemType.Treasure),
	/**
	 * 互動類型：好友
	 */
	FRIEND(eChatType.CHAT_FRIEND,  eAttachItemType.Friend),
	/**
	 * 互動類型：隊伍
	 */
	TEAM(eChatType.CHAT_TEAM,  eAttachItemType.Team),
	/**
	 * 互動類型：隨機BOSS
	 */
	RANDOM_BOSS(eChatType.CHAT_RANDOM_BOSS,  eAttachItemType.RandomBoss),
	;
	public final eChatType chatType;
	public final eAttachItemType attachItemType;
	
	private ChatInteractiveType(eChatType pChatType, eAttachItemType pAttachItemType) {
		this.chatType = pChatType;
		this.attachItemType = pAttachItemType;
	}
}
