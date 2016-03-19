package com.rw.service.worship;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.playerdata.WorshipMgr;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;
import com.rwproto.WorshipServiceProtos.EWorshipRequestType;
import com.rwproto.WorshipServiceProtos.WorshipRequest;

public class WorshipService  implements FsService{
	private WorshipHandler worshipHandler = WorshipHandler.getInstance();
	
	public ByteString doTask(Request request, Player pPlayer) {
		ByteString result = null;
		try {
			WorshipRequest worshipRequest = WorshipRequest.parseFrom(request.getBody().getSerializedContent());
			EWorshipRequestType requestType = worshipRequest.getRequestType();
			switch (requestType) {
				case BY_WORSHIPPED_LIST:
					result = WorshipMgr.getInstance().getByWorshipedInfo();
					break;
				case WORSHIP:
					result = worshipHandler.worship(worshipRequest, pPlayer);
					break;
				case WORSHIP_STATE:
					result = worshipHandler.getWorshipState(worshipRequest, pPlayer);
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