package com.rw.service.redpoint.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.playerdata.Player;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;
import com.rwbase.dao.openLevelLimit.pojo.CfgOpenLevelLimit;

public class StoreCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		// CfgOpenLevelLimit taskOpenLevel = (CfgOpenLevelLimit)
		// CfgOpenLevelLimitDAO.getInstance().getCfgById(String.valueOf(eOpenLevelType.SHOP.getOrder()));
		// if (taskOpenLevel == null || player.getLevel() >=
		// taskOpenLevel.getMinLevel()) {
		if (player.getStoreMgr().isStoreRefresh()) {
			map.put(RedPointType.HOME_WINDOW_STORE, Collections.EMPTY_LIST);
			return;
		}

		if (player.getStoreMgr().isWakenStoreRedPoint()) {
			map.put(RedPointType.HOME_WINDOW_STORE, Collections.EMPTY_LIST);
			return;
		}
		// }
	}

	@Override
	public eOpenLevelType getOpenType() {
		return eOpenLevelType.SHOP;
	}

}
