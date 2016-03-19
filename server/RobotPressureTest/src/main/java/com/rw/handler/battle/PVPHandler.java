package com.rw.handler.battle;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.battle.army.ArmyInfo;
import com.rwproto.ArenaServiceProtos.ArenaInfo;
import com.rwproto.ArenaServiceProtos.MsgArenaRequest;
import com.rwproto.ArenaServiceProtos.MsgArenaResponse;
import com.rwproto.ArenaServiceProtos.eArenaResultType;
import com.rwproto.ArenaServiceProtos.eArenaType;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;

public class PVPHandler {

	
	private static PVPHandler instance = new PVPHandler();
	public static PVPHandler instance(){
		return instance;
	}

	/**
	 * 开始战斗
	 * 
	 * @param serverId
	 * @param accountId
	 */
	public boolean doPvP(Client client) {

		
		String enenmyUserId = obtainEnemyId(client);
		
		ArmyInfo armyInfo = null;
		if(enenmyUserId!=null){
			armyInfo = obtainEnemy(client, enenmyUserId);
		}
		boolean success = false;
		if(armyInfo!=null){
			success = sendResult(client, enenmyUserId);
		}
		
		return success;
	}

	private String obtainEnemyId(Client client) {
		MsgArenaRequest.Builder req = MsgArenaRequest.newBuilder();
		req.setArenaType(eArenaType.GET_INFO);
		final List<String> enemyIdlist = new ArrayList<String>();
		client.getMsgHandler().sendMsg( Command.MSG_ARENA, req.build().toByteString(), new MsgReciver() {
			
			@Override
			public Command getCmd() {
				return Command.MSG_ARENA;
			}
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					
					MsgArenaResponse rsp = MsgArenaResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("PVPHandler[before] 转换响应消息为null");
						return false;
					}
					
					List<ArenaInfo> listInfoList = rsp.getListInfoList();
					if(listInfoList.size()>0){
						enemyIdlist.add(listInfoList.get(0).getUserId());
						return true;
					}
					
					RobotLog.fail("PVPHandler[before] 返回的竞技对手列表为空");
					return false;
					
					
					
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("PVPHandler[before] 失败", e);
					return false;
				}
			}
			
		});
		return enemyIdlist.size()>0?enemyIdlist.get(0):null;
	}
	private ArmyInfo obtainEnemy(Client client, String enemyUserId) {
		
		MsgArenaRequest.Builder req = MsgArenaRequest.newBuilder();
		req.setArenaType(eArenaType.ARENA_FIGHT_PREPARE);
		req.setUserId(enemyUserId);
		
		final List<ArmyInfo> armyInfoList = new ArrayList<ArmyInfo>();
				 
		
		client.getMsgHandler().sendMsg( Command.MSG_ARENA, req.build().toByteString(), new MsgReciver() {
			
			@Override
			public Command getCmd() {
				return Command.MSG_ARENA;
			}
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					
					MsgArenaResponse rsp = MsgArenaResponse.parseFrom(serializedContent);
					if (rsp == null ) {
						RobotLog.fail("PVPHandler[obtainEnemy] 转换响应消息为null");
						return false;
					}else if(rsp.getArenaResultType()!=eArenaResultType.ARENA_SUCCESS){
						RobotLog.fail("PVPHandler[obtainEnemy] 转换响应消息失败，返回结果为："+rsp.getArenaResultType());
						return false;
					}else{
						String armyJson = rsp.getArenaData().getArmyInfo();
						ArmyInfo armyInfo = ArmyInfo.fromJson(armyJson);
						if(armyInfo!=null){
							armyInfoList.add(armyInfo);
							RobotLog.info("PVPHandler[obtainEnemy] 成功");
							return true;
						}
					}
					
					RobotLog.fail("PVPHandler[obtainEnemy] 根据userId返回的竞技对手为空");
					return false;
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("PVPHandler[obtainEnemy] 失败", e);
					return false;
				}
			}
			
		});
		return armyInfoList.size()>0?armyInfoList.get(0):null;
	}
	
	private boolean sendResult(Client client, String enemyUserId) {
		
		MsgArenaRequest.Builder req = MsgArenaRequest.newBuilder();
		req.setArenaType(eArenaType.ARENA_FIGHT_FINISH);
		req.setUserId(enemyUserId);
		req.setWin(1);
		
		
		boolean success = client.getMsgHandler().sendMsg( Command.MSG_ARENA, req.build().toByteString(), new MsgReciver() {
			
			@Override
			public Command getCmd() {
				return Command.MSG_ARENA;
			}
			
			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					
					MsgArenaResponse rsp = MsgArenaResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("PVPHandler[sendResult] 转换响应消息为null");
						return false;
					}else{
						eArenaResultType resultType = rsp.getArenaResultType();
						if(resultType == eArenaResultType.ARENA_SUCCESS){
							
							RobotLog.info("PVPHandler[sendResult] 成功");
							return true;
						}else{
							RobotLog.fail("PVPHandler[sendResult] 返回结果为失败");
							return false;
						}
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("PVPHandler[obtainEnemy] 失败", e);
					return false;
				}
			}
			
		});
		return success;
	}
	
	

}
