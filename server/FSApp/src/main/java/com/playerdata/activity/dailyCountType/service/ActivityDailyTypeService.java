package com.playerdata.activity.dailyCountType.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ActivityDailyTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityDailyTypeProto.RequestType;
import com.rwproto.RequestProtos.Request;

public class ActivityDailyTypeService implements
		FsService<ActivityCommonReqMsg, RequestType> {

	@Override
	public ByteString doTask(ActivityCommonReqMsg request, Player player) {
		// TODO Auto-generated method stub
		ActivityDailyTypeHandler handler = ActivityDailyTypeHandler
				.getInstance();

		ByteString byteString = null;
		try {
			RequestType reqType = request.getReqType();
			switch (reqType) {
			case TAKE_GIFT:// 获取奖励
				byteString = handler.takeGift(player, request);
				break;
			default:
				GameLog.error(LogModule.ComActivityDailyCount,
						player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
		} catch (Exception e) {
			GameLog.error(LogModule.ComActivityDailyCount, player.getUserId(),
					"出现了Exception异常", e);
		}
		return byteString;
	}

	@Override
	public ActivityCommonReqMsg parseMsg(Request request)
			throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		ActivityCommonReqMsg commonReq = ActivityCommonReqMsg.parseFrom(request
				.getBody().getSerializedContent());
		return commonReq;
	}

	@Override
	public RequestType getMsgType(ActivityCommonReqMsg request) {
		// TODO Auto-generated method stub
		return request.getReqType();
	}
}