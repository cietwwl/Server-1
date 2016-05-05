package com.playerdata.activity.dailyCountType.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activity.dailyCountType.ActivityDailyCountTypeEnum;
import com.playerdata.activity.dailyCountType.ActivityDailyCountTypeMgr;
import com.rwproto.ActivityCountTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityCountTypeProto.ActivityCommonRspMsg;

public class ActivityDailyCountTypeHandler {
	
	private static ActivityDailyCountTypeHandler instance = new ActivityDailyCountTypeHandler();
	
	public static ActivityDailyCountTypeHandler getInstance(){
		return instance;
	}

	public ByteString takeGift(Player player, ActivityCommonReqMsg commonReq) {
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		String activityId = commonReq.getActivityId();
		String subItemId =  commonReq.getSubItemId();
	
		ActivityDailyCountTypeEnum countType = ActivityDailyCountTypeEnum.getById(activityId);
		
		boolean success = false;
		String tips = null;
		
		if(countType!=null){
			ActivityComResult result = ActivityDailyCountTypeMgr.getInstance().takeGift(player, countType, subItemId);
			success = result.isSuccess();
			tips = result.getReason()+"";
		}
		response.setIsSuccess(success);
		response.setTipMsg(tips);
		return response.build().toByteString();
	}


}
