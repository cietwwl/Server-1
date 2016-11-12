package com.rw.handler.activity.daily;

import java.util.Map;
import java.util.Map.Entry;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.ActivityDailyTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityDailyTypeProto.ActivityCommonRspMsg;
import com.rwproto.ActivityDailyTypeProto.RequestType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;



public class ActivityDailyCountHandler {
	private static ActivityDailyCountHandler handler = new ActivityDailyCountHandler();

	public static ActivityDailyCountHandler getHandler() {
		return handler;
	}
	
	/**通用活动领取所有未领取奖励*/
	public boolean ActivityDailyCountTakeGift(Client client) {
		Map<String,String> giftList = client.getActivityDailyCountHolder().getGiftlist();
		
		
		boolean istakeall = true;
//		System.out.println("@@@@@@@@@@@ activitydaily,申请领取的size=" + giftList.size());
		for(Entry<String, String> entry:giftList.entrySet()){
			ActivityCommonReqMsg.Builder req = ActivityCommonReqMsg.newBuilder();
			req.setReqType(RequestType.TAKE_GIFT);
			req.setActivityId(entry.getValue());
			req.setSubItemId(entry.getKey());
//			System.out.println("@@@@@@@@@@@ activitydaily,申请领取" + entry.getKey());
			boolean success = client.getMsgHandler().sendMsg(Command.MSG_ACTIVITY_DAILY_TYPE, req.build().toByteString(), new MsgReciver() {

				@Override
				public Command getCmd() {
					return Command.MSG_ACTIVITY_DAILY_TYPE;
				}

				@Override
				public boolean execute(Client client, Response response) {
					ByteString serializedContent = response.getSerializedContent();
					try {

						ActivityCommonRspMsg rsp = ActivityCommonRspMsg.parseFrom(serializedContent);
						if (rsp == null) {
							RobotLog.fail("ActivityDailyCountHandler[send] 转换响应消息为null");
							return false;
						}

						boolean result = rsp.getIsSuccess();
						if (result != true) {
							RobotLog.fail("ActivityDailyCountHandler[send] 服务器处理消息失败 " + result);
							return false;
						}

					} catch (InvalidProtocolBufferException e) {
						RobotLog.fail("ActivityDailyCountHandler[send] 失败", e);
						return false;
					}
					return true;
				}

			});
//			System.out.println("@@@@@@@@@@@ activitydaily,申请领取是否成功" + success);
			if(!success){
				istakeall = success;
			}			
		}		
		return istakeall;
	}
	
	
	
}
