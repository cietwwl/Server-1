package com.playerdata.activity.exChangeType.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.exChangeType.ActivityExChangeTypeEnum;
import com.playerdata.activity.exChangeType.ActivityExchangeTypeMgr;
import com.rwproto.ActivityExchangeTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityExchangeTypeProto.ActivityCommonRspMsg;

public class ActivityExchangeTypeHandler {
	
	private static ActivityExchangeTypeHandler instance = new ActivityExchangeTypeHandler();
	
	public static ActivityExchangeTypeHandler getInstance(){
		return instance;
	}

	public ByteString takeGift(Player player, ActivityCommonReqMsg commonReq) {
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		String activityId = commonReq.getActivityId();
		String subItemId =  commonReq.getSubItemId();
	
		ActivityExChangeTypeEnum countType = ActivityExChangeTypeEnum.getById(activityId);
		
		boolean success = false;
		String tips = "没找到对应的活动";
		
		if(countType!=null){
			ActivityComResult result = ActivityExchangeTypeMgr.getInstance().takeGift(player, countType, subItemId);
			success = result.isSuccess();
			tips = result.getReason()+"";
		}
		response.setIsSuccess(success);
		response.setTipMsg(tips);
		return response.build().toByteString();
	}
	
//	public static void GmTakeGift(Player player,String[] strs){
//		ActivityExchangeTypeMgr.getInstance().takeGift(player, ActivityExChangeTypeEnum.getById(strs[0]), strs[1]);
//	}
	
	
	
}
