package com.playerdata.activity.limitHeroType.service;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ActivityLimitHeroTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityLimitHeroTypeProto.RequestType;
import com.rwproto.RequestProtos.Request;

public class ActivityLimitHeroTypeService implements FsService{

	@Override
	public ByteString doTask(Request request, Player player) {
		ActivityLimitHeroTypeHandler activityLimitHeroHandler = ActivityLimitHeroTypeHandler.getInstance();
		ByteString byteString = null;
		try {
			ActivityCommonReqMsg commonReq = ActivityCommonReqMsg.parseFrom(request.getBody().getSerializedContent());

			RequestType reqType = commonReq.getReqType();
			switch (reqType) {
			case GAMBLE:
				byteString = activityLimitHeroHandler.gamble(player, commonReq);
				break;
			case GET_REWARDS:
				byteString = activityLimitHeroHandler.getRewards(player, commonReq);
				break;
			case VIEW_RANK:
				byteString = activityLimitHeroHandler.viewRank(player, commonReq);
				break;
			default:
				GameLog.error(LogModule.ComActivityRedEnvelope, player.getUserId(), "接受到一个unknow的消息", null);
				break;
			}
		} catch (Exception e) {
			GameLog.error(LogModule.ComActivityRedEnvelope, player.getUserId(), "出现了exception异常", e);
		}
		
		return byteString;
	}
	
	
	
	
}
