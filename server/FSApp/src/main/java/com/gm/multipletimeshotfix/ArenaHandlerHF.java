package com.gm.multipletimeshotfix;

import java.util.ArrayList;
import java.util.List;

import com.bm.arena.ArenaBM;
import com.bm.arena.ArenaConstant;
import com.bm.rank.arena.ArenaExtAttribute;
import com.google.protobuf.ByteString;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.playerdata.embattle.EmbattlePositonHelper;
import com.playerdata.hero.core.FSHeroMgr;
import com.rw.fsutil.ranking.ListRanking;
import com.rw.fsutil.ranking.ListRankingEntry;
import com.rw.service.arena.ArenaHandler;
import com.rw.service.log.BILogMgr;
import com.rw.service.log.template.BIActivityCode;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwbase.dao.arena.pojo.TableArenaData;
import com.rwproto.ArenaServiceProtos.ArenaEmbattleType;
import com.rwproto.ArenaServiceProtos.MsgArenaRequest;
import com.rwproto.ArenaServiceProtos.MsgArenaResponse;
import com.rwproto.ArenaServiceProtos.eArenaResultType;
import com.rwproto.BattleCommon.BattleHeroPosition;
import com.rwproto.BattleCommon.eBattlePositionType;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class ArenaHandlerHF extends ArenaHandler {
	
	protected static ArenaHandlerHF instance = new ArenaHandlerHF();
	
	protected ArenaHandlerHF() {
	}
	
	private void synHeroInfo(Player player) {
		EmbattlePositionInfo positionInfo = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(player.getUserId(), eBattlePositionType.ArenaPos_VALUE, "");
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

	public ByteString arenaFightStart(MsgArenaRequest request, Player player) {
		String userId = player.getUserId();

		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		int career = player.getCareer();
		if (career <= 0) {
			return sendFailResponse(response, "数据错误", player);
		}
		response.setArenaType(request.getArenaType());
		TableArenaData m_MyArenaData = ArenaBM.getInstance().getArenaData(userId);
		if (m_MyArenaData == null) {
			return sendFailResponse(response, "数据错误", player);
		}

		// 检查挑战次数
		if (m_MyArenaData.getRemainCount() <= 0) {
			return sendFailResponse(response, "挑战次数已用完", player);
		}

		String enemyUserId = request.getUserId();
		TableArenaData enemyArenaData = ArenaBM.getInstance().getArenaData(enemyUserId);
		if (enemyArenaData == null) {
			return sendFailResponse(response, ArenaConstant.ENEMY_NOT_EXIST, player);
		}
		ArenaBM arenaBM = ArenaBM.getInstance();
		ListRanking<String, ArenaExtAttribute> ranking = arenaBM.getRanking();
		ListRankingEntry<String, ArenaExtAttribute> entry = ranking.getRankingEntry(userId);
		if (entry == null && !ranking.isFull()) {
			arenaBM.addArenaData(player);
			entry = ranking.getRankingEntry(userId);
			if (entry == null) {
				return sendFailResponse(response, ArenaConstant.UNKOWN_EXCEPTION, player);
			}
		}

		// 转职的多线程保护，是木有的=。=
		ListRankingEntry<String, ArenaExtAttribute> enmeyEntry = ranking.getRankingEntry(enemyUserId);
		if (enmeyEntry == null) {
			return sendFailResponse(response, "无法挑战该名对手，请重新选择对手", player);
		}
		if (!enmeyEntry.getExtension().setFighting()) {
			return sendFailResponse(response, "对手正在战斗中，请重新选择对手", player);
		}
		// 强行设置挑战者
		entry.getExtension().forceSetFighting();
		// 设置后挑战者掉线，可怜的被挑战者只能等待超时时间(可以监听挑战者断线事件)
		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		BILogMgr.getInstance().logActivityBegin(player, null, BIActivityCode.ARENA, 0, 0);

		List<BattleHeroPosition> heroPosList = request.getAtkIdListList();

		int size = heroPosList.size();
		List<String> heroIds = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			heroIds.add(heroPosList.get(i).getHeroId());
		}

		// 存储到阵容中
		EmbattleInfoMgr.getMgr().updateOrAddEmbattleInfo(player, eBattlePositionType.ArenaPos_VALUE, String.valueOf(ArenaEmbattleType.ARENA_ATK_VALUE), EmbattlePositonHelper.parseMsgHeroPos2Memery(heroPosList));

		arenaBM.updateAtkHeroList(heroIds, player);
		synHeroInfo(player);
		return response.build().toByteString();
	}

	private ByteString sendFailResponse(MsgArenaResponse.Builder response, String failTips, Player player) {
		player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, failTips);
		response.setArenaResultType(eArenaResultType.ARENA_FAIL);
		return response.build().toByteString();
	}

}
