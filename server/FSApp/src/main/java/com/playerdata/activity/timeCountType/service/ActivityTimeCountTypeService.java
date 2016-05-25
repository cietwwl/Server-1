package com.playerdata.activity.timeCountType.service;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ActivityTimeCountTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityTimeCountTypeProto.RequestType;
import com.rwproto.RequestProtos.Request;


public class ActivityTimeCountTypeService implements FsService {


	@Override
	public ByteString doTask(Request request, Player player) {
		
		ActivityTimeCountTypeHandler handler = ActivityTimeCountTypeHandler.getInstance();
		
		ByteString byteString = null;
		try {
			ActivityCommonReqMsg commonReq = ActivityCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
			
			RequestType reqType = commonReq.getReqType();
			switch (reqType) {	
			case TAKE_GIFT:// 获取奖励
				byteString = handler.takeGift(player, commonReq);
				break;
			default:
				GameLog.error(LogModule.ComActivityTimeCount, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
			
		} catch (Exception e) {
			GameLog.error(LogModule.ComActivityTimeCount, player.getUserId(), "出现了Exception异常", e);
		}
		return byteString;
		
	}
}