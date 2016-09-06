package com.bm.worldBoss.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.countType.ActivityCountTypeEnum;
import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfg;
import com.playerdata.activity.countType.cfg.ActivityCountTypeCfgDAO;
import com.rwproto.ActivityCountTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityCountTypeProto.ActivityCommonRspMsg;

public class WBHandler {
	
	private static WBHandler instance = new WBHandler();
	
	public static WBHandler getInstance(){
		return instance;
	}

	public ByteString takeGift(Player player, ActivityCommonReqMsg commonReq) {
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		String activityId = commonReq.getActivityId();
		String subItemId =  commonReq.getSubItemId();
		ActivityCountTypeCfg cfg = ActivityCountTypeCfgDAO.getInstance().getCfgById(activityId); 
		ActivityCountTypeEnum countType = null;
		if(cfg != null){
			countType = ActivityCountTypeEnum.getById(cfg.getEnumId());
		}
		
		
		boolean success = false;
		String tips = null;
		
		if(countType!=null){
			ActivityComResult result = ActivityCountTypeMgr.getInstance().takeGift(player, countType, subItemId);
			success = result.isSuccess();
			tips = result.getReason()+"";
			response.setIsSuccess(success);
			response.setTipMsg(tips);
		}
		
		return response.build().toByteString();
	}


}
