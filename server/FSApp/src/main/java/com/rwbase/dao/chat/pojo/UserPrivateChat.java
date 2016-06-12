package com.rwbase.dao.chat.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.rw.service.chat.ChatHandler;

/*
 * @author HC
 * @date 2015年8月12日 下午2:14:11
 * @Description 
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPrivateChat {
	@Id
	private String userId;// 主键
	// private List<String> privateChatList;// 私聊信息列表
	// private List<String> treasureChatList;// 密境信息列表
	private List<ChatMessageSaveData> privateChat;// 私聊信息列表
	private List<ChatMessageSaveData> secretChat;// 帮派秘境的聊天信息列表

	public UserPrivateChat() {
		privateChat = new ArrayList<ChatMessageSaveData>(ChatHandler.MAX_CACHE_MSG_SIZE);
		secretChat = new ArrayList<ChatMessageSaveData>(ChatHandler.MAX_CACHE_MSG_SIZE);
	}

	/**
	 * 获取角色Id
	 * 
	 * @return
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * 设置角色Id
	 * 
	 * @param userId
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	// /**
	// * 获取私聊消息列表
	// *
	// * @return
	// */
	// public List<String> getPrivateChatList() {
	// return privateChatList == null ? new ArrayList<String>() : privateChatList;
	// }
	//
	// /**
	// * 设置私聊消息列表
	// *
	// * @param privateChatList
	// */
	// public void setPrivateChatList(List<String> privateChatList) {
	// this.privateChatList = privateChatList;
	// }
	//
	// /**
	// * 设置密境分享消息列表
	// *
	// * @return
	// */
	// public List<String> getTreasureChatList() {
	// return treasureChatList == null ? new ArrayList<String>() : treasureChatList;
	// }
	//
	// /**
	// * 获取密境分享消息列表
	// *
	// * @param treasureChatList
	// */
	// public void setTreasureChatList(List<String> treasureChatList) {
	// this.treasureChatList = treasureChatList;
	// }
	//
	// /**
	// * 添加一个私聊信息
	// *
	// * @param privateChatMsgData
	// */
	// public synchronized void addPrivateChatMessage(String privateChatMsgData) {
	// if (privateChatList == null) {
	// privateChatList = new ArrayList<String>(ChatHandler.MAX_CACHE_MSG_SIZE);
	// }
	//
	// int size = privateChatList.size();
	// if (size >= ChatHandler.MAX_CACHE_MSG_SIZE) {
	// privateChatList.remove(0);
	// }
	//
	// privateChatList.add(privateChatMsgData);
	// }
	//
	// /**
	// * 添加一个密境分享信息
	// *
	// * @param treasureChatMsgData
	// */
	// public synchronized void addTreasureChatMessage(ByteString treasureChatMsgData) {
	// if (treasureChatList == null) {
	// treasureChatList = new ArrayList<String>(ChatHandler.MAX_CACHE_MSG_SIZE);
	// }
	//
	// int size = treasureChatList.size();
	// if (size >= ChatHandler.MAX_CACHE_MSG_SIZE) {
	// treasureChatList.remove(0);
	// }
	//
	// treasureChatList.add(treasureChatMsgData.toStringUtf8());
	// }
	//
	// /**
	// * 私聊转换成对应的消息
	// *
	// * @return
	// */
	// @JsonIgnore
	// public synchronized List<ChatMessageData> getPrivateChatMessageList() {
	// List<ChatMessageData> list = new ArrayList<ChatMessageData>();
	// if (privateChatList != null && !privateChatList.isEmpty()) {
	// for (int i = 0, size = privateChatList.size(); i < size; i++) {
	// try {
	// list.add(ChatMessageData.parseFrom(Base64.decode(privateChatList.get(i))));
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// return list;
	// }
	//
	// /**
	// * 更新私聊信息中的某条信息的状态
	// *
	// * @param index
	// * @param chatMsgData
	// */
	// @JsonIgnore
	// public synchronized void updatePrivateChatMessageState(int index, ChatMessageData chatMsgData) {
	// this.privateChatList.set(index, chatMsgData.toByteString().toStringUtf8());
	// }
	//
	// /**
	// * 密境转换成对应的消息
	// *
	// * @return
	// */
	// @JsonIgnore
	// public synchronized List<ChatMessageData> getTreasureChatMessageList() {
	// List<ChatMessageData> list = new ArrayList<ChatMessageData>();
	// if (treasureChatList != null && !treasureChatList.isEmpty()) {
	// for (int i = 0, size = treasureChatList.size(); i < size; i++) {
	// try {
	// list.add(ChatMessageData.parseFrom(treasureChatList.get(i).getBytes(Charset.forName("UTF-8"))));
	// } catch (InvalidProtocolBufferException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// return list;
	// }

	/**
	 * 添加一个私聊信息
	 * 
	 * @param privateChatMsgData
	 */
	public synchronized void addPrivateChatMessage(ChatMessageSaveData privateChatMsgData) {
		int size = privateChat.size();
		if (size >= ChatHandler.MAX_CACHE_MSG_SIZE) {
			privateChat.remove(0);
		}

		privateChat.add(privateChatMsgData);
	}

	/**
	 * 添加一个密境分享信息
	 * 
	 * @param treasureChatMsgData
	 */
	public synchronized void addTreasureChatMessage(ChatMessageSaveData treasureChatMsgData) {
		int size = secretChat.size();
		if (size >= ChatHandler.MAX_CACHE_MSG_SIZE) {
			secretChat.remove(0);
		}

		secretChat.add(treasureChatMsgData);
	}

	/**
	 * 私聊转换成对应的消息
	 * 
	 * @return
	 */
	@JsonIgnore
	public List<ChatMessageSaveData> getPrivateChatMessageList() {
		return new ArrayList<ChatMessageSaveData>(privateChat);
	}

	/**
	 * 更新私聊信息中的某条信息的状态
	 * 
	 * @param index
	 * @param chatMsgData
	 */
	@JsonIgnore
	public synchronized void updatePrivateChatMessageState(int index, ChatMessageSaveData saveData) {
		this.privateChat.set(index, saveData);
	}

	/**
	 * 更新帮派秘境信息中的某条信息的状态
	 * 
	 * @param index
	 * @param chatMsgData
	 */
	@JsonIgnore
	public synchronized void updateGroupSecretChatMessageState(int index, ChatMessageSaveData saveData) {
		this.secretChat.set(index, saveData);
	}

	/**
	 * 密境转换成对应的消息
	 * 
	 * @return
	 */
	@JsonIgnore
	public List<ChatMessageSaveData> getTreasureChatMessageList() {
		return new ArrayList<ChatMessageSaveData>(secretChat);
	}

	/**
	 * 退出帮派的时候清除掉所有的秘境聊天信息
	 */
	public synchronized void clearAllTreasureChatMessage() {
		secretChat.clear();
	}
}