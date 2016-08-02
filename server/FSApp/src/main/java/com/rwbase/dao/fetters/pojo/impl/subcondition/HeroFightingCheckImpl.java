package com.rwbase.dao.fetters.pojo.impl.subcondition;

import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.rwbase.dao.fetters.FettersBM;
import com.rwbase.dao.fetters.pojo.IFettersSubCondition;

/*
 * @author HC
 * @date 2016年4月28日 下午5:16:04
 * @Description 检查英雄战力
 */
public class HeroFightingCheckImpl implements IFettersSubCondition {

	@Override
	public boolean match(Player player, int checkId, int value) {
		HeroMgr heroMgr = player.getHeroMgr();
//		Hero hero = heroMgr.getHeroByModerId(checkId);
		Hero hero = heroMgr.getHeroByModerId(player, checkId);
		if (hero == null) {
			return false;
		}

		return hero.getFighting() >= value;
	}

	@Override
	public int getSubConditionType() {
		return FettersBM.SubConditionType.FIGHTING.type;
	}
}