package com.rw.service.friend;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.FriendServiceProtos.EFriendRequestType;
import com.rwproto.FriendServiceProtos.FriendRequest;
import com.rwproto.RequestProtos.Request;

public class FriendService implements FsService<FriendRequest, EFriendRequestType>{
	private FriendHandler friendHandler = FriendHandler.getInstance();

	@Override
	public ByteString doTask(FriendRequest request, Player pPlayer) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			EFriendRequestType requestType = request.getRequestType();
			switch (requestType) {
				case ALL_LIST:
					result = friendHandler.allList(request, pPlayer);
					break;
				case FRIEND_LIST:
					result = friendHandler.friendList(request, pPlayer);
					break;
				case BLACK_LIST:
					result = friendHandler.blackList(request, pPlayer);
					break;
				case REQUEST_LIST:
					result = friendHandler.requestList(request, pPlayer);
					break;
				case SEARCH_FRIEND:
					result = friendHandler.searchFriend(request, pPlayer);
					break;
				case GIVE_POWER:
					result = friendHandler.givePower(request, pPlayer);
					break;
				case RECEIVE_POWER:
					result = friendHandler.receivePower(request, pPlayer);
					break;
				case GIVE_POWER_ALL:
					result = friendHandler.givePowerAll(request, pPlayer);
					break;
				case RECEIVE_POWER_ALL:
					result = friendHandler.receivePowerAll(request, pPlayer);
					break;
				case REQUEST_ADD_FRIEND:
					result = friendHandler.requestAddFriend(request, pPlayer);
					break;
				case REQUEST_ADD_MUTI_FRIEND:
					result = friendHandler.requestAddFriendList(request, pPlayer);
					break;					
				case REMOVE_FRIEND:
					result = friendHandler.removeFriend(request, pPlayer);
					break;
				case ADD_BLACK:
					result = friendHandler.addBlack(request, pPlayer);
					break;
				case REMOVE_BLACK:
					result = friendHandler.removeBlack(request, pPlayer);
					break;
				case CONSENT_ADD_FRIEND:
					result = friendHandler.consentAddFriend(request, pPlayer);
					break;
				case REFUSED_ADD_FRIEND:
					result = friendHandler.refusedAddFriend(request, pPlayer);
					break;
				case CONSENT_ADD_FRIEND_ALL:
					result = friendHandler.consentAddFriendAll(request, pPlayer);
					break;
				case REFUSED_ADD_FRIEND_ALL:
					result = friendHandler.refusedAddFriendAll(request, pPlayer);
					break;
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public FriendRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		FriendRequest friendRequest = FriendRequest.parseFrom(request.getBody().getSerializedContent());
		return friendRequest;
	}

	@Override
	public EFriendRequestType getMsgType(FriendRequest request) {
		// TODO Auto-generated method stub
		return request.getRequestType();
	}
}
