package com.rw.handler.chat;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.GMServiceProtos;
import com.rwproto.GMServiceProtos.MsgGMRequest;
import com.rwproto.GMServiceProtos.MsgGMResponse;
import com.rwproto.GMServiceProtos.eGMResultType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;


public class GmHandler {
	
	private static GmHandler instance = new GmHandler();
	public static GmHandler instance(){
		return instance;
	}

	/**
	 * 创建角色
	 * 
	 * @param serverId
	 * @param accountId
	 */
	public boolean send(Client client, String message ) {
		
		MsgGMRequest.Builder req = MsgGMRequest.newBuilder();
		req.setGMType(GMServiceProtos.eGMType.GM_COMMAND);
		req.setContent(message);
		
		
		boolean success = client.getMsgHandler().sendMsg( Command.MSG_GM, req.build().toByteString(), new MsgReciver() {
			
			@Override
			public Command getCmd() {
				return Command.MSG_GM;
			}
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					
					MsgGMResponse rsp = MsgGMResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("ChatHandler[sendGm] 转换响应消息为null");
						return false;
					}

					eGMResultType result = rsp.getEGMResultType();
					if (result == eGMResultType.FAIL) {
						RobotLog.fail("ChatHandler[sendGm] 服务器处理消息失败:"+result);
						return false;
					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("ChatHandler[sendGm] 失败", e);
					return false;
				}
				return true;
			}

		});
		return success;
		
		
	}

	

}
