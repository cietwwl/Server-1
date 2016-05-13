package com.playerdata.army;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.SkillMgr;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.skill.pojo.Skill;

public class ArmyInfoHelper {

	public static ArmyInfo getArmyInfo(String playerId, List<String> heroIdList) {

		Player player = PlayerMgr.getInstance().find(playerId);

		ArmyHero armyPlayer = getArmyHero(player.getMainRoleHero());
		armyPlayer.setPlayer(true);

		ArmyInfo armyInfo = new ArmyInfo();
		armyInfo.setPlayer(armyPlayer);
		armyInfo.setPlayerName(player.getUserName());
		armyInfo.setPlayerHeadImage(player.getHeadImage());
//		armyInfo.setGuildName(player.getGuildUserMgr().getGuildName());
//		armyPlayer.setFighting(player.);
		ItemData magic = player.getMagic();
		if(magic!=null){
			armyInfo.setArmyMagic(new ArmyMagic(magic));
		}

		List<ArmyHero> heroList = getArmyHeros(player, heroIdList);
		armyInfo.setHeroList(heroList);
		return armyInfo;
	}

	private static List<ArmyHero> getArmyHeros(Player player, List<String> heroIdList) {
		List<ArmyHero> heroList = new ArrayList<ArmyHero>();
		if (heroIdList == null) return heroList;
		HeroMgr heroMgr = player.getHeroMgr();
		for (String heroId : heroIdList) {
			Hero heroTmp = heroMgr.getHeroById(heroId);
			ArmyHero armyHero = getArmyHero(heroTmp);
			heroList.add(armyHero);
		}
		return heroList;
	}

	private static ArmyHero getArmyHero(Hero role) {
		SkillMgr skillMgr = role.getSkillMgr();
		List<Skill> skillList = skillMgr.getSkillList();
		AttrData totalAttrData = role.getAttrMgr().getTotalAttrData();
		RoleBaseInfo baseInfo = role.getRoleBaseInfoMgr().getBaseInfo();
		ArmyHero armyHero = new ArmyHero(baseInfo, totalAttrData, skillList);
		armyHero.setFighting(role.getFighting());
		return armyHero;
	}
	// private static ArmyHero getArmyHero(Hero hero) {
	// SkillMgr skillMgr = hero.getSkillMgr();
	//
	// List<Skill> skillList = skillMgr.getSkillList();
	// AttrData totalAttrData = hero.getAttrMgr().getTotalAttrData();
	// RoleBaseInfo baseInfo = hero.getRoleBaseInfoMgr().getBaseInfo();
	// ArmyHero armyHero = new ArmyHero(baseInfo, totalAttrData, skillList);
	//
	// return armyHero;
	// }

}
