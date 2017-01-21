package com.rw.service.redpoint.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.UserGameDataMgr;
import com.playerdata.fightinggrowth.FSUserFightingGrowthData;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.common.enu.eSpecialItemId;
import com.rwbase.dao.fightinggrowth.FSUserFightingGrowthDataDAO;
import com.rwbase.dao.fightinggrowth.FSUserFightingGrowthTitleCfgDAO;
import com.rwbase.dao.fightinggrowth.pojo.FSUserFightingGrowthTitleCfg;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class FightGrowthCollector implements RedPointCollector {

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map, int level) {
		FSUserFightingGrowthData userFightingGrowthData = FSUserFightingGrowthDataDAO.getInstance().get(player.getUserId());
		FSUserFightingGrowthTitleCfg nextTitleCfg = FSUserFightingGrowthTitleCfgDAO.getInstance().getNextFightingGrowthTitleCfgSafely(userFightingGrowthData.getCurrentTitleKey());
		if (nextTitleCfg != null && player.getHeroMgr().getFightingTeam(player) >= nextTitleCfg.getFightingRequired()) {
			Map<Integer, Integer> itemRequiredMap = nextTitleCfg.getRequiredOfItemOnly();
			Map<eSpecialItemId, Integer> currencyRequiredMap = nextTitleCfg.getRequiredOfCurrency();
			boolean requiredMatch = true;
			if (itemRequiredMap.size() > 0) {
				requiredMatch &= ItemBagMgr.getInstance().hasEnoughItems(player.getUserId(), itemRequiredMap);
			}
			if(currencyRequiredMap.size() > 0) {
				UserGameDataMgr userGameDataMgr = player.getUserGameDataMgr();
				for(Iterator<eSpecialItemId> itr = currencyRequiredMap.keySet().iterator(); itr.hasNext();) {
					eSpecialItemId id = itr.next();
					requiredMatch &= userGameDataMgr.isEnoughCurrency(id, currencyRequiredMap.get(id));
					if(!requiredMatch) {
						break;
					}
				}
			}
			if (requiredMatch) {
				List<String> list = new ArrayList<String>(1);
				list.add(nextTitleCfg.getKey());
				map.put(RedPointType.HOME_TOP_FIGHTING_GROWTH, list);
			}
		}
	}

	@Override
	public eOpenLevelType getOpenType() {
		return null;
	}

}
