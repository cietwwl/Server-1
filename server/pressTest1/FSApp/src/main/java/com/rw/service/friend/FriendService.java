package com.rw.service.friend;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.FriendServiceProtos.EFriendRequestType;
import com.rwproto.FriendServiceProtos.FriendRequest;
import com.rwproto.RequestProtos.Request;

public class FriendService implements FsService{
	private FriendHandler friendHandler = FriendHandler.getInstance();
	
	public ByteString doTask(Request request, Player pPlayer) {
		ByteString result = null;
		try {
			FriendRequest friendRequest = FriendRequest.parseFrom(request.getBody().getSerializedContent());
			EFriendRequestType requestType = friendRequest.getRequestType();
			switch (requestType) {
				case ALL_LIST:
					result = friendHandler.allList(friendRequest, pPlayer);
					break;
				case FRIEND_LIST:
					result = friendHandler.friendList(friendRequest, pPlayer);
					break;
				case BLACK_LIST:
					result = friendHandler.blackList(friendRequest, pPlayer);
					break;
				case REQUEST_LIST:
					result = friendHandler.requestList(friendRequest, pPlayer);
					break;
				case SEARCH_FRIEND:
					result = friendHandler.searchFriend(friendRequest, pPlayer);
					break;
				case GIVE_POWER:
					result = friendHandler.givePower(friendRequest, pPlayer);
					break;
				case RECEIVE_POWER:
					result = friendHandler.receivePower(friendRequest, pPlayer);
					break;
				case GIVE_POWER_ALL:
					result = friendHandler.givePowerAll(friendRequest, pPlayer);
					break;
				case RECEIVE_POWER_ALL:
					result = friendHandler.receivePowerAll(friendRequest, pPlayer);
					break;
				case REQUEST_ADD_FRIEND:
					result = friendHandler.requestAddFriend(friendRequest, pPlayer);
					break;
				case REMOVE_FRIEND:
					result = friendHandler.removeFriend(friendRequest, pPlayer);
					break;
				case ADD_BLACK:
					result = friendHandler.addBlack(friendRequest, pPlayer);
					break;
				case REMOVE_BLACK:
					result = friendHandler.removeBlack(friendRequest, pPlayer);
					break;
				case CONSENT_ADD_FRIEND:
					result = friendHandler.consentAddFriend(friendRequest, pPlayer);
					break;
				case REFUSED_ADD_FRIEND:
					result = friendHandler.refusedAddFriend(friendRequest, pPlayer);
					break;
				case CONSENT_ADD_FRIEND_ALL:
					result = friendHandler.consentAddFriendAll(friendRequest, pPlayer);
					break;
				case REFUSED_ADD_FRIEND_ALL:
					result = friendHandler.refusedAddFriendAll(friendRequest, pPlayer);
					break;
				default:
					break;
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return result;
	}
}
