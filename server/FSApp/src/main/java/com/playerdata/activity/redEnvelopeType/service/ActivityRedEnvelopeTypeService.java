package com.playerdata.activity.redEnvelopeType.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ActivityRedEnvelopeTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityRedEnvelopeTypeProto.RequestType;
import com.rwproto.RequestProtos.Request;

public class ActivityRedEnvelopeTypeService implements FsService<ActivityCommonReqMsg, RequestType> {

	@Override
	public ByteString doTask(ActivityCommonReqMsg request, Player player) {
		// TODO Auto-generated method stub
		ActivityRedEnvelopeTypeHandler activityRedEnvelopeTypeHandler = ActivityRedEnvelopeTypeHandler.getInstance();
		ByteString byteString = null;
		try {

			RequestType reqType = request.getReqType();
			switch (reqType) {
			case TAKE_GIFT:
				byteString = activityRedEnvelopeTypeHandler.takeGift(player, request);
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
