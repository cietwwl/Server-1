package com.playerdata.activity.VitalityType.service;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ActivityVitalityTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityVitalityTypeProto.RequestType;
import com.rwproto.RequestProtos.Request;

public class ActivityVitalityTypeService implements FsService{

	@Override
	public ByteString doTask(Request request, Player player) {
		
		ActivityVitalityTypeHandler handler = ActivityVitalityTypeHandler.getInstance();
		
		ByteString byteString = null;
		
		try {
			ActivityCommonReqMsg commonReq = ActivityCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
			
			RequestType reqType = commonReq.getReqType();
			switch (reqType) {	
			case TAKE_GIFT:// 获取奖励
				byteString = handler.takeGift(player, commonReq);
				break;
			case OPEN_BOX:
				byteString = handler.openBox(player, commonReq);
				break;
			default:
				GameLog.error(LogModule.ComActivityVitality, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
			
		} catch (Exception e) {
			GameLog.error(LogModule.ComActivityVitality, player.getUserId(), "出现了Exception异常", e);
		}
		
		
		
		return byteString;
	}

	
	
	
	
}
