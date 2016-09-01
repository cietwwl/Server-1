package com.rw.service.redpoint;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RedPointServiceProtos.RedPointServiceRequest;
import com.rwproto.RedPointServiceProtos.RequestType;
import com.rwproto.RequestProtos.Request;

public class RedPointService implements FsService<RedPointServiceRequest, RequestType>{

	@Override
	public ByteString doTask(RedPointServiceRequest request, Player player) {
		// TODO Auto-generated method stub
		RedPointHandler redPointHandler = RedPointHandler.getInstance();
		ByteString byteString = null;
		try {
			RequestType type = request.getReqType();
			switch (type) {
			case RT_ALL:				
				break;
			case RT_ONE:
				byteString = redPointHandler.reFreshRedPoint(player,request);
				break;
			default:
				break;
			}

		} catch (Exception e) {
			GameLog.error(LogModule.RedPoint, player.getUserId(), "跑出了一个unknow的消息，无法处理", null);
		}		
		return byteString;
	}

	@Override
	public RedPointServiceRequest parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		RedPointServiceRequest commnreq = RedPointServiceRequest.parseFrom(request.getBody().getSerializedContent());
		return commnreq;
	}

	@Override
	public RequestType getMsgType(RedPointServiceRequest request) {
		// TODO Auto-generated method stub
		return request.getReqType();
	}
	
	
	
}
