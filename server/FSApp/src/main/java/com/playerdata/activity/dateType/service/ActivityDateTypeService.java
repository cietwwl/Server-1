package com.playerdata.activity.dateType.service;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ActivityDateTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityDateTypeProto.RequestType;
import com.rwproto.RequestProtos.Request;


public class ActivityDateTypeService implements FsService {


	@Override
	public ByteString doTask(Request request, Player player) {
		
		ActivityDateTypeHandler handler = ActivityDateTypeHandler.getInstance();
		
		ByteString byteString = null;
		try {
			ActivityCommonReqMsg commonReq = ActivityCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
			
			RequestType reqType = commonReq.getReqType();
			switch (reqType) {	
			case TAKE_DAY_GIFT:// 获取奖励
				byteString = handler.takeGift(player, commonReq);
				break;
			case TAKE_BIG_GIFT:// 获取奖励
				byteString = handler.takeBigGift(player, commonReq);
				break;
			default:
				GameLog.error(LogModule.ComActivityDate, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
			
		} catch (Exception e) {
			GameLog.error(LogModule.ComActivityDate, player.getUserId(), "出现了Exception异常", e);
		}
		return byteString;
		
	}
}