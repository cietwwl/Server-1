package com.playerdata.army;

import java.util.ArrayList;
import java.util.List;

import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.SkillMgr;
import com.playerdata.army.simple.ArmyHeroSimple;
import com.playerdata.army.simple.ArmyInfoSimple;
import com.playerdata.dataSyn.ClientDataSynMgr;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.skill.pojo.Skill;

public class ArmyInfoHelper {

	public static ArmyInfo getArmyInfo(String playerId, List<String> heroIdList) {

		Player player = PlayerMgr.getInstance().find(playerId);
		ItemData magic = player.getMagic();

		ArmyInfo armyInfo = build(heroIdList, player, magic);
		return armyInfo;
	}
	
	public static ArmyInfo getArmyInfo(ArmyInfoSimple armyInfoSimple,boolean setCurData) {
		String playerId = armyInfoSimple.getPlayer().getId();
		List<String> heroIdList = armyInfoSimple.getHeroIdList();
		Player player = PlayerMgr.getInstance().find(playerId );
		
		ItemData magic = player.getItemBagMgr().getFirstItemByModelId(armyInfoSimple.getArmyMagic().getModelId());
		if(magic == null) magic = player.getMagic();
		
		ArmyInfo armyInfo = build(heroIdList , player, magic);
		if(setCurData){
			setCurData(armyInfo,armyInfoSimple);
		}
		return armyInfo;
	}

	private static void setCurData(ArmyInfo armyInfo, ArmyInfoSimple armyInfoSimple) {
		ArmyHero armyHero_ = armyInfo.getPlayer();
		if(armyHero_!=null){
			armyHero_.setCurAttrData(armyInfoSimple.getPlayer().getCurAttrData());
		}
		
		List<ArmyHero> heroList = armyInfo.getHeroList();
		for (ArmyHero armyHero : heroList) {
			String heroId = armyHero.getRoleBaseInfo().getId();
			ArmyHeroSimple simpleHero = armyInfoSimple.getByHeroId(heroId);
			armyHero.setCurAttrData(simpleHero.getCurAttrData());
		}
		
	}

	private static ArmyInfo build(List<String> heroIdList, Player player, ItemData magic) {
		ArmyHero armyPlayer = getArmyHero(player.getMainRoleHero());
		armyPlayer.setPlayer(true);

		ArmyInfo armyInfo = new ArmyInfo();
		armyInfo.setPlayer(armyPlayer);
		armyInfo.setPlayerName(player.getUserName());
		armyInfo.setPlayerHeadImage(player.getHeadImage());
//		armyInfo.setGuildName(player.getGuildUserMgr().getGuildName());
//		armyPlayer.setFighting(player.);
		if(magic!=null){
			armyInfo.setArmyMagic(new ArmyMagic(magic));
		}else{
			armyInfo.setArmyMagic(new ArmyMagic(player.getMagic()));
		}

		List<ArmyHero> heroList = getArmyHeros(player, heroIdList);
		armyInfo.setHeroList(heroList);
		return armyInfo;
	}
	
	public static ArmyHero getArmyHero(String playerId, String heroId){
		if (playerId == null || heroId == null) return null;
		Player player = PlayerMgr.getInstance().find(playerId);
		return getArmyHero(player,heroId);
	}
	
	public static ArmyHero getArmyHero(Player player, String heroId){
		if (player == null || heroId == null) return null;
		HeroMgr heroMgr = player.getHeroMgr();
		Hero heroTmp = heroMgr.getHeroById(heroId);
		ArmyHero armyHero = getArmyHero(heroTmp);
		return armyHero;
	}

	private static List<ArmyHero> getArmyHeros(Player player, List<String> heroIdList) {
		List<ArmyHero> heroList = new ArrayList<ArmyHero>();
		if (heroIdList == null) return heroList;
		HeroMgr heroMgr = player.getHeroMgr();
		for (String heroId : heroIdList) {
			Hero heroTmp = heroMgr.getHeroById(heroId);
			if(heroTmp == null){
				continue;
			}
			ArmyHero armyHero = getArmyHero(heroTmp);
			heroList.add(armyHero);
		}
		return heroList;
	}

	private static ArmyHero getArmyHero(Hero role) {
		if (role == null) return null;
		SkillMgr skillMgr = role.getSkillMgr();
		List<Skill> skillList = skillMgr.getSkillList();
		AttrData totalAttrData = role.getAttrMgr().getTotalAttrData();
		RoleBaseInfo baseInfo = role.getRoleBaseInfoMgr().getBaseInfo();
		ArmyHero armyHero = new ArmyHero(baseInfo, totalAttrData, skillList);
		armyHero.setFighting(role.getFighting());
		return armyHero;
	}

	public static ArmyInfoSimple getSimpleInfo(String playerId, String magicID, List<String> heroIdList) {
		return ArmySimpleInfoHelper.getSimpleInfo(playerId, magicID, heroIdList);
	}

	public CurArmyAttrData fromJsonToCurArmy(String json){
		
		try {
			return (CurArmyAttrData) ClientDataSynMgr.fromClientJson2Data(CurArmyAttrData.class, json);
		} catch (Exception e) {
			GameLog.error(LogModule.Util, "ArmyInfoHelper[fromJsonToCurArmy]", "json parse error,json:"+json, e);
		}
		return null;
		
	}
	
}
