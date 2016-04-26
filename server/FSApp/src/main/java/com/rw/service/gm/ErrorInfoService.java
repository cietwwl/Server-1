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

public class ErrorInfoService implements FsService{


	
	public ByteString doTask(Request request, Player player) {
		ByteString result = null;
		try {
			MsgErrorInfoRequest msgGMRequest = MsgErrorInfoRequest.parseFrom(request.getBody().getSerializedContent());
			EMsgErrorInfoType type = msgGMRequest.getType();
			switch (type) {
			case ServerMsg:
				
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
