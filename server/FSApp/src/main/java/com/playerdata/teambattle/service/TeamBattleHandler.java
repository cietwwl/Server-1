package com.playerdata.teambattle.service;

import com.google.protobuf.ByteString;
import com.playerdata.Player;
import com.playerdata.teambattle.bm.TeamBattleBM;
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

	public ByteString saveTeamInfo(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.saveTeamInfo(player, tbRsp);
		return tbRsp.build().toByteString();
	}

	public ByteString createTeam(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.createTeam(player, tbRsp);
		return tbRsp.build().toByteString();
	}

	public ByteString joinTeam(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.joinTeam(player, tbRsp);
		return tbRsp.build().toByteString();
	}

	public ByteString acceptInvite(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.acceptInvite(player, tbRsp);
		return tbRsp.build().toByteString();
	}

	public ByteString setTeamFreeJion(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.setTeamFreeJion(player, tbRsp);
		return tbRsp.build().toByteString();
	}

	public ByteString kickoffMember(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.kickoffMember(player, tbRsp);
		return tbRsp.build().toByteString();
	}

	public ByteString invitePlayer(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.invitePlayer(player, tbRsp);
		return tbRsp.build().toByteString();
	}

	public ByteString startFight(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.startFight(player, tbRsp);
		return tbRsp.build().toByteString();
	}

	public ByteString informFightResult(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.informFightResult(player, tbRsp);
		return tbRsp.build().toByteString();
	}
}