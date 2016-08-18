package com.rw.service.gm.hero;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.common.Utils;
import com.playerdata.Hero;
import com.playerdata.Player;
import com.rwbase.dao.item.HeroEquipCfgDAO;
import com.rwbase.dao.item.pojo.HeroEquipCfg;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.role.pojo.RoleQualityCfg;
import com.rwbase.dao.skill.SkillCfgDAO;
import com.rwbase.dao.skill.pojo.Skill;
import com.rwbase.dao.skill.pojo.SkillCfg;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.PlayerTask;

public class GMHeroProcesser {
	
	private final static int[] gemTypes = new int[] { 1, 2, 3, 4, 5, 6 };
	
	/**
	 * 处理指令teambringit
	 * @param arrCommandContents
	 * @param player
	 * 如果传入的转职为5，则为迷你版增加英雄
	 */
	public static void processTeamBringit(String[] arrCommandContents, Player player){
		int career = Integer.parseInt(arrCommandContents[0]);
		// 添加英雄
		final int maxLevel = GMHeroBase.gmGetMaxLevel();
				
		final int maxQuality = GMHeroBase.gmGetMaxQuality();

		
		gmChangeCareer(player, career);
		
		
		
		
		bringitMainHero(player, maxLevel, maxQuality);
		
		GameWorldFactory.getGameWorld().asyncExecute(player.getUserId(), new PlayerTask() {
			
			@Override
			public void run(Player player) {
				player.setVip(15);
				player.getUserGameDataMgr().addCoin(1999999999);
				player.getUserGameDataMgr().addGold(1999999999);
				Map<String, RoleCfg> allRoleCfgCopy = RoleCfgDAO.getInstance().getAllRoleCfgCopy();
				for (Iterator<Entry<String, RoleCfg>> iterator = allRoleCfgCopy.entrySet().iterator(); iterator.hasNext();) {
					Entry<String, RoleCfg> entry = iterator.next();
					RoleCfg roleCfg = entry.getValue();
					String templateId = roleCfg.getRoleId();
					GMHeroBase.gmAddHero(entry.getKey(), player);
					int maxStar = GMHeroBase.gmGetMaxStar(templateId);
//					Hero hero = player.getHeroMgr().getHeroByTemplateId(templateId);
					Hero hero = player.getHeroMgr().getHeroByTemplateId(player, templateId);
					GMHeroBase.gmEditHeroLevel(hero, maxLevel, player);
					GMHeroBase.gmEditHeroStarLevel(hero, maxStar, player);
					String qualityId = getQualityId(hero, maxQuality,false);
					GMHeroBase.gmEditHeroQuality(hero, qualityId, player);
					
					List<Skill> skillList = hero.getSkillMgr().getSkillList(hero.getUUId());
					for (Skill skill : skillList) {
						SkillCfg cfg = SkillCfgDAO.getInstance().getCfg(skill.getSkillId());
						String maxSkillId = GMHeroBase.gmGetMaxSkillId(cfg.getSkillId(), maxLevel);
						GMHeroBase.gmEditHeroSkillLevel(hero, skill.getSkillId(), maxSkillId, player);
					}
					GMHeroBase.gmRemoveHeroEquip(hero, player);
					for (int i = 0; i < 6; i++) {
						GMHeroBase.gmHeroEequip(hero, i, player);	
						
						GMHeroBase.gmUpgradeHeroEquipment(hero, i, -2, player);
					}
					//镶嵌指定宝石
					int[] gems = new int[] { 800009, 800019, 800029, 800039, 800049, 800059 };
					for (int gemId : gems) {
						GMHeroBase.gmInlayJewel(hero, player, gemId);
					}
				}
			}
		});
		
	}
	
