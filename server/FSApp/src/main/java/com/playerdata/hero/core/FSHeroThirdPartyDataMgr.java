package com.playerdata.hero.core;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.common.IHeroAction;
import com.playerdata.EquipMgr;
import com.playerdata.Hero;
import com.playerdata.InlayMgr;
import com.playerdata.Player;
import com.playerdata.SkillMgr;
import com.playerdata.SpriteAttachMgr;
import com.playerdata.eRoleType;
import com.playerdata.fixEquip.exp.FixExpEquipMgr;
import com.playerdata.fixEquip.norm.FixNormEquipMgr;
import com.rw.dataaccess.attachment.RoleExtPropertyFactory;
import com.rw.dataaccess.hero.HeroCreateParam;
import com.rw.service.role.MainMsgHandler;
import com.rwbase.common.enu.eActivityType;
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

	private static final Queue<String> _initingHeroIds = new ConcurrentLinkedQueue<String>();

	public static final IHeroAction ATTR_CHANGE_ACTION = new IHeroAction() {

		@Override
		public void doAction(String userId, String heroId) {
			if (_initingHeroIds.contains(heroId)) {
				// 正在初始化中的不处理
				return;
			}
			FSHero hero = FSHeroMgr.getInstance().getHeroById(userId, heroId);
			if (hero.hasBeenFirstInited()) {
				hero.reCal();
			}
		}
	};

	private static FSHeroThirdPartyDataMgr _instance = new FSHeroThirdPartyDataMgr();

	private static final int marqueeStar = 1;
	private static final int marqueeQuality = 2;

	private final SkillMgr _skillMgr = SkillMgr.getInstance();
	private final EquipMgr _equipMgr = EquipMgr.getInstance();
	private final InlayMgr _inlayMgr = InlayMgr.getInstance();
	private final FixNormEquipMgr _fixNromEquipMgr = FixNormEquipMgr.getInstance();
	private final FixExpEquipMgr _fixExpEquipMgr = FixExpEquipMgr.getInstance();
	private final SpriteAttachMgr _spriteAttachMgr = SpriteAttachMgr.getInstance();

	protected FSHeroThirdPartyDataMgr() {
		_skillMgr.regDataChangeCallback(ATTR_CHANGE_ACTION);
		_equipMgr.regDataChangeCallback(ATTR_CHANGE_ACTION);
		_inlayMgr.regDataChangeCallback(ATTR_CHANGE_ACTION);
		_fixNromEquipMgr.regDataChangeCallback(ATTR_CHANGE_ACTION);
		_fixExpEquipMgr.regDataChangeCallback(ATTR_CHANGE_ACTION);
		_spriteAttachMgr.regDataChangeCallback(ATTR_CHANGE_ACTION);
	}

	public static FSHeroThirdPartyDataMgr getInstance() {
		return _instance;
	}

	void marqueeMsg(Player player, Hero hero, int type, int num) {
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

	void fireHeroLevelChangeEvent(Player player, Hero hero, int preLv) {
		UserEventMgr.getInstance().heroUpGradeVitality(player, hero.getLevel());
		if (preLv != hero.getLevel()) {
			FettersBM.whenHeroChange(player, hero.getModeId());
		}
	}

	void fireStarLevelChangeEvent(Player player, Hero hero, int preStar) {
		if (preStar != hero.getStarLevel()) {
			FettersBM.whenHeroChange(player, hero.getModeId());
			hero.getAttrMgr().reCal();
		}
		marqueeMsg(player, hero, marqueeStar, hero.getStarLevel());
		player.getFresherActivityMgr().doCheck(eActivityType.A_HeroStar);
		// player.getUserGameDataMgr().notifySingleStarChange(hero.getStarLevel(), preStar);
		FSUserHeroGlobalDataMgr.getInstance().increaseStarAll(player.getUserId(), (hero.getStarLevel() - preStar));
	}

	void fireQualityChangeEvent(Player player, Hero hero, String preQualityId) {
		FettersBM.whenHeroChange(player, hero.getModeId());
		RoleQualityCfg cfg = RoleQualityCfgDAO.getInstance().getConfig(hero.getQualityId());
		if (cfg == null) {
			return;
		}
		marqueeMsg(player, hero, marqueeQuality, cfg.getQuality());
		activeSkill(player, hero.getId(), hero.getLevel(), cfg.getQuality());
		player.getFresherActivityMgr().doCheck(eActivityType.A_HeroGrade);
	}

	void fireHeroAddedEvent(Player player, Hero hero) {
		player.getTempAttribute().setHeroFightingChanged();
		FettersBM.whenHeroChange(player, hero.getModeId());
		// player.getUserGameDataMgr().increaseFightingAll(hero.getFighting());
		// player.getUserGameDataMgr().increaseStarAll(hero.getStarLevel());
		FSUserHeroGlobalDataMgr.getInstance().increaseFightingAndStar(player.getUserId(), hero.getFighting(), hero.getStarLevel());
	}

	void notifySave(FSHero hero) {
		String heroId = hero.getId();
		_equipMgr.save(heroId);
		_inlayMgr.save(heroId);

	}

	void afterHeroInitAndAddedToCache(Player player, FSHero hero, RoleCfg rolecfg) {
		_skillMgr.initSkill(player, hero, rolecfg);
		player.getUserTmpGameDataFlag().setSynFightingAll(true);
		RoleExtPropertyFactory.fristCreateHeroExtProperty(hero.getId(), newHeroCreateParam(player, hero, rolecfg));
	}

	private HeroCreateParam newHeroCreateParam(Player player, Hero hero, RoleCfg rolecfg) {
		String userId = player.getUserId();
		int playerLevel = player.getLevel();
		String heroId = hero.getId();
		String qualityId = hero.getQualityId();
		int heroLevel = hero.getLevel();
		int modelId = hero.getModeId();
		HeroCreateParam heroCreateParam = new HeroCreateParam(userId, heroId, qualityId, playerLevel, heroLevel, modelId, rolecfg);

		return heroCreateParam;
	}

	public void notifyFirstInit(FSHero hero) {
		Player player = FSHeroMgr.getInstance().getOwnerOfHero(hero);
		RoleCfg heroCfg = RoleCfgDAO.getInstance().getRoleCfgByModelId(hero.getModeId());
		RoleExtPropertyFactory.loadAndCreateHeroExtProperty(hero.getId(), newHeroCreateParam(player, hero, heroCfg));
		_initingHeroIds.add(hero.getId());
		_skillMgr.init(hero);
		_inlayMgr.init(hero);
		_equipMgr.init(hero);
		_initingHeroIds.remove(hero.getId());
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

	public SpriteAttachMgr getSpriteAttachMgr() {
		return _spriteAttachMgr;
	}

	public void notifySync(Player player, Hero hero, int version) {
		_skillMgr.syncAllSkill(player, hero.getId(), version);
		_inlayMgr.syncAllInlay(player, hero.getId(), version);
		_equipMgr.syncAllEquip(player, hero.getId(), version);
		_fixNromEquipMgr.synAllData(player, hero);
		_fixExpEquipMgr.synAllData(player, hero);
		_spriteAttachMgr.synAllData(player, hero);
	}
}
