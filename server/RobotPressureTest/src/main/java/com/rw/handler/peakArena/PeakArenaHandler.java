package com.rw.handler.peakArena;


import java.util.List;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.RandomMethodIF;
import com.rwproto.MsgDef.Command;
import com.rwproto.PeakArenaServiceProtos.ArenaInfo;
import com.rwproto.PeakArenaServiceProtos.MsgArenaRequest;
import com.rwproto.PeakArenaServiceProtos.MsgArenaResponse;
import com.rwproto.PeakArenaServiceProtos.eArenaResultType;
import com.rwproto.PeakArenaServiceProtos.eArenaType;
import com.rwproto.ResponseProtos.Response;


public class PeakArenaHandler implements RandomMethodIF{
	private static PeakArenaHandler handler = new PeakArenaHandler();
	private  String enemyUserid ; 
	public static PeakArenaHandler getHandler() {
		return handler;
	}
	
	/**
	 * 开始挑战界面,用于获得敌对对象列表
	 * 
	 * @param client
	 * @return
	 */
	public boolean changeEnemy(Client client, String  enemyUserId) {
		MsgArenaRequest.Builder req = MsgArenaRequest.newBuilder();
		req.setArenaType(eArenaType.CHANGE_ENEMY);
		
		

		boolean success = client.getMsgHandler().sendMsg(Command.MSG_PEAK_ARENA, req.build().toByteString(), new MsgReciver() {

			@Override
			public Command getCmd() {
				return Command.MSG_PEAK_ARENA;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {

					MsgArenaResponse rsp = MsgArenaResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("PeakArenaHandler[send]changeEnemy。 转换响应消息为null");
						return false;
					}

					eArenaResultType result = rsp.getArenaResultType();
					if (result == eArenaResultType.ARENA_FAIL) {
						RobotLog.fail("PeakArenaHandler[send] changeEnemy。服务器处理获取列表消息失败 " + result);
						return false;
					}
					List<ArenaInfo> listInfoList = rsp.getListInfoList();
					client.getPeakArenaDataHolder().setListInfoList(listInfoList);
					if(listInfoList == null || listInfoList.size() <=0){
						RobotLog.fail("PeakArenaHandler[send] changeEnemy。找不到对手");
						return true;
					}
					ArenaInfo arenaInfo = rsp.getListInfo(0);
					enemyUserid = arenaInfo.getUserId();
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("PeakArenaHandler[send]changeEnemy。获取列表 失败", e);
					return false;
				}
				RobotLog.info("PeakArenaHandler[send]changeEnemy。 获取列表成功");
				return true;
			}

		});
		return success;		
	}
	
	/**
	 * 战斗开始,触发playerdata
	 * 
	 * @param client
	 * @return
	 */
	public boolean fightStart(Client client, String  enemyUserId) {
		MsgArenaRequest.Builder req = MsgArenaRequest.newBuilder();
		req.setArenaType(eArenaType.ARENA_FIGHT_START);
		req.setUserId(enemyUserid);		

		boolean success = client.getMsgHandler().sendMsg(Command.MSG_PEAK_ARENA, req.build().toByteString(), new MsgReciver() {

			@Override
			public Command getCmd() {
				return Command.MSG_PEAK_ARENA;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {

					MsgArenaResponse rsp = MsgArenaResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("PeakArenaHandler[send]fightStart。 转换响应消息为null");
						return false;
					}

					eArenaResultType result = rsp.getArenaResultType();
					if (result == eArenaResultType.ARENA_FAIL) {
						RobotLog.fail("PeakArenaHandler[send]fightStart。 服务器处理 申请开始战斗消息失败 " + result);
						return false;
					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("PeakArenaHandler[send] fightStart。 申请开始战斗失败", e);
					return false;
				}
				RobotLog.info("PeakArenaHandler[send] fightStart。申请开始战斗成功");
				return true;
			}

		});
		return success;		
	}
	
	/**
	 * 战斗胜利,获得boolean
	 * 
	 * @param client
	 * @return
	 */
	public boolean fightFinish(Client client, String  enemyUserId) {
		MsgArenaRequest.Builder req = MsgArenaRequest.newBuilder();
		req.setArenaType(eArenaType.ARENA_FIGHT_FINISH);
		req.setWin(true);
		req.setUserId(enemyUserid);	

		boolean success = client.getMsgHandler().sendMsg(Command.MSG_PEAK_ARENA, req.build().toByteString(), new MsgReciver() {

			@Override
			public Command getCmd() {
				return Command.MSG_PEAK_ARENA;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {

					MsgArenaResponse rsp = MsgArenaResponse.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("PeakArenaHandler[send]fightFinish。 转换响应消息为null");
						return false;
					}

					eArenaResultType result = rsp.getArenaResultType();
					if (result == eArenaResultType.ARENA_FAIL) {
						RobotLog.fail("PeakArenaHandler[send]fightFinish。 服务器处理 申请结束战斗消息失败 " + result);
						return false;
					}

				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("PeakArenaHandler[send]fightFinish。  申请结束战斗失败", e);
					return false;
				}
				RobotLog.info("PeakArenaHandler[send]fightFinish。 申请结束战斗成功");
				return true;
			}

		});
		return success;		
	}

	@Override
	public boolean executeMethod(Client client) {
		return changeEnemy(client, "");
	}
}

