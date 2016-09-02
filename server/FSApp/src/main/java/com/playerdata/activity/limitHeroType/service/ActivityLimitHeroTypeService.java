package com.playerdata.activity.limitHeroType.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ActivityLimitHeroTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityLimitHeroTypeProto.RequestType;
import com.rwproto.RequestProtos.Request;

public class ActivityLimitHeroTypeService implements FsService<ActivityCommonReqMsg, RequestType> {

	@Override
	public ByteString doTask(ActivityCommonReqMsg request, Player player) {
		// TODO Auto-generated method stub
		ActivityLimitHeroTypeHandler activityLimitHeroHandler = ActivityLimitHeroTypeHandler.getInstance();
		ByteString byteString = null;
		try {

			RequestType reqType = request.getReqType();
			switch (reqType) {
			case GAMBLE:
				byteString = activityLimitHeroHandler.gamble(player, request);
				break;
			case GET_REWARDS:
				byteString = activityLimitHeroHandler.getRewards(player, request);
				break;
			case VIEW_RANK:
				byteString = activityLimitHeroHandler.viewRank(player, request);
				break;
			default:
				GameLog.error(LogModule.ComActivityLimitHero, player.getUserId(), "接受到一个unknow的消息", null);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			GameLog.error(LogModule.ComActivityLimitHero, player.getUserId(), "出现了exception异常", e);
		}

		return byteString;
	}

	@Override
	public ActivityCommonReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		ActivityCommonReqMsg msg = ActivityCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
		return msg;
	}

	@Override
	public RequestType getMsgType(ActivityCommonReqMsg request) {
		// TODO Auto-generated method stub
		return request.getReqType();
	}

}
