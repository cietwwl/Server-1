package com.gm.multipletimeshotfix;

import java.util.ArrayList;
import java.util.List;

import com.bm.arena.ArenaConstant;
import com.google.protobuf.ByteString;
import com.log.GameLog;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.playerdata.hero.core.FSHeroMgr;
import com.rw.fsutil.ranking.ListRankingEntry;
import com.rw.service.PeakArena.PeakArenaBM;
import com.rw.service.PeakArena.PeakArenaHandler;
import com.rw.service.PeakArena.datamodel.PeakArenaExtAttribute;
import com.rw.service.PeakArena.datamodel.TablePeakArenaData;
import com.rw.service.PeakArena.datamodel.TablePeakArenaDataDAO;
import com.rwbase.common.enu.ECommonMsgTypeDef;
import com.rwproto.BattleCommon.eBattlePositionType;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;
import com.rwproto.PeakArenaServiceProtos.MsgArenaRequest;
import com.rwproto.PeakArenaServiceProtos.MsgArenaResponse;
import com.rwproto.PeakArenaServiceProtos.eArenaResultType;

public class PeakArenaHandlerHF extends PeakArenaHandler {

	protected static PeakArenaHandlerHF instance = new PeakArenaHandlerHF();

	private void synHeroInfo(Player player) {
		try {
			Hero mainHero = FSHeroMgr.getInstance().getMainRoleHero(player);
			ClientDataSynMgr.synData(player, mainHero.getAttrMgr().getRoleAttrData(), eSynType.ROLE_ATTR_ITEM, eSynOpType.UPDATE_SINGLE, -1);
			EmbattlePositionInfo positionInfo1 = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(player.getUserId(), eBattlePositionType.PeakArenaPos_VALUE, "0");
			if (positionInfo1 != null) {
				synHeroInfo(player, positionInfo1);
			}

			EmbattlePositionInfo positionInfo2 = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(player.getUserId(), eBattlePositionType.PeakArenaPos_VALUE, "1");
			if (positionInfo2 != null) {
				synHeroInfo(player, positionInfo2);
			}

			EmbattlePositionInfo positionInfo3 = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(player.getUserId(), eBattlePositionType.PeakArenaPos_VALUE, "2");
			if (positionInfo3 != null) {
				synHeroInfo(player, positionInfo3);
			}
		} catch (Exception e) {
			e.printStackTrace();
			List<Hero> allHeros = FSHeroMgr.getInstance().getAllHeros(player, null);
			for (Hero h : allHeros) {
				ClientDataSynMgr.synData(player, h.getAttrMgr().getRoleAttrData(), eSynType.ROLE_ATTR_ITEM, eSynOpType.UPDATE_SINGLE, -1);
			}
		}
	}

	private void synHeroInfo(Player player, EmbattlePositionInfo positionInfo) {
		List<EmbattleHeroPosition> posList = positionInfo.getPos();
		List<String> heroIds = new ArrayList<String>(posList.size());
		for (EmbattleHeroPosition heroPos : posList) {
			heroIds.add(heroPos.getId());
		}
		List<Hero> heros = FSHeroMgr.getInstance().getHeros(player, heroIds);
		for (Hero h : heros) {
			ClientDataSynMgr.synData(player, h.getAttrMgr().getRoleAttrData(), eSynType.ROLE_ATTR_ITEM, eSynOpType.UPDATE_SINGLE, -1);
		}
	}

	public ByteString fightContinue(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());

		TablePeakArenaData arenaData = PeakArenaBM.getInstance().getOrAddPeakArenaData(player);
		if (arenaData == null) {
			return sendFailRespon(player, response, ArenaConstant.UNKOWN_EXCEPTION);
		}

		String enemyId = request.getUserId();
		ListRankingEntry<String, PeakArenaExtAttribute> enemyEntry = PeakArenaBM.getInstance().getEnemyEntry(enemyId);
		if (enemyEntry == null) {
			return sendFailRespon(player, response, ArenaConstant.ENEMY_NOT_EXIST);
		}
		ListRankingEntry<String, PeakArenaExtAttribute> playerEntry = PeakArenaBM.getInstance().getPlayerRankEntry(player, arenaData);
		// TODO 这次不管 超出排行榜容量的容错处理，让他打，赢了重新尝试加入排行榜
		if (playerEntry == null) {
			GameLog.error("巅峰竞技场", player.getUserId(), "玩家未入榜");
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}

		String lastEnemy = arenaData.getLastFightEnemy();
		if (enemyId.equals(lastEnemy)) {
			// 延长超时时间
			enemyEntry.getExtension().extendTimeOut();
			final long currentTimeMillis = System.currentTimeMillis();
			arenaData.setFightStartTime(currentTimeMillis);
			response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		} else {
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
		}

		synHeroInfo(player);
		return response.build().toByteString();
	}

	// 第一场战斗开始的时候发送
	public ByteString fightStart(MsgArenaRequest request, Player player) {
		MsgArenaResponse.Builder response = MsgArenaResponse.newBuilder();
		response.setArenaType(request.getArenaType());
		TablePeakArenaData arenaData = PeakArenaBM.getInstance().getOrAddPeakArenaData(player);
		if (arenaData == null) {
			return sendFailRespon(player, response, ArenaConstant.UNKOWN_EXCEPTION);
		}

		String enemyId = request.getUserId();
		ListRankingEntry<String, PeakArenaExtAttribute> enemyEntry = PeakArenaBM.getInstance().getEnemyEntry(enemyId);
		if (enemyEntry == null) {
			return sendFailRespon(player, response, ArenaConstant.ENEMY_NOT_EXIST);
		}
		ListRankingEntry<String, PeakArenaExtAttribute> entry = PeakArenaBM.getInstance().getPlayerRankEntry(player, arenaData);
		// TODO 这次不管 超出排行榜容量的容错处理，让他打，赢了重新尝试加入排行榜
		if (entry == null) {
			response.setArenaResultType(eArenaResultType.ARENA_FAIL);
			return response.build().toByteString();
		}

		// combined transaction
		if (!enemyEntry.getExtension().setFighting()) {
			return sendFailRespon(player, response, ArenaConstant.ENEMY_IS_FIGHTING);
		}

		// TODO 同宇超商量不对挑战者加锁
		entry.getExtension().forceSetFighting();

		arenaData.setLastFightEnemy(enemyId);
		final long currentTimeMillis = System.currentTimeMillis();
		arenaData.setFightStartTime(currentTimeMillis);

		int challengeCount = arenaData.getChallengeCount();
		TablePeakArenaDataDAO.getInstance().update(arenaData);
		response.setChallengeCount(challengeCount);

		response.setArenaResultType(eArenaResultType.ARENA_SUCCESS);
		synHeroInfo(player);
		return response.build().toByteString();
	}

	private ByteString sendFailRespon(Player player, MsgArenaResponse.Builder response, String tips) {
		player.NotifyCommonMsg(ECommonMsgTypeDef.MsgBox, tips);
		response.setArenaResultType(eArenaResultType.ARENA_FAIL);
		return response.build().toByteString();
	}
}
