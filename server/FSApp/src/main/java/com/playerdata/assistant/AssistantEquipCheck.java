package com.playerdata.assistant;

import java.util.Enumeration;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.dao.assistant.cfg.AssistantCfg.AssistantEventID;

public class AssistantEquipCheck extends DefaultAssistantChecker {

	
	@Override
	public AssistantEventID doCheck(Player player) {
		super.doCheck(player);
		if(check(player)){
			return AssistantEventID.TakeUpEquip;
		}
		return null;
	}
	
	private boolean check(Player player){
		Enumeration<Hero> heroMap = player.getHeroMgr().getHerosEnumeration();
		while (heroMap.hasMoreElements()) {
			Hero hero = (Hero) heroMap.nextElement();
			if(hero.getEquipMgr().canWearEquip()){
				return true;
			}
		}
		return false;
	}

}
