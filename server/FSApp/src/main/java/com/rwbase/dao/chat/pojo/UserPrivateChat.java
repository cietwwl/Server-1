package com.rwbase.dao.chat.pojo;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.bm.chat.ChatInteractiveType;
import com.rw.service.chat.ChatHandler;
import com.rwbase.common.IBIFunction;
import com.rwbase.common.IFunction;
import com.rwproto.ChatServiceProtos.ChatMessageData;

/**
 * @author HC
 * @date 2015年8月12日 下午2:14:11
 * @Description 
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using=UserPrivateChat.UserPrivateChatDeserializer.class)
public class UserPrivateChat {
	
	private static final IFunction<ChatMessageSaveData, String> _getSenderIdFunc = new GetSenderUserIdFunc();
	private static final IFunction<ChatMessageSaveData, String> _getReceiverIdFunc = new GetReceiverUserIdFunc();
	private static final IFunction<ChatMessageData, String> _getSenderIdOfProtoFunc = new GetSenderUserIdOfProtoFunc();
	private static final IFunction<ChatMessageData, String> _getReceiverIdOfProtoFunc = new GetReceiverUserIdOfProtoFunc();
	
	
	// 以下function通過反射賦值給_checkSaveUserInfoFuncs BEGIN >>>>>>
	@SuppressWarnings("unused")
	private static final IBIFunction<UserPrivateChat, String, Boolean> _checkPrivateSentFunc = new CheckPrivateChatSentFunc();
	@SuppressWarnings("unused")
	private static final IBIFunction<UserPrivateChat, String, Boolean> _checkReceiveCountFunc = new CheckReceiveCountFunc();
	@SuppressWarnings("unused")
	private static final IBIFunction<UserPrivateChat, String, Boolean> _checkInteractiveFunc = new CheckInteractiveDataFunc();
	// END <<<<<<
	private static final List<IBIFunction<UserPrivateChat, String, Boolean>> _checkSaveUserInfoFuncs = new ArrayList<IBIFunction<UserPrivateChat,String,Boolean>>();
	static {
		Field[] allFields = UserPrivateChat.class.getDeclaredFields();
		for (Field f : allFields) {
			if (f.getType().isAssignableFrom(IBIFunction.class)) {
				try {
					@SuppressWarnings("unchecked")
					IBIFunction<UserPrivateChat, String, Boolean> instance = (IBIFunction<UserPrivateChat, String, Boolean>) f.get(null);
					_checkSaveUserInfoFuncs.add(instance);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static class UserPrivateChatDeserializer extends JsonDeserializer<UserPrivateChat> {
		
		private static final Map<String, Field> _fieldMap = new HashMap<String, Field>();
		
		static {
			Field[] allFields = UserPrivateChat.class.getDeclaredFields();
			for(int i = 0; i < allFields.length; i++) {
				Field f = allFields[i];
				if(f.isAnnotationPresent(JsonProperty.class)) {
					f.setAccessible(true);
					_fieldMap.put(f.getName(), f);
				}
			}
		}
		
		private void handleUserInfo(String ownerUserId, List<ChatMessageSaveData> mList, Map<String, ChatUserInfo> uMap) {
			if (mList.isEmpty()) {
				return;
			}
			for (ChatMessageSaveData m : mList) {
				ChatUserInfo cui = null;
				if (m.getSendInfo() != null) {
					if (!m.getSendInfo().getUserId().equals(ownerUserId)) {
						cui = m.getSendInfo();
					}
				} else if (m.getReceiveInfo() != null) {
					if (!m.getReceiveInfo().getUserId().equals(ownerUserId)) {
						cui = m.getReceiveInfo();
					}
				}
				if (cui != null) {
					if (!uMap.containsKey(cui.getUserId())) {
						uMap.put(cui.getUserId(), cui.createAndCopy());
					}
				}
			}
		}
		
		private void setUserInfo(List<ChatMessageSaveData> mList, Map<String, ChatUserInfo> uMap) {
			// 把ChatUserInfo賦值ChatMessageSaveData的sender或者receiver
			if(mList.isEmpty()) {
				return;
			}
			for(ChatMessageSaveData data : mList) {
				if(data.getReceiverUserId() != null) {
					data.setReceiveInfo(uMap.get(data.getReceiverUserId()));
				}
				if(data.getSenderUserId() != null) {
					data.setSendInfo(uMap.get(data.getSenderUserId()));
				}
			}
		}
		
		@Override
		public UserPrivateChat deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			UserPrivateChat u = new UserPrivateChat();
			JsonNode node = jp.getCodec().readTree(jp);
			Map.Entry<String, Field> entry;
			for (Iterator<Map.Entry<String, Field>> itr = _fieldMap.entrySet().iterator(); itr.hasNext();) {
				entry = itr.next();
				JsonNode currentNode = node.get(entry.getKey());
				if (currentNode != null) {
					CommonJsonFieldValueSetter.setValue(currentNode, entry.getValue(), u);
				}
			}
			if ((u.privateChat.size() > 0 || u.privateChatSent.size() > 0)) {
				if (u.saveUserInfos.isEmpty()) {
					// 舊數據沒有saveUserInfos，所以要生成一次
					this.handleUserInfo(u.userId, u.privateChat, u.saveUserInfos);
					this.handleUserInfo(u.userId, u.privateChatSent, u.saveUserInfos);
				} else {
					// 賦值ChatUserInfo到ChatMessageSaveData
					this.setUserInfo(u.privateChat, u.saveUserInfos);
					this.setUserInfo(u.privateChatSent, u.saveUserInfos);
				}
				if (u.interactiveDatas.size() > 0) {
					for (Iterator<List<ChatMessageSaveData>> itr = u.interactiveDatas.values().iterator(); itr.hasNext();) {
						this.setUserInfo(itr.next(), u.saveUserInfos);
					}
				}
			}
			return u;
		}
	}
	
	@Id
	@JsonProperty
	private String userId;// 主键
	// private List<String> privateChatList;// 私聊信息列表
	// private List<String> treasureChatList;// 密境信息列表
	@JsonIgnore
	private Map<String, Integer> _receiveCountOfUser = new HashMap<String, Integer>();
	@JsonIgnore
	private String _currentTargetUserIdOfPrivateChat = ""; // 當前私聊的目標id
	@JsonIgnore
	private long _lastSentPrivateChatTime; // 上一次發送私聊信息的時間
	@JsonSerialize(include=Inclusion.NON_EMPTY)
	@JsonProperty
	private List<ChatMessageSaveData> privateChat;// 私聊信息列表
	@JsonSerialize(include=Inclusion.NON_EMPTY)
	@JsonProperty
	private List<ChatMessageSaveData> privateChatSent; // 我发出的私聊信息
	@JsonSerialize(include=Inclusion.NON_EMPTY)
	@JsonProperty
	private List<ChatMessageSaveData> secretChat;// 帮派秘境的聊天信息列表;
	@JsonSerialize(include=Inclusion.NON_EMPTY)
	@JsonProperty
	private Map<ChatInteractiveType, List<ChatMessageSaveData>> interactiveDatas; // 互動信息
	@JsonSerialize(include=Inclusion.NON_EMPTY)
	@JsonProperty
	private Map<String, ChatUserInfo> saveUserInfos; // 保存的userInfo
	
//	private static void handleUserInfo(String ownerUserId, List<ChatMessageSaveData> mList, Map<String, ChatUserInfo> uMap) {
//		if (mList.isEmpty()) {
//			return;
//		}
//		for (ChatMessageSaveData m : mList) {
//			ChatUserInfo cui = null;
//			if (m.getSendInfo() != null) {
//				if (!m.getSendInfo().getUserId().equals(ownerUserId)) {
//					cui = m.getSendInfo();
//				}
//			} else if (m.getReceiveInfo() != null) {
//				if (!m.getReceiveInfo().getUserId().equals(ownerUserId)) {
//					cui = m.getReceiveInfo();
//				}
//			}
//			if (cui != null) {
//				if (!uMap.containsKey(cui.getUserId())) {
//					uMap.put(cui.getUserId(), cui.createAndCopy());
//				}
//			}
//		}
//	}
//	
//	private static void setUserInfo(List<ChatMessageSaveData> mList, Map<String, ChatUserInfo> uMap) {
//		// 把ChatUserInfo賦值ChatMessageSaveData的sender或者receiver
//		if(mList.isEmpty()) {
//			return;
//		}
//		for(ChatMessageSaveData data : mList) {
//			if(data.getReceiverUserId() != null) {
//				data.setReceiveInfo(uMap.get(data.getReceiverUserId()));
//			}
//			if(data.getSenderUserId() != null) {
//				data.setSendInfo(uMap.get(data.getSenderUserId()));
//			}
//		}
//	}
	
//	@JsonCreator
//	public static UserPrivateChat forValue(@JsonProperty("userId") String userId, @JsonProperty("privateChat") List<ChatMessageSaveData> privateChat, @JsonProperty("privateChatSent") List<ChatMessageSaveData> privateChatSent, @JsonProperty("secretChat") List<ChatMessageSaveData> secretChat,
//			@JsonProperty("interactiveDatas") Map<ChatInteractiveType, List<ChatMessageSaveData>> interactiveData, @JsonProperty("saveUserInfos") Map<String, ChatUserInfo> saveUserInfos) {
//		UserPrivateChat u = new UserPrivateChat();
//		u.userId = userId;
//		if (privateChat != null) {
//			u.privateChat = privateChat;
//		}
//		if (privateChatSent != null) {
//			u.privateChatSent = privateChatSent;
//		}
//		if (secretChat != null) {
//			u.secretChat = secretChat;
//		}
//		if (interactiveData != null) {
//			u.interactiveDatas = interactiveData;
//		}
//		if (saveUserInfos != null) {
//			u.saveUserInfos = saveUserInfos;
//		}
//		if ((u.privateChat.size() > 0 || u.privateChatSent.size() > 0)) {
//			if (u.saveUserInfos.isEmpty()) {
//				// 舊數據沒有saveUserInfos，所以要生成一次
//				handleUserInfo(u.userId, u.privateChat, u.saveUserInfos);
//				handleUserInfo(u.userId, u.privateChatSent, u.saveUserInfos);
//			} else {
//				// 賦值ChatUserInfo到ChatMessageSaveData
//				setUserInfo(u.privateChat, u.saveUserInfos);
//				setUserInfo(u.privateChatSent, u.saveUserInfos);
//			}
//			if (u.interactiveDatas.size() > 0) {
//				for (Iterator<List<ChatMessageSaveData>> itr = u.interactiveDatas.values().iterator(); itr.hasNext();) {
//					setUserInfo(itr.next(), u.saveUserInfos);
//				}
//			}
//		}
//		return u;
//	}

	public UserPrivateChat() {
		privateChat = new ArrayList<ChatMessageSaveData>(ChatHandler.MAX_CACHE_MSG_SIZE_OF_PRIVATE_CHAT);
		privateChatSent = new ArrayList<ChatMessageSaveData>(ChatHandler.MAX_CACHE_MSG_SIZE_OF_PRIVATE_CHAT);
		secretChat = new ArrayList<ChatMessageSaveData>(ChatHandler.MAX_CACHE_MSG_SIZE);
		interactiveDatas = new HashMap<ChatInteractiveType, List<ChatMessageSaveData>>(ChatInteractiveType.values().length + 1, 1.0f);
		saveUserInfos = new HashMap<String, ChatUserInfo>();
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
	
	private boolean checkUserExists(String targetUserId) {
		for(IBIFunction<UserPrivateChat, String, Boolean> func : _checkSaveUserInfoFuncs) {
			if(func.apply(this, targetUserId)) {
				return true;
			}
		}
		return false;
	}

	private void afterRemoveOld(ChatMessageSaveData data, List<ChatMessageSaveData> targetList) {
		String tempUserId;
		if (targetList == privateChat) {
			// 如果是收到的消息，需要處理收到的數量
			tempUserId = data.getSenderUserId();
			this.decreaseCount(tempUserId, _receiveCountOfUser);
		} else {
			if (data.getSenderUserId() != null) {
				tempUserId = data.getSenderUserId();
			} else {
				tempUserId = data.getReceiverUserId();
			}
		}
		if (!this.checkUserExists(tempUserId)) {
			saveUserInfos.remove(tempUserId);
		}
	}
	
	private void afterAddNew(ChatMessageSaveData data, List<ChatMessageSaveData> targetList) {
		if (targetList == privateChat || targetList == privateChatSent) {
			String userId;
			if (data.getSendInfo() != null) {
				if (!(userId = data.getSendInfo().getUserId()).equals(this.userId)) {
					this.increaseCount(userId, _receiveCountOfUser);
				}
			}
		}
	}
	
	private void addToList(ChatMessageSaveData privateChatMsgData, List<ChatMessageSaveData> target, int sizeControl) {
		int size = target.size();
		if (size >= sizeControl) {
			ChatMessageSaveData removed = target.remove(0);
			this.afterRemoveOld(removed, target);
		}

		target.add(privateChatMsgData);
		
		this.afterAddNew(privateChatMsgData, target);
	}
	
	@JsonIgnore
	public String getCurrentTargetUserIdOfPrivateChat() {
		return _currentTargetUserIdOfPrivateChat;
	}
	
	@JsonIgnore
	public void setCurrentTargetUserIdOfPrivateChat(String userId) {
		this._currentTargetUserIdOfPrivateChat = userId;
	}
	
	public long getLastSentPrivateChatTime() {
		return _lastSentPrivateChatTime;
	}
	
	public void setLastSentPrivateChatTime(long timeMillis) {
		this._lastSentPrivateChatTime = timeMillis;
	}

	/**
	 * 添加一个私聊信息
	 * 
	 * @param privateChatMsgData
	 */
	public synchronized void addPrivateChatMessage(ChatMessageSaveData privateChatMsgData) {
		List<ChatMessageSaveData> targetList;
		int sizeControl;
		ChatUserInfo relativeUserInfo;
		if (privateChatMsgData.getSendInfo() == null || privateChatMsgData.getSendInfo().getUserId().equals(userId)) {
			// 我發出的
			sizeControl = ChatHandler.MAX_CACHE_MSG_SIZE_OF_PRIVATE_CHAT;
			targetList = privateChatSent;
			relativeUserInfo = privateChatMsgData.getReceiveInfo();
		} else {
			// 別人發給我的
			checkOnAdd(privateChatMsgData);
			sizeControl = ChatHandler.MAX_CACHE_MSG_SIZE_OF_PRIVATE_CHAT;
			targetList = privateChat;
			relativeUserInfo = privateChatMsgData.getSendInfo();
		}
		this.addToList(privateChatMsgData, targetList, sizeControl);
		ChatUserInfo saveInfo = saveUserInfos.get(relativeUserInfo.getUserId());
		if (saveInfo == null) {
			saveUserInfos.put(relativeUserInfo.getUserId(), relativeUserInfo.createAndCopy());
		} else {
			saveInfo.update(relativeUserInfo);
		}
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
	
	public void addInteractiveChatMessage(ChatInteractiveType type, ChatMessageSaveData pInteractiveData) {
		synchronized (interactiveDatas) {
			List<ChatMessageSaveData> list = interactiveDatas.get(type);
			if (list == null) {
				list = new ArrayList<ChatMessageSaveData>();
				interactiveDatas.put(type, list);
			}
			this.addToList(pInteractiveData, list, ChatHandler.MAX_CACHE_INTERACTIVE_SIZE);
		}
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
	
	public Map<ChatInteractiveType, List<ChatMessageSaveData>> getInteractiveChatMsg() {
		return Collections.unmodifiableMap(interactiveDatas);
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
	
	private static class CheckReceiveCountFunc implements IBIFunction<UserPrivateChat, String, Boolean> {

		@Override
		public Boolean apply(UserPrivateChat t, String targetUserId) {
			// 是否包含該user
			return t._receiveCountOfUser.containsKey(targetUserId);
		}
	}
	
	private static class CheckPrivateChatSentFunc implements IBIFunction<UserPrivateChat, String, Boolean> {

		@Override
		public Boolean apply(UserPrivateChat t, String targetUserId) {
			List<ChatMessageSaveData> list = t.privateChatSent;
			for (ChatMessageSaveData cmsd : list) {
				if (cmsd.getReceiverUserId().equals(targetUserId)) {
					return true;
				}
			}
			return false;
		}
	}
	
	private static class CheckInteractiveDataFunc implements IBIFunction<UserPrivateChat, String, Boolean> {

		@Override
		public Boolean apply(UserPrivateChat t, String targetUserId) {
			if (t.interactiveDatas.isEmpty()) {
				return false;
			}
			for (List<ChatMessageSaveData> list : t.interactiveDatas.values()) {
				for (ChatMessageSaveData cmsd : list) {
					if (cmsd.getSenderUserId().equals(targetUserId)) {
						return true;
					}
				}
			}
			return false;
		}

	}
}