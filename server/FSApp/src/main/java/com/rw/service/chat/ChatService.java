package com.rw.service.chat;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.playerdata.UserDataMgr;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.FsService;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwproto.ChatServiceProtos.MsgChatRequest;
import com.rwproto.ChatServiceProtos.MsgChatRequestPrivateChats;
import com.rwproto.ChatServiceProtos.MsgChatResponse;
import com.rwproto.ChatServiceProtos.eChatResultType;
import com.rwproto.ChatServiceProtos.eChatType;
import com.rwproto.RequestProtos.Request;

public class ChatService implements FsService {

	private ChatHandler chatHandler = ChatHandler.getInstance();

	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			switch (request.getHeader().getCommand()) {
			case MSG_CHAT:
				MsgChatRequest msgChatRequest = MsgChatRequest.parseFrom(request.getBody().getSerializedContent());
				eChatType chatType = msgChatRequest.getChatType();
				if (isChatBan(player, msgChatRequest) == null) {
					switch (chatType) {
					case CHAT_WORLD:
						result = chatHandler.chatWorld(player, msgChatRequest);
						break;
					case CHAT_FAMILY:
						result = chatHandler.chatInGroup(player, msgChatRequest);
						break;
					case CHAT_PERSON:
						result = chatHandler.chatPerson(player, msgChatRequest);
						break;
					case CHAT_TREASURE:
						result = chatHandler.getChatTreasure(player, msgChatRequest);
						break;
					default:
						break;
					}
				}
				break;
			case MSG_CHAT_REQUEST_PRIVATE_CHATS: // 获取用户相关的私聊列表
				MsgChatRequestPrivateChats privateChatsRequest = MsgChatRequestPrivateChats.parseFrom(request.getBody().getSerializedContent());
				result = ChatHandler.getInstance().getChatPrivate(player, privateChatsRequest);
				break;
			case MSG_CHAT_SET_CURRENT_TARGET:
				MsgChatRequestPrivateChats setTargetRequest = MsgChatRequestPrivateChats.parseFrom(request.getBody().getSerializedContent());
				result = ChatHandler.getInstance().setCurrentTargetOfPrivateChat(player, setTargetRequest);
				break;
			default:
				break;
			}

		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public ByteString isChatBan(Player player, MsgChatRequest msgChatRequest) {
		UserDataMgr userDataMgr = player.getUserDataMgr();
		if (userDataMgr.isChatBan()) {
			MsgChatResponse.Builder msgChatResponse = MsgChatResponse.newBuilder();
			msgChatResponse.setChatType(msgChatRequest.getChatType());
			StringBuilder sb = new StringBuilder();
			sb.append("禁言原因:").append(userDataMgr.getChatBanReason());
			sb.append("\n");
			long chatBanCoolTime = userDataMgr.getChatBanCoolTime();
			if (chatBanCoolTime == -1) {
				sb.append("解封时间:永久禁言");
			} else {
				sb.append("解封时间:" + DateUtils.getDateTimeFormatString(chatBanCoolTime, "yyyy-MM-dd HH:mm"));
			}
			player.NotifyCommonMsg(ECommonMsgTypeDef.MsgTips, sb.toString());
			msgChatResponse.setChatResultType(eChatResultType.FAIL);
			return msgChatResponse.build().toByteString();
		} else {
			return null;
		}
	}
}
