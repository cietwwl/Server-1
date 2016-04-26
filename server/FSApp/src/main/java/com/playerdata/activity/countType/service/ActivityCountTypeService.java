package com.playerdata.activity.countType.service;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ActivityCountTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityCountTypeProto.RequestType;
import com.rwproto.RequestProtos.Request;


public class ActivityCountTypeService implements FsService {


	@Override
	public ByteString doTask(Request request, Player player) {
		
		ActivityCountTypeHandler handler = ActivityCountTypeHandler.getInstance();
		
		ByteString byteString = null;
		try {
			ActivityCommonReqMsg commonReq = ActivityCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
			
			RequestType reqType = commonReq.getReqType();
			switch (reqType) {	
			case TAKE_GIFT:// 获取奖励
				byteString = handler.takeGift(player, commonReq);
				break;
			default:
				GameLog.error(LogModule.ComActivityCount, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
			
		} catch (Exception e) {
			GameLog.error(LogModule.ComActivityCount, player.getUserId(), "出现了Exception异常", e);
		}
		return byteString;
		
	}
}