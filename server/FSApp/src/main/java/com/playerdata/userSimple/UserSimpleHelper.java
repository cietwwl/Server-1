package com.playerdata.userSimple;

import java.util.ArrayList;
import java.util.List;

import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.SkillMgr;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.skill.pojo.Skill;

public class UserSimpleHelper {

	public static UserSimpleInfo getUserSimple(String playerId, List<String> heroIdList) {

		Player player = PlayerMgr.getInstance().find(playerId);

		HeroSimple armyPlayer = getHeroSimple(player.getMainRoleHero());
		armyPlayer.setPlayer(true);

		UserSimpleInfo userSimple = new UserSimpleInfo();
		userSimple.setPlayer(armyPlayer);
		userSimple.setPlayerName(player.getUserName());
		userSimple.setPlayerHeadImage(player.getHeadImage());
		//userSimple.setGuildName(player.getGuildUserMgr().getGuildName());

		List<HeroSimple> heroList = getHeroSimples(player, heroIdList);
		userSimple.setHeroList(heroList);
		return userSimple;
	}

	private static List<HeroSimple> getHeroSimples(Player player, List<String> heroIdList) {
		List<HeroSimple> heroList = new ArrayList<HeroSimple>();
		HeroMgr heroMgr = player.getHeroMgr();
		for (String heroId : heroIdList) {
			Hero heroTmp = heroMgr.getHeroById(heroId);
			HeroSimple armyHero = getHeroSimple(heroTmp);
			heroList.add(armyHero);
		}
		return heroList;
	}

	private static HeroSimple getHeroSimple(Hero role) {
		SkillMgr skillMgr = role.getSkillMgr();
		List<Skill> skillList = skillMgr.getSkillList(role.getUUId());
		AttrData totalAttrData = role.getAttrMgr().getTotalAttrData();
		RoleBaseInfo baseInfo = role.getRoleBaseInfoMgr().getBaseInfo();
		HeroSimple armyHero = new HeroSimple(baseInfo, totalAttrData, skillList);
		armyHero.setFighting(role.getFighting());
		return armyHero;
	}

}
