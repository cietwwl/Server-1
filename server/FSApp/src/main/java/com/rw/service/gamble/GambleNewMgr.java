package com.rw.service.gamble;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.common.PlayerEventListener;
import com.rw.service.gamble.datamodel.GambleHotHeroPlan;
import com.rw.service.gamble.datamodel.GambleRecordDAO;
import com.rw.service.redpoint.RedPointType;
import com.rw.service.redpoint.impl.RedPointCollector;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class GambleNewMgr implements RedPointCollector, PlayerEventListener {
	private Player m_pPlayer;

	public boolean getHasFree() {
		return GambleHandler.canGambleFreely(m_pPlayer);
	}

	/**
	 * 每天五点重置玩家的钓鱼台数据
	 * 
	 * @param player
	 */
	public void resetForNewDay() {
		GambleRecordDAO.getInstance().reset(m_pPlayer.getUserId());
		GameLog.info("钓鱼台", m_pPlayer.getUserId(), "每天五点重置玩家数据", null);
	}

	// 用于GM命令！
	public void resetHotHeroList() {
		GambleHotHeroPlan.resetHotHeroList();
	}

	public boolean isPrimaryOneFree() {
		return GambleHandler.isFree(m_pPlayer, GambleLogicHelper.Primary_One);
	}

	public boolean isMiddleOneFree() {
		return GambleHandler.isFree(m_pPlayer, GambleLogicHelper.Middle_One);
	}

	public void syncMainCityGambleHotPoint() {
	}

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		if (isPrimaryOneFree()) {
			map.put(RedPointType.FISHING_WINDOW_LOW_LEVEL, Collections.<String> emptyList());
		}
		if (isMiddleOneFree()) {
			map.put(RedPointType.FISHING_WINDOW_MIDDLE_LEVEL, Collections.<String> emptyList());
		}
	}

	@Override
	public void notifyPlayerCreated(Player player) {
		GambleRecordDAO gambleRecords = GambleRecordDAO.getInstance();
		String userId = player.getUserId();
		gambleRecords.getOrCreate(userId);
		// GambleRecord record = gambleRecords.getOrCreate(userId);
		// gambleRecords.update(record);
	}

	@Override
	public void notifyPlayerLogin(Player player) {
	}

	@Override
	public void init(Player player) {
		m_pPlayer = player;
	}

	@Override
	public eOpenLevelType getOpenType() {
		return null;
	}
}
