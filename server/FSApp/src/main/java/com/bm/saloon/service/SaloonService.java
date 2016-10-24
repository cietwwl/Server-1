package com.bm.saloon.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.RequestProtos.Request;
import com.rwproto.SaloonServiceProto.CommonReqMsg;
import com.rwproto.SaloonServiceProto.RequestType;


public class SaloonService implements FsService<CommonReqMsg, RequestType> {

	@Override
	public ByteString doTask(CommonReqMsg request, Player player) {
		SaloonHandler saloonHandler = SaloonHandler.getInstance();
		
		ByteString byteString = null;
		try {
			
			RequestType reqType = request.getReqType();
			switch (reqType) {
			case Enter:
				byteString = saloonHandler.enter(player, request);
				break;
			
			case Leave:
				byteString = saloonHandler.leave(player, request);
				break;
			case UpdatePosition:
				byteString = saloonHandler.updatePosition(player, request);
				break;
			default:
				GameLog.error(LogModule.Saloon, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			GameLog.error(LogModule.Saloon, player.getUserId(), "出现了Exception异常", e);
		}
		return byteString;
	}

	@Override
	public CommonReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		CommonReqMsg commonReq = CommonReqMsg.parseFrom(request.getBody().getSerializedContent());
		return commonReq;
	}

	@Override
	public RequestType getMsgType(CommonReqMsg request) {
		return request.getReqType();
	}

	
}