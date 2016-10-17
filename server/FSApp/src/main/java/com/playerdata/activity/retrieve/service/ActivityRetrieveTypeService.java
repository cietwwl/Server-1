package com.playerdata.activity.retrieve.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ActivityRetrieveTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityRetrieveTypeProto.RequestType;
import com.rwproto.RequestProtos.Request;

public class ActivityRetrieveTypeService implements FsService<ActivityCommonReqMsg, RequestType>{

	@Override
	public ByteString doTask(ActivityCommonReqMsg request, Player player) {
		ByteString byteString = null;
		ActivityRetrieveTypeHandler handler = ActivityRetrieveTypeHandler.getInstance();
		try{
			RequestType type = request.getReqType();
			switch (type) {
			case RETRIEVE:
				byteString = handler.retrieve(player,request);
				break;				
			default:
				GameLog.error(LogModule.ComActivityRetrieve, player.getUserId(), "接受到一个unknow请求", null);
				break;
			}
			
			
		}catch(Exception e){
			GameLog.error(LogModule.ComActivityRetrieve, player.getUserId(), "出现了Exception异常", null);
		}		
		return byteString;
	}

	@Override
	public ActivityCommonReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		ActivityCommonReqMsg msg = ActivityCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
		return msg;
	}

	@Override
	public RequestType getMsgType(ActivityCommonReqMsg request) {
		// TODO Auto-generated method stub
		return request.getReqType();
	}
	
}
