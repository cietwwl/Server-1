package com.rw.handler.teamBattle.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rw.Client;
import com.rw.common.MsgReciver;
import com.rw.common.RobotLog;
import com.rw.handler.teamBattle.data.TBTeamItem;
import com.rw.handler.teamBattle.data.TBTeamItemHolder;
import com.rw.handler.teamBattle.data.UserTeamBattleData;
import com.rw.handler.teamBattle.data.UserTeamBattleDataHolder;
import com.rwproto.MsgDef.Command;
import com.rwproto.ResponseProtos.Response;
import com.rwproto.TeamBattleProto.TBRequestType;
import com.rwproto.TeamBattleProto.TBResultType;
import com.rwproto.TeamBattleProto.TeamBattleReqMsg;
import com.rwproto.TeamBattleProto.TeamBattleRspMsg;

public class TeamBattleHandler {

	private static TeamBattleHandler instance = new TeamBattleHandler();
	
	public static String[] HARD_ARR = new String[]{"170101", "170201", "170301", "170401", "170501", "170601"};
	
	public static TeamBattleHandler getInstance() {
		return instance;
	}

	/**
	 * 开始组队功能，主要是组队
	 * @param client
	 * @return
	 */
	public boolean startTBCreateTeam(Client client){
		boolean result = synTeamBattle(client);
		if (!result) {
			RobotLog.fail("startTBCreateTeam[send]组队同步数据反馈结果=" + result);
			return result;
		}
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().getUserTBData();
		if(null != utbData.getTeamID() && !utbData.getTeamID().isEmpty()) {
			if(null != UserTeamBattleDataHolder.getInstance().getCurrentHardID() &&
					utbData.getTeamID().split("_")[0].equals(UserTeamBattleDataHolder.getInstance().getCurrentHardID())){
				RobotLog.info("startTBCreateTeam[send]玩家已经有队伍，不能再创建或加入队伍");
				return true;
			}
		}
		int rankKey = (int)(Math.random() * 3);
		if(0 == rankKey){
			result = createTeam(client);
			if (!result) {
				RobotLog.fail("startTBCreateTeam[send]创建队伍反馈结果=" + result);
				return result;
			}
		}else{
			result = jionTeam(client);
			if (!result) {
				RobotLog.fail("startTBCreateTeam[send]快速加入队伍反馈结果=" + result);
				return result;
			}
		}
		return true;
	}
	
	/**
	 * 组队开战，如果没有队伍，就先组队（如果队伍没满员，直接退出）
	 * @param client
	 * @return
	 */
	public boolean startTBFight(Client client){
		boolean result = synTeamBattle(client);
		if (!result) {
			RobotLog.fail("startTBFight[send]组队开战数据同步反馈结果=" + result);
			return result;
		}
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().getUserTBData();
		if(null == utbData.getTeamID() || utbData.getTeamID().isEmpty()) {
			return startTBCreateTeam(client);
		}
		TBTeamItem teamItem = TBTeamItemHolder.getInstance().getTeamData();
		if(teamItem.getMembers().size() < 3){
			RobotLog.info("startTBFight[send]组队队伍人数没有满员，不能开战");
			return true;
		}
		result = startTeamFight(client);
		if (!result) {
			RobotLog.fail("startTBFight[send]组队开战反馈结果=" + result);
			return result;
		}
		result = informTBFightResult(client);
		if (!result) {
			RobotLog.fail("startTBFight[send]组队通知战斗结果反馈结果=" + result);
			return result;
		}
		return true;
	}
	
