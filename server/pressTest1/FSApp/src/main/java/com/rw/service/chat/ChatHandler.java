package com.rw.service.chat;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.bm.chat.ChatBM;
import com.bm.chat.ChatInfo;
import com.bm.guild.GuildGTSMgr;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.guild.GuildDataMgr;
import com.playerdata.readonly.PlayerIF;
import com.rwbase.common.dirtyword.CharFilterFactory;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwbase.dao.chat.TableUserPrivateChatDao;
import com.rwbase.dao.chat.pojo.UserPrivateChat;
import com.rwbase.dao.friend.FriendUtils;
import com.rwbase.dao.gulid.faction.GuildMember;
import com.rwbase.dao.publicdata.PublicData;
import com.rwbase.dao.publicdata.PublicDataCfgDAO;
import com.rwproto.ChatServiceProtos.ChatMessageData;
import com.rwproto.ChatServiceProtos.MessageUserInfo;
import com.rwproto.ChatServiceProtos.MsgChatRequest;
import com.rwproto.ChatServiceProtos.MsgChatResponse;
import com.rwproto.ChatServiceProtos.eChatResultType;
import com.rwproto.ChatServiceProtos.eChatType;
import com.rwproto.MsgDef;
import com.rwproto.MsgDef.Command;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class ChatHandler {
	private static final long CHAT_DELAY_TIME_MILLIS = TimeUnit.SECONDS.toMillis(10);// 发言间隔10秒
	public static final int MAX_CACHE_MSG_SIZE = 6;// 各个聊天频道缓存的最大聊天记录数
	private static ChatHandler instance;
	private int m_ChatCost = 0;

	private ChatHandler() {
	};

	public static ChatHandler getInstance() {
		if (instance == null) {
			instance = new ChatHandler();
		}
		return instance;
	}

	public ByteString chatWorld(Player player, MsgChatRequest msgChatRequest) {
		MsgChatResponse.Builder msgChatResponse = MsgChatResponse.newBuilder();
		msgChatResponse.setChatType(msgChatRequest.getChatType());

		ChatMessageData message = msgChatRequest.getChatMessageData();
		String userId = player.getUserId();
		if (!message.hasSendMessageUserInfo()) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "发送聊天数据异常");
			msgChatResponse.setChatResultType(eChatResultType.FAIL);
			return msgChatResponse.build().toByteString();
		}

		long nowTime = System.currentTimeMillis();
		if (player.getLastWorldChatCacheTime() > 0) {
			if (nowTime - player.getLastWorldChatCacheTime() < CHAT_DELAY_TIME_MILLIS) {// 间隔10秒
				player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "发言太快");
				msgChatResponse.setChatResultType(eChatResultType.FAIL);
				return msgChatResponse.build().toByteString();
			}
		}

		player.setLastWorldChatCacheTime(nowTime);// 更新上次聊天的时间

		// 收费
		int freeChat = player.getUserGameDataMgr().getFreeChat();
		if (freeChat == 0) {
			// 扣钻石
			if (m_ChatCost == 0) {
				m_ChatCost = PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.ID_CHAT_COST);
			}
			if (player.getUserGameDataMgr().getGold() >= m_ChatCost) {
				player.getUserGameDataMgr().addGold(-m_ChatCost);
			} else {
				msgChatResponse.setChatResultType(eChatResultType.FAIL);
				return msgChatResponse.build().toByteString();
			}
		} else {
			player.getUserGameDataMgr().setFreeChat(freeChat - 1);
		}

		ChatMessageData.Builder data = ChatMessageData.newBuilder();
		MessageUserInfo.Builder sendMsgInfo = message.getSendMessageUserInfo().toBuilder();
		sendMsgInfo.setUserId(userId);

		data.setSendMessageUserInfo(sendMsgInfo);
		if (message.hasReceiveMessageUserInfo()) {
			data.setReceiveMessageUserInfo(message.getReceiveMessageUserInfo());
		}

		data.setMessage(filterDirtyWord(message.getMessage()));
		data.setTime(getMessageTime());

		msgChatResponse.addListMessage(data);
		msgChatResponse.setChatResultType(eChatResultType.SUCCESS);
		ByteString result = msgChatResponse.build().toByteString();

		ChatBM.getInstance().updateWroldList(data);
		// player.setLastWorldChatId(messageId);

		// // 推送给所有的在线的人
		// Map<String, Player> allPlayer = PlayerMgr.getInstance().getAllPlayer();
		// Set<Entry<String, Player>> players = allPlayer.entrySet();
		// for (Entry<String, Player> entry : players) {
		// Player p = entry.getValue();
		// if (p != null && !p.getUserId().equals(userId) && !FriendUtils.isBlack(p, userId)) {// 没有被拉黑，并且不是自己
		// p.SendMsgByOther(Command.MSG_CHAT, result);
		// }
		// }
		return result;
	}

	/**
	 * 发送帮派聊天信息
	 * 
	 * @param player
	 * @param msgChatRequest
	 * @return
	 */
	public ByteString chatInGruild(Player player, MsgChatRequest msgChatRequest) {
		MsgChatResponse.Builder msgChatResponse = MsgChatResponse.newBuilder();
		msgChatResponse.setChatType(msgChatRequest.getChatType());

//		String guildId = player.getGuildUserMgr().getGuildId();
		String guildId = "";
		if (guildId == null || guildId.isEmpty()) {
			msgChatResponse.setChatResultType(eChatResultType.FAIL);
			ByteString result = msgChatResponse.build().toByteString();
			return result;
		}

		GuildDataMgr guildDataMgr = GuildGTSMgr.getInstance().getById(guildId);
		// 帮派不存在
		if (guildDataMgr == null) {
			msgChatResponse.setChatResultType(eChatResultType.FAIL);
			ByteString result = msgChatResponse.build().toByteString();
			return result;
		}

		ChatMessageData message = msgChatRequest.getChatMessageData();
		if (!message.hasSendMessageUserInfo()) {
			List<ChatMessageData.Builder> list = ChatBM.getInstance().getFamilyChatList(guildId);
			for (int i = 0, size = list.size(); i < size; i++) {
				msgChatResponse.addListMessage(list.get(i));
			}
		} else {
			ChatMessageData.Builder data = ChatMessageData.newBuilder();
			MessageUserInfo.Builder sendMsgInfo = message.getSendMessageUserInfo().toBuilder();
			sendMsgInfo.setUserId(player.getUserId());
			data.setSendMessageUserInfo(sendMsgInfo);
			data.setTime(getMessageTime());
			data.setMessage(filterDirtyWord(message.getMessage()));
			msgChatResponse.addListMessage(data);
			ChatBM.getInstance().addFamilyChat(guildId, data);
		}

		// 填充完整的消息
		msgChatResponse.setChatResultType(eChatResultType.SUCCESS);
		ByteString result = msgChatResponse.build().toByteString();

		// 发送给其他成员
		String pId = player.getUserId();
		List<GuildMember> itemList = guildDataMgr.getGuildMemberHolder().getItemList();
		for (GuildMember guildMember : itemList) {
			String memUserId = guildMember.getUserId();
			if (pId.equals(memUserId)) {
				continue;
			}

			Player p = PlayerMgr.getInstance().find(memUserId);
			if (p == null) {
				continue;
			}

			p.SendMsg(Command.MSG_CHAT, result);
		}

		return result;
	}

	/**
	 * 私聊：和副本一样有严重的公用数据，处理私有聊天信息
	 * 
	 * @param player
	 * @param msgChatRequest
	 * @return
	 */
	public ByteString chatPerson(Player player, MsgChatRequest msgChatRequest) {
		MsgChatResponse.Builder msgChatResponse = MsgChatResponse.newBuilder();
		msgChatResponse.setChatType(msgChatRequest.getChatType());

		ChatMessageData message = msgChatRequest.getChatMessageData();
		// if (!message.hasSendMessageUserInfo()) {
		// // TODO @modify 2015-08-14 HC 消息缓存在数据库中取
		// Map<Integer, ChatMessageData> updateStateMsgMap = new HashMap<Integer, ChatMessageData>();
		// TableUserPrivateChatDao dao = TableUserPrivateChatDao.getDao();
		// UserPrivateChat userPrivateChat = dao.get(player.getUserId());
		// if (userPrivateChat != null) {
		// List<ChatMessageData> privateChatMessageList = userPrivateChat.getPrivateChatMessageList();
		// for (int i = 0, size = privateChatMessageList.size(); i < size; i++) {
		// ChatMessageData chatMsgData = privateChatMessageList.get(i);
		// msgChatResponse.addListMessage(chatMsgData);
		//
		// if (!chatMsgData.hasIsRead() || !chatMsgData.getIsRead()) {
		// updateStateMsgMap.put(i, chatMsgData);
		// }
		// }
		// }
		//
		// msgChatResponse.setChatResultType(eChatResultType.SUCCESS);
		// ByteString result = msgChatResponse.build().toByteString();
		//
		// if (userPrivateChat != null) {
		// // 更新
		// for (Entry<Integer, ChatMessageData> e : updateStateMsgMap.entrySet()) {
		// ChatMessageData.Builder chatMsgData = ChatMessageData.newBuilder(e.getValue());
		// chatMsgData.setIsRead(true);
		// userPrivateChat.updatePrivateChatMessageState(e.getKey(), chatMsgData.build());
		// }
		//
		// dao.update(userPrivateChat);
		// }
		//
		// return result;
		// } else {
		String sendUserId = player.getUserId();
		String receiveUserId = message.getReceiveMessageUserInfo().getUserId();
		if (FriendUtils.isBlack(sendUserId, receiveUserId)) {// 已经把对方拉黑
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "您已把对方拉黑");
			msgChatResponse.setChatResultType(eChatResultType.FAIL);
			return msgChatResponse.build().toByteString();
		} else if (FriendUtils.isBlack(receiveUserId, sendUserId)) {// 被对方拉黑
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "您已被对方拉黑");
			msgChatResponse.setChatResultType(eChatResultType.FAIL);
			return msgChatResponse.build().toByteString();
		} else if (sendUserId.equals(receiveUserId)) {// 是否是同一个角色Id
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "您不能私聊自己");
			msgChatResponse.setChatResultType(eChatResultType.FAIL);
			return msgChatResponse.build().toByteString();
		}

		PlayerIF toPlayer = PlayerMgr.getInstance().getReadOnlyPlayer(receiveUserId);
		if (toPlayer == null) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "您所私聊的角色不存在");
			msgChatResponse.setChatResultType(eChatResultType.FAIL);
			return msgChatResponse.build().toByteString();
		}

		MessageUserInfo.Builder receiveUserInfo = MessageUserInfo.newBuilder(message.getReceiveMessageUserInfo());
		receiveUserInfo.setLevel(toPlayer.getLevel());// 等级
		receiveUserInfo.setHeadImage(toPlayer.getTableUser().getHeadImageWithDefault());// 头像Id
		receiveUserInfo.setUserName(toPlayer.getTableUser().getUserName());// 角色名字

		ChatMessageData.Builder data = ChatMessageData.newBuilder();
		MessageUserInfo.Builder sendMessageUserInfo = message.getSendMessageUserInfo().toBuilder();
		sendMessageUserInfo.setUserId(sendUserId);
		data.setSendMessageUserInfo(sendMessageUserInfo);// 发送消息的人
		data.setReceiveMessageUserInfo(receiveUserInfo);// 接受消息的人

		data.setTime(getMessageTime());
		data.setMessage(filterDirtyWord(message.getMessage()));
		msgChatResponse.addListMessage(data);

		msgChatResponse.setChatResultType(eChatResultType.SUCCESS);
		ByteString result = msgChatResponse.build().toByteString();
		boolean isOnline = PlayerMgr.getInstance().isOnline(receiveUserId);
		if (isOnline) {
			PlayerMgr.getInstance().SendToPlayer(Command.MSG_CHAT, result, toPlayer);// 发送给玩家
		}

		// 存储两个数据
		data.setIsRead(true);
		updatePlayerChatMsg(sendUserId, data, eChatType.CHAT_PERSON);

		if (isOnline) {
			data.setIsRead(true);
		} else {
			data.setIsRead(false);
		}
		updatePlayerChatMsg(receiveUserId, data, eChatType.CHAT_PERSON);
		return result;
		// }
	}

	/**
	 * 添加私聊消息到数据库
	 * 
	 * @param userId 用户Id
	 * @param data 聊天数据
	 */
	private void updatePlayerChatMsg(String userId, ChatMessageData.Builder data, eChatType chatType) {
		TableUserPrivateChatDao dao = TableUserPrivateChatDao.getDao();
		UserPrivateChat userPrivateChat = dao.get(userId);
		if (userPrivateChat == null) {
			userPrivateChat = new UserPrivateChat();
			userPrivateChat.setUserId(userId);
		}

		if (chatType == eChatType.CHAT_PERSON) {
			// userPrivateChat.addPrivateChatMessage(data.build().toByteString());// 存储私聊信息
			userPrivateChat.addPrivateChatMessage(Base64.encode(data.build().toByteArray()));
		} else if (chatType == eChatType.CHAT_TREASURE) {
			userPrivateChat.addTreasureChatMessage(data.build().toByteString());// 存储密境分享信息
		}

		dao.update(userPrivateChat);
	}

	/**
	 * 密境分享，聊天这里不验证是否是本帮派的人，请调用模块自行验证
	 * 
	 * @param player 请求的人
	 * @param treasureId 密境的Id
	 * @param type 密境类型
	 * @param num 可以驻守的人数
	 * @param playerList 要分享给那些帮会成员
	 * @Param message 自定义的消息
	 * @return 是否成功:传入的player是null，playerList是null或者isEmpty都会返回false
	 */
	public boolean chatTreasure(Player player, String treasureId, int type, int num, String message, List<String> playerList) {
		if (player == null || playerList == null || playerList.isEmpty()) {
			return false;
		}

		String headImage = player.getHeadImage();// 头像
//		String familyId = player.getGuildUserMgr().getGuildId();// 帮派Id
		String familyId = "";
		//String familyName = player.getGuildUserMgr().getGuildName();// 帮派名字？player持有一个帮派名字，万一以后帮派出来改名功能？
		String familyName = "";
		int pLevel = player.getLevel();// 等级
		String playerName = player.getUserName();// 角色名字
		String msgTime = getMessageTime();// 发布消息的时间
		String userId = player.getUserId();

		for (int i = 0, size = playerList.size(); i < size; i++) {
			Player p = PlayerMgr.getInstance().find(playerList.get(i));

			ChatMessageData.Builder msgData = ChatMessageData.newBuilder();

			MessageUserInfo.Builder sendMessaegUserInfo = MessageUserInfo.newBuilder();
			sendMessaegUserInfo.setUserId(userId);
			sendMessaegUserInfo.setUserName(playerName);
			sendMessaegUserInfo.setLevel(pLevel);
			sendMessaegUserInfo.setHeadImage(headImage);
			sendMessaegUserInfo.setFamilyId(familyId);
			sendMessaegUserInfo.setFamilyName(familyName);

			msgData.setSendMessageUserInfo(sendMessaegUserInfo);// 信息
			msgData.setTreasureId(treasureId);// 密境Id
			msgData.setTreasureDefNum(num);// 邀请的人数
			msgData.setTreasureType(type);// 类型
			msgData.setTime(msgTime);
			msgData.setMessage(message == null ? "" : message);

			if (p != null) {// 在线才有发送
				MsgChatResponse.Builder msgChatResponse = MsgChatResponse.newBuilder();
				msgChatResponse.setChatType(eChatType.CHAT_TREASURE);
				msgChatResponse.setChatResultType(eChatResultType.SUCCESS);

				msgChatResponse.addListMessage(msgData);
				ByteString result = msgChatResponse.build().toByteString();
				PlayerMgr.getInstance().SendToPlayer(Command.MSG_CHAT, result, p);// 发送给玩家
			}

			updatePlayerChatMsg(playerList.get(i), msgData, eChatType.CHAT_TREASURE);
		}

		return true;
	}

	/**
	 * 获取密境分享的消息列表
	 * 
	 * @param player
	 * @param msgChatRequest
	 * @return
	 */
	public ByteString getChatTreasure(Player player, MsgChatRequest msgChatRequest) {
		MsgChatResponse.Builder msgChatResponse = MsgChatResponse.newBuilder();
		msgChatResponse.setChatType(msgChatRequest.getChatType());

		// TODO @modify 2015-08-14 HC 消息缓存在数据库中取
		TableUserPrivateChatDao dao = TableUserPrivateChatDao.getDao();
		UserPrivateChat userPrivateChat = dao.get(player.getUserId());
		if (userPrivateChat != null) {
			List<ChatMessageData> treasureChatMessageList = userPrivateChat.getTreasureChatMessageList();
			for (int i = 0, size = treasureChatMessageList.size(); i < size; i++) {
				msgChatResponse.addListMessage(treasureChatMessageList.get(i));
			}
		}

		msgChatResponse.setChatResultType(eChatResultType.SUCCESS);
		return msgChatResponse.build().toByteString();
	}

	private String getMessageTime() {
		String time = "";

		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		if (hour < 10) {
			time += "0";
		}
		time += hour + ":";
		if (minute < 10) {
			time += "0";
		}
		time += minute;

		return time;
	}

	/**
	 * 发送聊天数据
	 * 
	 * @param player
	 */
	public void sendChatAllMsg(Player player) {
		// 发送世界
		sendWorldMsg(player);
		// 发送帮派 屏蔽帮派
		// sendFamilyMsg(player);
		// 发送私聊
		sendPrivateMsg(player);
		// 发送密境
		sendTreasureMsg(player);
	}

	private void sendWorldMsg(Player player) {
		MsgChatResponse.Builder msgChatResponse = MsgChatResponse.newBuilder();
		msgChatResponse.setOnLogin(true);
		msgChatResponse.setChatType(eChatType.CHAT_WORLD);
		List<ChatInfo> list = ChatBM.getInstance().getWorldList();
		for (int i = 0; i < list.size(); i++) {
			ChatMessageData chatMsg = list.get(i).getMessage().build();
			if (!FriendUtils.isBlack(player, chatMsg.getSendMessageUserInfo().getUserId())) {// 不在黑名单
				msgChatResponse.addListMessage(chatMsg);
			}
		}
		msgChatResponse.setChatResultType(eChatResultType.SUCCESS);
		player.SendMsg(MsgDef.Command.MSG_CHAT, msgChatResponse.build().toByteString());
		player.setLastWorldChatId(ChatBM.getInstance().getChatVersion());// 缓存版本号
	}

	private void sendFamilyMsg(Player player) {
		String guildId = player.getGuildUserMgr().getGuildId();
		if (StringUtils.isBlank(guildId)) {
			return;
		}

		GuildDataMgr guildDataMgr = GuildGTSMgr.getInstance().getById(guildId);
		// 帮派不存在
		if (guildDataMgr == null) {
			return;
		}

		MsgChatResponse.Builder msgChatResponse = MsgChatResponse.newBuilder();
		msgChatResponse.setOnLogin(true);
		msgChatResponse.setChatType(eChatType.CHAT_FAMILY);
		List<ChatMessageData.Builder> list = ChatBM.getInstance().getFamilyChatList(guildId);
		for (int i = 0, size = list.size(); i < size; i++) {
			msgChatResponse.addListMessage(list.get(i));
		}

		// 填充完整的消息
		msgChatResponse.setChatResultType(eChatResultType.SUCCESS);
		msgChatResponse.build().toByteString();
		player.SendMsg(MsgDef.Command.MSG_CHAT, msgChatResponse.build().toByteString());
	}

	private void sendPrivateMsg(Player player) {
		MsgChatResponse.Builder msgChatResponse = MsgChatResponse.newBuilder();
		msgChatResponse.setChatType(eChatType.CHAT_PERSON);
		msgChatResponse.setOnLogin(true);

		Map<Integer, ChatMessageData> updateStateMsgMap = new HashMap<Integer, ChatMessageData>();
		TableUserPrivateChatDao dao = TableUserPrivateChatDao.getDao();
		UserPrivateChat userPrivateChat = dao.get(player.getUserId());
		if (userPrivateChat != null) {
			List<ChatMessageData> privateChatMessageList = userPrivateChat.getPrivateChatMessageList();
			for (int i = 0, size = privateChatMessageList.size(); i < size; i++) {
				ChatMessageData chatMsgData = privateChatMessageList.get(i);
				msgChatResponse.addListMessage(chatMsgData);

				if (!chatMsgData.hasIsRead() || !chatMsgData.getIsRead()) {
					updateStateMsgMap.put(i, chatMsgData);
				}
			}
		}

		msgChatResponse.setChatResultType(eChatResultType.SUCCESS);
		player.SendMsg(MsgDef.Command.MSG_CHAT, msgChatResponse.build().toByteString());

		if (userPrivateChat != null) {
			// 更新
			for (Entry<Integer, ChatMessageData> e : updateStateMsgMap.entrySet()) {
				ChatMessageData.Builder chatMsgData = ChatMessageData.newBuilder(e.getValue());
				chatMsgData.setIsRead(true);
				userPrivateChat.updatePrivateChatMessageState(e.getKey(), chatMsgData.build());
			}

			dao.update(userPrivateChat);
		}
	}

	private void sendTreasureMsg(Player player) {
		MsgChatResponse.Builder msgChatResponse = MsgChatResponse.newBuilder();
		msgChatResponse.setOnLogin(true);
		msgChatResponse.setChatType(eChatType.CHAT_TREASURE);

		TableUserPrivateChatDao dao = TableUserPrivateChatDao.getDao();
		UserPrivateChat userPrivateChat = dao.get(player.getUserId());
		if (userPrivateChat != null) {
			List<ChatMessageData> treasureChatMessageList = userPrivateChat.getTreasureChatMessageList();
			for (int i = 0, size = treasureChatMessageList.size(); i < size; i++) {
				msgChatResponse.addListMessage(treasureChatMessageList.get(i));
			}
		}

		msgChatResponse.setChatResultType(eChatResultType.SUCCESS);
		player.SendMsg(MsgDef.Command.MSG_CHAT, msgChatResponse.build().toByteString());
	}

	private String filterDirtyWord(String content) {
		return CharFilterFactory.getCharFilter().replaceDiryWords(content, "**", true, false);
	}

	// private byte[] privateMessageDecode(String message) {
	// return Base64.decode(message);
	// }
	//
	// private String privateMessageEncode(byte[] message) {
	// return Base64.encode(message);
	// }
}