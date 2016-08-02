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
 * @date 2016年4月28日 下午5:03:25
 * @Description 需要英雄个数的条件达成
 */
public class HeroNumConditionImpl implements IFettersSubRestrictCondition {

	@Override
	public boolean match(Player player, List<Integer> fettersHeroIdList, List<Integer> forceUseHeroIdList, FettersSubConditionTemplate subCondition) {
		if (subCondition == null) {
			return false;
		}

		int heroNum = subCondition.getSubConditionRestrictValue();// 限定英雄的数量
		if (heroNum <= 0) {
			return true;
		}

		Map<Integer, Integer> subConditionValueMap = subCondition.getSubConditionValueMap();

		int matchHeroNum = 0;
		for (int i = 0, heroSize = fettersHeroIdList.size(); i < heroSize; i++) {
			if (matchHeroNum >= heroNum) {// 已经达到了数量
				return true;
			}

			int heroModelId = fettersHeroIdList.get(i);
			if (forceUseHeroIdList != null && forceUseHeroIdList.contains(heroModelId)) {
				continue;
			}

//			Hero hero = player.getHeroMgr().getHeroByModerId(heroModelId);
			Hero hero = player.getHeroMgr().getHeroByModerId(player, heroModelId);
			if (hero == null) {
				continue;
			}

			if (subConditionValueMap == null || subConditionValueMap.isEmpty()) {// 没有其他限定条件
				matchHeroNum++;
				forceUseHeroIdList.add(heroModelId);
				continue;
			}

			boolean canMatch = true;
			// 检查条件
			for (Entry<Integer, Integer> e : subConditionValueMap.entrySet()) {
				IFettersSubCondition check = FettersBM.getCheckSubCondition(e.getKey());
				if (check == null) {
					continue;
				}

				if (!check.match(player, heroModelId, e.getValue())) {
					canMatch = false;
					break;
				}
			}

			if (!canMatch) {
				continue;
			}

			// 条件符合
			matchHeroNum++;
			forceUseHeroIdList.add(heroModelId);
		}
		return matchHeroNum >= heroNum;
	}

	@Override
	public int getSubRestrictConditionType() {
		return FettersBM.SubConditionRestrictType.HERO_NUM.type;
	}
}