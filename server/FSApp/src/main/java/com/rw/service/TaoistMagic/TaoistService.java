package com.rw.service.TaoistMagic;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;
import com.rwproto.TaoistMagicProtos.TaoistRequest;
import com.rwproto.TaoistMagicProtos.TaoistRequestType;

public class TaoistService implements FsService {

	private TaoistHandler handler = TaoistHandler.getInstance();

	@SuppressWarnings("finally")
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			TaoistRequest req = TaoistRequest.parseFrom(request.getBody().getSerializedContent());
			TaoistRequestType reqType = req.getReqType();
			switch (reqType) {
			case updateTaoist:
				result = handler.upgradeTaoist(player, req);
				break;
			case getTaoistData:
				result = handler.getData(player, req);
				break;
			case getRandom:
				result = handler.getRandom(player, req);
				break;
			default:
				break;
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		} finally {
			return result;
		}

	}
}