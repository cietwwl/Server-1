package com.playerdata.activity.redEnvelopeType.service;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ActivityRedEnvelopeTypeProto.*;
import com.rwproto.RequestProtos.Request;

public class ActivityRedEnvelopeTypeService implements FsService{

	@Override
	public ByteString doTask(Request request, Player player) {
		ActivityRedEnvelopeTypeHandler activityRedEnvelopeTypeHandler = ActivityRedEnvelopeTypeHandler.getInstance();
		ByteString byteString = null;
		try {
			ActivityCommonReqMsg commonReq = ActivityCommonReqMsg.parseFrom(request.getBody().getSerializedContent());

			RequestType reqType = commonReq.getReqType();
			switch (reqType) {
			case TAKE_GIFT:
				byteString = activityRedEnvelopeTypeHandler.takeGift(player, commonReq);
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
