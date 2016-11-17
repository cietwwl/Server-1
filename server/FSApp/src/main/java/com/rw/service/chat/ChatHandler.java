package com.rw.service.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.util.StringUtils;

import com.bm.chat.ChatBM;
import com.bm.chat.ChatInfo;
import com.bm.chat.ChatInteractiveType;
//import com.bm.chat.ChatInfo;
import com.bm.group.GroupBM;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.readonly.PlayerIF;
import com.rw.netty.UserChannelMgr;
import com.rw.service.fashion.FashionHandle;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.template.BIChatType;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwbase.dao.chat.pojo.ChatMessageSaveData;
import com.rwbase.dao.chat.pojo.ChatUserInfo;
import com.rwbase.dao.friend.FriendUtils;
import com.rwbase.dao.group.pojo.Group;
import com.rwbase.dao.group.pojo.readonly.GroupBaseDataIF;
import com.rwbase.dao.group.pojo.readonly.GroupMemberDataIF;
import com.rwbase.dao.group.pojo.readonly.UserGroupAttributeDataIF;
import com.rwbase.dao.user.readonly.TableUserIF;
import com.rwproto.ChatServiceProtos.ChatAttachItem;
import com.rwproto.ChatServiceProtos.ChatMessageData;
import com.rwproto.ChatServiceProtos.MessageUserInfo;
import com.rwproto.ChatServiceProtos.MsgChatRequest;
import com.rwproto.ChatServiceProtos.MsgChatRequestPrivateChats;
import com.rwproto.ChatServiceProtos.MsgChatResponse;
import com.rwproto.ChatServiceProtos.MsgPersonChatUserInfo;
import com.rwproto.ChatServiceProtos.eChatResultType;
import com.rwproto.ChatServiceProtos.eChatType;
import com.rwproto.FashionServiceProtos.FashionUsed;
import com.rwproto.MsgDef;
import com.rwproto.MsgDef.Command;

public class ChatHandler {

	private static final long CHAT_DELAY_TIME_MILLIS = TimeUnit.SECONDS.toMillis(10);// 发言间隔10秒
	private static final long CHAT_DELAY_TIME_MILLIS_PRIVATE = TimeUnit.SECONDS.toMillis(10);// 私聊发言间隔10秒
	public static final int MAX_CACHE_MSG_SIZE = 20;// 各个聊天频道缓存的最大聊天记录数
	public static final int MAX_CACHE_INTERACTIVE_SIZE = 10; // 互動最大保存記錄數
	public static final int MAX_CACHE_MSG_SIZE_OF_PRIVATE_CHAT = 200; // 私聊频道最高的保存数量
	public static final int MAX_CACHE_MSG_SIZE_PER_ONE = 10; // 私聊频道最每个人最高的保存数量
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

	/**
	 * 
	 * @param player
	 * @param appendBasic 是否添加基本信息（等级，头像id，名字，头像品质框，职业类型，性别）
	 * @return
	 */
	public MessageUserInfo.Builder createMessageUserInfoBuilder(PlayerIF player, boolean appendBasic) {
		MessageUserInfo.Builder messageUserInfoBuilder = MessageUserInfo.newBuilder();
		messageUserInfoBuilder.setUserId(player.getUserId());
		if (appendBasic) {
			TableUserIF tableUser = player.getTableUser();
			messageUserInfoBuilder.setLevel(player.getLevel());// 等级
			messageUserInfoBuilder.setHeadImage(tableUser.getHeadImageWithDefault());// 头像Id
			messageUserInfoBuilder.setUserName(tableUser.getUserName());// 角色名字
			messageUserInfoBuilder.setHeadbox(player.getHeadFrame());// 头像品质框
			messageUserInfoBuilder.setCareerType(player.getCareer()); // 职业类型
			messageUserInfoBuilder.setGender(player.getSex()); // 性别
			messageUserInfoBuilder.setFighting(player.getHeroMgr().getFightingTeam(player));
		}
		UserGroupAttributeDataIF userGroupAttributeData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupName = userGroupAttributeData.getGroupName();
		String groupId = userGroupAttributeData.getGroupId();
		if (!StringUtils.isEmpty(groupId)) {
			messageUserInfoBuilder.setGroupId(groupId);
		}
		if (!StringUtils.isEmpty(groupName)) {
			messageUserInfoBuilder.setGroupName(groupName);
		}
		messageUserInfoBuilder.setVipLv(player.getVip());
		messageUserInfoBuilder.setFashionTemplateId(player.getFashionMgr().getFashionUsed().getSuitId());
		FashionUsed.Builder usingFashion = FashionHandle.getInstance().getFashionUsedProto(player.getUserId());
		if(null != usingFashion){
			messageUserInfoBuilder.setFashionUsed(usingFashion);
		}
		return messageUserInfoBuilder;
	}

