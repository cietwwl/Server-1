package com.bm.targetSell.param.attrs;

import java.util.Map;

import com.bm.targetSell.param.ERoleAttrs;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.playerdata.eRoleType;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.targetSell.BenefitAttrCfgDAO;
import com.rwbase.dao.user.User;

public class AchieveStar extends AbsAchieveAttrValue {

	@Override
	public void achieveAttrValue(Player player, User user, ERoleAttrs roleType, Object param, Map<String, Object> AttrMap, BenefitAttrCfgDAO benefitAttrCfgDAO) {
		HeroMgr heroMgr = player.getHeroMgr();
		BenefitAttrCfg cfg = benefitAttrCfgDAO.getCfgById(roleType.getIdStr());
		int heroModelId = cfg.getHeroModelId();
		Hero hero = heroMgr.getHeroByModerId(player, heroModelId);
		if (hero != null) {
			int starLevel = hero.getStarLevel();
			AttrMap.put(cfg.getAttrName(), starLevel);
		}

	}

}
