package com.playerdata.activity.evilBaoArrive.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.evilBaoArrive.EvilBaoArriveMgr;
import com.playerdata.activity.evilBaoArrive.cfg.EvilBaoArriveCfg;
import com.playerdata.activity.evilBaoArrive.cfg.EvilBaoArriveCfgDAO;
import com.rwproto.ActivityEvilBaoArriveProto.ActivityCommonReqMsg;
import com.rwproto.ActivityEvilBaoArriveProto.ActivityCommonRspMsg;


public class ActivityEvilBaoArriveHandler {
	
	private static ActivityEvilBaoArriveHandler instance = new ActivityEvilBaoArriveHandler();
	
	public static ActivityEvilBaoArriveHandler getInstance(){
		return instance;
	}

	public ByteString takeGift(Player player, ActivityCommonReqMsg commonReq) {
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		String activityId = commonReq.getActivityId();
		String subItemId =  commonReq.getSubItemId();
		EvilBaoArriveCfg cfg = EvilBaoArriveCfgDAO.getInstance().getCfgById(activityId);		
		boolean success = false;
		String tips = "没找到对应的活动";
		if(cfg!=null){
			ActivityComResult result = EvilBaoArriveMgr.getInstance().takeGift(player, activityId, subItemId);
			success = result.isSuccess();
			tips = result.getReason();
		}
		response.setIsSuccess(success);
		response.setTipMsg(tips);
		return response.build().toByteString();
	}
}
