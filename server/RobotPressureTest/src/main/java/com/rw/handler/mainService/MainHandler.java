package com.rw.handler.mainService;

import java.net.Authenticator.RequestorType;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.MainServiceProtos.EMainResultType;
import com.rwproto.MainServiceProtos.EMainServiceType;
import com.rwproto.MainServiceProtos.MsgMainRequest;
import com.rwproto.MainServiceProtos.MsgMainResponse;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;


public class MainHandler {
	private static MainHandler handler = new MainHandler();

	public static MainHandler getHandler() {
		return handler;
	}
	
	/**
	 * 买体
	 * 
	 * @param client
	 * @return
	 */
	public boolean buyTower(Client client) {
		MsgMainRequest.Builder req = MsgMainRequest.newBuilder();
		req.setRequestType(EMainServiceType.BUY_POWER);
//		req.setWorshipCareer
		

		boolean success = client.getMsgHandler().sendMsg(Command.MSG_MainService, req.build().toByteString(), new MsgReciver() {

			@Override
			public Command getCmd() {
				return Command.MSG_MainService;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {

					MsgMainResponse rsp = MsgMainResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("MainHandler[send] 转换响应消息为null");
						return false;
					}

					EMainResultType result = rsp.getEMainResultType();
					if (result != EMainResultType.SUCCESS) {
						RobotLog.fail("MainHandler[send] 服务器处理消息失败 " + result);
						return false;
					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("MainHandler[send] 失败", e);
					return false;
				}
				return true;
			}

		});
		return success;
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
