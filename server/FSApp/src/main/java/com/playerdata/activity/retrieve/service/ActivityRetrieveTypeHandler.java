package com.playerdata.activity.retrieve.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.retrieve.ActivityRetrieveTypeMgr;
import com.rwproto.ActivityRetrieveTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityRetrieveTypeProto.ActivityCommonRspMsg;

public class ActivityRetrieveTypeHandler {
	private static ActivityRetrieveTypeHandler instance = new ActivityRetrieveTypeHandler();
	
	public static ActivityRetrieveTypeHandler getInstance(){
		return instance;
	}

	public ByteString retrieve(Player player, ActivityCommonReqMsg request) {
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		response.setReqType(request.getReqType());
		String typeId = request.getActivityId();
		int costType =request.getType();
		boolean success = false;
		String tips = null;
		ActivityComResult result = ActivityRetrieveTypeMgr.getInstance().retrieve(player,typeId,costType);
		success = result.isSuccess();
		tips = result.getReason()+"";
		response.setIsSuccess(success);
		response.setTipMsg(tips);
		return response.build().toByteString();
	}
	
	
	
}
