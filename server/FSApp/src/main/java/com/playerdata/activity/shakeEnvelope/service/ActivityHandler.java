package com.playerdata.activity.shakeEnvelope.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.chargeRank.ActivityChargeRankMgr;
import com.playerdata.activity.consumeRank.ActivityConsumeRankMgr;
import com.rwproto.ActivityChargeRankProto.ActivityCommonReqMsg;
import com.rwproto.ActivityChargeRankProto.ActivityCommonRspMsg;

public class ActivityHandler {
	
	private static ActivityHandler instance = new ActivityHandler();
	
	public static ActivityHandler getInstance(){
		return instance;
	}

	public ByteString getChargeRank(Player player, ActivityCommonReqMsg request) {
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		response.setReqType(request.getReqType());
		int fromRank = request.getFromRank();
		int toRank = request.getToRank();
		ActivityChargeRankMgr.getInstance().getChargeRank(player, fromRank, toRank, response);
		response.setIsSuccess(true);
		return response.build().toByteString();
	}

	public ByteString getConsumeRank(Player player, ActivityCommonReqMsg request) {
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		response.setReqType(request.getReqType());
		int fromRank = request.getFromRank();
		int toRank = request.getToRank();
		ActivityConsumeRankMgr.getInstance().getConsumeRank(player, fromRank, toRank, response);
		response.setIsSuccess(true);
		return response.build().toByteString();
	}
}
