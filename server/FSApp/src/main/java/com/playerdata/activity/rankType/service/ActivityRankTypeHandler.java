package com.playerdata.activity.rankType.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.rankType.ActivityRankTypeEnum;
import com.playerdata.activity.rankType.ActivityRankTypeMgr;
import com.playerdata.activity.rankType.data.ActivityRankTypeEntry;
import com.playerdata.activity.rankType.data.ActivityRankTypeUserInfo;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwproto.ActivityRankTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityRankTypeProto.ActivityCommonRspMsg;

public class ActivityRankTypeHandler {
	
	private static ActivityRankTypeHandler instance = new ActivityRankTypeHandler();
	
	public static ActivityRankTypeHandler getInstance(){
		return instance;
	}

	public ByteString getRankInfo(Player player, ActivityCommonReqMsg commonReq) {
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		String activityId = commonReq.getActivityId();
		int offset = commonReq.getOffset();
		int limit =  commonReq.getLimit();
	
		ActivityRankTypeEnum rankType = ActivityRankTypeEnum.getById(activityId);
		
		boolean success = false;
		String tips = null;
		
		
		ActivityRankTypeUserInfo rankUserInfo = ActivityRankTypeMgr.getInstance().getUserInfo(player, rankType);
		if(rankUserInfo!=null){
			String userInfoJson = ClientDataSynMgr.toClientData(rankUserInfo);
			if(StringUtils.isNotBlank(userInfoJson)){
				response.setUserInfoJson(userInfoJson);
			}
		}
		
		List<ActivityRankTypeEntry> rankList = ActivityRankTypeMgr.getInstance().getRankList(rankType, offset, limit);
		if(!rankList.isEmpty()){
			for (ActivityRankTypeEntry rankTmp : rankList) {
				String entryJson = ClientDataSynMgr.toClientData(rankTmp);	
				if(StringUtils.isNotBlank(entryJson)){
					response.addRankEntryJson(entryJson);
				}
			}
			
		}	
		
		response.setIsSuccess(success);
		response.setTipMsg(tips);
		return response.build().toByteString();
	}
	


}
