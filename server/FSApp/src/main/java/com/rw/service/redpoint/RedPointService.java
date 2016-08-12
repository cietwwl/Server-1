package com.rw.service.redpoint;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RedPointServiceProtos.RedPointServiceRequest;
import com.rwproto.RedPointServiceProtos.RequestType;
import com.rwproto.RequestProtos.Request;

public class RedPointService implements FsService{

	@Override
	public ByteString doTask(Request request, Player player) {
		RedPointHandler redPointHandler = RedPointHandler.getInstance();
		ByteString byteString = null;
		try {
			RedPointServiceRequest commnreq = RedPointServiceRequest.parseFrom(request.getBody().getSerializedContent());
			RequestType type = commnreq.getReqType();
			switch (type) {
			case RT_ALL:				
				break;
			case RT_ONE:
				byteString = redPointHandler.reFreshRedPoint(player,commnreq);
				break;
			default:
				break;
			}

		} catch (Exception e) {
			GameLog.error(LogModule.RedPoint, player.getUserId(), "跑出了一个unknow的消息，无法处理", null);
		}		
		return byteString;
	}
	
	
	
}
