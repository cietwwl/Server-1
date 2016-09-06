package com.rw.service.worship;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.playerdata.WorshipMgr;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;
import com.rwproto.WorshipServiceProtos.EWorshipRequestType;
import com.rwproto.WorshipServiceProtos.WorshipRequest;

public class WorshipService  implements FsService<WorshipRequest, EWorshipRequestType>{
	private WorshipHandler worshipHandler = WorshipHandler.getInstance();

	@Override
	public ByteString doTask(WorshipRequest request, Player pPlayer) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			EWorshipRequestType requestType = request.getRequestType();
			switch (requestType) {
				case BY_WORSHIPPED_LIST:
					result = WorshipMgr.getInstance().getByWorshipedInfo();
					break;
				case WORSHIP:
					result = worshipHandler.worship(request, pPlayer);
					break;
				case WORSHIP_STATE:
					result = worshipHandler.getWorshipState(request, pPlayer);
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
	public WorshipRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		WorshipRequest worshipRequest = WorshipRequest.parseFrom(request.getBody().getSerializedContent());
		return worshipRequest;
	}

	@Override
	public EWorshipRequestType getMsgType(WorshipRequest request) {
		// TODO Auto-generated method stub
		return request.getRequestType();
	}
}