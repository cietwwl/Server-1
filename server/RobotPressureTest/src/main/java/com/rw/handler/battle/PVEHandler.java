package com.rw.handler.battle;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rwproto.CopyServiceProtos.EBattleStatus;
import com.rwproto.CopyServiceProtos.ERequestType;
import com.rwproto.CopyServiceProtos.EResultType;
import com.rwproto.CopyServiceProtos.MsgCopyRequest;
import com.rwproto.CopyServiceProtos.MsgCopyResponse;
import com.rwproto.CopyServiceProtos.TagBattleData;
import com.rwproto.CopyServiceProtos.TagBattleData.Builder;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class PVEHandler {

	
	private static PVEHandler instance = new PVEHandler();
	public static PVEHandler instance(){
		return instance;
	}

	/**
	 * 开始战斗
	 * 
	 * @param serverId
	 * @param accountId
	 */
	public boolean before(Client client) {
		
		MsgCopyRequest.Builder req = MsgCopyRequest.newBuilder();
		req.setRequestType(ERequestType.BATTLE_ITEMS_BACK);
//		
//		Builder tagBattleData = TagBattleData.newBuilder().setBattleClearingTime(1);
//		req.setTagBattleData(tagBattleData);
		req.setLevelId(110101);
		
		
		boolean success = client.getMsgHandler().sendMsg( Command.MSG_CopyService, req.build().toByteString(), new MsgReciver() {
			
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
						RobotLog.fail("PVEHandler[before] 转换响应消息为null");
						return false;
					}
					
					EResultType result = rsp.getEResultType();
					if (result == EResultType.ITEM_BACK) {
						RobotLog.info("PVEHandler[before] 成功");
						return true;
					}else{
						RobotLog.fail("PVEHandler[before] 服务器处理消息失败");
						return false;
						
					}
					
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("PVEHandler[before] 失败", e);
					return false;
				}
			}
			
		});
		return success;
	}
	/**
	 * 战斗结算
	 * 
	 * @param serverId
	 * @param accountId
	 */
	public boolean after(Client client) {

        MsgCopyRequest.Builder req = MsgCopyRequest.newBuilder();
        req.setRequestType(ERequestType.BATTLE_CLEARING);

        Builder tagBattleData = TagBattleData.newBuilder().setLevelId(110101).setStarLevel(2).setBattleStatus(EBattleStatus.WIN).setBattleClearingTime(1);
        req.setTagBattleData(tagBattleData);
       
		
		boolean success = client.getMsgHandler().sendMsg( Command.MSG_CopyService, req.build().toByteString(), new MsgReciver() {
			
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
						RobotLog.fail("PVEHandler[after] 转换响应消息为null");
						return false;
					}

					EResultType result = rsp.getEResultType();
					if (result == EResultType.BATTLE_CLEAR) {
						RobotLog.info("PVEHandler[after] 成功");
						return true;
					}else{
						RobotLog.fail("PVEHandler[after] 服务器处理消息失败");
						return false;
						
					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("PVEHandler[after] 失败", e);
					return false;
				}
			}

		});
		return success;
	}
	
	

}
