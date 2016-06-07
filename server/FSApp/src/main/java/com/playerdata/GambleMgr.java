package com.playerdata;

import java.util.List;
import java.util.Map;

import com.playerdata.common.PlayerEventListener;
import com.rw.service.gamble.GambleNewMgr;
import com.rw.service.redpoint.RedPointType;
import com.rw.service.redpoint.impl.RedPointCollector;

public class GambleMgr implements RedPointCollector, PlayerEventListener {
	private GambleNewMgr wrapper = new GambleNewMgr();

	public boolean getHasFree() {
		return wrapper.getHasFree();
	}

	public void resetForNewDay() {
		wrapper.resetForNewDay();
	}

	public boolean isPrimaryOneFree() {
		return wrapper.isPrimaryOneFree();
	}

	public boolean isMiddleOneFree() {
		return wrapper.isMiddleOneFree();
	}

	public void syncMainCityGambleHotPoint() {
		wrapper.syncMainCityGambleHotPoint();
	}

	@Override
	public void notifyPlayerCreated(Player player) {
		wrapper.notifyPlayerCreated(player);
	}

	@Override
	public void notifyPlayerLogin(Player player) {
		wrapper.notifyPlayerLogin(player);
	}

	@Override
	public void init(Player player) {
		wrapper.init(player);
	}

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map) {
		wrapper.fillRedPoints(player, map);
	}

	public void resetHotHeroList() {
		wrapper.resetHotHeroList();
	}
}
