package com.playerdata.activity.fortuneCatType.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.countType.ActivityCountTypeMgr;
import com.playerdata.activity.fortuneCatType.ActivityFortuneCatTypeMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.ActivityFortuneCatTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityFortuneCatTypeProto.ActivityCommonRspMsg;

public class ActivityFortuneCatTypeHandler {
	
	private static ActivityFortuneCatTypeHandler instance = new ActivityFortuneCatTypeHandler();
	
	public static ActivityFortuneCatTypeHandler getInstance(){
		return instance;
	}

	public ByteString getGold(Player player, ActivityCommonReqMsg commonReq) {
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
	
		
		boolean success = false;
		String tips = null;
		
		ActivityComResult result = ActivityFortuneCatTypeMgr.getInstance().getGold(player,response);
		success = result.isSuccess();
		tips = result.getReason()+"";
		
		

		
		response.setIsSuccess(success);
		response.setTipMsg(tips);
		return response.build().toByteString();
	}
	


}
