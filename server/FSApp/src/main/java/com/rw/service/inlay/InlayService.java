package com.rw.service.inlay;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.InlayProtos.EInlayType;
import com.rwproto.InlayProtos.InlayResult;
import com.rwproto.InlayProtos.MsgInlayRequest;
import com.rwproto.InlayProtos.MsgInlayResponse;
import com.rwproto.RequestProtos.Request;

public class InlayService implements FsService<MsgInlayRequest, EInlayType> {

	@Override
	public ByteString doTask(MsgInlayRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			EInlayType type = request.getType();
			switch (type) {
			case Inlay_One:
				result = InlayHandler.getInstance().InlayOne(player, request);

				break;

			case Inlay_All:
				result = InlayHandler.getInstance().InlayAll(player, request);
				break;

			case XieXia_All:
				result = InlayHandler.getInstance().XieXiaAll(player, request);
				break;

			default:
				break;
			}
			if (result == null) {
				MsgInlayResponse.Builder res = MsgInlayResponse.newBuilder();
				res.setType(request.getType());
				res.setResult(InlayResult.InlayFailed);
				result = res.build().toByteString();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public MsgInlayRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		MsgInlayRequest msgRequest = MsgInlayRequest.parseFrom(request.getBody().getSerializedContent());
		return msgRequest;
	}

	@Override
	public EInlayType getMsgType(MsgInlayRequest request) {
		// TODO Auto-generated method stub
		return request.getType();
	}

}
