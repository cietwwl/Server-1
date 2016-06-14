package com.rw.service.redpoint.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.rw.service.redpoint.RedPointType;

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

		checkQualityUP(map, player, heroMgr, heroIdList);
		
		checkStarUp(map, player, heroMgr, heroIdList);

	}

	private void checkStarUp(Map<RedPointType, List<String>> map,
			Player player, HeroMgr heroMgr, List<String> heroIdList) {
		List<String> starParamList = new ArrayList<String>();
		for (String id : heroIdList) {
			Hero hero = heroMgr.getHeroById(id);
			String heroId = hero.getUUId();	
			
			
			List<String> starUpList = hero.getFixExpEquipMgr().starUpList(player, heroId);			
			starUpList.addAll(hero.getFixNormEquipMgr().starUpList(player, heroId));
			if(!starUpList.isEmpty()){
				String param = toParam(heroId,starUpList);
				starParamList.add(param);
			}		
			
			
		}
		if(!starParamList.isEmpty()){
			map.put(RedPointType.HERO_FIX_EQUIP_START_UP, starParamList);
		}
	}

	private void checkQualityUP(Map<RedPointType, List<String>> map,Player player, HeroMgr heroMgr, List<String> heroIdList) {
		
		List<String> qualityParamList = new ArrayList<String>();
		for (String id : heroIdList) {
			Hero hero = heroMgr.getHeroById(id);
			String heroId = hero.getUUId();
			
			List<String> qualityUpList = hero.getFixExpEquipMgr().qualityUpList(player, heroId);			
			qualityUpList.addAll(hero.getFixNormEquipMgr().qualityUpList(player, heroId));
			if(!qualityUpList.isEmpty()){
				String param = toParam(heroId,qualityUpList);
				qualityParamList.add(param);
			}
			
			
		}
		if(!qualityParamList.isEmpty()){
			map.put(RedPointType.HERO_FIX_EQUIP_QUALITY_UP, qualityParamList);
		}
	}

	private String toParam(String heroId, List<String> fixEquipIdList) {

		StringBuilder param = new StringBuilder(heroId);
		param.append(":");
		for (String fixEquipId : fixEquipIdList) {
			param.append(fixEquipId);
			param.append(",");
		}
		return StringUtils.removeEnd(param.toString(), ",");
	}


	
	
	
	
}
