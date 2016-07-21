package com.rwbase.dao.fetters.pojo.impl.subcondition;

import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.rwbase.dao.fetters.FettersBM;
import com.rwbase.dao.fetters.pojo.IFettersSubCondition;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;

/*
 * @author HC
 * @date 2016年4月28日 下午5:13:30
 * @Description 
 */
public class HeroStarCheckImpl implements IFettersSubCondition {

	@Override
	public boolean match(Player player, int checkId, int value) {
		HeroMgr heroMgr = player.getHeroMgr();
		Hero hero = heroMgr.getHeroByModerId(checkId);
		if (hero == null) {
			return false;
		}

		RoleBaseInfo baseInfo = hero.getRoleBaseInfoMgr().getBaseInfo();
		if (baseInfo == null) {
			return false;
		}

		return baseInfo.getStarLevel() >= value;
	}

	@Override
	public int getSubConditionType() {
		return FettersBM.SubConditionType.STAR.type;
	}
}