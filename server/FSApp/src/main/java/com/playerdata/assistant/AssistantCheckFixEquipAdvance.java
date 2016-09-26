package com.playerdata.assistant;

import java.util.List;

import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipQualityCfgDAO;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipQualityCfgDAO;
import com.rwbase.dao.assistant.cfg.AssistantCfg.AssistantEventID;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class AssistantCheckFixEquipAdvance extends DefaultAssistantChecker {

	@Override
	public AssistantEventID doCheck(Player player) {
		super.doCheck(player);
		
		HeroMgr heroMgr = player.getHeroMgr();
		List<String> heroIdList = heroMgr.getHeroIdList(player);
		AssistantEventID result = null;
		if(CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.FIX_EQUIP, player)){
			result = checkQualityUP(player, heroMgr, heroIdList);
			if (result == null){
				result = checkLevelUP(player, heroMgr, heroIdList);
			}

		}
		
		return result;
	}

	private AssistantEventID checkLevelUP(Player player, HeroMgr heroMgr, List<String> heroIdList) {
		for (String id : heroIdList) {
			Hero hero = heroMgr.getHeroById(player, id);
			String heroId = hero.getUUId();
			
			List<String> qualityUpListTmp = hero.getFixExpEquipMgr().levelUpList(player, heroId);
			if(!qualityUpListTmp.isEmpty()){
				param = heroId;
				return AssistantEventID.FixEquipAdvance;
			}
			qualityUpListTmp.addAll(hero.getFixNormEquipMgr().levelUpList(player, heroId));
			if(!qualityUpListTmp.isEmpty()){
				param = heroId;
				return AssistantEventID.FixEquipAdvance;
			}
		}
		return null;
	}

	private AssistantEventID checkQualityUP(Player player, HeroMgr heroMgr, List<String> heroIdList) {
		FixExpEquipQualityCfgDAO expCfgDAO = FixExpEquipQualityCfgDAO.getInstance();
		FixNormEquipQualityCfgDAO normalCfgDAO = FixNormEquipQualityCfgDAO.getInstance();
		for (String id : heroIdList) {
			Hero hero = heroMgr.getHeroById(player, id);
			String heroId = hero.getUUId();
			
			List<String> qualityUpListTmp = hero.getFixExpEquipMgr().qualityUpList(player, heroId, expCfgDAO);			
			qualityUpListTmp.addAll(hero.getFixNormEquipMgr().qualityUpList(player, heroId, expCfgDAO ,normalCfgDAO));
			if(!qualityUpListTmp.isEmpty()){
				param = qualityUpListTmp.get(0);
				return AssistantEventID.FixEquipAdvance;
			}
		}
		return null;
	}

}
