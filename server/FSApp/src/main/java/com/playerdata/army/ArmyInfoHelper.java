package com.playerdata.army;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.common.beanCopy.FastBeanCopyer;
import com.common.beanCopy.bak.BeanCopyerGen;
import com.log.GameLog;
import com.log.LogModule;
import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.ItemBagMgr;
import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.playerdata.SkillMgr;
import com.playerdata.army.simple.ArmyHeroSimple;
import com.playerdata.army.simple.ArmyInfoSimple;
import com.rw.service.fashion.FashionHandle;
import com.rwbase.common.attrdata.AttrData;
import com.rwbase.common.attrdata.AttrData.Builder;
import com.rwbase.dao.battle.pojo.BattleCfgDAO;
import com.rwbase.dao.battle.pojo.cfg.CopyMonsterInfoCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.skill.pojo.SkillItem;
import com.rwproto.FashionServiceProtos.FashionUsed;

public class ArmyInfoHelper {

	public static ArmyInfo getArmyInfo(String playerId, List<String> heroIdList) {

		Player player = PlayerMgr.getInstance().find(playerId);
		ItemData magic = player.getMagic();

		ArmyInfo armyInfo = build(heroIdList, player, magic);

		return armyInfo;
	}

	public static ArmyInfo getArmyInfo(ArmyInfoSimple armyInfoSimple, boolean setCurData) {
		String playerId = armyInfoSimple.getPlayer().getId();
		List<String> heroIdList = armyInfoSimple.getHeroIdList();
		Player player = PlayerMgr.getInstance().find(playerId);

		ItemData magic = ItemBagMgr.getInstance().getFirstItemByModelId(playerId, armyInfoSimple.getArmyMagic().getModelId());
		if (magic == null)
			magic = player.getMagic();

		ArmyInfo armyInfo = build(heroIdList, player, magic);

		for (ArmyHero hero : armyInfo.getHeroList()) {
			int index = heroIdList.indexOf(hero.getRoleBaseInfo().getId());
			if (index < 0)
				continue;
			hero.setPosition(index + 1);
		}

		if (setCurData) {
			setCurData(armyInfo, armyInfoSimple);
		}

		return armyInfo;
	}

