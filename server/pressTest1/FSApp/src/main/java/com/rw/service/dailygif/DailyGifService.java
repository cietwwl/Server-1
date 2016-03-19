package com.rw.service.dailygif;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.DailyGifProtos.DailyGifRequest;
import com.rwproto.DailyGifProtos.EType;
import com.rwproto.RequestProtos.Request;

public class DailyGifService implements FsService {
	@Override
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			DailyGifRequest req = DailyGifRequest.parseFrom(request.getBody().getSerializedContent());
			EType requestType = req.getType();
			switch (requestType) {
			case InfoMsg:
				result = DailyGifHandler.getInstance().getInfo(player);
				break;
			case GetGif:
				result = DailyGifHandler.getInstance().getGif(player, req.getCount());
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