package com.playerdata.hero.core;

import com.playerdata.Hero;
import com.playerdata.Player;
import com.playerdata.RoleBaseInfoMgr;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleQualityCfg;

public class FSHeroBaseInfoMgr implements RoleBaseInfoMgr {
	
	private static final FSHeroBaseInfoMgr _instance = new FSHeroBaseInfoMgr();
	
	public static final FSHeroBaseInfoMgr getInstance() {
		return _instance;
	}
	
	protected FSHeroBaseInfoMgr() {
		
	}
	
	void updateHeroLevelAndExp(Player player, Hero hero, int toLv, long toExp) {
		int preLv = hero.getLevel();
		hero.setExp(toExp);
		hero.setLevel(toLv);
		FSHeroHolder.getInstance().synBaseInfo(player, hero);
		if (preLv != hero.getLevel()) {
			hero.getAttrMgr().reCal();
		}
		FSHeroThirdPartyDataMgr.getInstance().fireHeroLevelChangeEvent(player, hero, preLv);
	}

	@Override
	public void setQualityId(Hero hero, String qualityId) {
		Player player = FSHeroMgr.getInstance().getOwnerOfHero(hero);
		String preQualityId = hero.getQualityId();
		hero.setQualityId(qualityId);
		FSHeroHolder.getInstance().synBaseInfo(player, hero);
		FSHeroThirdPartyDataMgr.getInstance().fireQualityChangeEvent(player, hero, preQualityId);
	}

	@Override
	public void setCareerType(Hero hero, int career) {
		hero.setCareerType(career);
		FSHeroHolder.getInstance().synBaseInfo(FSHeroMgr.getInstance().getOwnerOfHero(hero), hero);
	}

	@Override
	public void setModelId(Hero hero, int modelId) {
		hero.setModelId(modelId);
		FSHeroHolder.getInstance().synBaseInfo(FSHeroMgr.getInstance().getOwnerOfHero(hero), hero);
	}

	@Override
	public void setTemplateId(Hero hero, String templateId) {
		hero.setTemplateId(templateId);
		FSHeroHolder.getInstance().synBaseInfo(FSHeroMgr.getInstance().getOwnerOfHero(hero), hero);
	}

	@Override
	public void setStarLevel(Hero hero, int starLevel) {
		Player player = FSHeroMgr.getInstance().getOwnerOfHero(hero);
		int preStarLv = hero.getStarLevel();
		hero.setStarLevel(starLevel);
		FSHeroHolder.getInstance().synBaseInfo(player, hero);
		FSHeroThirdPartyDataMgr.getInstance().fireStarLevelChangeEvent(player, hero, preStarLv);
	}

	@Override
	public void setLevel(Hero hero, int level) {
		Player player = FSHeroMgr.getInstance().getOwnerOfHero(hero);
		int preLevel = hero.getLevel();
		this.updateHeroLevelAndExp(player, hero, level, hero.getExp());
		if (preLevel < level) {
			RoleQualityCfg cfg = RoleQualityCfgDAO.getInstance().getCfgById(hero.getQualityId());
			FSHeroThirdPartyDataMgr.getInstance().activeSkill(player, hero.getId(), hero.getLevel(), cfg.getQuality());
		}
	}

	@Override
	public void setExp(Hero hero, long exp) {
		hero.setExp(exp);
		FSHeroHolder.getInstance().synBaseInfo(FSHeroMgr.getInstance().getOwnerOfHero(hero), hero);
	}

	@Override
	public void setLevelAndExp(Hero hero, int level, long exp) {
		this.updateHeroLevelAndExp(FSHeroMgr.getInstance().getOwnerOfHero(hero), hero, level, exp);
	}

}