	private static void setCurData(ArmyInfo armyInfo, ArmyInfoSimple armyInfoSimple) {
		ArmyHero armyHero_ = armyInfo.getPlayer();
		if (armyHero_ != null) {
			armyHero_.setCurAttrData(armyInfoSimple.getPlayer().getCurAttrData());
		}

		List<ArmyHero> heroList = armyInfo.getHeroList();
		for (ArmyHero armyHero : heroList) {
			String heroId = armyHero.getRoleBaseInfo().getId();
			ArmyHeroSimple simpleHero = armyInfoSimple.getArmyHeroByID(heroId);
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
		// armyInfo.setGuildName(player.getGuildUserMgr().getGuildName());
		// armyPlayer.setFighting(player.);
		if (magic != null) {
			armyInfo.setArmyMagic(new ArmyMagic(magic));
		} else {
			armyInfo.setArmyMagic(new ArmyMagic(player.getMagic()));
		}
		FashionUsed.Builder fashion = FashionHandle.getInstance().getFashionUsedProto(player.getUserId());
		if (fashion != null) {
			ArmyFashion armyFs = new ArmyFashion();
			armyFs.setPetId(fashion.getPetId());
			armyFs.setSuitId(fashion.getSuitId());
			armyFs.setWingId(fashion.getWingId());
			armyFs.setCareer(player.getCareer());
			armyFs.setGender(player.getSex());
			armyInfo.setArmyFashion(armyFs);
		}
		List<ArmyHero> heroList = getArmyHeros(player, heroIdList);
		armyInfo.setHeroList(heroList);
		return armyInfo;
	}

	public static ArmyHero getArmyHero(String playerId, String heroId) {
		if (playerId == null || heroId == null)
			return null;
		Player player = PlayerMgr.getInstance().find(playerId);
		return getArmyHero(player, heroId);
	}

	public static ArmyHero getArmyHero(Player player, String heroId) {
		if (player == null || heroId == null)
			return null;
		HeroMgr heroMgr = player.getHeroMgr();
		// Hero heroTmp = heroMgr.getHeroById(heroId);
		Hero heroTmp = heroMgr.getHeroById(player, heroId);
		ArmyHero armyHero = getArmyHero(heroTmp);
		return armyHero;
	}

	private static List<ArmyHero> getArmyHeros(Player player, List<String> heroIdList) {
		List<ArmyHero> heroList = new ArrayList<ArmyHero>();
		if (heroIdList == null)
			return heroList;
		HeroMgr heroMgr = player.getHeroMgr();
		for (String heroId : heroIdList) {
			// Hero heroTmp = heroMgr.getHeroById(heroId);
			Hero heroTmp = heroMgr.getHeroById(player, heroId);
			if (heroTmp == null) {
				continue;
			}
			//这里要去掉主角
			if(StringUtils.equals(heroTmp.getId(), player.getUserId())){
				continue;
			}
			ArmyHero armyHero = getArmyHero(heroTmp);
			heroList.add(armyHero);
		}
		return heroList;
	}

	private static ArmyHero getArmyHero(Hero role) {
		if (role == null)
			return null;
		SkillMgr skillMgr = role.getSkillMgr();
		List<SkillItem> skillList = skillMgr.getSkillList(role.getUUId());
		AttrData totalAttrData = role.getAttrMgr().getTotalAttrData();
		
		//------------这里不可以直接用 totalAttrData,否则有被外部修改的危险  by Alex 2016.11.26
		AttrData.Builder builder = new Builder();		
		FastBeanCopyer.getInstance().copy(totalAttrData, builder);
		
		AttrData data = builder.build();
		
		
		ArmyHero armyHero = new ArmyHero(role, data, skillList);
		armyHero.setFighting(role.getFighting());
		return armyHero;
	}

	public static ArmyInfoSimple getSimpleInfo(String playerId, String magicID, List<String> heroIdList) {
		return ArmySimpleInfoHelper.getSimpleInfo(playerId, magicID, heroIdList);
	}

	public static ArmyInfo buildMonsterArmy(List<String> monsterIdList, List<CurAttrData> attrDataList, String copyID) {
		ArmyInfo armyInfo = buildMonsterArmy(monsterIdList);
		setCurAttrData(armyInfo, attrDataList);
		setPositionOffset(armyInfo, copyID);
		return armyInfo;
	}

	
	public static ArmyInfo buildMonsterArmy (String monsterId, String copyID)
	{
		ArmyInfo army = new ArmyInfo ();	

		ArmyHero armyHero = MonsterArmyHelper.buildMonster(monsterId);
		army.setPlayer(armyHero);
		setPositionOffset(army, copyID);

		return army;
	}

	/**
	 * 设置位置信息
	 * 
	 * @param armyInfo
	 * @param copyID
	 */
	private static void setPositionOffset(ArmyInfo armyInfo, String copyID) {
		CopyMonsterInfoCfg cfg = BattleCfgDAO.getInstance().getCopyMonsterInfoByCopyID(copyID);
		if (cfg == null) {
			GameLog.cfgError(LogModule.COMMON, "ArmyInfoHelper[setPositionOffset]", "读取armyInfo 位置信息时battle表无法找到copyID=" + copyID + "的数据记录");
			return;
		}

		if (armyInfo.getPlayer() != null) {

			ArmyVector3 position = cfg.getPosition(armyInfo.getPlayer().getRoleBaseInfo().getId());
			if (position != null) {
				armyInfo.getPlayer().setPositionOffset(position);
			}
		}

		for (ArmyHero h : armyInfo.getHeroList()) {
			ArmyVector3 v = cfg.getPosition(h.getRoleBaseInfo().getId());
			if (v != null) {
				h.setPositionOffset(v);
			}
		}

	}

	public static ArmyInfo buildMonsterArmy(List<String> monsterIdList) {
		ArmyInfo army = new ArmyInfo();

		for (String monsterId : monsterIdList) {
			ArmyHero armyHero = MonsterArmyHelper.buildMonster(monsterId);
			army.addHero(armyHero);
		}

		return army;
	}
	
	/**
	 * 增加army的攻击力
	 * @param army
	 * @param percent 增加万分比
	 */
	public static void IncreaseArmyAttrack(ArmyInfo army, int percent){
		ArmyHero player = army.getPlayer();
		if(player != null){
			AttrData.Builder builder = new Builder();
			builder.setPhysiqueAttack(percent);
			player.getAttrData().addPercent(builder.build());
		}
		
		for (ArmyHero hero : army.getHeroList()) {
			if(hero == null){
				continue;
			}
			AttrData.Builder builder = new Builder();
			builder.setPhysiqueAttack(percent);
			hero.getAttrData().addPercent(builder.build());
		}
	}
	
	
	private static void setCurAttrData(ArmyInfo armyInfo, List<CurAttrData> attrDataList){
		
		Map<String,ArmyHero> heroDic = new HashMap<String,ArmyHero>();
	
		ArmyHero player = armyInfo.getPlayer();
		if (player != null) {
			heroDic.put(player.getRoleBaseInfo().getId(), player);
		}

		for (ArmyHero hero : armyInfo.getHeroList()) {
			heroDic.put(hero.getRoleBaseInfo().getId(), hero);
		}

		for (CurAttrData attrData : attrDataList) {
			String attrDataId = attrData.getId();
			if (heroDic.containsKey(attrDataId)) {
				heroDic.get(attrDataId).setCurAttrData(attrData);
			}
		}

	}

}
