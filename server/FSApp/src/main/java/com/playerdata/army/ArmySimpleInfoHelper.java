package com.playerdata.army;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.army.simple.ArmyHeroSimple;
import com.playerdata.army.simple.ArmyInfoSimple;
import com.rwbase.dao.item.pojo.ItemData;

public class ArmySimpleInfoHelper {

	public static ArmyInfoSimple fromArmyInfo(ArmyInfo armyInfo) {

		ArmyInfoSimple armyInfoSimple = new ArmyInfoSimple();
		ArmyHero player = armyInfo.getPlayer();
		armyInfoSimple.setPlayer(ArmyHeroSimple.newInstance(player));
		armyInfoSimple.setPlayerName(armyInfo.getPlayerName());
		armyInfoSimple.setPlayerHeadImage(armyInfo.getPlayerHeadImage());
		armyInfoSimple.setArmyMagic(armyInfo.getArmyMagic());

		List<ArmyHeroSimple> heroList = new ArrayList<ArmyHeroSimple>();
		for (ArmyHero armyHeroTmp : armyInfo.getHeroList()) {
			heroList.add(ArmyHeroSimple.newInstance(armyHeroTmp));
		}
		armyInfoSimple.setHeroList(heroList);

		int teamFighting = getTeamFighting(armyInfoSimple);
		armyInfoSimple.setTeamFighting(teamFighting);
		return armyInfoSimple;

	}

	public static ArmyInfoSimple getSimpleInfo(String playerId, List<String> heroIdList) {

		Player player = PlayerMgr.getInstance().find(playerId);
		ItemData magic = player.getMagic();

		ArmyInfoSimple armyInfoSimple = build(heroIdList, player, magic);
		return armyInfoSimple;
	}

	public static ArmyInfoSimple getSimpleInfo(String playerId, String magicID, List<String> heroIdList) {

		Player player = PlayerMgr.getInstance().find(playerId);
		ItemData magic = null;
		if (StringUtils.isNotBlank(magicID)) {
			magic = ItemBagMgr.getInstance().findBySlotId(playerId, magicID);
		}
		ArmyInfoSimple armyInfoSimple = build(heroIdList, player, magic);
		return armyInfoSimple;
	}

	private static ArmyInfoSimple build(List<String> heroIdList, Player player, ItemData magic) {
		Hero mainRoleHero = player.getMainRoleHero();
		ArmyHeroSimple armyPlayer = ArmyHeroSimple.newInstance(mainRoleHero);

		ArmyInfoSimple armyInfoSimple = new ArmyInfoSimple();
		armyInfoSimple.setPlayer(armyPlayer);
		armyInfoSimple.setPlayerName(player.getUserName());
		armyInfoSimple.setPlayerHeadImage(player.getHeadImage());

		if (magic != null) {
			armyInfoSimple.setArmyMagic(new ArmyMagic(magic));
		} else {
			armyInfoSimple.setArmyMagic(new ArmyMagic(player.getMagic()));
		}

		List<ArmyHeroSimple> heroList = getSimpleArmyHeros(player, heroIdList);
		armyInfoSimple.setHeroList(heroList);

		int teamFighting = getTeamFighting(armyInfoSimple);
		armyInfoSimple.setTeamFighting(teamFighting);
		return armyInfoSimple;
	}

	private static List<ArmyHeroSimple> getSimpleArmyHeros(Player player, List<String> heroIdList) {
		List<ArmyHeroSimple> heroList = new ArrayList<ArmyHeroSimple>();
		if (heroIdList == null)
			return heroList;
		HeroMgr heroMgr = player.getHeroMgr();
		for (String heroId : heroIdList) {
			if (StringUtils.isBlank(heroId) || StringUtils.equals(heroId, "0"))
				heroList.add(ArmyHeroSimple.newBlankInstance());
			else {
				// Hero heroTmp = heroMgr.getHeroById(heroId);
				Hero heroTmp = heroMgr.getHeroById(player, heroId);
				ArmyHeroSimple armyHero = ArmyHeroSimple.newInstance(heroTmp);
				heroList.add(armyHero);
			}
		}
		return heroList;
	}

	private static int getTeamFighting(ArmyInfoSimple armyInfoSimple) {
		int totalFighting = armyInfoSimple.getPlayer().getFighting();
		for (ArmyHeroSimple heroTmp : armyInfoSimple.getHeroList()) {
			totalFighting = totalFighting + heroTmp.getFighting();
		}
		return totalFighting;
	}
}
