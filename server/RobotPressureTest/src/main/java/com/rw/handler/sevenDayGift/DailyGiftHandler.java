package com.rw.handler.sevenDayGift;


import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.CopyServiceProtos.ERequestType;
import com.rwproto.CopyServiceProtos.EResultType;
import com.rwproto.CopyServiceProtos.MsgCopyRequest;
import com.rwproto.CopyServiceProtos.MsgCopyResponse;
import com.rwproto.DailyGifProtos.DailyGifRequest;
import com.rwproto.DailyGifProtos.DailyGifResponse;
import com.rwproto.DailyGifProtos.EType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class DailyGiftHandler {
	private static  DailyGiftHandler handler = new DailyGiftHandler();
	private  DailyGiftDao dao = new DailyGiftDao();
	public static  DailyGiftHandler getHandler() {
		return handler;
	}
	
	/**七日获取未领取奖励*/
	public void getSevenDayGiftItem(Client client) {
		DailyGifRequest.Builder req = DailyGifRequest.newBuilder();
		req.setType(EType.InfoMsg);
		client.getMsgHandler().sendMsg(Command.MSG_DailyGif, req.build().toByteString(), new MsgReciver() {

			@Override
			public Command getCmd() {
				return Command.MSG_DailyGif;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {

					DailyGifResponse rsp = DailyGifResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("DailyGiftHandler[send] 转换响应消息为null");
						return false;
					}

					EType result =rsp.getType();
					if (!result.equals(EType.InfoMsg)) {
						RobotLog.fail("DailyGiftHandler[send] 服务器处理消息失败 " + result);
						return false;
					}
					dao.setCount(rsp.getCount());
					dao.setCounts(rsp.getGetCountList());
				
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("DailyGiftHandler[send] 失败", e);
					return false;
				}
				return true;
			}

		});
		
		
	}

	public boolean getSevenDayGift(Client client) {
		boolean istakeall = true;
		for(int i =1;i < dao.getCount()+1;i++){
			boolean ishastaken = false;
			for(int j = 0;j < dao.getCounts().size();j++){
				if(i == dao.getCounts().get(j)){
					ishastaken = true;
				}
			}
			if(ishastaken){
				continue;
			}
			DailyGifRequest.Builder req = DailyGifRequest.newBuilder();
			req.setType(EType.GetGif);
			req.setCount(i);
			boolean success =client.getMsgHandler().sendMsg(Command.MSG_DailyGif, req.build().toByteString(), new MsgReciver() {

				@Override
				public Command getCmd() {
					return Command.MSG_DailyGif;
				}

				@Override
				public boolean execute(Client client, Response response) {
					ByteString serializedContent = response.getSerializedContent();
					try {

						DailyGifResponse rsp = DailyGifResponse.parseFrom(serializedContent);
						if (rsp == null) {
							RobotLog.fail("DailyGiftHandler[send] 转换响应消息为null");
							return false;
						}

						EType result = rsp.getType();
						RobotLog.fail("DailyGiftHandler[send] 服务器处理消息结果 " + result);
						if (result != EType.GetGif) {
							RobotLog.fail("DailyGiftHandler[send] 服务器处理消息失败 " + result);
							return false;
						}
						
					} catch (InvalidProtocolBufferException e) {
						RobotLog.fail("DailyGiftHandler[send] 失败", e);
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
