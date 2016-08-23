package com.playerdata.activity.fortuneCatType.service;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ActivityFortuneCatTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityFortuneCatTypeProto.RequestType;
import com.rwproto.RequestProtos.Request;


public class ActivityFortuneCatTypeService implements FsService {


	@Override
	public ByteString doTask(Request request, Player player) {
		
		ActivityFortuneCatTypeHandler handler = ActivityFortuneCatTypeHandler.getInstance();
		
		ByteString byteString = null;
		try {
			ActivityCommonReqMsg commonReq = ActivityCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
			
			RequestType reqType = commonReq.getReqType();
			switch (reqType) {	
			case GET_GOLD:// 获取钻石
				byteString = handler.getGold(player, commonReq);
				break;
			case VIEW_OTHER_PLAYER:
				byteString = handler.getRecord(player, commonReq);
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
}