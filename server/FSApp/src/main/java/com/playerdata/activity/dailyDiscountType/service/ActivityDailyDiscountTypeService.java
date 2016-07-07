package com.playerdata.activity.dailyDiscountType.service;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.rw.service.FsService;
import com.rwproto.ActivityDailyDiscountTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityDailyDiscountTypeProto.RequestType;
import com.rwproto.RequestProtos.Request;





public class ActivityDailyDiscountTypeService implements FsService {


	@Override
	public ByteString doTask(Request request, Player player) {
		
		ActivityDailyDiscountTypeHandler handler = ActivityDailyDiscountTypeHandler.getInstance();
		
		ByteString byteString = null;
		try {
			ActivityCommonReqMsg commonReq = ActivityCommonReqMsg.parseFrom(request.getBody().getSerializedContent());
//			
			RequestType reqType = commonReq.getReqType();			
			switch (reqType) {	
			case BUY_ITEM:// 获取奖励
				byteString = handler.buyItem(player, commonReq);
				break;
			default:
				GameLog.error(LogModule.ComActivityDailyDisCount, player.getUserId(), "接收到了一个Unknown的消息，无法处理", null);
				break;
			}
			
		} catch (Exception e) {
			GameLog.error(LogModule.ComActivityDailyDisCount, player.getUserId(), "出现了Exception异常", e);
		}
		return byteString;
		
	}


}