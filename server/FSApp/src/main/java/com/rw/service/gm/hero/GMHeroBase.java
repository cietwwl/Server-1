package com.rw.service.gm.hero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.Hero;
import com.playerdata.InlayMgr;
import com.playerdata.ItemCfgHelper;
import com.playerdata.MagicMgr;
import com.playerdata.Player;
import com.playerdata.SkillMgr;
import com.rwbase.dao.item.pojo.GemCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.role.pojo.RoleQualityCfg;
import com.rwbase.dao.skill.SkillCfgDAO;
import com.rwbase.dao.skill.pojo.Skill;
import com.rwbase.dao.skill.pojo.SkillCfg;
import com.rwbase.dao.user.LevelCfgDAO;
import com.rwbase.dao.user.pojo.LevelCfg;

public class GMHeroBase {
	
	private static int maxLevel = 0;
	private static int maxQuality = 0;
	private static int NOT_UPDATE_SIGN = -1;
	
	private final static Map<String, String> MaxSkillIdMap = new HashMap<String, String>();
	
	/**
	 * gm指令添加英雄
	 * @param templateId
	 * @param player
	 */
	public static void gmAddHero(String templateId, Player player){
		player.getHeroMgr().addHero(templateId);
	}
	
	/**
	 * 获取玩家的英雄列表
	 * @param player
	 * @return
	 */
	public static List<String> gmHeroIdList(Player player){
		List<String> heroIdList = player.getHeroMgr().getHeroIdList();
		return heroIdList;
	}
	
	/**
	 * 修改英雄的等级
	 * @param templateId
	 * @param level
	 * @param player
	 */
	public static void gmEditHeroLevel(Hero hero, int level, Player player) {
		if (level == -1) {
			return;
		}
		int maxLevel = gmGetMaxLevel();
		if (level > maxLevel) {
			level = maxLevel;
		}
		hero.gmEditHeroLevel(level);
	}

	/**
	 * 修改英雄的星级
	 * @param templateId
	 * @param starLevel
	 * @param player
	 */
	public static int gmEditHeroStarLevel(Hero hero, int star, Player player) {
		if (star == -1) {
			return -1;
		}
		int maxStar = gmGetMaxStar(hero.getTemplateId());
		if (star > maxStar) {
			star = maxStar;
		}
		hero.setStarLevel(star);
		return star;
	}
	
	public static void gmUpdateTemplateId(int sex, int carrer, int startLevel, Hero hero){
		RoleCfg cfg = RoleCfgDAO.getInstance().GetConfigBySexCareer(sex, carrer, startLevel);
		hero.setTemplateId(cfg.getRoleId());
	}
	
	/**
	 * 修改英雄品质
	 * @param templateId
	 * @param qualityId
	 * @param player
	 */
	public static void gmEditHeroQuality(Hero hero, String qualityId, Player player){
		if(qualityId.equals("")){
			return;
		}
		hero.setQualityId(qualityId);
		hero.gmCheckActiveSkill();
	}
	
	/**
	 * 修改英雄技能等级
	 * @param templateId
	 * @param skillId
	 * @param skillLevel
	 * @param player
	 */
	public static void gmEditHeroSkillLevel(Hero hero, String skillId, String maxSkillId, Player player){
		if(maxSkillId.equals("")){
			return;
		}
		SkillMgr skillMgr = hero.getSkillMgr();
		List<Skill> skillList = skillMgr.getSkillList(hero.getUUId());
		for (Skill skill : skillList) {
			if(skill.getSkillId().equals(skillId)){
				SkillCfg cfg = SkillCfgDAO.getInstance().getCfg(maxSkillId);
				skill.setLevel(cfg.getLevel());
				skill.setSkillId(maxSkillId);
				skillMgr.gmUpgradeSkillLv(player, hero.getUUId(), skill);
				return;
			}
		}
	}
	
