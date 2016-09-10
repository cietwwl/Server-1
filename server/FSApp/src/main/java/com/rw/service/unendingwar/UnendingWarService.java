package com.rw.service.unendingwar;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;
import com.rwproto.UnendingWarProtos.EUnendingWarType;
import com.rwproto.UnendingWarProtos.UnendingWarRequest;

public class UnendingWarService implements FsService<UnendingWarRequest, EUnendingWarType> {

	@Override
	public ByteString doTask(UnendingWarRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			EUnendingWarType requestType = request.getType();
			switch (requestType) {
			case BaseMsg:
				result = UnendingWarHandler.getInstance().getInfo(player);
				break;
			case OtherMsg:
				result = UnendingWarHandler.getInstance().endMap(player, request.getNum());
				break;
			case AddMsg:
				result = UnendingWarHandler.getInstance().addNum(player);
				break;
			case EndMsg:
				result = UnendingWarHandler.getInstance().end(player, request.getNum());
				break;
			case ResetMsg:
				result = UnendingWarHandler.getInstance().ResetNum(player);
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
	public UnendingWarRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		UnendingWarRequest req = UnendingWarRequest.parseFrom(request.getBody().getSerializedContent());
		return req;
	}

	@Override
	public EUnendingWarType getMsgType(UnendingWarRequest request) {
		// TODO Auto-generated method stub
		return request.getType();
	}

}
