package com.playerdata.assistant;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.dao.assistant.cfg.AssistantCfg.AssistantEventID;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class AssistantHeroLevelUpCheck extends DefaultAssistantChecker {

	
	@Override
	public AssistantEventID doCheck(Player player) {
		super.doCheck(player);
		if(check(player)){
			return AssistantEventID.HeroLevelUp;
		}
		return null;
	}
	
	private boolean check(Player player){
//		Enumeration<Hero> heroMap = player.getHeroMgr().getHerosEnumeration();
		Enumeration<? extends Hero> heroMap = player.getHeroMgr().getHerosEnumeration(player);
		List<Integer> materialId =new ArrayList<Integer>();
		materialId.add(803001);//经验丹id
		materialId.add(803002);//经验丹id
		materialId.add(803003);//经验丹id
		materialId.add(803004);//经验丹id
		boolean hasMaterail = false; 
		if(!CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.USE_EXP_ITEM, player.getLevel())){
			return hasMaterail;
		}
		for (int i = 0; i < materialId.size(); i++) {
			int count = player.getItemBagMgr().getItemCountByModelId(materialId.get(i));
			if(count > 0){
				hasMaterail = true;
				break;
			}
		}
		if(!hasMaterail){
			return false;
		}
		
		while (heroMap.hasMoreElements()) {
			Hero hero = (Hero) heroMap.nextElement();
			if(hero.getLevel() < player.getLevel()){
				return true;
			}
		}
		return false;
	}

}
