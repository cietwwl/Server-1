package com.playerdata.activity.exChangeType.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.exChangeType.ActivityExchangeTypeMgr;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfg;
import com.playerdata.activity.exChangeType.cfg.ActivityExchangeTypeCfgDAO;
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
		ActivityExchangeTypeCfg cfg = ActivityExchangeTypeCfgDAO.getInstance().getCfgById(activityId);		
		boolean success = false;
		String tips = "没找到对应的活动";
		if(cfg!=null){
			ActivityComResult result = ActivityExchangeTypeMgr.getInstance().takeGift(player, String.valueOf(cfg.getId()), subItemId);
			success = result.isSuccess();
			tips = result.getReason()+"";
		}
		response.setIsSuccess(success);
		response.setTipMsg(tips);
		return response.build().toByteString();
	}
}
