package com.playerdata.hero.core;

import com.common.IHeroAction;
import com.playerdata.EquipMgr;
import com.playerdata.InlayMgr;
import com.playerdata.Player;
import com.playerdata.SkillMgr;
import com.playerdata.eRoleType;
import com.playerdata.fixEquip.exp.FixExpEquipMgr;
import com.playerdata.fixEquip.norm.FixNormEquipMgr;
import com.rw.service.role.MainMsgHandler;
import com.rwbase.common.userEvent.UserEventMgr;
import com.rwbase.dao.fetters.FettersBM;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.role.pojo.RoleQualityCfg;

/**
 * 
 * 英雄第三方数据管理器
 * 
 * @author CHEN.P
 *
 */
public class FSHeroThirdPartyDataMgr {

	private static final FSHeroThirdPartyDataMgr _instance = new FSHeroThirdPartyDataMgr();
	
	private static final int marqueeStar = 1;
	private static final int marqueeQuality = 2;
	
	private final SkillMgr _skillMgr = SkillMgr.getInstance();
	private final EquipMgr _equipMgr = EquipMgr.getInstance();
	private final InlayMgr _inlayMgr = InlayMgr.getInstance();
	private final FixNormEquipMgr _fixNromEquipMgr = FixNormEquipMgr.getInstance();
	private final FixExpEquipMgr _fixExpEquipMgr = FixExpEquipMgr.getInstance();
	
	public static FSHeroThirdPartyDataMgr getInstance() {
		return _instance;
	}

	void marqueeMsg(Player player, FSHero hero, int type, int num) {
		if (hero.getRoleType() == eRoleType.Player) {
			if (type == marqueeStar) {
				MainMsgHandler.getInstance().sendPmdZjsx(player, num);
			} else if (type == marqueeQuality) {
				MainMsgHandler.getInstance().sendPmdZjJj(player, num);
			}
		} else {
			RoleCfg heroCfg = RoleCfgDAO.getInstance().getConfig(hero.getTemplateId());
			if (heroCfg == null)
				return;
			if (type == marqueeStar) {
				MainMsgHandler.getInstance().sendPmdHpsx(player, heroCfg.getName(), num);
			} else if (type == marqueeQuality) {
				String qualityId = hero.getQualityId();
				RoleQualityCfg roleQualityCfg = RoleQualityCfgDAO.getInstance().getCfgById(qualityId);
				MainMsgHandler.getInstance().sendPmdHpJj(player, heroCfg.getName(), num, roleQualityCfg);
			}
		}
	}
	
	void activeSkill(Player player, String heroId, int level, int quality) {
		_skillMgr.activeSkill(player, heroId, level, quality);
	}
	
	void fireLevelUpEvent(Player player, FSHero hero, int preLv) {
		UserEventMgr.getInstance().heroUpGradeVitality(player, hero.getLevel());
		FettersBM.whenHeroChange(player, hero.getModelId());
	}
	
	void fireStarLevelChangeEvent(Player player, FSHero hero, int preStar) {
		FettersBM.whenHeroChange(player, hero.getModelId());
	}
	
	void fireQualityChangeEvent(Player player, FSHero hero, String preQualityId) {
		FettersBM.whenHeroChange(player, hero.getModelId());
	}
	
	void fireHeroAddedEvent(Player player, FSHero hero) {
		player.getTempAttribute().setHeroFightingChanged();
		FettersBM.whenHeroChange(player, hero.getModelId());
	}
	
	void notifySave(FSHero hero, boolean immediately) {
		String heroId = hero.getId();
		_inlayMgr.save(heroId);
		_equipMgr.save(heroId);
		if (immediately) {
			_skillMgr.flush(heroId);
		} else {
			_skillMgr.save(heroId);
		}
	}
	
	public void notifyFirstInit(Player player, FSHero hero, boolean fresh) {
		_skillMgr.initV2(hero);
		_inlayMgr.initV2(hero);
		_equipMgr.initV2(hero);
		if (!player.isRobot()) {
			_fixExpEquipMgr.initIfNeedV2(player, hero);
			_fixNromEquipMgr.initIfNeedV2(player, hero);
		}
		_skillMgr.checkSkill(player, hero.getId(), hero.getTemplateId());
	}
	
	public SkillMgr getSkillMgr() {
		return _skillMgr;
	}
	
	public InlayMgr getInlayMgr() {
		return _inlayMgr;
	}
	
	public EquipMgr getEquipMgr() {
		return _equipMgr;
	}
	
	public FixNormEquipMgr getFixNormEquipMgr() {
		return _fixNromEquipMgr;
	}
	
	public FixExpEquipMgr getFixExpEquipMgr() {
		return _fixExpEquipMgr;
	}
	
	public void notifySync(Player player, FSHero hero, int version) {
		_skillMgr.syncAllSkill(player, hero.getId(), version);
		_inlayMgr.syncAllInlay(player, hero.getId(), version);
		_equipMgr.syncAllEquip(player, hero.getId(), version);
		_fixNromEquipMgr.synAllDataV2(player, hero);
		_fixExpEquipMgr.synAllDataV2(player, hero);
	}
	
	public static final IHeroAction ATTR_CHANGE_ACTION = new IHeroAction() {

		@Override
		public void doAction(String userId, String heroId) {
			FSHero f = FSHeroDAO.getInstance().getHeroNew(userId, heroId);
			if (f.hasBeenFirstInited()) {
				f.calculateAttrs(true);
			}
		}
	};
}
