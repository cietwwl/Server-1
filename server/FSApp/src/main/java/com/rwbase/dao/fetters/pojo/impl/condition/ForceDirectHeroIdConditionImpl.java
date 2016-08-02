package com.rwbase.dao.fetters.pojo.impl.condition;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.dao.fetters.FettersBM;
import com.rwbase.dao.fetters.pojo.IFettersSubCondition;
import com.rwbase.dao.fetters.pojo.IFettersSubRestrictCondition;
import com.rwbase.dao.fetters.pojo.cfg.template.FettersSubConditionTemplate;

/*
 * @author HC
 * @date 2016年4月28日 下午5:00:50
 * @Description 强指定英雄Id的条件达成
 */
public class ForceDirectHeroIdConditionImpl implements IFettersSubRestrictCondition {

	@Override
	public boolean match(Player player, List<Integer> fettersHeroIdList, List<Integer> forceUseHeroIdList, FettersSubConditionTemplate subCondition) {
		if (subCondition == null) {
			return false;
		}

		int heroModelId = subCondition.getSubConditionRestrictValue();// 限定英雄的值
		if (!fettersHeroIdList.contains(heroModelId)) {
			return false;
		}

//		Hero hero = player.getHeroMgr().getHeroByModerId(heroModelId);
		Hero hero = player.getHeroMgr().getHeroByModerId(player, heroModelId);
		if (hero == null) {
			return false;
		}

		Map<Integer, Integer> subConditionValueMap = subCondition.getSubConditionValueMap();
		if (subConditionValueMap == null || subConditionValueMap.isEmpty()) {
			return true;
		}

		for (Entry<Integer, Integer> e : subConditionValueMap.entrySet()) {
			IFettersSubCondition check = FettersBM.getCheckSubCondition(e.getKey());
			if (check == null) {
				continue;
			}

			if (!check.match(player, heroModelId, e.getValue())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int getSubRestrictConditionType() {
		return FettersBM.SubConditionRestrictType.FORCE_DIRECT_HERO_MODEL_ID.type;
	}
}