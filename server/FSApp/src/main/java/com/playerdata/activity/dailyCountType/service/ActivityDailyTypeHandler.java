package com.playerdata.activity.dailyCountType.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.activity.ActivityComResult;
import com.playerdata.activity.dailyCountType.ActivityDailyTypeMgr;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfg;
import com.playerdata.activity.dailyCountType.cfg.ActivityDailyTypeCfgDAO;
import com.rwproto.ActivityDailyTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityDailyTypeProto.ActivityCommonRspMsg;

public class ActivityDailyTypeHandler {

	private static ActivityDailyTypeHandler instance = new ActivityDailyTypeHandler();

	public static ActivityDailyTypeHandler getInstance() {
		return instance;
	}

	public ByteString takeGift(Player player, ActivityCommonReqMsg commonReq) {
		ActivityCommonRspMsg.Builder response = ActivityCommonRspMsg.newBuilder();
		response.setReqType(commonReq.getReqType());
		String activityId = commonReq.getActivityId();
		String subItemId = commonReq.getSubItemId();
		ActivityDailyTypeCfg cfg = ActivityDailyTypeCfgDAO.getInstance().getCfgById(activityId);
		boolean success = false;
		String tips = "没有可以领取的奖励，每日福利配置缺失";
		if (cfg != null) {
			ActivityComResult result = ActivityDailyTypeMgr.getInstance().takeGift(player, cfg.getEnumId(), subItemId);
			success = result.isSuccess();
			tips = result.getReason() + "";
			response.setIsSuccess(success);
			response.setTipMsg(tips);
		}
		return response.build().toByteString();
	}
}
