package com.playerdata.activity.fortuneCatType.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ActivityFortuneCatTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityFortuneCatTypeProto.RequestType;
import com.rwproto.RequestProtos.Request;

public class ActivityFortuneCatTypeService implements FsService<ActivityCommonReqMsg, RequestType> {

	@Override
	public ByteString doTask(ActivityCommonReqMsg request, Player player) {
		// TODO Auto-generated method stub
		ActivityFortuneCatTypeHandler handler = ActivityFortuneCatTypeHandler.getInstance();

		ByteString byteString = null;
		try {
			RequestType reqType = request.getReqType();
			switch (reqType) {
			case GET_GOLD:// 获取钻石
				byteString = handler.getGold(player, request);
				break;
			case VIEW_OTHER_PLAYER:
				byteString = handler.getRecord(player, request);
				break;
			default:
				GameLog.error(LogModule.ComActivityFortuneCat, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}

		} catch (Exception e) {
			GameLog.error(LogModule.ComActivityFortuneCat, player.getUserId(), "出现了Exception异常", e);
		}
		return byteString;
	}

	@Override
	public ActivityCommonReqMsg parseMsg(Request request) throws InvalidProtocolBufferException {
		// TODO Auto-generated method stub
		ActivityCommonReqMsg commonReq = ActivityCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
		return commonReq;
	}

	@Override
	public RequestType getMsgType(ActivityCommonReqMsg request) {
		// TODO Auto-generated method stub
		return request.getReqType();
	}
}