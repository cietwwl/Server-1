package com.rw.service.chat;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mysql.jdbc.TimeUtil;
import com.playerdata.Player;
import com.playerdata.UserDataMgr;
import com.rw.fsutil.util.DateUtils;
import com.rw.service.FsService;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwproto.ChatServiceProtos.MsgChatRequest;
import com.rwproto.ChatServiceProtos.MsgChatResponse;
import com.rwproto.ChatServiceProtos.eChatResultType;
import com.rwproto.ChatServiceProtos.eChatType;
import com.rwproto.RequestProtos.Request;

public class ChatService implements FsService {

	private ChatHandler chatHandler = ChatHandler.getInstance();

	public ByteString doTask(Request request, Player player) {
		ByteString result = null;

		try {
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
					result = chatHandler
							.getChatTreasure(player, msgChatRequest);
					break;
				default:
					break;
				}
			}

		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public ByteString isChatBan(Player player, MsgChatRequest msgChatRequest){
		UserDataMgr userDataMgr = player.getUserDataMgr();
		if(userDataMgr.isChatBan()){
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
			return  msgChatResponse.build().toByteString();
		}else{
			return null;
		}
	}
}
