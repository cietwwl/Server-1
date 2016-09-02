package com.rw.service.gm;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.GMServiceProtos.MsgGMRequest;
import com.rwproto.GMServiceProtos.eGMType;
import com.rwproto.MsgErrInfoProtos.EMsgErrorInfoType;
import com.rwproto.MsgErrInfoProtos.MsgErrorInfoRequest;
import com.rwproto.RequestProtos.Request;

public class ErrorInfoService implements FsService<MsgErrorInfoRequest, EMsgErrorInfoType>{

	@Override
	public ByteString doTask(MsgErrorInfoRequest request, Player player) {
		// TODO Auto-generated method stub
		ByteString result = null;
		try {
			EMsgErrorInfoType type = request.getType();
			switch (type) {
			case ServerMsg:
				
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
	public MsgErrorInfoRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		MsgErrorInfoRequest msgGMRequest = MsgErrorInfoRequest.parseFrom(request.getBody().getSerializedContent());
		return msgGMRequest;
	}

	@Override
	public EMsgErrorInfoType getMsgType(MsgErrorInfoRequest request) {
		// TODO Auto-generated method stub
		return request.getType();
	}

}
