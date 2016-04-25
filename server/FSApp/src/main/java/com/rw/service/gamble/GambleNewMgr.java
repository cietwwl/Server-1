package com.rw.service.gamble;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.log.GameLog;
import com.playerdata.HotPointMgr;
import com.playerdata.Player;
import com.playerdata.common.PlayerEventListener;
import com.rw.service.gamble.datamodel.GambleHotHeroPlan;
import com.rw.service.gamble.datamodel.GambleRecordDAO;
import com.rw.service.redpoint.RedPointType;
import com.rw.service.redpoint.impl.RedPointCollector;
import com.rwbase.dao.hotPoint.EHotPointType;

public class GambleNewMgr implements RedPointCollector, PlayerEventListener{
	private Player m_pPlayer;
	public boolean getHasFree() {
		return GambleLogic.canGambleFreely(m_pPlayer.getUserId());
	}
	
	/**
	 * 每天五点重置数据
	 * @param player 
	 */
	public void resetForNewDay(){
		GambleHotHeroPlan.resetHotHeroList(GambleLogic.getInstance().getRandom());
		GambleRecordDAO.getInstance().reset(m_pPlayer.getUserId());
		GameLog.info("钓鱼台", m_pPlayer.getUserId(), "每天五点重置数据", null);
	}
	
	public boolean isPrimaryOneFree(){
		return GambleLogic.isFree(m_pPlayer.getUserId(),GambleLogicHelper.Primary_One);
	}
	
	public boolean isMiddleOneFree(){
		return GambleLogic.isFree(m_pPlayer.getUserId(),GambleLogicHelper.Middle_One);
	}
	
	public void syncMainCityGambleHotPoint(){
		HotPointMgr.changeHotPointState(m_pPlayer.getUserId(), EHotPointType.Gamble, !getHasFree());
	}

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map) {
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
		//GambleRecord record = gambleRecords.getOrCreate(userId);
		//gambleRecords.update(record);
	}

	@Override
	public void notifyPlayerLogin(Player player) {
	}

	@Override
	public void init(Player player) {
		m_pPlayer = player;
	}
}
