package com.rw.handler.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.ActivityCountTypeProto.ActivityCommonReqMsg;
import com.rwproto.ActivityCountTypeProto.ActivityCommonRspMsg;
import com.rwproto.ActivityCountTypeProto.RequestType;
import com.rwproto.MainServiceProtos.EMainResultType;
import com.rwproto.MainServiceProtos.EMainServiceType;
import com.rwproto.MainServiceProtos.MsgMainRequest;
import com.rwproto.MainServiceProtos.MsgMainResponse;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;



public class ActivityCountHandler {
	private static ActivityCountHandler handler = new ActivityCountHandler();
	
	public static ActivityCountHandler getHandler() {
		return handler;
	}
	
	/**
	 * 获取所有可领
	 * 
	 * @param client
	 * @return
	 */
	public boolean ActivityCountTakeGift(Client client) {
		Map<String,String> giftList = client.getActivityCountHolder().getGiftlist();
		
		
		boolean istakeall = true;
//		System.out.println("@@@@@@@@@@@ activity,申请领取的size=" + giftList.size());
		for(Entry<String, String> entry:giftList.entrySet()){
			ActivityCommonReqMsg.Builder req = ActivityCommonReqMsg.newBuilder();
			req.setReqType(RequestType.TAKE_GIFT);
			req.setActivityId(entry.getValue());
			req.setSubItemId(entry.getKey());
//			System.out.println("@@@@@@@@@@@ activity,申请领取" + entry.getKey());
			boolean success = client.getMsgHandler().sendMsg(Command.MSG_ACTIVITY_COUNTTYPE, req.build().toByteString(), new MsgReciver() {

				@Override
				public Command getCmd() {
					return Command.MSG_ACTIVITY_COUNTTYPE;
				}

				@Override
				public boolean execute(Client client, Response response) {
					ByteString serializedContent = response.getSerializedContent();
					try {

						ActivityCommonRspMsg rsp = ActivityCommonRspMsg.parseFrom(serializedContent);
						if (rsp == null) {
							RobotLog.fail("ActivityCountHandler[send] 转换响应消息为null");
							return false;
						}

						boolean result = rsp.getIsSuccess();
						if (result != true) {
							RobotLog.fail("ActivityCountHandler[send] 服务器处理消息失败 " + result);
							return false;
						}

					} catch (InvalidProtocolBufferException e) {
						RobotLog.fail("ActivityCountHandler[send] 失败", e);
						return false;
					}
					return true;
				}

			});
			if(!success){
				istakeall = success;
			}			
		}		
		return istakeall;
	}
	
	
	
}


