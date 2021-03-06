package com.bm.targetSell.param.attrs;

import java.util.Map;

import com.bm.targetSell.TargetSellManager;
import com.bm.targetSell.param.TargetSellRoleChange;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.playerdata.hero.core.FSHero;
import com.playerdata.hero.core.FSHeroMgr;
import com.rwbase.dao.targetSell.BenefitAttrCfg;
import com.rwbase.dao.targetSell.BenefitAttrCfgDAO;
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
		FSHero hero = FSHeroMgr.getInstance().getHeroById(userID, heroID);
		if(hero == null){
			return;
		}
		int heroModelID = userID.equals(heroID) ? MainRoleModelID : hero.getModeId();
		
		BenefitAttrCfg cfg = BenefitAttrCfgDAO.getInstance().getCfgByHeroModelIdAndProcessType(heroModelID, change.getId());
		if(cfg != null){
			value.addChange(cfg.getId());
		}
	}

}
