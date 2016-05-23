package com.playerdata.activity.dateType.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.dateType.ActivityDateTypeEnum;
import com.playerdata.activity.dateType.ActivityDateTypeMgr;
import com.rwproto.ActivityDateTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityDateTypeProto.ActivityCommonRspMsg;

public class ActivityDateTypeHandler {
	
	private static ActivityDateTypeHandler instance = new ActivityDateTypeHandler();
	
	public static ActivityDateTypeHandler getInstance(){
		return instance;
	}

	public ByteString takeGift(Player player, ActivityCommonReqMsg commonReq) {
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		String activityId = commonReq.getActivityId();
		String subItemId =  commonReq.getSubItemId();
	
		ActivityDateTypeEnum dateType = ActivityDateTypeEnum.getById(activityId);
		
		boolean success = false;
		String tips = null;
		
		if(dateType!=null){
			ActivityComResult result = ActivityDateTypeMgr.getInstance().takeGift(player, dateType, subItemId);
			success = result.isSuccess();
			tips = result.getReason()+"";
		}
		response.setIsSuccess(success);
		response.setTipMsg(tips);
		return response.build().toByteString();
	}
	public ByteString takeBigGift(Player player, ActivityCommonReqMsg commonReq) {
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		String activityId = commonReq.getActivityId();
		String subItemId =  commonReq.getSubItemId();
		
		ActivityDateTypeEnum dateType = ActivityDateTypeEnum.getById(activityId);
		
		boolean success = false;
		String tips = null;
		
		if(dateType!=null){
			ActivityComResult result = ActivityDateTypeMgr.getInstance().takeGift(player, dateType, subItemId);
			success = result.isSuccess();
			tips = result.getReason()+"";
		}
		response.setIsSuccess(success);
		response.setTipMsg(tips);
		return response.build().toByteString();
	}


}
