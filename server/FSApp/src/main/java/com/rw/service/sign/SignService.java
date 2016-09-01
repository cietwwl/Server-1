package com.rw.service.sign;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;
import com.rwproto.SignServiceProtos.ERequestType;
import com.rwproto.SignServiceProtos.MsgSignRequest;

public class SignService implements FsService<MsgSignRequest, ERequestType> {

	private SignHandler handler = SignHandler.getInstance();

	@Override
	public ByteString doTask(MsgSignRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			ERequestType requestType = request.getRequestType();
			switch (requestType) {
			case SIGN:
				result = handler.sign(player, request);
				break;
			case SIGNDATA_BACK:
				result = handler.returnSignData(player);
				break;
			case SIGN_REWARD:
				result = handler.processSignReward(player);
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
	public MsgSignRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		MsgSignRequest req = MsgSignRequest.parseFrom(request.getBody().getSerializedContent());
		return req;
	}

	@Override
	public ERequestType getMsgType(MsgSignRequest request) {
		// TODO Auto-generated method stub
		return request.getRequestType();
	}
}
