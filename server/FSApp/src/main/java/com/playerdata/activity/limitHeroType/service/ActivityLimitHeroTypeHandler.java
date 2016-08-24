package com.playerdata.activity.limitHeroType.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.limitHeroType.ActivityLimitHeroTypeMgr;
import com.rwproto.ActivityLimitHeroTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityLimitHeroTypeProto.ActivityCommonRspMsg;


public class ActivityLimitHeroTypeHandler {
	private static ActivityLimitHeroTypeHandler instance = new ActivityLimitHeroTypeHandler();
	
	public static ActivityLimitHeroTypeHandler getInstance(){
		return instance;
	}

	public ByteString getRewards(Player player, ActivityCommonReqMsg commonReq) {
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		int id = commonReq.getBoxCfgId();
		
		ActivityComResult result = ActivityLimitHeroTypeMgr.getInstance().getRewards(player,id);
		response.setIsSuccess(result.isSuccess());
		response.setTipMsg(result.getReason());
		return response.build().toByteString();
	}

	public ByteString gamble(Player player, ActivityCommonReqMsg commonReq) {
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		
		ActivityComResult result = ActivityLimitHeroTypeMgr.getInstance().gamble(player,commonReq,response);
		response.setIsSuccess(result.isSuccess());
		response.setTipMsg(result.getReason());
		return response.build().toByteString();
	}
	
	

	
}
