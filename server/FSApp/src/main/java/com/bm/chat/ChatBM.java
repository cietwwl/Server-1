package com.bm.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.StringUtils;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rw.fsutil.common.SimpleThreadFactory;
import com.rw.netty.UserChannelMgr;
import com.rw.service.chat.ChatHandler;
import com.rwbase.dao.chat.TableUserPrivateChatDao;
import com.rwbase.dao.chat.pojo.ChatMessageSaveData;
import com.rwbase.dao.chat.pojo.ChatUserInfo;
import com.rwbase.dao.chat.pojo.UserPrivateChat;
import com.rwbase.dao.friend.FriendUtils;
import com.rwproto.ChatServiceProtos.ChatMessageData;
import com.rwproto.ChatServiceProtos.MessageUserInfo;
import com.rwproto.ChatServiceProtos.MsgChatResponse;
import com.rwproto.ChatServiceProtos.eChatResultType;
import com.rwproto.ChatServiceProtos.eChatType;
import com.rwproto.MsgDef;

// 聊天缓存

public class ChatBM {

	private static ChatBM instance = new ChatBM();
	// private static List<ChatMessageData.Builder> listWorld = Collections.synchronizedList(new
	// ArrayList<ChatMessageData.Builder>(ChatHandler.MAX_CACHE_MSG_SIZE));// 多线程保护
	private static ConcurrentHashMap<String, List<ChatMessageData.Builder>> familyChatMap = new ConcurrentHashMap<String, List<ChatMessageData.Builder>>();

	private static List<ChatInfo> worldMessageList = new ArrayList<ChatInfo>(ChatHandler.MAX_CACHE_MSG_SIZE);
	private AtomicInteger messageId = new AtomicInteger();// 当前最新的消息Id
	private AtomicInteger checkMessageId = new AtomicInteger();// 上次检查的版本号
	private ScheduledExecutorService ses = Executors.newScheduledThreadPool(1, new SimpleThreadFactory("chat_broadcast"));// 线程池

	private class ChatRun implements Runnable {

