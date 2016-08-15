package com.rw.service.redpoint.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.dao.openLevelLimit.CfgOpenLevelLimitDAO;
import com.rwbase.dao.openLevelLimit.eOpenLevelType;

public class HeroFixEquipChecker implements RedPointCollector {

	// 装备部位列表，游戏逻辑中暂时没有装备部位记录
	private LinkedList<Integer> equipList;

	{
		this.equipList = new LinkedList<Integer>();
		for (int i = 0; i < 6; i++) {
			this.equipList.add(i);
		}
	}

	@Override
	public void fillRedPoints(Player player, Map<RedPointType, List<String>> map) {
	
		HeroMgr heroMgr = player.getHeroMgr();
		List<String> heroIdList = heroMgr.getHeroIdList();

		Set<String> heroIdSet = new HashSet<String>();
		if(CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.FIX_EQUIP, player.getLevel())){
			
			checkQualityUP(map, player, heroMgr, heroIdList, heroIdSet);
		}
		
		if(CfgOpenLevelLimitDAO.getInstance().isOpen(eOpenLevelType.FIX_EQUIP_STAR, player.getLevel())){
			
			checkStarUp(map, player, heroMgr, heroIdList, heroIdSet);
		}
		
		if(!heroIdSet.isEmpty()){
			List<String> redHeroIdList = new ArrayList<String>(heroIdSet);
			map.put(RedPointType.FIX_EQUIP_HERO_LIST, redHeroIdList);
		}

	}

	/*
	 * 觉醒列表
	 */
	private void checkStarUp(Map<RedPointType, List<String>> map, Player player, HeroMgr heroMgr, List<String> heroIdList, Set<String> heroIdSet) {
		List<String> starUPList = new ArrayList<String>();
		for (String id : heroIdList) {
			Hero hero = heroMgr.getHeroById(id);
			String heroId = hero.getUUId();	
			
			
			List<String> starUpListTmp = hero.getFixExpEquipMgr().starUpList(player, heroId);			
			starUpListTmp.addAll(hero.getFixNormEquipMgr().starUpList(player, heroId));
			if(!starUpListTmp.isEmpty()){
				starUPList.addAll(starUpListTmp);
				heroIdSet.add(hero.getTemplateId());
			}		
			
			
		}
		if(!starUPList.isEmpty()){
			map.put(RedPointType.HERO_FIX_EQUIP_START_UP, starUPList);
		}
	}

	/*
	 * 进阶品质列表
	 */
	private void checkQualityUP(Map<RedPointType, List<String>> map,Player player, HeroMgr heroMgr, List<String> heroIdList, Set<String> heroIdSet) {
		
		List<String> qualityUpList = new ArrayList<String>();
		for (String id : heroIdList) {
			Hero hero = heroMgr.getHeroById(id);
			String heroId = hero.getUUId();
			
			List<String> qualityUpListTmp = hero.getFixExpEquipMgr().qualityUpList(player, heroId);			
			qualityUpListTmp.addAll(hero.getFixNormEquipMgr().qualityUpList(player, heroId));
			if(!qualityUpListTmp.isEmpty()){
				qualityUpList.addAll(qualityUpListTmp);
				heroIdSet.add(hero.getTemplateId());
			}
			
			
		}
		if(!qualityUpList.isEmpty()){
			map.put(RedPointType.HERO_FIX_EQUIP_QUALITY_UP, qualityUpList);
		}
	}

//	private String toParam(String heroId, List<String> fixEquipIdList) {
//
//		StringBuilder param = new StringBuilder(heroId);
//		param.append(":");
//		for (String fixEquipId : fixEquipIdList) {
//			param.append(fixEquipId);
//			param.append(",");
//		}
//		return StringUtils.removeEnd(param.toString(), ",");
//	}


	
	
	
	
}
