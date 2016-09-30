package com.rw.service.gm.hero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.playerdata.Hero;
import com.playerdata.HeroMgr;
import com.playerdata.InlayMgr;
import com.playerdata.ItemBagMgr;
import com.playerdata.ItemCfgHelper;
import com.playerdata.MagicMgr;
import com.playerdata.Player;
import com.playerdata.SkillMgr;
import com.playerdata.fixEquip.cfg.FixEquipCfg;
import com.playerdata.fixEquip.cfg.FixEquipCfgDAO;
import com.playerdata.fixEquip.exp.FixExpEquipMgr;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipQualityCfg;
import com.playerdata.fixEquip.exp.cfg.FixExpEquipQualityCfgDAO;
import com.playerdata.fixEquip.exp.data.FixExpEquipDataItem;
import com.playerdata.fixEquip.norm.FixNormEquipMgr;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipQualityCfg;
import com.playerdata.fixEquip.norm.cfg.FixNormEquipQualityCfgDAO;
import com.playerdata.fixEquip.norm.data.FixNormEquipDataItem;
import com.playerdata.hero.core.FSHeroBaseInfoMgr;
import com.playerdata.hero.core.FSHeroMgr;
import com.playerdata.team.HeroFixEquipInfo;
import com.rw.service.TaoistMagic.ITaoistMgr;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfg;
import com.rw.service.TaoistMagic.datamodel.TaoistMagicCfgHelper;
import com.rwbase.dao.item.MagicCfgDAO;
import com.rwbase.dao.item.pojo.GemCfg;
import com.rwbase.dao.item.pojo.ItemData;
import com.rwbase.dao.item.pojo.MagicCfg;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.role.pojo.RoleQualityCfg;
import com.rwbase.dao.skill.SkillCfgDAO;
import com.rwbase.dao.skill.pojo.SkillCfg;
import com.rwbase.dao.skill.pojo.SkillItem;
import com.rwbase.dao.user.LevelCfgDAO;
import com.rwbase.dao.user.pojo.LevelCfg;
import com.rwproto.ItemBagProtos.EItemAttributeType;
import com.rwproto.TaoistMagicProtos.TaoistInfo;

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
//		player.getHeroMgr().addHero(templateId);
		player.getHeroMgr().addHero(player, templateId);
	}
	
	/**
	 * 获取玩家的英雄列表
	 * @param player
	 * @return
	 */
	public static List<String> gmHeroIdList(Player player){
//		List<String> heroIdList = player.getHeroMgr().getHeroIdList();
		List<String> heroIdList = player.getHeroMgr().getHeroIdList(player);
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
		FSHeroMgr.getInstance().gmEditHeroLevel(hero, level);
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
//		hero.setStarLevel(star);
		FSHeroBaseInfoMgr.getInstance().setStarLevel(hero, star);
		return star;
	}
	
	public static void gmUpdateTemplateId(Hero hero){
//		RoleCfg cfg = RoleCfgDAO.getInstance().GetConfigBySexCareer(sex, carrer, startLevel);
		String templateId = hero.getTemplateId();
//		hero.setTemplateId(cfg.getRoleId());
		FSHeroBaseInfoMgr.getInstance().setTemplateId(hero, templateId);
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
//		hero.setQualityId(qualityId);
		FSHeroBaseInfoMgr.getInstance().setQualityId(hero, qualityId);
		FSHeroMgr.getInstance().gmCheckActiveSkill(hero);
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
		List<SkillItem> skillList = skillMgr.getSkillList(hero.getUUId());
		for (SkillItem skill : skillList) {
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
	
	public static List<SkillItem> gmGetHeroBaseSkillList(RoleCfg roleCfg){
		List<SkillItem> list = new ArrayList<SkillItem>();
		list.add(parseSkill(roleCfg.getSkillId01()));
		list.add(parseSkill(roleCfg.getSkillId02()));
		list.add(parseSkill(roleCfg.getSkillId03()));
		list.add(parseSkill(roleCfg.getSkillId04()));
		list.add(parseSkill(roleCfg.getSkillId05()));
		return list;
	}
	
	private static SkillItem parseSkill(String skillValue){
		if(skillValue == null || skillValue.equals("")){
			return null;
		}
		SkillItem skill = new SkillItem();
		String[] tmpIds = skillValue.split("_");
		skill.setSkillId(skillValue);
		skill.setLevel(Integer.parseInt(tmpIds[1]));
		return skill;
	}
	
	/**
	 * 升级道术
	 * @param player
	 */
	public static void gmUpgradeTaoist(Player player, int upgradelevel){
		ITaoistMgr taoistMgr = player.getTaoistMgr();
		Iterable<TaoistMagicCfg> openList = TaoistMagicCfgHelper.getInstance().getAllTaoistMagic();
		for (Iterator<TaoistMagicCfg> iterator = openList.iterator(); iterator.hasNext();) {
			TaoistMagicCfg taoistMagicCfg = iterator.next();
			taoistMgr.setLevel(taoistMagicCfg.getKey(), upgradelevel);
		}
//		Iterable<TaoistInfo> magicList = taoistMgr.getMagicList();
//		Iterable<Entry<Integer, Integer>> allTaoist = taoistMgr.getAllTaoist();
//		for (Iterator<Entry<Integer, Integer>> iterator = allTaoist.iterator(); iterator.hasNext();) {
//			Entry<Integer, Integer> entry = iterator.next();
//			Integer taoistId = entry.getKey();
//			taoistMgr.setLevel(taoistId, upgradelevel);
//		}
	}
	
	/**
	 * 升级神器到指定等级
	 * @param player
	 * @param upgradeLevel 指定等级
	 */
	public static void gmFixEquipLevelUp(Player player, int upgradeLevel){
		String userId = player.getUserId();
		int level = player.getLevel();
		if(upgradeLevel > level){
			upgradeLevel = level;
		}
		HeroMgr heroMgr = player.getHeroMgr();
		List<Hero> allHeros = heroMgr.getAllHeros(player, null);
		for (Hero hero : allHeros) {

			FixNormEquipMgr fixNormEquipMgr = hero.getFixNormEquipMgr();
			List<FixNormEquipDataItem> fixNorEquipItemList = fixNormEquipMgr.getFixNorEquipItemList(hero.getId());
			for (FixNormEquipDataItem fixNormEquipDataItem : fixNorEquipItemList) {
				gmFixNormalEquipQualityUp(fixNormEquipDataItem, upgradeLevel);
				fixNormEquipDataItem.setLevel(upgradeLevel);
				fixNormEquipMgr.gmSaveFixNormEquip(player, fixNormEquipDataItem);
			}
			FixExpEquipMgr fixExpEquipMgr = hero.getFixExpEquipMgr();
			List<FixExpEquipDataItem> gmGetHeroFixExpEquipDataItems = fixExpEquipMgr.gmGetHeroFixExpEquipDataItems(hero.getId());
			for (FixExpEquipDataItem fixExpEquipDataItem : gmGetHeroFixExpEquipDataItems) {

				gmFixExpEquipQualityUp(fixExpEquipDataItem, upgradeLevel);
				fixExpEquipDataItem.setLevel(upgradeLevel);
				fixExpEquipMgr.gmSaveFixEquip(player, fixExpEquipDataItem);
			}
		}
	}
	
	private static void gmFixNormalEquipQualityUp(FixNormEquipDataItem dataItem, int toLevel){
		int quality = 1;
		FixEquipCfg fixEquipCfg = FixEquipCfgDAO.getInstance().getCfgById(dataItem.getCfgId());
		FixNormEquipQualityCfg curQualityCfg = FixNormEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(fixEquipCfg.getQualityPlanId(), quality);
		int allowMaxLevel = curQualityCfg.getLevelNeed();
		while(allowMaxLevel < toLevel){
			quality++;
			curQualityCfg = FixNormEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(fixEquipCfg.getQualityPlanId(), quality);
			allowMaxLevel = curQualityCfg.getLevelNeed();
		}
		
		dataItem.setQuality(quality);
		
	}
	
	private static void gmFixExpEquipQualityUp(FixExpEquipDataItem dataItem, int toLevel){
		int quality = 1;
		FixExpEquipQualityCfg curQualityCfg = FixExpEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(dataItem.getQualityPlanId(), quality);
		int allowMaxLevel = curQualityCfg.getLevelNeed();
		while(allowMaxLevel < toLevel){
			quality++;
			curQualityCfg = FixExpEquipQualityCfgDAO.getInstance().getByPlanIdAndQuality(dataItem.getQualityPlanId(), quality);
			allowMaxLevel = curQualityCfg.getLevelNeed();
		}
		
		dataItem.setQuality(quality);
	}
	
	/**
	 * 修改神器的觉醒星级
	 * @param player
	 * @param starLevel
	 */
	public static void gmFixEquipStarUp(Player player, int starLevel){
		String userId = player.getUserId();
		HeroMgr heroMgr = player.getHeroMgr();
		List<Hero> allHeros = heroMgr.getAllHeros(player, null);
		for (Hero hero : allHeros) {
			FixNormEquipMgr fixNormEquipMgr = hero.getFixNormEquipMgr();
			List<FixNormEquipDataItem> fixNorEquipItemList = fixNormEquipMgr.getFixNorEquipItemList(hero.getId());
			for (FixNormEquipDataItem fixNormEquipDataItem : fixNorEquipItemList) {
				fixNormEquipDataItem.setStar(starLevel);
				fixNormEquipMgr.gmSaveFixNormEquip(player, fixNormEquipDataItem);
			}
			FixExpEquipMgr fixExpEquipMgr = hero.getFixExpEquipMgr();
			List<FixExpEquipDataItem> gmGetHeroFixExpEquipDataItems = fixExpEquipMgr.gmGetHeroFixExpEquipDataItems(hero.getId());
			for (FixExpEquipDataItem fixExpEquipDataItem : gmGetHeroFixExpEquipDataItems) {
				fixExpEquipDataItem.setStar(starLevel);
				fixExpEquipMgr.gmSaveFixEquip(player, fixExpEquipDataItem);
			}
		}
	}
	
	public static void gmUpgradeMagic(Player player, int upgradeLevel){
		MagicMgr magicMgr = player.getMagicMgr();
		ItemData magic = magicMgr.getMagic();
		int level = player.getLevel();
		if(upgradeLevel > level){
			upgradeLevel = level;
		}
		int magicLevel = magic.getMagicLevel();
		String modelId = String.valueOf(magic.getModelId());
		ItemBagMgr itemBagMgr = player.getItemBagMgr();
		if(upgradeLevel > magicLevel){
			//升级
			
			MagicCfg magicCfg = (MagicCfg) MagicCfgDAO.getInstance().getCfgById(modelId);
			while (magicCfg.getUplevel() < upgradeLevel) {
				modelId = magicCfg.getUpMagic();
				magicCfg = (MagicCfg) MagicCfgDAO.getInstance().getCfgById(modelId);
				if(magicCfg == null){
					break;
				}
			}
			
		}else{
			//降级
			String beforeMagicModelId = beforeMagicModelId(modelId);
			MagicCfg beforeMagicCfg = (MagicCfg) MagicCfgDAO.getInstance().getCfgById(beforeMagicModelId);
			
			while(beforeMagicCfg.getUplevel() > upgradeLevel){
				modelId = beforeMagicModelId;
				beforeMagicModelId = beforeMagicModelId(modelId);
				beforeMagicCfg = (MagicCfg) MagicCfgDAO.getInstance().getCfgById(beforeMagicModelId);
			}
			
			
		}
		magic.setModelId(Integer.parseInt(modelId));
		magic.setExtendAttr(EItemAttributeType.Magic_Level_VALUE, String.valueOf(upgradeLevel));
		itemBagMgr.updateItem(magic);
		List<ItemData> updateItems = new ArrayList<ItemData>(1);
		updateItems.add(magic);
		itemBagMgr.syncItemData(updateItems);
	}
	
	private static String beforeMagicModelId(String modelId){
		List<MagicCfg> allCfg = MagicCfgDAO.getInstance().getAllCfg();
		for (MagicCfg magicCfg : allCfg) {
			if(magicCfg.getUpMagic().equals(modelId)){
				modelId = String.valueOf(magicCfg.getId());
				break;
			}
		}
		return modelId;
	}
}
