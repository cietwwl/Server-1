package com.rwbase.dao.chat.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.rw.service.chat.ChatHandler;
import com.rwbase.common.IFunction;
import com.rwproto.ChatServiceProtos.ChatMessageData;

/**
 * @author HC
 * @date 2015年8月12日 下午2:14:11
 * @Description 
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPrivateChat {
	
	private static final IFunction<ChatMessageSaveData, String> _getSenderIdFunc = new GetSenderUserIdFunc();
	private static final IFunction<ChatMessageSaveData, String> _getReceiverIdFunc = new GetReceiverUserIdFunc();
	private static final IFunction<ChatMessageData, String> _getSenderIdOfProtoFunc = new GetSenderUserIdOfProtoFunc();
	private static final IFunction<ChatMessageData, String> _getReceiverIdOfProtoFunc = new GetReceiverUserIdOfProtoFunc();
	
	@Id
	private String userId;// 主键
	// private List<String> privateChatList;// 私聊信息列表
	// private List<String> treasureChatList;// 密境信息列表
	@JsonIgnore
	private Map<String, Integer> _receiveCountOfUser = new HashMap<String, Integer>();
	@JsonIgnore
	private String _currentTargetUserIdOfPrivateChat = ""; // 當前私聊的目標id
	@JsonSerialize(include=Inclusion.NON_EMPTY)
	private List<ChatMessageSaveData> privateChat;// 私聊信息列表
	@JsonSerialize(include=Inclusion.NON_EMPTY)
	private List<ChatMessageSaveData> privateChatSent; // 我发出的私聊信息
	@JsonSerialize(include=Inclusion.NON_EMPTY)
	private List<ChatMessageSaveData> secretChat;// 帮派秘境的聊天信息列表;

	public UserPrivateChat() {
		privateChat = new ArrayList<ChatMessageSaveData>(ChatHandler.MAX_CACHE_MSG_SIZE_OF_PRIVATE_CHAT);
		privateChatSent = new ArrayList<ChatMessageSaveData>(ChatHandler.MAX_CACHE_MSG_SIZE_OF_PRIVATE_CHAT);
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
	private void handleCount(String userId) {
		Integer count = _receiveCountOfUser.get(userId);
		if (count == null) {
			count = 1;
		} else {
			count++;
		}
		_receiveCountOfUser.put(userId, count);
	}
	
	private void checkOnAdd(ChatMessageSaveData privateChatMsgData) {
		if (privateChat.size() > 0 && _receiveCountOfUser.isEmpty()) {
			// 初始化
			for (ChatMessageSaveData cmsd : privateChat) {
				String userId = cmsd.getSendInfo().getUserId();
				handleCount(userId);
			}
		}
		String targetUserId = privateChatMsgData.getSendInfo().getUserId();
		Integer count = _receiveCountOfUser.get(targetUserId);
//		System.out.println("targetUserId=" + targetUserId + ", count=" + count);
		if (count != null && count >= ChatHandler.MAX_CACHE_MSG_SIZE_PER_ONE) {
//			System.out.println("超出數量限制！srcUserId=" + this.userId + ", targetUserId=" + targetUserId);
			for (Iterator<ChatMessageSaveData> itr = privateChat.iterator(); itr.hasNext();) {
				ChatMessageSaveData temp = itr.next();
				if (temp.getSendInfo().getUserId().equals(targetUserId)) {
					itr.remove();
//					System.out.println("移除：" + temp);
					_receiveCountOfUser.put(targetUserId, --count);
					break;
				}
			}
		}
	}
	
	private int decreaseCount(String pUserId, Map<String, Integer> map) {
		Integer count = map.remove(pUserId);
		count--;
		if (count > 0) {
			map.put(pUserId, count);
		}
		return count;
	}
	
	private int increaseCount(String pUserId, Map<String, Integer> map) {
		Integer count = map.get(pUserId);
		if (count == null) {
			count = 1;
		} else {
			count++;
		}
		map.put(pUserId, count);
		return count;
	}
	
	private void afterRemoveOld(ChatMessageSaveData data) {
		String tempUserId;
		if (data.getSendInfo() != null) {
			if (!(tempUserId = data.getSendInfo().getUserId()).equals(this.userId)) {
				this.decreaseCount(tempUserId, _receiveCountOfUser);
			}
		}
	}
	
	private void afterAddNew(ChatMessageSaveData data) {
		String userId;
		if (data.getSendInfo() != null && !(userId = data.getSendInfo().getUserId()).equals(this.userId)) {
			this.increaseCount(userId, _receiveCountOfUser);
			return;
		}
	}
	
	private void addToList(ChatMessageSaveData privateChatMsgData, List<ChatMessageSaveData> target, int sizeControl) {
		int size = target.size();
		if (size >= sizeControl) {
			ChatMessageSaveData removed = target.remove(0);
			this.afterRemoveOld(removed);
		}

		target.add(privateChatMsgData);
		
		this.afterAddNew(privateChatMsgData);
	}
	
	@JsonIgnore
	public String getCurrentTargetUserIdOfPrivateChat() {
		return _currentTargetUserIdOfPrivateChat;
	}
	
	@JsonIgnore
	public void setCurrentTargetUserIdOfPrivateChat(String userId) {
		this._currentTargetUserIdOfPrivateChat = userId;
	}

	/**
	 * 添加一个私聊信息
	 * 
	 * @param privateChatMsgData
	 */
	public synchronized void addPrivateChatMessage(ChatMessageSaveData privateChatMsgData) {
		List<ChatMessageSaveData> targetList;
		int sizeControl;
		if (privateChatMsgData.getSendInfo() == null || privateChatMsgData.getSendInfo().getUserId().equals(userId)) {
			// 我發出的
			sizeControl = ChatHandler.MAX_CACHE_MSG_SIZE_OF_PRIVATE_CHAT;
			targetList = privateChatSent;
		} else {
			// 別人發給我的
			checkOnAdd(privateChatMsgData);
			sizeControl = ChatHandler.MAX_CACHE_MSG_SIZE_OF_PRIVATE_CHAT;
			targetList = privateChat;
		}
		this.addToList(privateChatMsgData, targetList, sizeControl);
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
		if (privateChat.isEmpty() && privateChatSent.isEmpty()) {
			return Collections.emptyList();
		}
//		List<ChatMessageSaveData> mergeList = new LinkedList<ChatMessageSaveData>(privateChat);
//		int lastIndex = 0;
//		int flagIndex = 0;
//		int mergeCount = 0;
//		ChatMessageSaveData sent;
//		ChatMessageSaveData rec;
//		for(int i = 0; i < privateChatSent.size(); i++) {
//			sent = privateChat.get(i);
//			flagIndex = mergeList.size();
//			for(int k = lastIndex; k < flagIndex; k++) {
//				rec = mergeList.get(k);
//				if (sent.getSendTime() < rec.getSendTime()) {
//					mergeList.add(k, sent);
//					lastIndex = flagIndex = k; // update flag index  and last index
//					mergeCount++;
//					break;
//				}
//			}
//			if(flagIndex == mergeList.size()) {
//				break;
//			}
//		}
//		if(mergeCount < privateChatSent.size()) {
//			for(int i = mergeCount; i < privateChatSent.size(); i++) {
//				mergeList.add(privateChatSent.get(i));
//			}
//		}
//		return mergeList;
		List<ChatMessageSaveData> mergeList = new ArrayList<ChatMessageSaveData>(privateChat.size() + privateChatSent.size());
		mergeList.addAll(privateChat);
		mergeList.addAll(privateChatSent);
		if (mergeList.size() > 1) {
			Collections.sort(mergeList);
		}
		return mergeList;
	}

