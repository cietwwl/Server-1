package com.rw.service.sign;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;
import com.rwproto.SignServiceProtos.ERequestType;
import com.rwproto.SignServiceProtos.MsgSignRequest;

public class SignService implements FsService {

	private SignHandler handler = SignHandler.getInstance();

	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			MsgSignRequest req = MsgSignRequest.parseFrom(request.getBody().getSerializedContent());
			ERequestType requestType = req.getRequestType();
			switch (requestType) 
			{
			case SIGN:
				result = handler.sign(player, req);
				break;
			case SIGNDATA_BACK:
				result = handler.returnSignData(player);
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
