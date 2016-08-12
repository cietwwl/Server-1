package com.playerdata.activity.dailyCharge.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeCfg;
import com.playerdata.activity.dailyCharge.cfg.ActivityDailyChargeCfgDAO;
import com.playerdata.activity.exChangeType.ActivityExChangeTypeEnum;
import com.playerdata.activity.exChangeType.ActivityExchangeTypeMgr;
import com.rwproto.ActivityDailyRechargeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityDailyRechargeProto.ActivityCommonRspMsg;

public class ActivityDailyRechargeTypeHandler {
	
	private static ActivityDailyRechargeTypeHandler instance = new ActivityDailyRechargeTypeHandler();
	
	public static ActivityDailyRechargeTypeHandler getInstance(){
		return instance;
	}

	public ByteString takeGift(Player player, ActivityCommonReqMsg commonReq) {
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		String activityId = commonReq.getActivityId();
		String subItemId =  commonReq.getSubItemId();
		ActivityDailyChargeCfg cfg = ActivityDailyChargeCfgDAO.getInstance().getCfgById(activityId);		
		boolean success = false;
		String tips = "没找到对应的活动";
		if(cfg!=null){
			ActivityExChangeTypeEnum countType = ActivityExChangeTypeEnum.getById(cfg.getEnumId());
			if(countType != null){
				ActivityComResult result = ActivityExchangeTypeMgr.getInstance().takeGift(player, countType, subItemId);
				success = result.isSuccess();
				tips = result.getReason()+"";
			}
		}
		response.setIsSuccess(success);
		response.setTipMsg(tips);
		return response.build().toByteString();
	}
}
