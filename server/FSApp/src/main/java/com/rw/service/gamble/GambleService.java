package com.rw.service.gamble;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.GambleServiceProtos.EGambleRequestType;
import com.rwproto.GambleServiceProtos.GambleRequest;
import com.rwproto.RequestProtos.Request;

public class GambleService implements FsService {

	public ByteString doTask(Request request, Player pPlayer) {
		ByteString result = null;
		try {
			GambleRequest gambleRequest = GambleRequest.parseFrom(request.getBody().getSerializedContent());
			EGambleRequestType requestType = gambleRequest.getRequestType();
			switch (requestType) {
			case GAMBLE:
				result = GambleLogic.getInstance().gamble(GambleLogicHelper.ConvertRequest(gambleRequest), pPlayer);
				//result = GambleHandler.getInatance().gamble(gambleRequest, pPlayer);
				break;
			case GAMBLE_GET:
				result = GambleLogic.getInstance().gambleData(gambleRequest, pPlayer);
				//result = GambleHandler.getInatance().gambleData(gambleRequest, pPlayer);
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