	/**非异步的增加英雄命令,一次加一个*/
	public static void processTeamBringitSigle(String[] arrCommandContents, Player player){
		// 添加英雄
		final int maxLevel = GMHeroBase.gmGetMaxLevel();

		Map<String, RoleCfg> allRoleCfgCopy = RoleCfgDAO
				.getInstance().getAllRoleCfgCopy();
//		long begin = System.currentTimeMillis();
//		System.out.println("~~~~~~~~~~~~~~~~~~~~~~begin");
		for (Iterator<Entry<String, RoleCfg>> iterator = allRoleCfgCopy
				.entrySet().iterator(); iterator.hasNext();) {
			
			Entry<String, RoleCfg> entry = iterator.next();
			RoleCfg roleCfg = entry.getValue();
			String templateId = roleCfg.getRoleId();
			if(player.getHeroMgr().getHeroByTemplateId(player, templateId) != null){
				continue;
			}			
			GMHeroBase.gmAddHero(entry.getKey(), player);
			Hero hero = player.getHeroMgr()
					.getHeroByTemplateId(player, templateId);
			GMHeroBase.gmEditHeroLevel(hero, maxLevel, player);
//			System.out.println("~~~~~~~~~~~~~~~~~~~~~~add");
			break;
		}
//		long end = System.currentTimeMillis();
//		System.out.println("~~~~~~~~~~~~~~~~~~~~~~end        time =" + (end - begin));
		
		return;
	}
	

	private static void gmChangeCareer(Player player, int career) {
		boolean blnChangeCareer = false;
		if (player.getCareer() != career) {
			player.SetCareer(career);
			player.getMainRoleHero().getEquipMgr().changeEquip(player, player.getMainRoleHero().getUUId());
			blnChangeCareer = true;
		}
		int sex = player.getSex();
		if (blnChangeCareer) {
			player.getFashionMgr().changeSuitCareer();
			player.onCareerChange(career, sex);
		}
	}
	
	private static void gmDefaultChangeCareer(Player player){
		boolean blnChangeCareer = false;
		int career = 1; 
		if (player.getCareer() <= 0) {
			player.SetCareer(career);
			player.getMainRoleHero().getEquipMgr().changeEquip(player, player.getMainRoleHero().getUUId());
			blnChangeCareer = true;
		}
		int sex = player.getSex();
		if (blnChangeCareer) {
			player.getFashionMgr().changeSuitCareer();
			player.onCareerChange(career, sex);
		}
	}

	private static void bringitMainHero(Player player, int maxLevel, int maxQuality) {
		Hero mainRoleHero = player.getMainRoleHero();
		String templateId = mainRoleHero.getTemplateId();
		RoleCfg roleCfg = (RoleCfg)RoleCfgDAO.getInstance().getCfgById(templateId);
		int maxStar = GMHeroBase.gmGetMaxStar(templateId);
//		Hero hero = player.getHeroMgr().getHeroByTemplateId(templateId);
		Hero hero = player.getHeroMgr().getHeroByTemplateId(player, templateId);
		int star = GMHeroBase.gmEditHeroStarLevel(hero, maxStar, player);
		GMHeroBase.gmUpdateTemplateId(player.getSex(), player.getCareer(), star, hero);
		GMHeroBase.gmEditHeroLevel(hero, maxLevel, player);
		String qualityId = getQualityId(hero, maxQuality,false);
		GMHeroBase.gmEditHeroQuality(hero, qualityId, player);
		List<Skill> skillList = hero.getSkillMgr().getSkillList(hero.getUUId());
		for (Skill skill : skillList) {
			SkillCfg cfg = SkillCfgDAO.getInstance().getCfg(skill.getSkillId());
			String maxSkillId = GMHeroBase.gmGetMaxSkillId(cfg.getSkillId(), maxLevel);
			GMHeroBase.gmEditHeroSkillLevel(hero, skill.getSkillId(), maxSkillId, player);
		}
		for (int i = 0; i < 6; i++) {
			GMHeroBase.gmHeroEequip(hero, i, player);	
			
			GMHeroBase.gmUpgradeHeroEquipment(hero, i, -2, player);
		}
		//镶嵌指定宝石
		int[] gems = new int[] { 800009, 800019, 800029, 800039, 800049, 800059 };
		for (int gemId : gems) {
			GMHeroBase.gmInlayJewel(hero, player, gemId);
		}
	}
	
	private static void bringitMainHeroSimple(Player player, int maxLevel, int maxQuality) {
		Hero mainRoleHero = player.getMainRoleHero();
		String templateId = mainRoleHero.getTemplateId();
//		Hero hero = player.getHeroMgr().getHeroByTemplateId(templateId);
		Hero hero = player.getHeroMgr().getHeroByTemplateId(player, templateId);
		GMHeroBase.gmEditHeroLevel(hero, maxLevel, player);
		
	}
	
	
	/**
	 * 添加英雄
	 * @param arrCommandContents
	 * @param player
	 */
	public static void processAddhero(String[] arrCommandContents, Player player){
		Map<String, RoleCfg> allRoleCfgCopy = RoleCfgDAO.getInstance().getAllRoleCfgCopy();
		for (Iterator<Entry<String, RoleCfg>> iterator = allRoleCfgCopy.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, RoleCfg> entry = iterator.next();
			String templateId = entry.getKey();
			GMHeroBase.gmAddHero(entry.getKey(), player);
		}
	}
	
