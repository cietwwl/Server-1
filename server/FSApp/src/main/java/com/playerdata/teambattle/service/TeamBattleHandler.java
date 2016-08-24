package com.playerdata.teambattle.service;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.teambattle.bm.TeamBattleBM;
import com.playerdata.teambattle.dataForClient.TBArmyHerosInfo;
import com.rwproto.TeamBattleProto.TeamBattleReqMsg;
import com.rwproto.TeamBattleProto.TeamBattleRspMsg;

public class TeamBattleHandler {

	private static TeamBattleHandler instance = new TeamBattleHandler();	

	public static TeamBattleHandler getInstance() {
		return instance;
	}

	public ByteString synTeamBattle(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.synTeamBattle(player, tbRsp);
		return tbRsp.build().toByteString();
	}
	
	public ByteString nonSynTeamBattle(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.nonSynTeamBattle(player, tbRsp);
		return tbRsp.build().toByteString();
	}

	public ByteString saveTeamInfo(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		try {
			TBArmyHerosInfo heros = (TBArmyHerosInfo)ClientDataSynMgr.fromClientJson2Data(TBArmyHerosInfo.class, msgTBRequest.getArmyHeros());
			tbBM.saveTeamInfo(player, tbRsp, heros);
		} catch (Exception ex) {
			GameLog.error(LogModule.TeamBattle.getName(), player.getUserId(), String.format("saveTeamInfo，无法将客户端json转成object"), ex);
		}
		return tbRsp.build().toByteString();
	}

	public ByteString createTeam(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.createTeam(player, tbRsp, msgTBRequest.getHardID());
		return tbRsp.build().toByteString();
	}

	public ByteString joinTeam(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.joinTeam(player, tbRsp, msgTBRequest.getHardID());
		return tbRsp.build().toByteString();
	}

	public ByteString acceptInvite(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.acceptInvite(player, tbRsp, msgTBRequest.getHardID(), msgTBRequest.getTeamID());
		return tbRsp.build().toByteString();
	}

	public ByteString setTeamFreeJion(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.setTeamFreeJion(player, tbRsp, msgTBRequest.getTeamID());
		return tbRsp.build().toByteString();
	}

	public ByteString kickoffMember(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.kickoffMember(player, tbRsp, msgTBRequest.getUserID(), msgTBRequest.getTeamID());
		return tbRsp.build().toByteString();
	}

	public ByteString invitePlayer(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.invitePlayer(player, tbRsp, msgTBRequest.getInviteType(), msgTBRequest.getInviteUsersList(), msgTBRequest.getInviteContent());
		return tbRsp.build().toByteString();
	}

	public ByteString startFight(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.startFight(player, tbRsp, msgTBRequest.getLoopID(), msgTBRequest.getBattleTime());
		return tbRsp.build().toByteString();
	}

	public ByteString informFightResult(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.informFightResult(player, tbRsp, msgTBRequest.getLoopID(), msgTBRequest.getBattleTime(), msgTBRequest.getFightResult() == 1);
		return tbRsp.build().toByteString();
	}

	public ByteString leaveTeam(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.leaveTeam(player, tbRsp);
		return tbRsp.build().toByteString();
	}

	public ByteString scoreExchage(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.scoreExchage(player, tbRsp, msgTBRequest.getRewardID(), msgTBRequest.getCount());
		return tbRsp.build().toByteString();
	}

	public ByteString saveMemPosition(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.saveMemPosition(player, tbRsp, msgTBRequest.getMemPos());
		return tbRsp.build().toByteString();
	}

	public ByteString buyBattleTimes(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.buyBattleTimes(player, tbRsp, msgTBRequest.getHardID());
		return tbRsp.build().toByteString();
	}
}