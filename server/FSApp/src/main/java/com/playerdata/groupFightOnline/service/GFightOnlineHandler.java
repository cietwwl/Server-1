package com.playerdata.groupFightOnline.service;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.groupFightOnline.bm.GFightFinalBM;
import com.playerdata.groupFightOnline.bm.GFightGroupBidBM;
import com.playerdata.groupFightOnline.bm.GFightOnFightBM;
import com.playerdata.groupFightOnline.bm.GFightPrepareBM;
import com.playerdata.groupFightOnline.data.version.GFightDataVersionMgr;
import com.playerdata.groupFightOnline.dataForClient.DefendArmyHerosInfo;
import com.playerdata.groupFightOnline.dataForClient.GFightResult;
import com.rwproto.GrouFightOnlineProto.GFResultType;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineReqMsg;
import com.rwproto.GrouFightOnlineProto.GroupFightOnlineRspMsg;

public class GFightOnlineHandler {
	
	private static GFightOnlineHandler instance = new GFightOnlineHandler();
	
	public static GFightOnlineHandler getInstance(){
		return instance;
	}
	
	public ByteString getResourceInfo(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		GFightGroupBidBM.getInstance().getResourceInfo(player, gfRsp);
		return gfRsp.build().toByteString();
	}
	
	public ByteString groupBidding(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		GFightGroupBidBM.getInstance().groupBidding(player, gfRsp, msgGFRequest.getResourceID(), msgGFRequest.getBidCount());
		return gfRsp.build().toByteString();
	}
	
	public ByteString personalBidding(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		GFightPrepareBM.getInstance().personalBidForGroup(player, gfRsp, msgGFRequest.getResourceID(), msgGFRequest.getGroupID(), msgGFRequest.getSelfBidRate());
		return gfRsp.build().toByteString();
	}
	
	public ByteString modifySelfDefender(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		List<String> herosJsonArr = msgGFRequest.getArmyHerosList();
		List<DefendArmyHerosInfo> herosList = new ArrayList<DefendArmyHerosInfo>();
		for(String herosJson : herosJsonArr){
			try{
				DefendArmyHerosInfo heros = (DefendArmyHerosInfo)ClientDataSynMgr.fromClientJson2Data(DefendArmyHerosInfo.class, herosJson);
				herosList.add(heros);
			}catch(Exception ex){
				GameLog.error(LogModule.GroupFightOnline.getName(), player.getUserId(), String.format("modifySelfDefender，无法将客户端json转成object"), ex);
			}
		}
		GFightPrepareBM.getInstance().modifySelfDefender(player, gfRsp, herosList, GFightDataVersionMgr.fromJson(msgGFRequest.getClientVersion()));
		return gfRsp.build().toByteString();
	}
	
	public ByteString getEnimyDefender(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		GFightOnFightBM.getInstance().getEnimyDefender(player, gfRsp, msgGFRequest.getGroupID());
		return gfRsp.build().toByteString();
	}
	
	public ByteString changeEnimyDefender(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		GFightOnFightBM.getInstance().changeEnimyDefender(player, gfRsp, msgGFRequest.getGroupID());
		return gfRsp.build().toByteString();
	}
	
	public ByteString startFight(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		GFightOnFightBM.getInstance().startFight(player, gfRsp);
		return gfRsp.build().toByteString();
	}
	
	public ByteString informFightResult(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		try {
			GFightResult fightResult = (GFightResult)ClientDataSynMgr.fromClientJson2Data(GFightResult.class, msgGFRequest.getFightResult());
			GFightOnFightBM.getInstance().informFightResult(player, gfRsp, fightResult, GFightDataVersionMgr.fromJson(msgGFRequest.getClientVersion()));
		} catch (Exception e) {
			gfRsp.setRstType(GFResultType.DATA_EXCEPTION);
		}
		return gfRsp.build().toByteString();
	}
	
	public ByteString getGroupBidRank(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		GFightGroupBidBM.getInstance().getGroupBidRank(player, gfRsp, msgGFRequest.getResourceID());
		return gfRsp.build().toByteString();
	}
	
	public ByteString getKillRank(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		GFightOnFightBM.getInstance().getKillRank(player, gfRsp, msgGFRequest.getResourceID());
		return gfRsp.build().toByteString();
	}
	
	public ByteString getHurtRank(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		GFightOnFightBM.getInstance().getHurtRank(player, gfRsp, msgGFRequest.getResourceID());
		return gfRsp.build().toByteString();
	}
	
	public ByteString getAllRankInGroup(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		GFightOnFightBM.getInstance().getAllRankInGroup(player, gfRsp, msgGFRequest.getResourceID());
		return gfRsp.build().toByteString();
	}
	
	//防守队伍用同步
	@Deprecated
	public ByteString getDefenderTeams(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		
		//防守队伍用同步
//		GFightPrepareMgr.getInstance().getDefenderTeams(player, gfRsp, msgGFRequest.getGroupID(), 
//				GFightDataVersionMgr.fromJson(msgGFRequest.getClientVersion()).getDefendArmyItem());
		gfRsp.setRstType(GFResultType.SUCCESS);
		return gfRsp.build().toByteString();
	}
	
	public ByteString viewDefenderTeam(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		GFightPrepareBM.getInstance().viewDefenderTeam(player, gfRsp, msgGFRequest.getGroupID(), msgGFRequest.getTeamID());
		return gfRsp.build().toByteString();
	}
	
	public ByteString getFightRecord(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		GFightOnFightBM.getInstance().getFightRecord(player, gfRsp, msgGFRequest.getResourceID());
		return gfRsp.build().toByteString();
	}
	
	public ByteString getFightOverReward(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		GFightFinalBM.getInstance().getFinalReward(player, gfRsp, msgGFRequest.getResourceID(), msgGFRequest.getRewardID());
		return gfRsp.build().toByteString();
	}
	
	@Deprecated
	public ByteString synGroupData(Player player, GroupFightOnlineReqMsg msgGFRequest) {
		GroupFightOnlineRspMsg.Builder gfRsp = GroupFightOnlineRspMsg.newBuilder();
		gfRsp.setReqType(msgGFRequest.getReqType());
		GFightPrepareBM.getInstance().synGroupData(player, gfRsp, msgGFRequest.getResourceID(), 
				GFightDataVersionMgr.fromJson(msgGFRequest.getClientVersion()));
		return gfRsp.build().toByteString();
	}
}
