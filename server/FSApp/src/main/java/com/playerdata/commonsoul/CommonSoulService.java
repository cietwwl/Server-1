package com.playerdata.commonsoul;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.CommonSoulServiceProto.CommonSoulRequest;
import com.rwproto.CommonSoulServiceProto.RequestType;
import com.rwproto.RequestProtos.Request;

public class CommonSoulService implements FsService<CommonSoulRequest, RequestType>{

	@Override
	public ByteString doTask(CommonSoulRequest request, Player player) {
		switch (request.getRequestType()) {
		case exchange:
			return CommonSoulHandler.getInstance().processExchange(player, request);
		}
		return null;
	}

	@Override
	public CommonSoulRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		return CommonSoulRequest.parseFrom(request.getBody().getSerializedContent());
	}

	@Override
	public RequestType getMsgType(CommonSoulRequest request) {
		return request.getRequestType();
	}

}
