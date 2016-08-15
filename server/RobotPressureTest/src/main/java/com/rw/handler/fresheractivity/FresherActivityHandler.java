package com.rw.handler.fresheractivity;

import java.util.List;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;

import com.rwproto.FrshActProtos.FrshActRequest;
import com.rwproto.FrshActProtos.FrshActResponse;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class FresherActivityHandler {
	private static FresherActivityHandler handler = new FresherActivityHandler();
	private static final Command command = Command.MSG_FRSH_ACT;
	private static final String functionName = "封神之路";
	public static FresherActivityHandler getInstance(){
		return handler;
	}
	
	public boolean testTakeFresherActivityRewards(Client client){
		List<Integer> giftIdList = client.getFresherActivityHolder().getGiftIdList();
		boolean isTakenAllGift = true;
		for(int i = 0;i< giftIdList.size();i++){
			FrshActRequest.Builder req = FrshActRequest.newBuilder();
			req.setCfgId(giftIdList.get(i));
			boolean success = client.getMsgHandler().sendMsg(command, req.build().toByteString(), new MsgReciver() {

				@Override
				public Command getCmd() {
					return command;
				}
				@Override
				public boolean execute(Client client, Response response) {
					ByteString serializedContent = response.getSerializedContent();
					try {

						FrshActResponse rsp = FrshActResponse.parseFrom(serializedContent);
						if (rsp == null) {
							RobotLog.fail("FresherActivityHandler[send] 转换响应消息为null");
							return false;
						}

						int result = rsp.getResult();
						if (result != 1) {
							RobotLog.fail("FresherActivityHandler[send] 服务器处理消息失败 " + result);
							return false;
						}

					} catch (InvalidProtocolBufferException e) {
						RobotLog.fail("FresherActivityHandler[send] 失败", e);
						return false;
					}
					return true;
				}

			});
			if(!success){
				isTakenAllGift = success;
			}
//			System.out.println("@@@@@@@@@@@@@@@@freshhandler.list =" + giftIdList.size() + " i =" + i+ " istaken =" + success + " cfgid="+giftIdList.get(i));
		}		
		return isTakenAllGift;
	}
	
	
}
