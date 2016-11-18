package com.bm.targetSell.param.attrs;

import java.util.Map;

import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.user.User;

public class AchieveStar extends AbsAchieveAttrValue {

	@Override
	public void achieveAttrValue(Player player, User user, BenefitAttrCfg cfg, Map<String, Object> AttrMap) {
		HeroMgr heroMgr = player.getHeroMgr();
		int heroModelId = Integer.parseInt(cfg.getParam());
		Hero hero = heroMgr.getHeroByModerId(player, heroModelId);
		if (hero != null) {
			int starLevel = hero.getStarLevel();
			AttrMap.put(cfg.getAttrName(), starLevel);
		}

	}

}