	/**
	 * 处理Setteam1 无限制
	 * @param arrCommandContents
	 * @param player
	 */
	public static void processSetteam1(final String[] arrCommandContents, Player player){
		
		if(arrCommandContents.length < 8){
			return;
		}
		
		gmDefaultChangeCareer(player);
		GameWorldFactory.getGameWorld().asyncExecute(player.getUserId(), new PlayerTask() {
			
			@Override
			public void run(Player player) {
				int heroLevel = Integer.parseInt(arrCommandContents[0]);
				int skillLevel = Integer.parseInt(arrCommandContents[1]);
				int starLevel = Integer.parseInt(arrCommandContents[2]);
				int quality = Integer.parseInt(arrCommandContents[3]);
				int equip = Integer.parseInt(arrCommandContents[4]);
				int equipLv = Integer.parseInt(arrCommandContents[5]);
				int gemCount = Integer.parseInt(arrCommandContents[6]);
				int gemLv = Integer.parseInt(arrCommandContents[7]);
				
				Map<String, RoleCfg> map = RoleCfgDAO.getInstance().getAllRoleCfgCopy();
				List<Hero> heroList = player.getHeroMgr().getAllHeros(player, new Comparator<Hero>() {
					public int compare(Hero o1, Hero o2) {
						if (o1.getFighting() < o2.getFighting())
							return 1;
						if (o1.getFighting() > o2.getFighting())
							return -1;
						return 0;
					}
				});
				for (Hero hero : heroList) {
					int star = GMHeroBase.gmEditHeroStarLevel(hero, starLevel, player);
					GMHeroBase.gmUpdateTemplateId(player.getSex(), player.getCareer(), star, hero);
					GMHeroBase.gmEditHeroLevel(hero, heroLevel, player);
					String qualityId = getQualityId(hero, quality, false);
					GMHeroBase.gmEditHeroQuality(hero, qualityId, player);
					List<Skill> skillList = hero.getSkillMgr().getSkillList(hero.getUUId());
					for (Skill skill : skillList) {
						if(skill == null){
							continue;
						}
						SkillCfg cfg = SkillCfgDAO.getInstance().getCfg(skill.getSkillId());
						String skillId = GMHeroBase.gmGetSkillIdByLevel(cfg.getSkillEffectId()+"_1", skillLevel);
						GMHeroBase.gmEditHeroSkillLevel(hero, skill.getSkillId(), skillId, player);
					}
					GMHeroBase.gmRemoveHeroEquip(hero, player);
					for (int i = 0; i < equip; i++) {
						GMHeroBase.gmHeroEequip(hero, i, player);	
						
						GMHeroBase.gmUpgradeHeroEquipment(hero, i, equipLv, player);
					}
					//镶嵌指定宝石
					GMHeroBase.gmUnloadGem(hero, player);
					for (int i = 0; i < gemCount; i++) {
						try {
							if(gemTypes.length <= i){
								break;
							}
							int type = gemTypes[i];
							GMHeroBase.gmInlayJewel(hero, player, type, gemLv, true);
						} catch (Exception ex) {
							continue;
						}
					}
				}
			}
		});
		
	}
	
