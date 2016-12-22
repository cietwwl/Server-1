package com.playerdata.activity.shakeEnvelope.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.shakeEnvelope.ActivityShakeEnvelopeMgr;
import com.rwproto.ActivityCommonTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityCommonTypeProto.ActivityCommonRspMsg;

public class ActivityShakeEnvelopeHandler {
	
	private static ActivityShakeEnvelopeHandler instance = new ActivityShakeEnvelopeHandler();
	
	public static ActivityShakeEnvelopeHandler getInstance(){
		return instance;
	}

	public ByteString getEnvelopeReward(Player player, ActivityCommonReqMsg request) {
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		response.setReqType(request.getReqType());
		ActivityShakeEnvelopeMgr.getInstance().getEnvelopeReward(player, response);
		return response.build().toByteString();
	}
}