	/**
	 * 英雄穿装备
	 * @param templateId
	 * @param equipIndex
	 * @param player
	 */
	public static void gmHeroEequip(Hero hero, int equipIndex, Player player){
		try {
			hero.getEquipMgr().gmEquip(player, hero.getUUId(), equipIndex);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param hero
	 * @param player
	 */
	public static void gmRemoveHeroEquip(Hero hero, Player player){
		hero.getEquipMgr().subAllEquip(player, hero.getUUId());
	}
	
	
	
	/**
	 * 附灵装备
	 * @param templateId
	 * @param equipIndex
	 * @param level    等级设成负数则可以升到顶级
	 * @param player
	 */
	public static void gmUpgradeHeroEquipment(Hero hero, int equipIndex, int level, Player player){
		if(level == -1){
			return;
		}
		hero.getEquipMgr().gmEquipAttach(player, hero.getUUId(), equipIndex, level);
	}
	
	/***
	 * 镶嵌宝石
	 * @param templateId
	 * @param player
	 * @param genType
	 * @param blnLimited
	 */
	public static void gmInlayJewel(Hero hero, Player player, int gemType, int level, boolean blnLimited){
		InlayMgr inlayMgr = hero.getInlayMgr();
		List<GemCfg> gemList = ItemCfgHelper.getGemCfgByType(gemType);
		int max = -1;
		int itemId = 0;
		if(blnLimited){
			for (GemCfg gemCfg : gemList) {
				if(gemCfg.getLevel() >= player.getLevel() && max < gemCfg.getLevel()){
					max = gemCfg.getLevel();
					itemId = gemCfg.getId();
					if(max == level){
						break;
					}
				} 
			}
		}else{
			for (GemCfg gemCfg : gemList) {
				if(max < gemCfg.getLevel()){
					max = gemCfg.getLevel();
					itemId = gemCfg.getId();
					if(max == level){
						break;
					}
				} 
			}
		}
		ItemData itemData = new ItemData();
		itemData.setCount(1);
		itemData.setUserId(player.getUserId());
		itemData.setModelId(itemId);
		
		inlayMgr.InlayGem(player, hero.getUUId(), itemData);
	}
	
	/**
	 * 移除所有宝石
	 * @param hero
	 * @param player
	 */
	public static void gmUnloadGem(Hero hero, Player player){
		hero.getInlayMgr().XieXiaAll(player, hero.getUUId());
		
	}
	
	/**
	 * 镶嵌宝石
	 * @param hero
	 * @param player
	 * @param itemId
	 */
	public static void gmInlayJewel(Hero hero, Player player, int itemId){
		InlayMgr inlayMgr = hero.getInlayMgr();
		ItemData itemData = new ItemData();
		itemData.setCount(1);
		itemData.setUserId(player.getUserId());
		itemData.setModelId(itemId);
		
		inlayMgr.InlayGem(player, hero.getUUId(), itemData);
		
	}
	
	/**
	 * 穿法宝 
	 * @param magicId
	 * @param player
	 */
	public static void gmWearMagic(String magicId, Player player){
		MagicMgr magicMgr = player.getMagicMgr();
		magicMgr.wearMagic(magicId);
	}
	
	/**
	 * 获取英雄最高的等级
	 * @return
	 */
	public static int gmGetMaxLevel() {
		if (maxLevel == 0) {
			List<LevelCfg> allCfg = LevelCfgDAO.getInstance().getAllCfg();
			for (LevelCfg levelCfg : allCfg) {
				if (levelCfg.getLevel() > maxLevel) {
					maxLevel = levelCfg.getLevel();
				}
			}

		}
		return maxLevel;
	}
	
	/**
	 * 获取最高星级
	 * @param templateId
	 * @return
	 */
	public static int gmGetMaxStar(String templateId) {
		RoleCfg roleCfg = (RoleCfg)RoleCfgDAO.getInstance().getCfgById(templateId);
		while (!roleCfg.getNextRoleId().equals("")) {
			roleCfg = (RoleCfg)RoleCfgDAO.getInstance().getCfgById(roleCfg.getNextRoleId());
		}
		return roleCfg.getStarLevel();
	}
	
	/**
	 * 获取最高级的品质
	 * @return
	 */
	public static int gmGetMaxQuality(){
		if(maxQuality == 0){
			List<RoleQualityCfg> allCfg = RoleQualityCfgDAO.getInstance().getAllCfg();
			for (RoleQualityCfg roleQualityCfg : allCfg) {
				if(maxQuality < roleQualityCfg.getQuality()){
					maxQuality = roleQualityCfg.getQuality();
				}
			}
		}
		return maxQuality;
	}
	
	/**
	 * 获取最高级技能id
	 * @param skillId
	 * @return
	 */
	public static String gmGetMaxSkillId(String skillId, int maxLevel){
		SkillCfg cfg = SkillCfgDAO.getInstance().getCfg(skillId);
		if (MaxSkillIdMap.containsKey(cfg.getSkillEffectId())) {
			return MaxSkillIdMap.get(cfg.getSkillEffectId());
		} else {
			while (!cfg.getNextSillId().equals("")) {
				cfg = SkillCfgDAO.getInstance().getCfg(cfg.getNextSillId());
				if(cfg.getLevel() == maxLevel){
					break;
				}
			}
			MaxSkillIdMap.put(cfg.getSkillEffectId(), cfg.getSkillId());
			return cfg.getSkillId();
		}
	}
	
	/**
	 * 获取指定等级的技能
	 * @param skillId
	 * @param level
	 * @return
	 */
	public static String gmGetSkillIdByLevel(String skillId, int level) {
		if (level == -1) {
			return "";
		}
		SkillCfg cfg = SkillCfgDAO.getInstance().getCfg(skillId);

		while (!cfg.getNextSillId().equals("")) {
			cfg = SkillCfgDAO.getInstance().getCfg(cfg.getNextSillId());
			if (cfg.getLevel() == level) {
				break;
			}
		}
		return cfg.getSkillId();
	}
	
	public static List<Skill> gmGetHeroBaseSkillList(RoleCfg roleCfg){
		List<Skill> list = new ArrayList<Skill>();
		list.add(parseSkill(roleCfg.getSkillId01()));
		list.add(parseSkill(roleCfg.getSkillId02()));
		list.add(parseSkill(roleCfg.getSkillId03()));
		list.add(parseSkill(roleCfg.getSkillId04()));
		list.add(parseSkill(roleCfg.getSkillId05()));
		return list;
	}
	
	private static Skill parseSkill(String skillValue){
		if(skillValue == null || skillValue.equals("")){
			return null;
		}
		Skill skill = new Skill();
		String[] tmpIds = skillValue.split("_");
		skill.setSkillId(skillValue);
		skill.setLevel(Integer.parseInt(tmpIds[1]));
		return skill;
	}
}
