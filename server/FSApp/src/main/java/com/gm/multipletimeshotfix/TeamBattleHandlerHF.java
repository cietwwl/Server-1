package com.gm.multipletimeshotfix;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.playerdata.hero.core.FSHeroMgr;
import com.playerdata.teambattle.bm.TeamBattleBM;
import com.playerdata.teambattle.service.TeamBattleHandler;
import com.rwproto.BattleCommon.eBattlePositionType;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.TeamBattleProto.TeamBattleReqMsg;
import com.rwproto.TeamBattleProto.TeamBattleRspMsg;

public class TeamBattleHandlerHF extends TeamBattleHandler{

	public ByteString startFight(Player player, TeamBattleReqMsg msgTBRequest) {
		synHeroInfo(player);
		TeamBattleRspMsg.Builder tbRsp = TeamBattleRspMsg.newBuilder();
		TeamBattleBM tbBM = TeamBattleBM.getInstance();
		tbBM.startFight(player, tbRsp, msgTBRequest.getLoopID(), msgTBRequest.getBattleTime());
		return tbRsp.build().toByteString();
	}
	
	private void synHeroInfo(Player player) {
		EmbattlePositionInfo positionInfo = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(player.getUserId(), eBattlePositionType.TeamBattle_VALUE, "");
		if (positionInfo != null) {
			List<EmbattleHeroPosition> posList = positionInfo.getPos();
			List<String> heroIds = new ArrayList<String>(posList.size());
			for (EmbattleHeroPosition heroPos : posList) {
				heroIds.add(heroPos.getId());
			}
			List<Hero> heros = FSHeroMgr.getInstance().getHeros(player, heroIds);
			for (Hero h : heros) {
				ClientDataSynMgr.synData(player, h.getAttrMgr().getRoleAttrData(), eSynType.ROLE_ATTR_ITEM, eSynOpType.UPDATE_SINGLE, -1);
			}
		} else {
			List<Hero> allHeros = FSHeroMgr.getInstance().getAllHeros(player, null);
			for (Hero h : allHeros) {
				ClientDataSynMgr.synData(player, h.getAttrMgr().getRoleAttrData(), eSynType.ROLE_ATTR_ITEM, eSynOpType.UPDATE_SINGLE, -1);
			}
		}
	}

}