package com.rw.handler.mainService;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.PrintMsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.MainServiceProtos.EMainResultType;
import com.rwproto.MainServiceProtos.EMainServiceType;
import com.rwproto.MainServiceProtos.MsgMainRequest;
import com.rwproto.MainServiceProtos.MsgMainResponse;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;


public class MainHandler {
	private static MainHandler handler = new MainHandler();
	private static final Command command = Command.MSG_MainService;
	private static final String functionName = "主城模块";

	public static MainHandler getHandler() {
		return handler;
	}
	
	public boolean buyCoin(Client client){
		MsgMainRequest.Builder req = MsgMainRequest.newBuilder();
		req.setRequestType(EMainServiceType.BUY_COIN);
		return client.getMsgHandler().sendMsg(Command.MSG_MainService, req.build().toByteString(), new MainMsgReceiver(command, functionName, "购买金币"));
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
	
	
	private class MainMsgReceiver extends PrintMsgReciver{

		public MainMsgReceiver(Command command, String functionName, String protoType) {
			super(command, functionName, protoType);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean execute(Client client, Response response) {
			// TODO Auto-generated method stub
			ByteString bs = response.getSerializedContent();
			try{
				MsgMainResponse resp = MsgMainResponse.parseFrom(bs);
				EMainResultType eMainResultType = resp.getEMainResultType();
				switch (eMainResultType) {
				case SUCCESS:
					RobotLog.info(parseFunctionDesc() + "成功");
					break;
				case LOW_VIP:
					throw new Exception("VIP 等级不足");
				case NOT_ENOUGH_GOLD:
					throw new Exception("砖石不足");
				default:
					throw new Exception("系统繁忙");
				}
				
			}catch(Exception ex){
				RobotLog.fail(parseFunctionDesc() + "失败", ex);
			}
			return false;
		}
		
		private String parseFunctionDesc() {
			return functionName + "[" + protoType + "] ";
		}
		
	}
	
	
	
	
	
	
	
	
	
}
