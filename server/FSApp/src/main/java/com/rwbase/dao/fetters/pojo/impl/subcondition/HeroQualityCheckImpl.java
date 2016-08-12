package com.rwbase.dao.fetters.pojo.impl.subcondition;

import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.rwbase.dao.fetters.FettersBM;
import com.rwbase.dao.fetters.pojo.IFettersSubCondition;
import com.rwbase.dao.role.RoleQualityCfgDAO;

/*
 * @author HC
 * @date 2016年4月28日 下午5:10:24
 * @Description 检查英雄品质
 */
public class HeroQualityCheckImpl implements IFettersSubCondition {

	@Override
	public boolean match(Player player, int checkId, int value) {
		HeroMgr heroMgr = player.getHeroMgr();
		Hero hero = heroMgr.getHeroByModerId(checkId);
		if (hero == null) {
			return false;
		}

		int quality = RoleQualityCfgDAO.getInstance().getQuality(hero.getQualityId());
		return quality >= value;
	}

	@Override
	public int getSubConditionType() {
		return FettersBM.SubConditionType.QUALITY.type;
	}
}