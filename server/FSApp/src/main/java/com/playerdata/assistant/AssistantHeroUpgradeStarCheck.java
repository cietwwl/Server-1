package com.playerdata.assistant;

import java.util.Enumeration;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.dao.assistant.cfg.AssistantCfg.AssistantEventID;

public class AssistantHeroUpgradeStarCheck extends DefaultAssistantChecker {

	@Override
	public AssistantEventID doCheck(Player player) {
		super.doCheck(player);
		if(hasHeroToUpgradeStar(player)){
			return AssistantEventID.HeroAdvance;
		}
		return null;
	}
	
	private boolean hasHeroToUpgradeStar(Player player){
		Enumeration<Hero> heroMap = player.getHeroMgr().getHerosEnumeration();
		while (heroMap.hasMoreElements()) {
			Hero hero = (Hero) heroMap.nextElement();
			if (hero.canUpgradeStar() == 0) {
				return true;
			}
//			if(hero.getEquipMgr().getEquipCount() >= 6){
//				return true;
//			}
		}
		return false;
	}

}
