package com.playerdata.assistant;

import java.util.Enumeration;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.dao.assistant.cfg.AssistantCfg.AssistantEventID;

public class AssistantUpdateSkillCheck extends DefaultAssistantChecker {

	
	@Override
	public AssistantEventID doCheck(Player player) {
		super.doCheck(player);
		if(check(player)){
			return AssistantEventID.UpdateSkill;
		}
		return null;
	}
	
	private boolean check(Player player){
//		Enumeration<Hero> heroMap = player.getHeroMgr().getHerosEnumeration();
		Enumeration<? extends Hero> heroMap = player.getHeroMgr().getHerosEnumeration(player);
		while (heroMap.hasMoreElements()) {
			Hero hero = (Hero) heroMap.nextElement();
			if(hero.getSkillMgr().canUpgradeSkill(player, hero.getUUId())){
				return true;
			}
		}
		return false;
	}

}
