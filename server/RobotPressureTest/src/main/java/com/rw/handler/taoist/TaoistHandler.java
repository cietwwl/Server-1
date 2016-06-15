package com.rw.handler.taoist;


import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;

import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;
import com.rwproto.TaoistMagicProtos.ErrorCode_Taoist;
import com.rwproto.TaoistMagicProtos.TaoistRequest;
import com.rwproto.TaoistMagicProtos.TaoistRequestType;
import com.rwproto.TaoistMagicProtos.TaoistResponse;




public class TaoistHandler {
	private static  TaoistHandler handler = new TaoistHandler();
	public static  TaoistHandler getHandler() {
		return handler;
	}
	
	public void getTaoistData(Client client){
		TaoistRequest.Builder req = TaoistRequest.newBuilder();		
		req.setReqType(TaoistRequestType.getTaoistData);
		
		client.getMsgHandler().sendMsg(Command.MSG_TAOIST, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_TAOIST;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {

					TaoistResponse rsp = TaoistResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("TaoistHandler[send] 转换响应消息为null");
						return false;
					}

					ErrorCode_Taoist result =rsp.getErrorCode();
					if (!result.equals(ErrorCode_Taoist.Success)) {
						RobotLog.fail("TaoistHandler[send] 服务器处理消息失败 " + result);
						return false;
					}
				
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("TaoistHandler[send] 失败", e);
					return false;
				}
				
				
				
				return true;
			}			
		});
	}
	
	
	
}