		@Override
		public void run() {
			try {
				// System.err.println("检查的版本Id：" + checkMessageId.get() + ",消息版本：" + messageId.get());
				if (checkMessageId.get() >= messageId.get()) {// 上次服务器发送版本大于当前
					return;
				}

				checkMessageId.set(messageId.get());// 检查的版本更新

				Set<String> playerIdList = UserChannelMgr.getOnlinePlayerIdSet();
				if (playerIdList == null || playerIdList.isEmpty()) {
					return;
				}

				if (worldMessageList.isEmpty()) {
					return;
				}

				List<ChatInfo> list = ChatBM.getInstance().getWorldList();
				int size = list.size();

				Iterator<String> itr = playerIdList.iterator();
				while (itr.hasNext()) {
					String playerId = itr.next();
					Player player = PlayerMgr.getInstance().find(playerId);
					if (player == null) {
						continue;
					}

					int lastWorldChatId = player.getLastWorldChatId();
					if (lastWorldChatId >= messageId.get()) {
						continue;
					}

					MsgChatResponse.Builder msgChatResponse = MsgChatResponse.newBuilder();
					msgChatResponse.setChatType(eChatType.CHAT_WORLD);

					// msgChatResponse.setOnLogin(false);
					for (int i = 0; i < size; i++) {
						ChatInfo chatInfo = list.get(i);
						ChatMessageData chatMsg = chatInfo.getMessage().build();
						String userId = chatMsg.getSendMessageUserInfo().getUserId();
						if (userId == null || userId.isEmpty()) {
							continue;
						}

						if (userId.equals(playerId)) {
							continue;
						}

						if (player.getLastWorldChatId() >= chatInfo.getId()) {
							continue;
						}

						if (!FriendUtils.isBlack(player, userId)) {// 不在黑名单
							msgChatResponse.addListMessage(chatMsg);
						}
					}

					msgChatResponse.setChatResultType(eChatResultType.SUCCESS);
					player.SendMsg(MsgDef.Command.MSG_CHAT, msgChatResponse.build().toByteString());
					player.setLastWorldChatId(messageId.get());// 缓存版本号
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private ChatBM() {
		ses.scheduleAtFixedRate(new ChatRun(), 0, 1500, TimeUnit.MILLISECONDS);
	}

	public static ChatBM getInstance() {
		return instance;
	}

	/**
	 * 增加一条聊天
	 * 
	 * @param data
	 */
	public synchronized int updateWroldList(ChatMessageData.Builder data) {
		int andIncrement = messageId.incrementAndGet();// 增加一个消息Id版本
		if (worldMessageList.size() > ChatHandler.MAX_CACHE_MSG_SIZE) {
			worldMessageList.remove(0);
		}
		worldMessageList.add(new ChatInfo(andIncrement, data));

		// System.err.println("增加一个迭代版本：" + messageId.get());
		return andIncrement;
	}

	public synchronized List<ChatInfo> getWorldList() {
		return new ArrayList<ChatInfo>(worldMessageList);
	}

	/**
	 * 获取当前的消息版本
	 * 
	 * @return
	 */
	public int getChatVersion() {
		return this.messageId.get();
	}

	/**
	 * 获取帮派聊天的列表
	 * 
	 * @param familyId
	 * @return
	 */
	public List<ChatMessageData.Builder> getFamilyChatList(String familyId) {
		List<ChatMessageData.Builder> list = familyChatMap.get(familyId);
		return (list == null || list.isEmpty()) ? new ArrayList<ChatMessageData.Builder>() : new ArrayList<ChatMessageData.Builder>(list);
	}

	/**
	 * 增加帮派聊天
	 * 
	 * @param familyId 帮派Id
	 * @param chatMsg 聊天消息
	 */
	public void addFamilyChat(String familyId, ChatMessageData.Builder chatMsg) {
		List<ChatMessageData.Builder> list = familyChatMap.get(familyId);
		if (list == null) {
			list = new ArrayList<ChatMessageData.Builder>();
			familyChatMap.put(familyId, list);
		}

		list.add(chatMsg);
		if (list.size() > ChatHandler.MAX_CACHE_MSG_SIZE) {
			list.remove(0);
		}
	}

	/**
	 * 更新私聊的消息
	 * 
	 * @param userId
	 * @param updateMap
	 */
	public void updatePrivateChatState(String userId, Map<Integer, ChatMessageData> updateMap) {
		if (updateMap == null || updateMap.isEmpty()) {
			return;
		}

		TableUserPrivateChatDao dao = TableUserPrivateChatDao.getDao();
		UserPrivateChat chat = dao.get(userId);

		for (Entry<Integer, ChatMessageData> e : updateMap.entrySet()) {
			ChatMessageSaveData saveData = parseMsgData2SaveData(e.getValue());
			if (saveData == null) {
				continue;
			}

			saveData.setRead(true);
			chat.updatePrivateChatMessageState(e.getKey(), saveData);
		}

		dao.update(userId);
	}

	/**
	 * 更新帮派秘境的消息
	 * 
	 * @param userId
	 * @param updateMap
	 */
	public void updateGroupSecretChatState(String userId, Map<Integer, ChatMessageData> updateMap) {
		if (updateMap == null || updateMap.isEmpty()) {
			return;
		}

		TableUserPrivateChatDao dao = TableUserPrivateChatDao.getDao();
		UserPrivateChat chat = dao.get(userId);

		for (Entry<Integer, ChatMessageData> e : updateMap.entrySet()) {
			ChatMessageSaveData saveData = parseMsgData2SaveData(e.getValue());
			if (saveData == null) {
				continue;
			}

			chat.updateGroupSecretChatMessageState(e.getKey(), saveData);
		}

		dao.update(userId);
	}

	/**
	 * 增加私聊信息
	 * 
	 * @param userId
	 * @param msgData
	 */
	public void addPrivateChat(String userId, ChatMessageData msgData) {
		ChatMessageSaveData saveData = parseMsgData2SaveData(msgData);
		if (saveData == null) {
			return;
		}

		TableUserPrivateChatDao dao = TableUserPrivateChatDao.getDao();
		UserPrivateChat chat = dao.get(userId);
		chat.addPrivateChatMessage(saveData);

		dao.update(userId);
	}

	/**
	 * 增加秘境信息
	 * 
	 * @param userId
	 * @param msgData
	 */
	public void addGroupSecretChat(String userId, ChatMessageData msgData) {
		ChatMessageSaveData saveData = parseMsgData2SaveData(msgData);
		if (saveData == null) {
			return;
		}

		TableUserPrivateChatDao dao = TableUserPrivateChatDao.getDao();
		UserPrivateChat chat = dao.get(userId);
		chat.addTreasureChatMessage(saveData);

		dao.update(userId);
	}

	/**
	 * 获取私聊的消息列表
	 * 
	 * @param userId
	 * @return
	 */
	public List<ChatMessageData> getPrivateChatList(String userId) {
		UserPrivateChat dao = TableUserPrivateChatDao.getDao().get(userId);
		List<ChatMessageSaveData> privateChatMessageList = dao.getPrivateChatMessageList();
		if (privateChatMessageList.isEmpty()) {
			return Collections.emptyList();
		}

		int size = privateChatMessageList.size();
		List<ChatMessageData> msgList = new ArrayList<ChatMessageData>(size);
		for (int i = 0; i < size; i++) {
			ChatMessageData msgData = parseSaveData2MsgData(privateChatMessageList.get(i));
			if (msgData == null) {
				continue;
			}

			msgList.add(msgData);
		}

		return msgList;
	}

	/**
	 * 获取秘境消息的列表
	 * 
	 * @param userId
	 * @return
	 */
	public List<ChatMessageData> getGroupSecretChatList(String userId) {
		UserPrivateChat dao = TableUserPrivateChatDao.getDao().get(userId);
		List<ChatMessageSaveData> groupSecretChatList = dao.getTreasureChatMessageList();
		if (groupSecretChatList.isEmpty()) {
			return Collections.emptyList();
		}

		int size = groupSecretChatList.size();
		List<ChatMessageData> msgList = new ArrayList<ChatMessageData>(size);
		for (int i = 0; i < size; i++) {
			ChatMessageData msgData = parseSaveData2MsgData(groupSecretChatList.get(i));
			if (msgData == null) {
				continue;
			}

			msgList.add(msgData);
		}

		return msgList;
	}

	/**
	 * 转换存储的消息到
	 * 
	 * @param saveData
	 * @return
	 */
	private ChatMessageData parseSaveData2MsgData(ChatMessageSaveData saveData) {
		if (saveData == null) {
			return null;
		}

		// 发送的人
		MessageUserInfo sendInfo = parseSaveUserData2MsgData(saveData.getSendInfo());
		if (sendInfo == null) {
			return null;
		}

		ChatMessageData.Builder messageData = ChatMessageData.newBuilder();

		messageData.setSendMessageUserInfo(sendInfo);

		// 接受的人
		MessageUserInfo receiveInfo = parseSaveUserData2MsgData(saveData.getReceiveInfo());
		if (receiveInfo != null) {
			messageData.setReceiveMessageUserInfo(receiveInfo);
		}

		// 设置其他消息
		messageData.setIsRead(saveData.isRead());
		// try {
		// messageData.setMessage(Base64.byteArrayToBase64((saveData.getMessage()).getBytes("UTF-8")));
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// return null;
		// }
		String message = saveData.getMessage();
		messageData.setMessage(!StringUtils.isEmpty(message) ? message : "");

		String time = saveData.getTime();
		if (!StringUtils.isEmpty(time)) {
			messageData.setTime(time);
		}

		String secId = saveData.getSecId();
		if (!StringUtils.isEmpty(secId)) {
			messageData.setTreasureId(secId);
			messageData.setTreasureDefNum(saveData.getInviteNum());
			messageData.setTreasureType(saveData.getSecCfgId());
		}

		return messageData.build();
	}

	/**
	 * 把聊天人的信息转换成协议中需要的对象
	 * 
	 * @param info
	 * @return
	 */
	private MessageUserInfo parseSaveUserData2MsgData(ChatUserInfo info) {
		if (info != null) {
			MessageUserInfo.Builder userInfo = MessageUserInfo.newBuilder();
			String groupId = info.getGroupId();
			if (!StringUtils.isEmpty(groupId)) {
				userInfo.setFamilyId(groupId);
			}

			String groupName = info.getGroupName();
			if (!StringUtils.isEmpty(groupName)) {
				userInfo.setFamilyName(groupName);
			}

			String userId = info.getUserId();
			if (!StringUtils.isEmpty(userId)) {
				userInfo.setUserId(userId);
			}

			String userName = info.getUserName();
			if (!StringUtils.isEmpty(userName)) {
				userInfo.setUserName(userName);
			}

			String headbox = info.getHeadbox();
			if (!StringUtils.isEmpty(headbox)) {
				userInfo.setHeadbox(headbox);
			}

			String headImage = info.getHeadImage();
			if (!StringUtils.isEmpty(headImage)) {
				userInfo.setHeadImage(headImage);
			}

			userInfo.setLevel(info.getLevel());
			return userInfo.build();
		}

		return null;
	}

	/**
	 * 转换传递的消息到存储的对象
	 * 
	 * @param chatMsgData
	 * @return
	 */
	private ChatMessageSaveData parseMsgData2SaveData(ChatMessageData chatMsgData) {
		if (chatMsgData == null) {
			return null;
		}

		ChatMessageSaveData saveData = new ChatMessageSaveData();

		ChatUserInfo sendInfo = parseMsgUserData2SaveData(chatMsgData.getSendMessageUserInfo());
		saveData.setSendInfo(sendInfo);

		if (chatMsgData.hasReceiveMessageUserInfo()) {
			ChatUserInfo receiveInfo = parseMsgUserData2SaveData(chatMsgData.getReceiveMessageUserInfo());
			saveData.setReceiveInfo(receiveInfo);
		}

		if (chatMsgData.hasIsRead()) {
			saveData.setRead(chatMsgData.getIsRead());
		}

		if (chatMsgData.hasTime()) {
			saveData.setTime(chatMsgData.getTime());
		}

		if (chatMsgData.hasMessage()) {
			// try {
			// saveData.setMessage(new String(Base64.base64ToByteArray(chatMsgData.getMessage()), "UTF-8"));
			// } catch (UnsupportedEncodingException e) {
			// e.printStackTrace();
			// return null;
			// }
			saveData.setMessage(chatMsgData.getMessage());
		}

		if (chatMsgData.hasTreasureId()) {
			saveData.setSecId(chatMsgData.getTreasureId());
		}

		if (chatMsgData.hasTreasureDefNum()) {
			saveData.setInviteNum(chatMsgData.getTreasureDefNum());
		}

		if (chatMsgData.hasTreasureType()) {
			saveData.setSecCfgId(chatMsgData.getTreasureType());
		}

		return saveData;
	}

	/**
	 * 把协议中传递的MessageUserInfo转换成存储结构
	 * 
	 * @param info
	 * @return
	 */
	private ChatUserInfo parseMsgUserData2SaveData(MessageUserInfo info) {
		if (info == null) {
			return null;
		}

		ChatUserInfo userInfo = new ChatUserInfo();
		if (info.hasFamilyId()) {
			userInfo.setGroupId(info.getFamilyId());
		}

		if (info.hasFamilyName()) {
			userInfo.setGroupName(info.getFamilyName());
		}

		if (info.hasHeadbox()) {
			userInfo.setHeadbox(info.getHeadbox());
		}

		if (info.hasHeadImage()) {
			userInfo.setHeadImage(info.getHeadImage());
		}

		if (info.hasLevel()) {
			userInfo.setLevel(info.getLevel());
		}

		if (info.hasUserId()) {
			userInfo.setUserId(info.getUserId());
		}

		if (info.hasUserName()) {
			userInfo.setUserName(info.getUserName());
		}

		return userInfo;
	}

	/**
	 * 清除所有的秘境邀请消息
	 * 
	 * @param userId
	 */
	public void clearAllGroupSecretChatMessage(String userId) {
		TableUserPrivateChatDao dao = TableUserPrivateChatDao.getDao();
		UserPrivateChat chat = dao.get(userId);
		chat.clearAllTreasureChatMessage();
		dao.update(userId);
	}
}