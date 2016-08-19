package com.playerdata.assistant;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.rwbase.dao.assistant.cfg.AssistantCfg.AssistantEventID;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class AssistantCheckFixEquipStarUp extends DefaultAssistantChecker {

	@Override
	public AssistantEventID doCheck(Player player) {
		super.doCheck(player);
		HeroMgr heroMgr = player.getHeroMgr();
		List<String> heroIdList = heroMgr.getHeroIdList(player);
		//检查是否可以觉醒
		if(CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.FIX_EQUIP_STAR, player.getLevel())){
			return checkStarUp(player, heroMgr, heroIdList);
		}

		return null;
	}
	/*
	 * 觉醒列表
	 */
	private AssistantEventID checkStarUp(Player player, HeroMgr heroMgr, List<String> heroIdList) {
		for (String id : heroIdList) {
			Hero hero = heroMgr.getHeroById(player, id);
			String heroId = hero.getUUId();	
			
			List<String> starUpListTmp = hero.getFixExpEquipMgr().starUpList(player, heroId);			
			if(!starUpListTmp.isEmpty()){
				param = heroId;
				return AssistantEventID.FixEquipUp;
			}		
			starUpListTmp.addAll(hero.getFixNormEquipMgr().starUpList(player, heroId));
			if(!starUpListTmp.isEmpty()){
				param = heroId;
				return AssistantEventID.FixEquipUp;
			}		
		}
		return null;
	}

}
