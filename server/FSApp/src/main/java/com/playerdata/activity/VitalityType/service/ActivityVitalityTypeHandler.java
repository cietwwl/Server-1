package com.playerdata.activity.VitalityType.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.VitalityType.ActivityVitalityTypeMgr;
import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.rwproto.ActivityVitalityTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityVitalityTypeProto.ActivityCommonRspMsg;



public class ActivityVitalityTypeHandler {
	
	private static ActivityVitalityTypeHandler instance = new ActivityVitalityTypeHandler();
	
	public static ActivityVitalityTypeHandler getInstance(){
		return instance;
	}

	public ByteString takeGift(Player player, ActivityCommonReqMsg commonReq) {
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		String subItemId =  commonReq.getSubItemId();
		boolean success = false;
		String tips = null;
		
		ActivityComResult result = ActivityVitalityTypeMgr.getInstance().takeGift(player, subItemId);
		success = result.isSuccess();
		tips = result.getReason()+"";	
		
		response.setIsSuccess(success);
		response.setTipMsg(tips);
		return response.build().toByteString();
	}

	public ByteString openBox(Player player, ActivityCommonReqMsg commonReq) {
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		String rewardItemId =  commonReq.getBoxId();
		boolean success = false;
		String tips = null;
		
		ActivityComResult result = ActivityVitalityTypeMgr.getInstance().openBox(player, rewardItemId);
		success = result.isSuccess();
		tips = result.getReason()+"";	
		
		response.setIsSuccess(success);
		response.setTipMsg(tips);
		return response.build().toByteString();
	}
}
