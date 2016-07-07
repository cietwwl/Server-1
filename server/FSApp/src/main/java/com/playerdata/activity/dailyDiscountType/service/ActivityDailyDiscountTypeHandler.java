package com.playerdata.activity.dailyDiscountType.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeEnum;
import com.playerdata.activity.dailyDiscountType.ActivityDailyDiscountTypeMgr;
import com.rwproto.ActivityDailyDiscountTypeProto.ActivityCommonRspMsg;


public class ActivityDailyDiscountTypeHandler {
	
	private static ActivityDailyDiscountTypeHandler instance = new ActivityDailyDiscountTypeHandler();
	
	public static ActivityDailyDiscountTypeHandler getInstance(){
		return instance;
	}



	public ByteString buyItem(
			Player player,
			com.rwproto.ActivityDailyDiscountTypeProto.ActivityCommonReqMsg commonReq) {
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		String activityId = commonReq.getActivityId();
		String subItemId =  commonReq.getSubItemId();
	
		ActivityDailyDiscountTypeEnum countType = ActivityDailyDiscountTypeEnum.getById(activityId);
		
		boolean success = false;
		String tips = "没找到对应的活动";
		
		if(countType!=null){
			ActivityComResult result = ActivityDailyDiscountTypeMgr.getInstance().buyItem(player, countType, subItemId);
			success = result.isSuccess();
			tips = result.getReason()+"";
		}
		response.setIsSuccess(success);
		response.setTipMsg(tips);
		return response.build().toByteString();
	}	
}
