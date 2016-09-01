package com.rw.service.dailygif;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.DailyGifProtos.DailyGifRequest;
import com.rwproto.DailyGifProtos.EType;
import com.rwproto.RequestProtos.Request;

public class DailyGifService implements FsService<DailyGifRequest, EType> {

	@Override
	public ByteString doTask(DailyGifRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			EType requestType = request.getType();
			switch (requestType) {
			case InfoMsg:
				result = DailyGifHandler.getInstance().getInfo(player);
				break;
			case GetGif:
				result = DailyGifHandler.getInstance().getGif(player, request.getCount());
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
	public DailyGifRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		DailyGifRequest req = DailyGifRequest.parseFrom(request.getBody().getSerializedContent());
		return req;
	}

	@Override
	public EType getMsgType(DailyGifRequest request) {
		// TODO Auto-generated method stub
		return request.getType();
	}
}