//	/**
//	 * 更新私聊信息中的某条信息的状态
//	 * 
//	 * @param index
//	 * @param chatMsgData
//	 */
//	@JsonIgnore
//	public synchronized void updatePrivateChatMessageState(int index, ChatMessageSaveData saveData) {
//		this.privateChat.set(index, saveData);
//	}
	
	/**
	 * 更新私聊信息中的某条信息的状态
	 * 
	 * @param index
	 * @param chatMsgData
	 */
	@JsonIgnore
	public synchronized void updatePrivateChatMessageState(ChatMessageData data) {
		List<ChatMessageSaveData> targetList = null;
		IFunction<ChatMessageSaveData, String> getFunc = null;
		IFunction<ChatMessageData, String> getOfProtoFunc = null;
		// 注：sendUserInfo和receiverUserInfo現在是互斥的
		if (data.hasSendMessageUserInfo()) {
			// 如果有sendUserInfo的情況
			if (data.getSendMessageUserInfo().getUserId() == userId) {
				// 如果sender的UserId是自身的userId，則表示這條私聊是我發出的
				getFunc = _getReceiverIdFunc;
				getOfProtoFunc = _getReceiverIdOfProtoFunc;
				targetList = privateChatSent;
			} else {
				// 否則，這條私聊是我收到的，因為我不是發送者
				getFunc = _getSenderIdFunc;
				getOfProtoFunc = _getSenderIdOfProtoFunc;
				targetList = privateChat;
			}
		} else {
			// 如果有接收者userInfo的情況
			if (data.getReceiveMessageUserInfo().getUserId().equals(userId)) {
				// 如果接收者的userId和自身的userId相同，則表示這條私聊是我收到的
				getFunc = _getSenderIdFunc;
				getOfProtoFunc = _getSenderIdOfProtoFunc;
				targetList = privateChat;
			} else {
				// 否則表示這條私聊是我發出的
				getFunc = _getReceiverIdFunc;
				getOfProtoFunc = _getReceiverIdOfProtoFunc;
				targetList = privateChatSent;
			}
		}
		for (int i = 0; i < targetList.size(); i++) {
			ChatMessageSaveData cmsd = targetList.get(i);
			String userIdOfCmsd = getFunc.apply(cmsd);
			String userIdOfData = getOfProtoFunc.apply(data);
//			System.out.println(String.format("userIdOfCmsd=%s, userIdOfData=%s, cmsd.getSendTime()=%d, data.getTime()=%d", userIdOfCmsd, userIdOfData, cmsd.getSendTime(), data.getTime()));
			if (userIdOfCmsd.equals(userIdOfData) && cmsd.getSendTime() == data.getTime()) {
				cmsd.setRead(true);
				break;
			}
		}
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
	
	private static class GetSenderUserIdFunc implements IFunction<ChatMessageSaveData, String> {

		@Override
		public String apply(ChatMessageSaveData t) {
			return t.getSendInfo().getUserId();
		}

	}
	
	private static class GetReceiverUserIdFunc implements IFunction<ChatMessageSaveData, String> {

		@Override
		public String apply(ChatMessageSaveData t) {
			return t.getReceiveInfo().getUserId();
		}

	}
	
	private static class GetSenderUserIdOfProtoFunc implements IFunction<ChatMessageData, String> {

		@Override
		public String apply(ChatMessageData t) {
			return t.getSendMessageUserInfo().getUserId();
		}
		
	}
	
	private static class GetReceiverUserIdOfProtoFunc implements IFunction<ChatMessageData, String> {

		@Override
		public String apply(ChatMessageData t) {
			return t.getReceiveMessageUserInfo().getUserId();
		}
		
	}
}