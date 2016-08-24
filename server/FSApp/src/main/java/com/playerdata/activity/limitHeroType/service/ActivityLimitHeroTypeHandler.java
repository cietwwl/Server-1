package com.playerdata.activity.limitHeroType.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.redEnvelopeType.ActivityRedEnvelopeTypeMgr;
import com.rwproto.ActivityRedEnvelopeTypeProto.*;

public class ActivityLimitHeroTypeHandler {
	private static ActivityLimitHeroTypeHandler instance = new ActivityLimitHeroTypeHandler();
	
	public static ActivityLimitHeroTypeHandler getInstance(){
		return instance;
	}
	
	public ByteString takeGift(Player player,ActivityCommonReqMsg commonReq){
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		
		ActivityComResult result = ActivityRedEnvelopeTypeMgr.getInstance().takeGift(player);
		
		response.setIsSuccess(result.isSuccess());
		response.setTipMsg(result.getReason());
		return response.build().toByteString();
	}
	
}
