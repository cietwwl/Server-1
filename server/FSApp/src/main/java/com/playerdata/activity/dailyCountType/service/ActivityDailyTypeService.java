package com.playerdata.activity.dailyCountType.service;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ActivityDailyTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityDailyTypeProto.RequestType;
import com.rwproto.RequestProtos.Request;


public class ActivityDailyTypeService implements FsService {


	@Override
	public ByteString doTask(Request request, Player player) {
		
		ActivityDailyTypeHandler handler = ActivityDailyTypeHandler.getInstance();
		
		ByteString byteString = null;
		try {
			ActivityCommonReqMsg commonReq = ActivityCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
			
			RequestType reqType = commonReq.getReqType();			
			switch (reqType) {	
			case TAKE_GIFT:// 获取奖励
				byteString = handler.takeGift(player, commonReq);
				break;
			default:
				GameLog.error(LogModule.ComActivityDailyCount, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
			
		} catch (Exception e) {
			GameLog.error(LogModule.ComActivityDailyCount, player.getUserId(), "出现了Exception异常", e);
		}
		return byteString;
		
	}
}