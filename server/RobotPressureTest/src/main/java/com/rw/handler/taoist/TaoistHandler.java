package com.rw.handler.taoist;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DebugGraphics;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;
import com.rwproto.TaoistMagicProtos.ErrorCode_Taoist;
import com.rwproto.TaoistMagicProtos.TaoistInfo;
import com.rwproto.TaoistMagicProtos.TaoistRequest;
import com.rwproto.TaoistMagicProtos.TaoistRequestType;
import com.rwproto.TaoistMagicProtos.TaoistResponse;




public class TaoistHandler {
	private static  TaoistHandler handler = new TaoistHandler();

	public static  TaoistHandler getHandler() {
		return handler;
	}
	
	public boolean updateTaoist(Client client){
		boolean result = getTaoistInfo(client);
		if(result){
			result = upTaoist(client);
		}else{
			RobotLog.fail("获取道术信息失败");
			return true;
		}
		if(result){
			RobotLog.fail("升级道术成功");
		}else{
			RobotLog.fail("升级道术失败----------------------");
		}
		return result;
	}
	
	public boolean getTaoistInfo(Client client){
		
		TaoistRequest.Builder req = TaoistRequest.newBuilder();		
		req.setReqType(TaoistRequestType.getTaoistData);
		
		boolean success =client.getMsgHandler().sendMsg(Command.MSG_TAOIST, req.build().toByteString(), new MsgReciver() {
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
						RobotLog.fail("userId:" + client.getUserId()+"|TaoistHandler[send] 服务器处理消息失败 " + result+",msg:"+rsp.getResultTip());
						return false;
					}
					
					List<TaoistInfo> list = rsp.getTaoistInfoListList();
					TaoistDataHolder taoistDataHolder = client.getTaoistDataHolder();
					taoistDataHolder.setTaoistInfoListList(list);
				
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("TaoistHandler[send] 失败", e);
					return false;
				}
				
				
				
				return true;
			}			
		});
		
		
		
		
		return success;
	}
	
	public boolean upTaoist(Client client) {
		int tmp = client.getTaoistDataHolder().getTaoistId(client);
		if(tmp == 0){
			RobotLog.fail("TaoistHandler[send] 找不到可以升级的道术");
			return true;
		}
		if(!client.getTaoistDataHolder().checkTaoistUpdate(tmp, client)){
			return true;
		}
		
		
		TaoistRequest.Builder req = TaoistRequest.newBuilder();		
		req.setReqType(TaoistRequestType.updateTaoist);
		req.setTaoistId(tmp);
		req.setUpgradeCount(1);
		System.out.println("@@@@@@@@@taoist" + tmp);
		
		boolean success =client.getMsgHandler().sendMsg(Command.MSG_TAOIST, req.build().toByteString(), new MsgReciver() {
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
						RobotLog.fail("userId:" + client.getUserId()+"|TaoistHandler[send] 服务器处理消息失败 " + result+",msg:"+rsp.getResultTip());
						return false;
					}
				
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("TaoistHandler[send] 失败", e);
					return false;
				}
				
				
				
				return true;
			}			
		});
		
		
		
		
		return success;
		
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
						RobotLog.fail("userId:" + client.getUserId()+"|TaoistHandler[send] 服务器处理消息失败 " + result+",msg:"+rsp.getResultTip());
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
