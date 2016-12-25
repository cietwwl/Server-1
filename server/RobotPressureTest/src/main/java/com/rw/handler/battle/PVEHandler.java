package com.rw.handler.battle;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.config.copyCfg.CopyCfgDAO;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.actionHelper.ActionEnum;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.RandomMethodIF;
import com.rwproto.CopyServiceProtos.EBattleStatus;
import com.rwproto.CopyServiceProtos.ERequestType;
import com.rwproto.CopyServiceProtos.EResultType;
import com.rwproto.CopyServiceProtos.MsgCopyRequest;
import com.rwproto.CopyServiceProtos.MsgCopyResponse;
import com.rwproto.CopyServiceProtos.TagBattleData;
import com.rwproto.CopyServiceProtos.TagBattleData.Builder;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class PVEHandler implements RandomMethodIF{
	
	private static ConcurrentHashMap<String, Integer> funcStageMap = new ConcurrentHashMap<String, Integer>();
	
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
		final int nextCopyId = getNextCopyId(client);
		MsgCopyRequest.Builder req = MsgCopyRequest.newBuilder();
		req.setRequestType(ERequestType.BATTLE_ITEMS_BACK);
		req.setLevelId(nextCopyId);
		
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
					if (result == EResultType.ITEM_BACK || result == EResultType.NOT_OPEN || 
							result == EResultType.NOT_ENOUGH_HP || result == EResultType.NOT_ENOUGH_TIMES) {
						RobotLog.info("PVEHandler[before] 成功");
						return true;
					}else{
						RobotLog.fail(String.format("PVEHandler[before] 服务器处理消息失败:{CopyId:%s,%s}", nextCopyId, result));
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

        Builder tagBattleData = TagBattleData.newBuilder().setLevelId(client.getCopyHolder().getFightingCopyId()).setStarLevel(new Random().nextInt(3) + 1).setBattleStatus(EBattleStatus.WIN).setBattleClearingTime(1);
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
			client.getRateHelper().addActionToQueue(ActionEnum.PVE);
			return before(client);
		case 1:
			funcStageMap.put(client.getAccountId(), 0);
			client.getRateHelper().addActionToQueue(ActionEnum.Daily);
			return after(client);
		default:
			return true;
		}
	}
	
	private int getNextCopyId(Client client){
		int rd = new Random().nextInt(4);
		int copyId = 0;
		if(rd != 0){
			copyId = CopyCfgDAO.getInstance().getNextNormalCopyId(client.getCopyHolder().getCurrentNormalCopyId());
		}else {
			copyId = CopyCfgDAO.getInstance().getNextEliteCopyId(client.getCopyHolder().getCurrentEliteCopyId());
		}
		client.getCopyHolder().setFightingCopyId(copyId);
		return copyId;
	}
}
