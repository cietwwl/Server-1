package com.rw.service.gamble;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.GambleServiceProtos.EGambleRequestType;
import com.rwproto.GambleServiceProtos.GambleRequest;
import com.rwproto.RequestProtos.Request;

public class GambleService implements FsService<GambleRequest, EGambleRequestType> {

	@Override
	public ByteString doTask(GambleRequest request, Player pPlayer) {
		ByteString result = null;
		try {
			EGambleRequestType requestType = request.getRequestType();
			switch (requestType) {
			case GAMBLE:
				result = GambleHandler.getInstance().gamble(GambleLogicHelper.ConvertRequest(request), pPlayer);
				break;
			case GAMBLE_GET:
				result = GambleHandler.getInstance().gambleData(request, pPlayer);
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
	public GambleRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		GambleRequest gambleRequest = GambleRequest.parseFrom(request.getBody().getSerializedContent());
		return gambleRequest;
	}

	@Override
	public EGambleRequestType getMsgType(GambleRequest request) {
		return request.getRequestType();
	}
}