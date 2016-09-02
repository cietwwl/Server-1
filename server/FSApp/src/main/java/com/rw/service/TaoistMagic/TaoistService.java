package com.rw.service.TaoistMagic;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;
import com.rwproto.TaoistMagicProtos.TaoistRequest;
import com.rwproto.TaoistMagicProtos.TaoistRequestType;

public class TaoistService implements FsService<TaoistRequest, TaoistRequestType> {

	private TaoistHandler handler = TaoistHandler.getInstance();

	@SuppressWarnings("finally")
	@Override
	public ByteString doTask(TaoistRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			TaoistRequestType reqType = request.getReqType();
			switch (reqType) {
			case updateTaoist:
				result = handler.upgradeTaoist(player, request);
				break;
			case getTaoistData:
				result = handler.getData(player, request);
				break;
			case getRandom:
				result = handler.getRandom(player, request);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return result;
		}
	}

	@Override
	public TaoistRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		TaoistRequest req = TaoistRequest.parseFrom(request.getBody().getSerializedContent());
		return req;
	}

	@Override
	public TaoistRequestType getMsgType(TaoistRequest request) {
		// TODO Auto-generated method stub
		return request.getReqType();
	}
}