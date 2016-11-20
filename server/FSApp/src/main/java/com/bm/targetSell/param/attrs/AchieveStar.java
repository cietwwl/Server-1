package com.bm.targetSell.param.attrs;

import java.util.Map;

import com.bm.targetSell.param.TargetSellRoleChange;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.user.User;

public class AchieveStar implements AbsAchieveAttrValue {

	@Override
	public void achieveAttrValue(Player player, User user, BenefitAttrCfg cfg, Map<String, Object> AttrMap) {
		HeroMgr heroMgr = player.getHeroMgr();
		int heroModelId = Integer.parseInt(cfg.getParam());
		Hero hero;
		if(heroModelId == MainRoleModelID){
			hero = player.getMainRoleHero();
		}else{
			hero = heroMgr.getHeroByModerId(player, heroModelId);
		}
		if (hero != null) {
			int starLevel = hero.getStarLevel();
			AttrMap.put(cfg.getAttrName(), starLevel);
		}

	}

	@Override
	public void addHeroAttrs(String userID, String heroID, EAchieveType change, TargetSellRoleChange value) {
		
	}

}
