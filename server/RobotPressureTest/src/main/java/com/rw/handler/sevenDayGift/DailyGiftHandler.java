package com.rw.handler.sevenDayGift;

import java.util.Map;
import java.util.Map.Entry;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.activity.ActivityCountHandler;
import com.rwproto.ActivityDailyTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityDailyTypeProto.ActivityCommonRspMsg;
import com.rwproto.ActivityDailyTypeProto.RequestType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class DailyGiftHandler {
	private static DailyGiftHandler handler = new DailyGiftHandler();
	
	public static DailyGiftHandler getHandler() {
		return handler;
	}
	
	/**通用活动领取所有未领取奖励*/
	public void getSevenDayGiftItem(Client client) {
		
		
		
		
	}
	
	
	
}
