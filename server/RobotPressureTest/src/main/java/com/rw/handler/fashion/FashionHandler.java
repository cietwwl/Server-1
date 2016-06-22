package com.rw.handler.fashion;

import com.google.protobuf.ByteString;
import com.rw.Client;
import com.rw.common.PrintMsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.ErrorService.ErrorType;
import com.rwproto.FashionServiceProtos.FashionEventType;
import com.rwproto.FashionServiceProtos.FashionRequest;
import com.rwproto.FashionServiceProtos.FashionResponse;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class FashionHandler {
	private static FashionHandler handler = new FashionHandler();
	private static final Command command = Command.MSG_FASHION;
	private static final String functionName = "时装模块";
	public static FashionHandler getInstance(){
		return handler;
	}
	
	public boolean processBuyFashion(Client client){
		FashionRequest.Builder  request = FashionRequest.newBuilder();
		request.setEventType(FashionEventType.buy);
		request.setFashionId(10002);
		request.setBuyRenewPlanId("10002_2");
		return client.getMsgHandler().sendMsg(Command.MSG_FASHION, request.build().toByteString(), new FashionMsgReceier(command, functionName, "购买时装"));
	}
	
	public boolean processBuyWing(Client client){
		FashionRequest.Builder  request = FashionRequest.newBuilder();
		request.setEventType(FashionEventType.buy);
		request.setFashionId(10007);
		request.setBuyRenewPlanId("10007_1");
		return client.getMsgHandler().sendMsg(Command.MSG_FASHION, request.build().toByteString(), new FashionMsgReceier(command, functionName, "购买翅膀"));
	}
	
	public boolean processBuyPet(Client client){
		FashionRequest.Builder  request = FashionRequest.newBuilder();
		request.setEventType(FashionEventType.buy);
		request.setFashionId(10004);
		request.setBuyRenewPlanId("10004_1");
		return client.getMsgHandler().sendMsg(Command.MSG_FASHION, request.build().toByteString(), new FashionMsgReceier(command, functionName, "购买宠物"));
	}
	
	public boolean processWearFashion(Client client){
		FashionRequest.Builder  request = FashionRequest.newBuilder();
		request.setEventType(FashionEventType.on);
		request.setFashionId(10002);
		return client.getMsgHandler().sendMsg(Command.MSG_FASHION, request.build().toByteString(), new FashionMsgReceier(command, functionName, "穿时装"));
	}
	
	public boolean processWearWing(Client client){
		FashionRequest.Builder  request = FashionRequest.newBuilder();
		request.setEventType(FashionEventType.on);
		request.setFashionId(10007);
		return client.getMsgHandler().sendMsg(Command.MSG_FASHION, request.build().toByteString(), new FashionMsgReceier(command, functionName, "穿翅膀"));
	}
	
	public void processWearPet(Client client){
		FashionRequest.Builder  request = FashionRequest.newBuilder();
		request.setEventType(FashionEventType.on);
		request.setFashionId(10004);
		client.getMsgHandler().sendMsg(Command.MSG_FASHION, request.build().toByteString(), new FashionMsgReceier(command, functionName, "穿宠物"));
	}
	
	private class FashionMsgReceier extends PrintMsgReciver{

		public FashionMsgReceier(Command command, String functionName, String protoType) {
			super(command, functionName, protoType);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean execute(Client client, Response response) {
			// TODO Auto-generated method stub
			ByteString bs = response.getSerializedContent();
			try{
				FashionResponse resp = FashionResponse.parseFrom(bs);
				ErrorType error = resp.getError();
				switch (error) {
				case SUCCESS:
					RobotLog.info(parseFunctionDesc() + "成功");
					return true;
				default:
					throw new Exception(resp.getTips());
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
