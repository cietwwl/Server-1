package com.playerdata.assistant;

import java.util.Enumeration;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.dao.assistant.cfg.AssistantCfg.AssistantEventID;

public class AssistantEquipCheck implements IAssistantCheck{

	
	@Override
	public AssistantEventID doCheck(Player player) {
		
		if(check(player)){
			return AssistantEventID.TakeUpEquip;
		}
		return null;
	}
	
	private boolean check(Player player){
//		Enumeration<Hero> heroMap = player.getHeroMgr().getHerosEnumeration();
		Enumeration<? extends Hero> heroMap = player.getHeroMgr().getHerosEnumeration(player);
		while (heroMap.hasMoreElements()) {
			Hero hero = (Hero) heroMap.nextElement();
			if(hero.getEquipMgr().canWearEquip(player, hero.getUUId())){
				return true;
			}
		}
		return false;
	}

}
