package com.playerdata.hero.core;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.playerdata.embattle.EmBattlePositionKey;
import com.playerdata.embattle.EmbattleHeroPosition;
import com.playerdata.embattle.EmbattleInfoMgr;
import com.playerdata.embattle.EmbattlePositionInfo;
import com.playerdata.hero.core.consumer.FSCalculateAllFightingConsumer;
import com.rwbase.dao.hero.FSUserHeroGlobalDataDAO;
import com.rwbase.dao.hero.pojo.FSUserHeroGlobalData;
import com.rwproto.BattleCommon.eBattlePositionType;
import com.rwproto.DataSynProtos.eSynOpType;
import com.rwproto.DataSynProtos.eSynType;

public class FSUserHeroGlobalDataMgr {

	private static FSUserHeroGlobalDataMgr _INSTANCE = new FSUserHeroGlobalDataMgr();

	public static FSUserHeroGlobalDataMgr getInstance() {
		return _INSTANCE;
	}

	protected FSUserHeroGlobalDataMgr() {
	}

	public void notifySingleFightingChange(String userId, String heroId, int newSingleValue, int preSingleValue) {
		if (newSingleValue != preSingleValue) {
			FSUserHeroGlobalData globalData = FSUserHeroGlobalDataDAO.getInstance().get(userId);
			int sub = newSingleValue - preSingleValue;
			if (globalData.getFightingTeamHeroIdsRO().contains(heroId)) {
				globalData.setFightingTeam(globalData.getFightingTeam() + sub);
			}
			globalData.setFightingAll(globalData.getFightingAll() + sub);
			FSUserHeroGlobalDataDAO.getInstance().update(globalData);
		}
	}

	public void increaseFightingAndStar(String userId, int addFighting, int addStar) {
		FSUserHeroGlobalData globalData = FSUserHeroGlobalDataDAO.getInstance().get(userId);
		if (addFighting > 0) {
			globalData.setFightingAll(globalData.getFightingAll() + addFighting);
		}
		if (addStar > 0) {
			globalData.setStartAll(globalData.getStartAll() + addStar);
		}
		FSUserHeroGlobalDataDAO.getInstance().update(globalData);
	}

	public void increaseFightingAll(String userId, int value) {
		this.increaseFightingAndStar(userId, value, 0);
	}

	public void increaseStarAll(String userId, int value) {
		this.increaseFightingAndStar(userId, 0, value);
	}

	public int getFightingTeam(String userId) {
		FSUserHeroGlobalData userHeroGlobalData = FSUserHeroGlobalDataDAO.getInstance().get(userId);
		List<Hero> heroList = new ArrayList<Hero>(5);
		EmbattlePositionInfo embattleInfo = EmbattleInfoMgr.getMgr().getEmbattlePositionInfo(userId, eBattlePositionType.Normal_VALUE, EmBattlePositionKey.posCopy.getKey());
		if (embattleInfo != null) {
			List<EmbattleHeroPosition> heroPosition = embattleInfo.getPos();
			if (heroPosition != null && !heroPosition.isEmpty()) {
				List<String> heroIds = new ArrayList<String>();
				for (int i = 0, size = heroPosition.size(); i < size; i++) {
					heroIds.add(heroPosition.get(i).getId());
				}
				heroList.addAll(FSHeroMgr.getInstance().getHeros(userId, heroIds));
			}
		}
		if (heroList.isEmpty()) {
			// 没有首页阵容信息，获取最高战力的五人
			heroList = FSHeroMgr.getInstance().getMaxFightingHeros(userId);
		}
		boolean recal = userHeroGlobalData.getFightingTeam() == 0;
		if (!recal) {
			List<String> heroIds = userHeroGlobalData.getFightingTeamHeroIdsRO();
			if (heroIds.size() > 0 && heroList.size() == heroIds.size()) {
				for (int i = 0, size = heroList.size(); i < size; i++) {
					if (!heroIds.contains(heroList.get(i).getId())) {
						recal = true;
						break;
					}
				}
			} else {
				recal = true;
			}
		}
		if (recal) {
			int result = 0;
			List<String> heroIds = new ArrayList<String>();
			Hero hero;
			for (int i = 0; i < heroList.size(); i++) {
				hero = heroList.get(i);
				result += hero.getFighting();
				heroIds.add(hero.getId());
			}
			userHeroGlobalData.setFightingTeam(result);
			userHeroGlobalData.setFightingTeamHeroIds(heroIds);
			FSUserHeroGlobalDataDAO.getInstance().update(userHeroGlobalData);
		}
		return userHeroGlobalData.getFightingTeam();
	}

	public void setFightingAllAndStarAll(String userId, int fightingAll, int starAll) {
		FSUserHeroGlobalData globalData = FSUserHeroGlobalDataDAO.getInstance().get(userId);
		boolean changed = false;
		if (globalData.getFightingAll() != fightingAll) {
			globalData.setFightingAll(fightingAll);
			changed = true;
		}
		if (globalData.getStartAll() != starAll) {
			globalData.setStartAll(starAll);
			changed = true;
		}
		if (changed) {
			FSUserHeroGlobalDataDAO.getInstance().update(globalData);
		}
	}

	public int getFightingAll(String userId) {
		// 新的内容
		FSUserHeroGlobalData userHeroGlobalData = FSUserHeroGlobalDataDAO.getInstance().get(userId);
		if (userHeroGlobalData.getFightingAll() == 0) {
			FSCalculateAllFightingConsumer consumer = new FSCalculateAllFightingConsumer();
			FSHeroMgr.getInstance().loopAll(userId, consumer);
			userHeroGlobalData.setFightingAll(consumer.getTotalFighting());
			FSUserHeroGlobalDataDAO.getInstance().update(userHeroGlobalData);
		}
		return userHeroGlobalData.getFightingAll();
	}

	/**
	 * 同步数据
	 * 
	 * @param userId
	 */
	public void synData(String userId) {
		Player player = PlayerMgr.getInstance().find(userId);
		if (player == null) {
			return;
		}

		synData(player);
	}

	/**
	 * 同步数据
	 * 
	 * @param player
	 */
	public void synData(Player player) {
		FSUserHeroGlobalData heroGlobalData = FSUserHeroGlobalDataDAO.getInstance().get(player.getUserId());
		if (heroGlobalData == null) {
			return;
		}

		ClientDataSynMgr.synData(player, heroGlobalData, eSynType.USER_GLOBAL_DATA_SYN, eSynOpType.UPDATE_SINGLE);
	}
}