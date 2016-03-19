package com.rw.handler.gamble;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.GambleServiceProtos.EGambleRequestType;
import com.rwproto.GambleServiceProtos.EGambleResultType;
import com.rwproto.GambleServiceProtos.EGambleType;
import com.rwproto.GambleServiceProtos.ELotteryType;
import com.rwproto.GambleServiceProtos.GambleRequest;
import com.rwproto.GambleServiceProtos.GambleResponse;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class GambleHandler {

	
	private static GambleHandler instance = new GambleHandler();
	public static GambleHandler instance(){
		return instance;
	}

	/**
	 * 创建角色
	 * 
	 * @param serverId
	 * @param accountId
	 */
	public boolean buy(Client client) {
		

		GambleRequest.Builder req = GambleRequest.newBuilder()
												.setRequestType(EGambleRequestType.GAMBLE)
												.setGambleType(EGambleType.PRIMARY)
												.setLotteryType(ELotteryType.ONE);
		
		
		boolean success = client.getMsgHandler().sendMsg( Command.MSG_GAMBLE, req.build().toByteString(), new MsgReciver() {
			
			@Override
			public Command getCmd() {
				return Command.MSG_GAMBLE;
			}
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					
					GambleResponse rsp = GambleResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("GambleHandler[buy] 转换响应消息为null");
						return false;
					}

					EGambleResultType result = rsp.getResultType();
					if (result == EGambleResultType.SUCCESS) {
						RobotLog.info("GambleHandler[buy] 购买成功");
						return true;
					}else{
						RobotLog.fail("GambleHandler[buy] 服务器处理消息失败:"+result);
						return false;
						
					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("StoreHandler[buy] 失败", e);
					return false;
				}
			}

		});
		return success;
	}

}