	private boolean synTeamBattle(Client client){
		TeamBattleReqMsg.Builder req = TeamBattleReqMsg.newBuilder();
		req.setReqType(TBRequestType.SYN_TEAM_BATTLE);
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_TEAM_BATTLE, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_TEAM_BATTLE;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					TeamBattleRspMsg rsp = TeamBattleRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("synTeamBattle[send] 转换响应消息为null");
						return false;
					}
					TBResultType result = rsp.getRstType();
					if (!result.equals(TBResultType.SUCCESS)) {
						RobotLog.fail("synTeamBattle[send] 服务器处理消息失败 " + rsp.getTipMsg());
						return true;
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("synTeamBattle[send] 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}
	
	private boolean createTeam(Client client){
		String currentHardID = UserTeamBattleDataHolder.getInstance().getCurrentHardID();
		if(null == currentHardID){
			RobotLog.info("createTeam[send] 没有可以打的关卡，或者已经全部通关");
			return true;
		}
		TeamBattleReqMsg.Builder req = TeamBattleReqMsg.newBuilder();
		req.setReqType(TBRequestType.CREATE_TEAM);
		req.setHardID(currentHardID);
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_TEAM_BATTLE, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_TEAM_BATTLE;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					TeamBattleRspMsg rsp = TeamBattleRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("createTeam[send] 转换响应消息为null");
						return false;
					}
					TBResultType result = rsp.getRstType();
					if (!result.equals(TBResultType.SUCCESS)) {
						RobotLog.fail("createTeam[send] 服务器处理消息失败 " + rsp.getTipMsg());
						return true;
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("createTeam[send] 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}
	
	private boolean jionTeam(Client client){
		String currentHardID = UserTeamBattleDataHolder.getInstance().getCurrentHardID();
		if(null == currentHardID){
			RobotLog.info("createTeam[send] 没有可以打的关卡，或者已经全部通关");
			return true;
		}
		TeamBattleReqMsg.Builder req = TeamBattleReqMsg.newBuilder();
		req.setReqType(TBRequestType.JOIN_TEAM);
		req.setHardID(currentHardID);
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_TEAM_BATTLE, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_TEAM_BATTLE;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					TeamBattleRspMsg rsp = TeamBattleRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("jionTeam[send] 转换响应消息为null");
						return false;
					}
					TBResultType result = rsp.getRstType();
					if (!result.equals(TBResultType.SUCCESS)) {
						RobotLog.fail("jionTeam[send] 服务器处理消息失败 " + rsp.getTipMsg());
						return true;
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("jionTeam[send] 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}
	
	private boolean startTeamFight(Client client){
		String currentHardID = UserTeamBattleDataHolder.getInstance().getCurrentHardID();
		if(null == currentHardID){
			RobotLog.info("createTeam[send] 没有可以打的关卡，或者已经全部通关");
			return true;
		}
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().getUserTBData();
		TeamBattleReqMsg.Builder req = TeamBattleReqMsg.newBuilder();
		req.setReqType(TBRequestType.START_FIGHT);
		req.setLoopID(utbData.getEnimyMap().get(currentHardID));
		req.setBattleTime(1);
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_TEAM_BATTLE, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_TEAM_BATTLE;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					TeamBattleRspMsg rsp = TeamBattleRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("startTeamFight[send] 转换响应消息为null");
						return false;
					}
					TBResultType result = rsp.getRstType();
					if (!result.equals(TBResultType.SUCCESS)) {
						RobotLog.fail("startTeamFight[send] 服务器处理消息失败 " + rsp.getTipMsg());
						return true;
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("startTeamFight[send] 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}
	
	private boolean informTBFightResult(Client client){
		String currentHardID = UserTeamBattleDataHolder.getInstance().getCurrentHardID();
		if(null == currentHardID){
			RobotLog.info("createTeam[send] 没有可以打的关卡，或者已经全部通关");
			return true;
		}
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().getUserTBData();
		TeamBattleReqMsg.Builder req = TeamBattleReqMsg.newBuilder();
		req.setReqType(TBRequestType.INFORM_FIGHT_RESULT);
		req.setLoopID(utbData.getEnimyMap().get(currentHardID));
		req.setBattleTime(1);
		req.setFightResult(1);
		boolean success = client.getMsgHandler().sendMsg(Command.MSG_TEAM_BATTLE, req.build().toByteString(), new MsgReciver() {
			@Override
			public Command getCmd() {
				return Command.MSG_TEAM_BATTLE;
			}

			@Override
			public boolean execute(Client client, Response response) {
				ByteString serializedContent = response.getSerializedContent();
				try {
					TeamBattleRspMsg rsp = TeamBattleRspMsg.parseFrom(serializedContent);
					if (rsp == null) {
						RobotLog.fail("startTeamFight[send] 转换响应消息为null");
						return false;
					}
					TBResultType result = rsp.getRstType();
					if (!result.equals(TBResultType.SUCCESS)) {
						RobotLog.fail("startTeamFight[send] 服务器处理消息失败 " + rsp.getTipMsg());
						return true;
					}
				} catch (InvalidProtocolBufferException e) {
					RobotLog.fail("startTeamFight[send] 失败", e);
					return false;
				}
				return true;
			}
		});
		return success;
	}
}