	public ChatAttachItem createChatAttachItemProto(int type, String id, String extraInfo) {
		if (id == null) {
			id = "";
		}
		ChatAttachItem.Builder builder = ChatAttachItem.newBuilder();
		builder.setType(type);
		builder.setId(id);
		if (!StringUtils.isEmpty(extraInfo)) {
			builder.setExtraInfo(extraInfo);
		}
		return builder.build();
	}

	/**
	 * 
	 * @param player
	 * @param clientData
	 * @param clearReceiverInfo 是否清理receiver的信息
	 * @return
	 */
	private ChatMessageData.Builder createChatMessageData(Player player, ChatMessageData clientData, boolean clearReceiverInfo) {
		ChatMessageData.Builder newBuilder = clientData.toBuilder();
		MessageUserInfo.Builder sendMsgInfo = this.createMessageUserInfoBuilder(player, true);
		newBuilder.setSendMessageUserInfo(sendMsgInfo);
		if (clearReceiverInfo && newBuilder.hasReceiveMessageUserInfo()) {
			newBuilder.clearReceiveMessageUserInfo();
		}

		String chatContent = ChatBM.getInstance().filterDirtyWord(clientData.getMessage());
		newBuilder.setMessage(chatContent);
		newBuilder.setTime(System.currentTimeMillis());
		return newBuilder;
	}

	public ByteString chatWorld(Player player, MsgChatRequest msgChatRequest) {
		MsgChatResponse.Builder msgChatResponse = MsgChatResponse.newBuilder();
		msgChatResponse.setChatType(msgChatRequest.getChatType());

		ChatMessageData message = msgChatRequest.getChatMessageData();
		// String userId = player.getUserId();
		// 2016-07-19 17:14 修改：接收的时候，可以不需要这个senderInfo BEGIN >>>>
		// if (!message.hasSendMessageUserInfo()) {
		// player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "发送聊天数据异常");
		// msgChatResponse.setChatResultType(eChatResultType.FAIL);
		// return msgChatResponse.build().toByteString();
		// }
		// 2016-07-19 17:14 <<<< END

		long nowTime = System.currentTimeMillis();
		if (player.getLastWorldChatCacheTime() > 0) {
			if (nowTime - player.getLastWorldChatCacheTime() < CHAT_DELAY_TIME_MILLIS) {// 间隔10秒
				player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "发言太快");
				msgChatResponse.setChatResultType(eChatResultType.FAIL);
				return msgChatResponse.build().toByteString();
			}
		}

		player.setLastWorldChatCacheTime(nowTime);// 更新上次聊天的时间

		// TODO HC 策划需求不再进行收费跟免费次数限制
		// // 收费
		// int freeChat = player.getUserGameDataMgr().getFreeChat();
		// if (freeChat == 0) {
		// // 扣钻石
		// if (m_ChatCost == 0) {
		// m_ChatCost = PublicDataCfgDAO.getInstance().getPublicDataValueById(PublicData.ID_CHAT_COST);
		// }
		// if (player.getUserGameDataMgr().getGold() >= m_ChatCost) {
		// player.getUserGameDataMgr().addGold(-m_ChatCost);
		// } else {
		// msgChatResponse.setChatResultType(eChatResultType.FAIL);
		// return msgChatResponse.build().toByteString();
		// }
		// } else {
		// player.getUserGameDataMgr().setFreeChat(freeChat - 1);
		// }

		// ChatMessageData.Builder data = ChatMessageData.newBuilder();
		// MessageUserInfo.Builder sendMsgInfo = message.getSendMessageUserInfo().toBuilder();
		// sendMsgInfo.setUserId(userId);
		//
		// UserGroupAttributeDataIF userGroupAttributeData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		// String groupName = userGroupAttributeData.getGroupName();
		// sendMsgInfo.setFamilyId(userGroupAttributeData.getGroupId());
		// if (!StringUtils.isEmpty(groupName)) {
		// sendMsgInfo.setFamilyName(groupName);
		// }
		// data.setSendMessageUserInfo(sendMsgInfo);
		// if (message.hasReceiveMessageUserInfo()) {
		// data.setReceiveMessageUserInfo(message.getReceiveMessageUserInfo());
		// }
		//
		// String chatContent = filterDirtyWord(message.getMessage());
		// data.setMessage(chatContent);
		// data.setTime(getMessageTime());

