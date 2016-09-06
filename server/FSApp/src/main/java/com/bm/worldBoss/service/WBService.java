package com.bm.worldBoss.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ActivityCountTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityCountTypeProto.RequestType;
import com.rwproto.RequestProtos.Request;


public class WBService implements FsService<ActivityCommonReqMsg, RequestType>  {

	@Override
	public ByteString doTask(ActivityCommonReqMsg request, Player player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActivityCommonReqMsg parseMsg(Request request)
			throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestType getMsgType(ActivityCommonReqMsg request) {
		// TODO Auto-generated method stub
		return null;
	}


	
}