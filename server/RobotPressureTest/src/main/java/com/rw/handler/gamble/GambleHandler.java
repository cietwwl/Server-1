package com.rw.handler.gamble;

import java.util.concurrent.ConcurrentHashMap;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.actionHelper.ActionEnum;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.RandomMethodIF;
import com.rwproto.GambleServiceProtos.EGambleRequestType;
import com.rwproto.GambleServiceProtos.EGambleResultType;
import com.rwproto.GambleServiceProtos.EGambleType;
import com.rwproto.GambleServiceProtos.ELotteryType;
import com.rwproto.GambleServiceProtos.GambleRequest;
import com.rwproto.GambleServiceProtos.GambleResponse;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class GambleHandler implements RandomMethodIF{

	private static ConcurrentHashMap<String, Integer> funcStageMap = new ConcurrentHashMap<String, Integer>();
	
	private static GambleHandler instance = new GambleHandler();
	public static GambleHandler instance(){
		return instance;
	}

	/**
	 * 初级单抽
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
	
	/**
	 * 初级单抽
	 * 
	 * @param serverId
	 * @param accountId
	 */
	public boolean buyByGold(Client client) {
		

		GambleRequest.Builder req = GambleRequest.newBuilder()
												.setRequestType(EGambleRequestType.GAMBLE)
												.setGambleType(EGambleType.MIDDLE)
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

	@Override
	public boolean executeMethod(Client client) {
		Integer stage = funcStageMap.get(client.getAccountId());
		if(null == stage){
			stage = new Integer(0);
			funcStageMap.put(client.getAccountId(), stage);
		}
		switch (stage) {
		case 0:
			funcStageMap.put(client.getAccountId(), 1);
			client.getRateHelper().addActionToQueue(ActionEnum.Gamble);
			return buy(client);
		case 1:
			funcStageMap.put(client.getAccountId(), 0);
			return buyByGold(client);
		default:
			return true;
		}
	}
}