	/**
	 * 有限制
	 * @param arrCommandContents
	 * @param player
	 */
	public static void processSetteam2(final String[] arrCommandContents, Player player){
		if(arrCommandContents.length < 8){
			return;
		}
		gmDefaultChangeCareer(player);
		
		GameWorldFactory.getGameWorld().asyncExecute(player.getUserId(), new PlayerTask() {
			
			@Override
			public void run(Player player) {
				int heroLevel = Integer.parseInt(arrCommandContents[0]);
				int skillLevel = Integer.parseInt(arrCommandContents[1]);
				int starLevel = Integer.parseInt(arrCommandContents[2]);
				int quality = Integer.parseInt(arrCommandContents[3]);
				int equip = Integer.parseInt(arrCommandContents[4]);
				int equipLv = Integer.parseInt(arrCommandContents[5]);
				int gemCount = Integer.parseInt(arrCommandContents[6]);
				int gemLv = Integer.parseInt(arrCommandContents[7]);
				
				List<Hero> heroList = player.getHeroMgr().getAllHeros(player, new Comparator<Hero>() {
					public int compare(Hero o1, Hero o2) {
						if (o1.getFighting() < o2.getFighting())
							return 1;
						if (o1.getFighting() > o2.getFighting())
							return -1;
						return 0;
					}
				});
				for (Hero hero : heroList) {
					int star = GMHeroBase.gmEditHeroStarLevel(hero, starLevel, player);
					GMHeroBase.gmUpdateTemplateId(player.getSex(), player.getCareer(), star, hero);
					GMHeroBase.gmEditHeroLevel(hero, heroLevel, player);
					String qualityId = getQualityId(hero, quality, true);
					GMHeroBase.gmEditHeroQuality(hero, qualityId, player);
					
					List<Skill> skillList = hero.getSkillMgr().getSkillList(hero.getUUId());
					for (Skill skill : skillList) {
						if(skill == null){
							continue;
						}
						SkillCfg cfg = SkillCfgDAO.getInstance().getCfg(skill.getSkillId());
						if(heroLevel < skillLevel){
							skillLevel = heroLevel;
						}
						String skillId = GMHeroBase.gmGetSkillIdByLevel(cfg.getSkillEffectId()+"_1", skillLevel);
						GMHeroBase.gmEditHeroSkillLevel(hero, skill.getSkillId(), skillId, player);
					}
					GMHeroBase.gmRemoveHeroEquip(hero, player);
					List<Integer> equips = RoleQualityCfgDAO.getInstance().getEquipList(qualityId);
					
					for (int i = 0; i < equip; i++) {
						if(equips.size() <= i){
							break;
						}
						Integer equipId = equips.get(i);
						if (equipId != null) {
							GMHeroBase.gmHeroEequip(hero, i, player);
						}
						if(heroLevel < equipLv){
							equipLv = heroLevel;
						}
						GMHeroBase.gmUpgradeHeroEquipment(hero, i, equipLv, player);
					}
					//镶嵌指定宝石
					GMHeroBase.gmUnloadGem(hero, player);
					for (int i = 1; i <= gemCount; i++) {
						try {
							if(gemTypes.length <= i){
								break;
							}
							int type = gemTypes[i];
							GMHeroBase.gmInlayJewel(hero, player, type, gemLv, true);
						} catch (Exception ex) {
							continue;
						}
					}
				}
			}
		});
		
	}
	
	private static String getQualityId(Hero hero, int quality, boolean limited) {
		if(quality == -1){
			return "";
		}
		if(limited){
//			String qualityId =	hero.getModeId()+"_"+1;
			String qualityId = Utils.computeQualityId(hero.getModeId(), 1);
			RoleQualityCfg cfg = RoleQualityCfgDAO.getInstance().getConfig(qualityId);
			int heroLv = hero.getLevel();
			while (true) {
				if (cfg.getQuality() >= quality) {
					break;
				} else {
					List<Integer> equips = RoleQualityCfgDAO.getInstance().getEquipList(cfg.getNextId());
					int min = Integer.MAX_VALUE;
					int max = Integer.MIN_VALUE;
					for (Integer equipId : equips) {
						HeroEquipCfg heroEquipCfg = (HeroEquipCfg) HeroEquipCfgDAO.getInstance().getCfgById(String.valueOf(equipId));
						if (min > heroEquipCfg.getLevel()) {
							min = heroEquipCfg.getLevel();
						}
						if (max < heroEquipCfg.getLevel()) {
							max = heroEquipCfg.getLevel();
						}
					}

					if(heroLv > max && heroLv > min){
						cfg = RoleQualityCfgDAO.getInstance().getConfig(cfg.getNextId());
						continue;
					}
					if(heroLv >= min && heroLv <= max){
						if(quality > cfg.getQuality()){
							quality = cfg.getQuality();
						}
						break;
					}
					if(heroLv < min){
						quality = cfg.getQuality();
						break;
					}
				}
			}
			
			
		}
//		return hero.getModeId() + "_" + (quality + 1);
		return Utils.computeQualityId(hero.getModeId(), (quality + 1));
	}
}
