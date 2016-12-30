package com.playerdata.teambattle.service;

import org.apache.commons.lang3.StringUtils;

import com.common.IHeroSynHandler;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.teambattle.bm.TeamBattleBM;
import com.playerdata.teambattle.data.TBTeamItem;
import com.playerdata.teambattle.data.UserTeamBattleData;
import com.playerdata.teambattle.data.UserTeamBattleDataHolder;
import com.playerdata.teambattle.dataException.JoinTeamException;
import com.playerdata.teambattle.dataForClient.TBArmyHerosInfo;
import com.playerdata.teambattle.manager.TBTeamItemMgr;
import com.playerdata.teambattle.manager.TeamMatchData;
import com.playerdata.teambattle.manager.TeamMatchMgr;
import com.rwbase.common.herosynhandler.CommonHeroSynHandler;
import com.rwproto.BattleCommon.eBattlePositionType;
import com.rwproto.TeamBattleProto.TBResultType;
import com.rwproto.TeamBattleProto.TeamBattleReqMsg;
import com.rwproto.TeamBattleProto.TeamBattleRspMsg;

public class TeamBattleHandler {

	private static TeamBattleHandler instance = new TeamBattleHandler();	

	public static TeamBattleHandler getInstance() {
		return instance;
	}
	
	private IHeroSynHandler _synHandler;
	protected TeamBattleHandler() {
		_synHandler = new CommonHeroSynHandler();
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

	public ByteString acceptInvite(Player player, TeamBattleReqMsg msgTBRequest, boolean isRefreshAllHard, boolean needRefreshJionAble) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.acceptInvite(player, tbRsp, msgTBRequest.getHardID(), msgTBRequest.getTeamID(), isRefreshAllHard, needRefreshJionAble);
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
		_synHandler.synHeroData(player, eBattlePositionType.TeamBattle, null);
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
	public ByteString addRobot(Player player, TeamBattleReqMsg msgTBRequest) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		tbRsp.setRstType(TBResultType.SUCCESS);
		
		UserTeamBattleData utbData = UserTeamBattleDataHolder.getInstance().get(player.getUserId());
		
		if(StringUtils.isNotBlank(utbData.getTeamID())){
			TBTeamItem teamItem = TBTeamItemMgr.getInstance().get(utbData.getTeamID());
			if(teamItem!=null){
				String hardID = teamItem.getHardID();
				String copyId = utbData.getEnimyMap().get(hardID);
				TeamMatchData matchTeamArmy = TeamMatchMgr.getInstance().newMatchTeamArmy(player, copyId);				
				if(matchTeamArmy!=null){					
					try {
						TBTeamItemMgr.getInstance().addRobot(player, teamItem, matchTeamArmy.toStaticMemberTeamInfo(), matchTeamArmy.getRandomData());
					} catch (JoinTeamException e) {
						tbRsp.setRstType(TBResultType.DATA_ERROR);
					}
				}else{					
					tbRsp.setRstType(TBResultType.DATA_ERROR);
				}
			}else{
				tbRsp.setRstType(TBResultType.DATA_ERROR);
			}
		}		
		
		return tbRsp.build().toByteString();
	}

	public ByteString getCanJionTeams(Player player, TeamBattleReqMsg request) {
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		String hardId = request.getHardID();
		if(StringUtils.isBlank(hardId)){
			tbBM.getCanJionTeams(player, tbRsp);
		}else{
			tbBM.getCanJionTeams(player, tbRsp, request.getHardID());
		}
		return tbRsp.build().toByteString();
	}
}