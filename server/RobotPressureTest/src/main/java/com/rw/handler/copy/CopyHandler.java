package com.rw.handler.copy;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.Test;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.CopyServiceProtos.EBattleStatus;
import com.rwproto.CopyServiceProtos.ERequestType;
import com.rwproto.CopyServiceProtos.EResultType;
import com.rwproto.CopyServiceProtos.MsgCopyRequest;
import com.rwproto.CopyServiceProtos.MsgCopyResponse;
import com.rwproto.MainServiceProtos.EMainResultType;
import com.rwproto.MainServiceProtos.EMainServiceType;
import com.rwproto.MainServiceProtos.MsgMainRequest;
import com.rwproto.MainServiceProtos.MsgMainResponse;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;



public class CopyHandler {
	private static CopyHandler handler = new CopyHandler();
	public static int levelId = 0;
	private static final int[] warFareCopyId = {150041,150042,150043,150044,150045};
	private static final int towCopyId = 190002;
	public static CopyHandler getHandler() {
		return handler;
	}
	
	public boolean battleItemsBack(Client client,int copytype) {
		MsgCopyRequest.Builder req = MsgCopyRequest.newBuilder();
		req.setRequestType(ERequestType.BATTLE_ITEMS_BACK);
		req.getTagBattleDataBuilder().setLevelId(getRadomLevelIdByCopytype(copytype));
		req.getTagBattleDataBuilder().setBattleClearingTime(1);
		req.setLevelId(this.levelId);
		System.out.println("@@@战斗id"+ this.levelId);
		
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_CopyService, req.build().toByteString(), new MsgReciver() {

			@Override
			public Command getCmd() {
				return Command.MSG_CopyService;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {

					MsgCopyResponse rsp = MsgCopyResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("CopyHandler[send] 转换响应消息为null");
						return false;
					}

					EResultType result = rsp.getEResultType();
					if (result != EResultType.ITEM_BACK) {
						RobotLog.fail("CopyHandler[send] 服务器处理消息失败 " + result);
						return false;
					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("CopyHandler[send] 失败", e);
					return false;
				}
				return true;
			}

		});
		return success;		
	}

	public boolean battleClear(Client client, int copyTypeWarfare,EBattleStatus iswin) {
		MsgCopyRequest.Builder req = MsgCopyRequest.newBuilder();
		req.setRequestType(ERequestType.BATTLE_CLEARING);
		req.getTagBattleDataBuilder().setLevelId(this.levelId);
		req.getTagBattleDataBuilder().setFightTime(0);
		req.getTagBattleDataBuilder().setBattleClearingTime(1);
		req.setLevelId(this.levelId);
//		req.getTagBattleDataBuilder().addHeroId("");
		req.getTagBattleDataBuilder().setFightResult(iswin);
		
		
		
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_CopyService, req.build().toByteString(), new MsgReciver() {

			@Override
			public Command getCmd() {
				return Command.MSG_CopyService;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {

					MsgCopyResponse rsp = MsgCopyResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("CopyHandler[send] 转换响应消息为null");
						return false;
					}

					EResultType result = rsp.getEResultType();
					if (result != EResultType.BATTLE_CLEAR) {
						RobotLog.fail("CopyHandler[send] 服务器处理消息失败 " + result);
						return false;
					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("CopyHandler[send] 失败", e);
					return false;
				}
				return true;
			}

		});
		return success;		
		
		
		
	}	
	
	private int getRadomLevelIdByCopytype(int copyType){
		int levelId = 0;
		if(copyType == CopyType.COPY_TYPE_WARFARE){
			int randomNum = Test.random.nextInt(5);
			
			levelId = warFareCopyId[randomNum];
			System.out.println("copyhandler,随机数 levelid =" + levelId + " num="+randomNum);
		}else if(copyType ==CopyType.COPY_TYPE_TOWER){
			levelId = towCopyId;			
		}		
		this.levelId = levelId;
		return levelId;
	}
	
}
