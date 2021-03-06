package com.playerdata.activity.fortuneCatType.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.fortuneCatType.ActivityFortuneCatTypeMgr;
import com.rwproto.ActivityFortuneCatTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityFortuneCatTypeProto.ActivityCommonRspMsg;

public class ActivityFortuneCatTypeHandler {
	
	private static ActivityFortuneCatTypeHandler instance = new ActivityFortuneCatTypeHandler();
	
	public static ActivityFortuneCatTypeHandler getInstance(){
		return instance;
	}

	public ByteString getGold(Player player, ActivityCommonReqMsg commonReq) {
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		
		boolean success = false;
		String tips = null;
		
		ActivityComResult result = ActivityFortuneCatTypeMgr.getInstance().getGold(player,response);
		success = result.isSuccess();
		tips = result.getReason()+"";
		
		response.setIsSuccess(success);
		response.setTipMsg(tips);
		return response.build().toByteString();
	}

	public ByteString getRecord(Player player, ActivityCommonReqMsg commonReq) {
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
	
		boolean success = false;
		String tips = null;
		
		ActivityComResult result = ActivityFortuneCatTypeMgr.getInstance().getRecord(player,response);
		success = result.isSuccess();
		tips = result.getReason()+"";
		
		response.setIsSuccess(success);
		response.setTipMsg(tips);
		return response.build().toByteString();
	}
}
