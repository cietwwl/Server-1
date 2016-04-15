package com.playerdata.activity.countType.service;

import com.common.playerFilter.FilterType;
import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.rwproto.ActivityCountTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityCountTypeProto.ActivityCommonRspMsg;

public class ActivityCountTypeHandler {
	
	private static ActivityCountTypeHandler instance = new ActivityCountTypeHandler();
	
	public static ActivityCountTypeHandler getInstance(){
		return instance;
	}

	public ByteString takeGift(Player player, ActivityCommonReqMsg commonReq) {
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		String activityId = "1";
		String subItemId = "0";
		if(commonReq != null){
			response.setReqType(commonReq.getReqType());
			activityId = commonReq.getActivityId();
			subItemId = commonReq.getSubItemId();
		}else{
//			response.setReqType(commonReq.getReqType());
		}
		ActivityCountTypeEnum countType = ActivityCountTypeEnum.valueOff(activityId);
		
		boolean success = false;
		String tips = null;
		
		if(countType!=null){
			ActivityComResult result = ActivityCountTypeMgr.getInstance().takeGift(player, countType, subItemId);
			success = result.isSuccess();
			tips = result.getReason()+"";
		}
		response.setIsSuccess(success);
		response.setTipMsg(tips);
		return response.build().toByteString();
	}


}