		ChatMessageData.Builder data = this.createChatMessageData(player, message, true);
		String chatContent = data.getMessage();

		msgChatResponse.addListMessage(data);
		// 聊天日志
		BILogMgr.getInstance().logChat(player, "", BIChatType.WORD.getType(), chatContent);
		msgChatResponse.setChatResultType(eChatResultType.SUCCESS);
		ByteString result = msgChatResponse.build().toByteString();

		ChatBM.getInstance().updateWroldList(data);
		return result;
	}

	/**
	 * 发送帮派聊天信息
	 * 
	 * @param player
	 * @param msgChatRequest
	 * @return
	 */
	public ByteString chatInGroup(Player player, MsgChatRequest msgChatRequest) {
		MsgChatResponse.Builder msgChatResponse = MsgChatResponse.newBuilder();
		msgChatResponse.setChatType(msgChatRequest.getChatType());

		UserGroupAttributeDataIF userGroupData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		String groupId = userGroupData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			msgChatResponse.setChatResultType(eChatResultType.FAIL);
			ByteString result = msgChatResponse.build().toByteString();
			return result;
		}

		Group group = GroupBM.get(groupId);
		if (group == null) {
			msgChatResponse.setChatResultType(eChatResultType.FAIL);
			ByteString result = msgChatResponse.build().toByteString();
			return result;
		}

		GroupBaseDataIF groupData = group.getGroupBaseDataMgr().getGroupData();
		if (groupData == null) {
			msgChatResponse.setChatResultType(eChatResultType.FAIL);
			ByteString result = msgChatResponse.build().toByteString();
			return result;
		}

		// 冷却时间
		long nowTime = System.currentTimeMillis();
		if (player.getLastGroupChatCacheTime() > 0) {
			if (nowTime - player.getLastGroupChatCacheTime() < CHAT_DELAY_TIME_MILLIS) {// 间隔10秒
				player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "发言太快");
				msgChatResponse.setChatResultType(eChatResultType.FAIL);
				return msgChatResponse.build().toByteString();
			}
		}

		player.setLastGroupChatCacheTime(nowTime);// 更新上次聊天的时间

		ChatMessageData message = msgChatRequest.getChatMessageData();
		// 2016-07-29 看不出這段用意，暫時去掉
		/*
		 * if (!message.hasSendMessageUserInfo()) { List<ChatMessageData.Builder> list = ChatBM.getInstance().getFamilyChatList(groupId); for (int i = 0, size = list.size(); i < size; i++) {
		 * msgChatResponse.addListMessage(list.get(i)); } } else
		 */{
			// ChatMessageData.Builder data = ChatMessageData.newBuilder();
			// MessageUserInfo.Builder sendMsgInfo = message.getSendMessageUserInfo().toBuilder();
			// sendMsgInfo.setUserId(player.getUserId());
			// // 设置帮派信息
			// String groupName = userGroupData.getGroupName();
			// sendMsgInfo.setFamilyId(userGroupData.getGroupId());
			// if (!StringUtils.isEmpty(groupName)) {
			// sendMsgInfo.setFamilyName(groupName);
			// }
			//
			// data.setSendMessageUserInfo(sendMsgInfo);
			// data.setTime(getMessageTime());
			// String chatContent = filterDirtyWord(message.getMessage());
			// data.setMessage(chatContent);
			ChatMessageData.Builder data = this.createChatMessageData(player, message, true);
			String chatContent = data.getMessage();
			msgChatResponse.addListMessage(data);
			ChatBM.getInstance().addFamilyChat(groupId, data);

			// 聊天日志
			BILogMgr.getInstance().logChat(player, "", BIChatType.GROUP.getType(), chatContent);
		}

		// 填充完整的消息
		msgChatResponse.setChatResultType(eChatResultType.SUCCESS);
		ByteString result = msgChatResponse.build().toByteString();

		// 发送给其他成员
		String pId = player.getUserId();
		List<? extends GroupMemberDataIF> memberSortList = group.getGroupMemberMgr().getMemberSortList(null);
		for (GroupMemberDataIF guildMember : memberSortList) {
			String memUserId = guildMember.getUserId();
			if (pId.equals(memUserId)) {
				continue;
			}

			Player p = PlayerMgr.getInstance().find(memUserId);
			if (p == null) {
				continue;
			}

			if (!FriendUtils.isBlack(p, pId)) {
				p.SendMsg(Command.MSG_CHAT, result);
			}
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
		MsgChatResponse.Builder msgChatResponseBuilder = MsgChatResponse.newBuilder();
		eChatType chatType = msgChatRequest.getChatType();
		msgChatResponseBuilder.setChatType(chatType);

		ChatMessageData message = msgChatRequest.getChatMessageData();

		String sendUserId = player.getUserId();
		String receiveUserId = message.getReceiveMessageUserInfo().getUserId();
		if (FriendUtils.getInstance().isBlack(sendUserId, receiveUserId)) {// 已经把对方拉黑
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "您已把对方拉黑");
			msgChatResponseBuilder.setChatResultType(eChatResultType.FAIL);
			return msgChatResponseBuilder.build().toByteString();
		} else if (FriendUtils.getInstance().isBlack(receiveUserId, sendUserId)) {// 被对方拉黑
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "您已被对方拉黑");
			msgChatResponseBuilder.setChatResultType(eChatResultType.FAIL);
			return msgChatResponseBuilder.build().toByteString();
		} else if (sendUserId.equals(receiveUserId)) {// 是否是同一个角色Id
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "您不能私聊自己");
			msgChatResponseBuilder.setChatResultType(eChatResultType.FAIL);
			return msgChatResponseBuilder.build().toByteString();
		}

		// 2016-08-03 聊天間隔判斷 BEGIN >>>>>>
		long lastSentTime = ChatBM.getInstance().getLastSentPrivateChatTime(sendUserId);
		long currentTimemillis = System.currentTimeMillis();
		if (currentTimemillis - lastSentTime < CHAT_DELAY_TIME_MILLIS_PRIVATE) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "发言太快");
			msgChatResponseBuilder.setChatResultType(eChatResultType.FAIL);
			return msgChatResponseBuilder.build().toByteString();
		}
		// END <<<<<<

		PlayerIF toPlayer = PlayerMgr.getInstance().getReadOnlyPlayer(receiveUserId);
		if (toPlayer == null) {
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "您所私聊的角色不存在");
			msgChatResponseBuilder.setChatResultType(eChatResultType.FAIL);
			return msgChatResponseBuilder.build().toByteString();
		}

		// MessageUserInfo.Builder receiveUserInfo = MessageUserInfo.newBuilder(message.getReceiveMessageUserInfo());
		// receiveUserInfo.setLevel(toPlayer.getLevel());// 等级
		// receiveUserInfo.setHeadImage(toPlayer.getTableUser().getHeadImageWithDefault());// 头像Id
		// receiveUserInfo.setUserName(toPlayer.getTableUser().getUserName());// 角色名字
		// receiveUserInfo.setHeadbox(toPlayer.getHeadFrame());// 头像品质框
		//
		// // 设置帮派信息
		// UserGroupAttributeDataIF toPlayerGroupData = toPlayer.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		// String toPlayerGroupName = toPlayerGroupData.getGroupName();
		// receiveUserInfo.setFamilyId(toPlayerGroupData.getGroupId());
		// if (!StringUtils.isEmpty(toPlayerGroupName)) {
		// receiveUserInfo.setFamilyName(toPlayerGroupName);
		// }

		// ChatMessageData.Builder data = ChatMessageData.newBuilder();
		// MessageUserInfo.Builder sendMessageUserInfo = message.getSendMessageUserInfo().toBuilder();
		// sendMessageUserInfo.setUserId(sendUserId);
		// // 设置帮派信息
		// UserGroupAttributeDataIF userGroupAttributeData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		// String groupName = userGroupAttributeData.getGroupName();
		// sendMessageUserInfo.setFamilyId(userGroupAttributeData.getGroupId());
		// if (!StringUtils.isEmpty(groupName)) {
		// sendMessageUserInfo.setFamilyName(groupName);
		// }
		// data.setSendMessageUserInfo(sendMessageUserInfo);// 发送消息的人
		// data.setReceiveMessageUserInfo(receiveUserInfo);// 接受消息的人
		//
		// data.setTime(getMessageTime());
		// String chatContent = filterDirtyWord(message.getMessage());
		// data.setMessage(chatContent);

		ChatMessageData.Builder data = this.createChatMessageData(player, message, true);
		String chatContent = data.getMessage();
		msgChatResponseBuilder.addListMessage(data);

		// 聊天日志
		BILogMgr.getInstance().logChat(player, receiveUserId, BIChatType.PRIVATE.getType(), chatContent);

		msgChatResponseBuilder.setChatResultType(eChatResultType.SUCCESS);
		ByteString result = msgChatResponseBuilder.build().toByteString();
		boolean isOnline = PlayerMgr.getInstance().isOnline(receiveUserId);
		if (isOnline) {
//			PlayerMgr.getInstance().SendToPlayer(Command.MSG_CHAT, result, toPlayer); // 发送给目标玩家
			UserChannelMgr.sendAyncResponse(toPlayer.getUserId(), Command.MSG_CHAT, chatType, result);
			String currentTargetUserId = ChatBM.getInstance().getCurrentTargetIdOfPirvateChat(toPlayer.getUserId());
//			 System.out.println("toPlayerUserId:" + toPlayer.getTableUser().getUserId() + ", currentTargetUserId:" + currentTargetUserId);
			if (player.getUserId().equals(currentTargetUserId)) {
				// 如果我是對方的當前聊天對象，表示對方正打開與我的聊天面板，所以這裡可以標示為已讀
				data.setIsRead(true);
			} else {
				data.setIsRead(false);
			}
		} else {
			data.setIsRead(false);
		}

		updatePlayerChatMsg(receiveUserId, data, eChatType.CHANNEL_PERSON);

		// 发送消息给接收者的时候不需要发送接收者的信息
		MessageUserInfo.Builder receiveUserInfo = this.createMessageUserInfoBuilder(toPlayer, true);
		data.setReceiveMessageUserInfo(receiveUserInfo);

		data.clearSendMessageUserInfo(); // 發送給發送者的時候，不需要發送者的信息

		// 存储两个数据
		data.setIsRead(true);
		updatePlayerChatMsg(sendUserId, data, eChatType.CHANNEL_PERSON);
		ChatBM.getInstance().updateLastSentPrivateChatTime(sendUserId, currentTimemillis);

		result = msgChatResponseBuilder.setListMessage(0, data.build()).build().toByteString();

		return result;
	}

	/**
	 * 发送组队聊天
	 * 
	 * @param player
	 * @param msgChatRequest
	 * @return
	 */
	public ByteString chatTeam(Player player, MsgChatRequest msgChatRequest) {
		MsgChatResponse.Builder msgChatResponse = MsgChatResponse.newBuilder();
		msgChatResponse.setChatType(msgChatRequest.getChatType());

		ChatMessageData message = msgChatRequest.getChatMessageData();
		long nowTime = System.currentTimeMillis();
		if (player.getLastTeamChatCahceTime() > 0) {
			if (nowTime - player.getLastTeamChatCahceTime() < CHAT_DELAY_TIME_MILLIS) {// 间隔10秒
				player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, "发言太快");
				msgChatResponse.setChatResultType(eChatResultType.FAIL);
				return msgChatResponse.build().toByteString();
			}
		}

		player.setLastTeamChatCahceTime(nowTime);// 更新上次聊天的时间
		ChatMessageData.Builder data = this.createChatMessageData(player, message, true);

		msgChatResponse.addListMessage(data);
		// 聊天日志
		msgChatResponse.setChatResultType(eChatResultType.SUCCESS);
		ByteString result = msgChatResponse.build().toByteString();

		// TODO HC @Modify 这里需要后边支持查到某个角色的队伍Id
		ChatBM.getInstance().updateTeamList("1", data);
		return result;
	}

	/**
	 * 添加私聊消息到数据库
	 * 
	 * @param userId 用户Id
	 * @param data 聊天数据
	 */
	private void updatePlayerChatMsg(String userId, ChatMessageData.Builder data, eChatType chatType) {
		if (chatType == eChatType.CHANNEL_PERSON) {
			ChatBM.getInstance().addPrivateChat(userId, data.build());
		} else if (chatType == eChatType.CHAT_TREASURE) {
			ChatBM.getInstance().addGroupSecretChat(userId, data.build());
		}
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

		// String headImage = player.getHeadImage();// 头像
		// UserGroupAttributeDataIF userGroupData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		// String familyId = userGroupData.getGroupId();
		// String familyName = userGroupData.getGroupName();
		// int pLevel = player.getLevel();// 等级
		// String playerName = player.getUserName();// 角色名字
		// String msgTime = getMessageTime();// 发布消息的时间
		// String userId = player.getUserId();
		// String headFrame = player.getHeadFrame();// 头像品质框
		//
		// for (int i = 0, size = playerList.size(); i < size; i++) {
		// ChatMessageData.Builder msgData = ChatMessageData.newBuilder();
		// MessageUserInfo.Builder sendMessaegUserInfo = MessageUserInfo.newBuilder();
		// sendMessaegUserInfo.setUserId(userId);
		// sendMessaegUserInfo.setUserName(playerName);
		// sendMessaegUserInfo.setLevel(pLevel);
		// sendMessaegUserInfo.setHeadImage(headImage);
		// sendMessaegUserInfo.setFamilyId(familyId);
		// sendMessaegUserInfo.setFamilyName(familyName);
		// sendMessaegUserInfo.setHeadbox(headFrame);
		//
		// msgData.setSendMessageUserInfo(sendMessaegUserInfo);// 信息
		// msgData.setTreasureId(treasureId);// 密境Id
		// msgData.setTreasureDefNum(num);// 邀请的人数
		// msgData.setTreasureType(type);// 类型
		// msgData.setTime(msgTime);
		// msgData.setMessage(message == null ? "" : message);
		//
		// String playerId = playerList.get(i);
		// Player p = PlayerMgr.getInstance().find(playerId);
		// if (p != null) {// 在线才有发送
		// MsgChatResponse.Builder msgChatResponse = MsgChatResponse.newBuilder();
		// msgChatResponse.setChatType(eChatType.CHAT_TREASURE);
		// msgChatResponse.setChatResultType(eChatResultType.SUCCESS);
		//
		// msgChatResponse.addListMessage(msgData);
		// ByteString result = msgChatResponse.build().toByteString();
		// PlayerMgr.getInstance().SendToPlayer(Command.MSG_CHAT, result, p);// 发送给玩家
		// }
		//
		// updatePlayerChatMsg(playerId, msgData, eChatType.CHAT_TREASURE);
		// }

		if (message == null) {
			message = "";
		}

		ChatMessageData.Builder msgData = ChatMessageData.newBuilder();
		MessageUserInfo.Builder sendMessaegUserInfo = this.createMessageUserInfoBuilder(player, true);

		msgData.setSendMessageUserInfo(sendMessaegUserInfo);// 信息
		msgData.setTreasureId(treasureId);// 密境Id
		msgData.setTreasureDefNum(num);// 邀请的人数
		msgData.setTreasureType(type);// 类型
		msgData.setTime(System.currentTimeMillis());
		msgData.setMessage(message == null ? "" : message);

		for (int i = 0, size = playerList.size(); i < size; i++) {

			String playerId = playerList.get(i);
			Player p = PlayerMgr.getInstance().find(playerId);
			if (p != null) {// 在线才有发送
				MsgChatResponse.Builder msgChatResponse = MsgChatResponse.newBuilder();
				eChatType chatType = eChatType.CHAT_TREASURE;
				msgChatResponse.setChatType(chatType);
				msgChatResponse.setChatResultType(eChatResultType.SUCCESS);

				msgChatResponse.addListMessage(msgData);
				ByteString result = msgChatResponse.build().toByteString();
//				PlayerMgr.getInstance().SendToPlayer(Command.MSG_CHAT, result, p);// 发送给玩家
				UserChannelMgr.sendAyncResponse(p.getUserId(), Command.MSG_CHAT, chatType, result);
			}

			updatePlayerChatMsg(playerId, msgData, eChatType.CHAT_TREASURE);
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
		List<ChatMessageData> treasureChatMessageList = ChatBM.getInstance().getGroupSecretChatList(player.getUserId());
		for (int i = 0, size = treasureChatMessageList.size(); i < size; i++) {
			msgChatResponse.addListMessage(treasureChatMessageList.get(i));
		}

		msgChatResponse.setChatResultType(eChatResultType.SUCCESS);
		return msgChatResponse.build().toByteString();
	}

	public ByteString getChatPrivate(Player player, MsgChatRequestPrivateChats request) {
		String targetUserId = request.getUserId();
		List<ChatMessageData> list = ChatBM.getInstance().getPrivateChatListOfTarget(player.getUserId(), targetUserId);
		MsgChatResponse.Builder msgChatResponse = MsgChatResponse.newBuilder();
		msgChatResponse.setChatType(eChatType.CHANNEL_PERSON);
		msgChatResponse.setChatResultType(eChatResultType.SUCCESS);
		ChatMessageData chatData;
		int size = list.size();
		List<ChatMessageData> unReadList = new ArrayList<ChatMessageData>();
		for (int i = 0; i < size; i++) {
			chatData = list.get(i);
			msgChatResponse.addListMessage(chatData);
			if (!chatData.getIsRead()) {
				unReadList.add(chatData);
			}
		}
		ChatBM.getInstance().updatePrivateChatState(player.getUserId(), unReadList);
		ChatBM.getInstance().updateCurrentTargetUserIdOfPrivateChat(player.getUserId(), targetUserId);
		return msgChatResponse.build().toByteString();
	}

	public ByteString setCurrentTargetOfPrivateChat(Player player, MsgChatRequestPrivateChats request) {
		String targetUserId = request.getUserId();
		ChatBM.getInstance().updateCurrentTargetUserIdOfPrivateChat(player.getUserId(), targetUserId);
		// System.out.println("player set current target of private chat, player id : " + player.getUserId() + ", target : " + targetUserId);
		ChatBM.getInstance().setAllChatsReadOfTarget(player.getUserId(), targetUserId);
		return ByteString.EMPTY;
	}

	// private String getMessageTime() {
	// String time = "";
	//
	// Calendar c = Calendar.getInstance();
	// int hour = c.get(Calendar.HOUR_OF_DAY);
	// int minute = c.get(Calendar.MINUTE);
	// if (hour < 10) {
	// time += "0";
	// }
	// time += hour + ":";
	// if (minute < 10) {
	// time += "0";
	// }
	// time += minute;
	//
	// return time;
	// }

	/**
	 * 发送聊天数据
	 * 
	 * @param player
	 */
	public void sendChatAllMsg(Player player) {
		// 发送世界
		sendWorldMsg(player);
		// 发送帮派 屏蔽帮派
		sendFamilyMsg(player);
		// 发送私聊
		sendPrivateMsg(player);
		// 发送密境
		sendTreasureMsg(player);
		// 發送互動信息
		sendInteractiveChat(player);
	}

	private void sendWorldMsg(Player player) {
		MsgChatResponse.Builder msgChatResponse = MsgChatResponse.newBuilder();
		msgChatResponse.setOnLogin(true);
		msgChatResponse.setChatType(eChatType.CHANNEL_WORLD);
		List<ChatInfo> list = ChatBM.getInstance().getWorldList();
		ChatInfo chatInfo;
		for (int i = 0; i < list.size(); i++) {
			chatInfo = list.get(i);
			ChatMessageData chatMsg = chatInfo.getMessage();
			if (!FriendUtils.isBlack(player, chatMsg.getSendMessageUserInfo().getUserId())) {// 不在黑名单
				msgChatResponse.addListMessage(chatMsg);
			}
		}
		msgChatResponse.setChatResultType(eChatResultType.SUCCESS);
		player.SendMsg(MsgDef.Command.MSG_CHAT, msgChatResponse.build().toByteString());
		player.setLastWorldChatId(ChatBM.getInstance().getChatVersion());// 缓存版本号
	}

	private void sendFamilyMsg(Player player) {
		UserGroupAttributeDataIF userGroupData = player.getUserGroupAttributeDataMgr().getUserGroupAttributeData();
		if (userGroupData == null) {
			return;
		}
		String groupId = userGroupData.getGroupId();
		if (StringUtils.isEmpty(groupId)) {
			return;
		}

		MsgChatResponse.Builder msgChatResponse = MsgChatResponse.newBuilder();
		msgChatResponse.setOnLogin(true);
		msgChatResponse.setChatType(eChatType.CHANNEL_GROUP);
		List<ChatMessageData.Builder> list = ChatBM.getInstance().getFamilyChatList(groupId);
		for (int i = 0, size = list.size(); i < size; i++) {
			ChatMessageData chatMsg = list.get(i).build();
			if (!FriendUtils.isBlack(player, chatMsg.getSendMessageUserInfo().getUserId())) {// 不在黑名单
				msgChatResponse.addListMessage(chatMsg);
			}
		}

		// 填充完整的消息
		msgChatResponse.setChatResultType(eChatResultType.SUCCESS);
		msgChatResponse.build().toByteString();
		player.SendMsg(MsgDef.Command.MSG_CHAT, msgChatResponse.build().toByteString());
	}

	private void sendPrivateMsg(Player player) {
		// System.out.println("發送私聊信息給：" + player.getUserId() + ", " + player.getUserName());
		// 2016-07-29 修改by CHEN.P，聊天現在改為先發送用戶列表
		// List<ChatMessageData> unReadList = new ArrayList<ChatMessageData>(); // 2016-07-29 這裡不需要unReadList
		ChatBM instance = ChatBM.getInstance();
		String userId = player.getUserId();
		// List<ChatMessageData> privateChatMessageList = instance.getPrivateChatList(userId);
		List<ChatMessageSaveData> privateChatMessageList = instance.getPrivateChatListSaveData(userId);
		Map<String, String> userInfos = new LinkedHashMap<String, String>();
		Map<String, Integer> unReadCountMap = new HashMap<String, Integer>();
		for (int i = 0, size = privateChatMessageList.size(); i < size; i++) {
			// ChatMessageData chatMsgData = privateChatMessageList.get(i);

			// if (!chatMsgData.hasIsRead() || !chatMsgData.getIsRead()) {
			// unReadList.add(chatMsgData);
			// }
			ChatMessageSaveData chatMsgData = privateChatMessageList.get(i);

			ChatUserInfo userInfo;
			if (chatMsgData.getSendInfo() != null) {
				// 有可能是別人發給我的
				userInfo = chatMsgData.getSendInfo();
			} else {
				// 有可能是我發給別人的
				userInfo = chatMsgData.getReceiveInfo();
			}

			if (userInfo != null) {
				String tempUserId = userInfo.getUserId();
				if (!tempUserId.equals(userId)) {
					userInfos.put(tempUserId, userInfo.getUserName());
					if (!chatMsgData.isRead()) {
						Integer count = unReadCountMap.get(tempUserId);
						if (count == null) {
							count = 0;
						}
						count++;
						unReadCountMap.put(tempUserId, count);
					}
				}
			}
		}

		MsgChatResponse.Builder msgChatResponse = MsgChatResponse.newBuilder();
		msgChatResponse.setChatType(eChatType.CHANNEL_PERSON);
		msgChatResponse.setOnLogin(true);
		msgChatResponse.setChatResultType(eChatResultType.SUCCESS);
		for (Iterator<Map.Entry<String, String>> itr = userInfos.entrySet().iterator(); itr.hasNext();) {
			Map.Entry<String, String> entry = itr.next();
			Integer unReadCount = unReadCountMap.get(entry.getKey());
			// System.out.println("tempUserInfos=[" + entry.getKey() + ", " + entry.getValue() + "," + unReadCount + "]");
			MsgPersonChatUserInfo.Builder builder = MsgPersonChatUserInfo.newBuilder();
			builder.setUserId(entry.getKey());
			builder.setName(entry.getValue());
			builder.setUnReadCount(unReadCount == null ? 0 : unReadCount);
			msgChatResponse.addUsersOfPrivateChannel(builder.build());
		}
		player.SendMsg(MsgDef.Command.MSG_CHAT, msgChatResponse.build().toByteString());

		instance.updateCurrentTargetUserIdOfPrivateChat(userId, "");

		// instance.updatePrivateChatState(userId, unReadList);
	}

	private void sendTreasureMsg(Player player) {
		MsgChatResponse.Builder msgChatResponse = MsgChatResponse.newBuilder();
		msgChatResponse.setOnLogin(true);
		msgChatResponse.setChatType(eChatType.CHAT_TREASURE);

		List<ChatMessageData> treasureChatMessageList = ChatBM.getInstance().getGroupSecretChatList(player.getUserId());
		for (int i = 0, size = treasureChatMessageList.size(); i < size; i++) {
			msgChatResponse.addListMessage(treasureChatMessageList.get(i));
		}

		msgChatResponse.setChatResultType(eChatResultType.SUCCESS);
		player.SendMsg(MsgDef.Command.MSG_CHAT, msgChatResponse.build().toByteString());
	}

	private void sendInteractiveChat(Player player) {
		Map<ChatInteractiveType, List<ChatMessageData>> map = ChatBM.getInstance().getInteractiveChatList(player.getUserId());
		for (Iterator<Map.Entry<ChatInteractiveType, List<ChatMessageData>>> itr = map.entrySet().iterator(); itr.hasNext();) {
			Map.Entry<ChatInteractiveType, List<ChatMessageData>> entry = itr.next();
			MsgChatResponse.Builder msgChatResponse = MsgChatResponse.newBuilder();
			msgChatResponse.setOnLogin(true);
			msgChatResponse.setChatResultType(eChatResultType.SUCCESS);
			msgChatResponse.setChatType(entry.getKey().chatType);
			msgChatResponse.addAllListMessage(entry.getValue());
			player.SendMsg(MsgDef.Command.MSG_CHAT, msgChatResponse.build().toByteString());
		}
	}
}