package com.rw.service.chat;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ChatServiceProtos.MsgChatRequest;
import com.rwproto.ChatServiceProtos.eChatType;
import com.rwproto.RequestProtos.Request;

public class ChatService implements FsService {

	private ChatHandler chatHandler = ChatHandler.getInstance();

	public ByteString doTask(Request request, Player player) {
		ByteString result = null;

		try {
			MsgChatRequest msgChatRequest = MsgChatRequest.parseFrom(request.getBody().getSerializedContent());
			eChatType chatType = msgChatRequest.getChatType();
			switch (chatType) {
			case CHAT_WORLD:
				result = chatHandler.chatWorld(player, msgChatRequest);
				break;
			case CHAT_FAMILY:
				result = chatHandler.chatInGruild(player, msgChatRequest);
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

		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

}
