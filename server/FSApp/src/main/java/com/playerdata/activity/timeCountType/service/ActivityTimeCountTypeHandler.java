package com.playerdata.activity.timeCountType.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.timeCardType.ActivityTimeCardTypeEnum;
import com.playerdata.activity.timeCountType.ActivityTimeCountTypeEnum;
import com.playerdata.activity.timeCountType.ActivityTimeCountTypeMgr;
import com.rwproto.ActivityTimeCountTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityTimeCountTypeProto.ActivityCommonRspMsg;

public class ActivityTimeCountTypeHandler {
	
	private static ActivityTimeCountTypeHandler instance = new ActivityTimeCountTypeHandler();
	
	public static ActivityTimeCountTypeHandler getInstance(){
		return instance;
	}

	public ByteString takeGift(Player player, ActivityCommonReqMsg commonReq) {
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		String activityId = commonReq.getActivityId();
		String subItemId =  commonReq.getSubItemId();
	
		ActivityTimeCountTypeEnum countType = ActivityTimeCountTypeEnum.getById(activityId);
		
		boolean success = false;
		String tips = "枚举类为空";
		
		if(countType!=null){
			ActivityComResult result = ActivityTimeCountTypeMgr.getInstance().takeGift(player, countType, subItemId);
			success = result.isSuccess();
			tips = result.getReason()+"";
		}
		response.setIsSuccess(success);
		response.setTipMsg(tips);
		return response.build().toByteString();
	}


}
