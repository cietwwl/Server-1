package com.playerdata;

import org.apache.commons.lang3.StringUtils;

import com.common.Action;
import com.log.GameLog;
import com.playerdata.fixEquip.exp.FixExpEquipMgr;
import com.playerdata.fixEquip.norm.FixNormEquipMgr;
import com.playerdata.readonly.HeroIF;
import com.rw.service.role.MainMsgHandler;
import com.rwbase.common.enu.eActivityType;
import com.rwbase.dao.hero.pojo.RoleBaseInfo;
import com.rwbase.dao.role.RoleCfgDAO;
import com.rwbase.dao.role.RoleQualityCfgDAO;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.role.pojo.RoleQualityCfg;
import com.rwbase.dao.user.LevelCfgDAO;
import com.rwbase.dao.user.pojo.LevelCfg;

/**
 * 镛兵(英雄)
 *
 * 
 * @inchage Yaoqiang
 * @version
 * 
 */

public class Hero implements HeroIF {

	private Player m_pPlayer = null;
	private String roleId;
	private eRoleType roleType;

	private RoleBaseInfoMgr m_roleBaseInfoMgr = new RoleBaseInfoMgr();// 角色基本信息属性
	private AttrMgr m_AttrMgr = new AttrMgr();// 角色属性
	private SkillMgr m_SkillMgr = new SkillMgr();
	private EquipMgr m_EquipMgr = new EquipMgr();
	private InlayMgr m_inlayMgr = new InlayMgr();// 镶嵌宝石
	private FixNormEquipMgr m_FixNormEquipMgr = new FixNormEquipMgr(); // 专属装备
	private FixExpEquipMgr m_FixExpEquipMgr = new FixExpEquipMgr(); // 专属装备

	// 新添加的英雄做基本属性和技能的初始化
	public Hero(Player pPlayer, eRoleType roleTypeP, RoleCfg heroCfg, String roleUUId) {
		roleType = roleTypeP;
		m_pPlayer = pPlayer;
		RoleBaseInfo roleBaseInfo = new RoleBaseInfo();
		roleBaseInfo.setId(roleUUId);
		roleBaseInfo.setTemplateId(heroCfg.getRoleId());
		int modelId = heroCfg.getModelId();
		roleBaseInfo.setModeId(modelId);
		roleBaseInfo.setLevel(1);
		roleBaseInfo.setCareerType(heroCfg.getCareerType());
		roleBaseInfo.setStarLevel(heroCfg.getStarLevel());
		roleBaseInfo.setQualityId(heroCfg.getQualityId());
		init(roleUUId, roleBaseInfo);
		m_SkillMgr.initSkill(heroCfg);

		pPlayer.getUserTmpGameDataFlag().setSynFightingAll(true);

		// m_FixNormEquipMgr.newHeroInit(pPlayer, roleUUId, modelId);
		// m_FixExpEquipMgr.newHeroInit(pPlayer, roleUUId, modelId);
	}

	public Hero(Player pPlayer, eRoleType roleTypeP, String roleUUId) {
		roleType = roleTypeP;
		m_pPlayer = pPlayer;
		init(roleUUId, null);
	}

	private void init(String roleUUId, RoleBaseInfo roleBaseInfoP) {
		roleId = roleUUId;
		m_roleBaseInfoMgr.init(this, roleBaseInfoP);
		m_SkillMgr.init(this);
		m_inlayMgr.init(this);
		m_EquipMgr.init(this);

		// Attrmgr要在最后做初始化
		m_AttrMgr.init(this);
		if (!m_pPlayer.isRobot()) {
			m_FixExpEquipMgr.initIfNeed(m_pPlayer, this);
			m_FixNormEquipMgr.initIfNeed(m_pPlayer, this);
		}
	}

	// 属性的初始化有依赖，要等所有的mgr初始化完了才能做属性的初始化。
	public void regAttrChangeCallBack() {

		m_roleBaseInfoMgr.regChangeCallBack(new Action() {
			@Override
			public void doAction() {
				m_AttrMgr.reCal();

			}
		});
		m_SkillMgr.regChangeCallBack(new Action() {
			@Override
			public void doAction() {
				m_AttrMgr.reCal();

			}
		});
		m_inlayMgr.regChangeCallBack(new Action() {
			@Override
			public void doAction() {
				m_AttrMgr.reCal();

			}
		});
		m_EquipMgr.regChangeCallBack(new Action() {
			@Override
			public void doAction() {
				m_AttrMgr.reCal();

			}
		});
		m_FixNormEquipMgr.regChangeCallBack(new Action() {
			@Override
			public void doAction() {
				m_AttrMgr.reCal();
			}
		});
		m_FixExpEquipMgr.regChangeCallBack(new Action() {
			@Override
			public void doAction() {
				m_AttrMgr.reCal();
			}
		});

	}

	public void syn(int version) {

		m_roleBaseInfoMgr.syn(version);
		m_SkillMgr.syncAllSkill(version);
		m_inlayMgr.syncAllInlay(version);
		m_EquipMgr.syncAllEquip(version);
		m_FixNormEquipMgr.synAllData(m_pPlayer, this);
		m_FixExpEquipMgr.synAllData(m_pPlayer, this);
		m_AttrMgr.syncAllAttr(version);

	}

	public void save(boolean immediately) {
		m_roleBaseInfoMgr.save();
		m_inlayMgr.save();
		m_EquipMgr.save();
		if (immediately) {
			m_SkillMgr.flush();
		} else {
			m_SkillMgr.save();
		}
		// m_AttrMgr.save(); 不需要持久化
	}

	public void save() {
		this.save(false);
	}

	public Player getPlayer() {
		return m_pPlayer;
	}

	public RoleCfg getHeroCfg() {
		String templateId = String.valueOf(getRoleBaseInfo().getTemplateId());
		return (RoleCfg) RoleCfgDAO.getInstance().getCfgById(templateId);
	}

	/**
	 * 是否可升星
	 * 
	 * @return -1:魂石不足;-2:铜钱不足;-3:最高星;-4满星；0:可升星
	 */
	public int canUpgradeStar() {
		int result = 0;
		RoleCfg rolecfg = getHeroCfg();
		int soulStoneCount = m_pPlayer.getItemBagMgr().getItemCountByModelId(rolecfg.getSoulStoneId());
		if (soulStoneCount < rolecfg.getRisingNumber()) {
			result = -1;
		} else if (m_pPlayer.getUserGameDataMgr().getCoin() < rolecfg.getUpNeedCoin()) {
			result = -2;
		} else if (!StringUtils.isNotBlank(rolecfg.getNextRoleId())) {
			result = -3;
		}

		return result;
	}

	public LevelCfg getHeroLevelCfg() {
		String level = String.valueOf(getRoleBaseInfo().getLevel());
		return (LevelCfg) LevelCfgDAO.getInstance().getCfgById(level);
	}

	private RoleBaseInfo getRoleBaseInfo() {
		return m_roleBaseInfoMgr.getBaseInfo();
	}

	public int getModelId() {
		return Integer.valueOf(getRoleBaseInfo().getModeId());
	}

	public RoleBaseInfo getHeroData() {
		return getRoleBaseInfo();
	}

	public String getTemplateId() {
		return getRoleBaseInfo().getTemplateId();
	}

	public void setTemplateId(String heroId) {
		m_roleBaseInfoMgr.setTemplateId(heroId);
	}

	public int GetHeroLevel() {
		return getRoleBaseInfo().getLevel();
	}

	public int GetHeroQuality() {
		String qualityId = getRoleBaseInfo().getQualityId();
		RoleQualityCfg cfg = (RoleQualityCfg) RoleQualityCfgDAO.getInstance().getCfgById(qualityId);
		if (cfg != null) {
			return cfg.getQuality();
		} else {
			return 0;
		}
	}

	public void SetHeroLevel(int level) {
		int preLevel = getRoleBaseInfo().getLevel();
		m_roleBaseInfoMgr.setLevel(level);
		// SetCommonAttr(eAttrIdDef.HERO_LEVEL, m_tableHeroData.getLevel());
		onHeroLevelChange(preLevel);
	}

	private void onHeroLevelChange(int preLevel) {
		int level = getRoleBaseInfo().getLevel();
		if (preLevel < level) {
			// 开启技能
			RoleQualityCfg cfg = (RoleQualityCfg) RoleQualityCfgDAO.getInstance().getCfgById(getRoleBaseInfo().getQualityId());
			m_SkillMgr.activeSkill(getRoleBaseInfo().getLevel(), cfg.getQuality());
		}

	}

	public void SetHeroExp(long exp) {
		m_roleBaseInfoMgr.setExp(exp);
	}

	public int getFighting() {
		return m_AttrMgr.getRoleAttrData().getFighting();
	}

	public int getLevel() {
		return getRoleBaseInfo().getLevel();
	}

	public void setStarLevel(int star) {
		m_roleBaseInfoMgr.setStarLevel(star);
		// 跑马灯
		marqueeMsg(marqueeStar, star);
		m_pPlayer.getFresherActivityMgr().doCheck(eActivityType.A_HeroStar);
	}

	private static final int marqueeStar = 1;
	private static final int marqueeQuality = 2;

	// 跑马灯
	private void marqueeMsg(int type, int num) {
		if (roleType == eRoleType.Player) {
			if (type == marqueeStar) {
				MainMsgHandler.getInstance().sendPmdZjsx(m_pPlayer, num);
			} else if (type == marqueeQuality) {
				MainMsgHandler.getInstance().sendPmdZjJj(m_pPlayer, num);
			}
		} else {
			RoleCfg heroCfg = RoleCfgDAO.getInstance().getConfig(getTemplateId());
			if (heroCfg == null)
				return;
			if (type == marqueeStar) {
				MainMsgHandler.getInstance().sendPmdHpsx(m_pPlayer, heroCfg.getName(), num);
			} else if (type == marqueeQuality) {
				Hero hero = m_pPlayer.getHeroMgr().getHeroByModerId(heroCfg.getModelId());
				if (hero != null) {
					String qualityId = hero.getQualityId();
					RoleQualityCfg roleQualityCfg = RoleQualityCfgDAO.getInstance().getCfgById(qualityId);
					MainMsgHandler.getInstance().sendPmdHpJj(m_pPlayer, heroCfg.getName(), num, roleQualityCfg);
				}
			}
		}
	}

	public int getStarLevel() {
		return getRoleBaseInfo().getStarLevel();
	}

	// 设置佣兵的品阶id
	public void setQualityId(String qualityId) {
		m_roleBaseInfoMgr.setQualityId(qualityId);
		RoleQualityCfg cfg = RoleQualityCfgDAO.getInstance().getConfig(qualityId);
		if (cfg == null)
			return;
		// 跑马灯
		marqueeMsg(marqueeQuality, cfg.getQuality());
		m_SkillMgr.activeSkill(getLevel(), cfg.getQuality());
		m_pPlayer.getFresherActivityMgr().doCheck(eActivityType.A_HeroGrade);
	}

	public String getQualityId() {
		return getRoleBaseInfo().getQualityId();
	}

	public int getCareer() {
		return getRoleBaseInfo().getCareerType();
	}

	/**
	 * gm修改英雄等级
	 * 
	 * @param level
	 */
	public void gmEditHeroLevel(int level) {
		int preLevel = getRoleBaseInfo().getLevel();
		m_roleBaseInfoMgr.setLevel(level);
	}

	/**
	 * gm激活技能
	 */
	public void gmCheckActiveSkill() {
		// 开启技能
		RoleQualityCfg cfg = (RoleQualityCfg) RoleQualityCfgDAO.getInstance().getCfgById(getRoleBaseInfo().getQualityId());
		m_SkillMgr.activeSkill(getRoleBaseInfo().getLevel(), cfg.getQuality());
	}

	/*
	 * 增加佣兵经验,-1表示升级不成功,0-成功,1-成功且升级
	 */
	public int addHeroExp(long heroExp) {
		RoleBaseInfo roleBaseInfo = getRoleBaseInfo();
		if (roleBaseInfo == null) {
			return -1;
		}
		int maxLevel = m_pPlayer.getLevel();
		int currentLevel = roleBaseInfo.getLevel();
		int currentExp = (int) roleBaseInfo.getExp();
		int oldLevel = currentLevel;
		LevelCfgDAO levelCfgDAO = LevelCfgDAO.getInstance();
		for (;;) {
			LevelCfg currentCfg = levelCfgDAO.getByLevel(currentLevel);
			if (currentCfg == null) {
				GameLog.error("hero", "addExp", "获取等级配置失败：" + currentLevel, null);
				break;
			}
			int upgradeExp = currentCfg.getHeroUpgradeExp();
			if (upgradeExp > currentExp) {
				int needExp = upgradeExp - currentExp;
				if (heroExp >= needExp) {
					// 升级并消耗部分经验
					heroExp -= needExp;
					currentExp = upgradeExp;
				} else {
					// 不能升级，把剩余经验用完
					currentExp += (int) heroExp;
					heroExp = 0;
					break;
				}
			}
			// 对等级进行判断
			if (currentLevel >= maxLevel) {
				currentExp = upgradeExp;
				break;
			}
			currentLevel++;
			currentExp = 0;
		}
		m_roleBaseInfoMgr.setLevelAndExp(currentLevel, currentExp);
		return oldLevel == currentLevel ? 0 : 1;
	}

	public String getUUId() {
		return roleId;
	}

	public eRoleType getRoleType() {
		return roleType;
	}

	public boolean isMainRole() {
		return roleType == eRoleType.Player;
	}

	public AttrMgr getAttrMgr() {
		return m_AttrMgr;
	}

	public RoleBaseInfoMgr getRoleBaseInfoMgr() {
		return m_roleBaseInfoMgr;
	}

	public InlayMgr getInlayMgr() {
		return m_inlayMgr;
	}

	public SkillMgr getSkillMgr() {
		return m_SkillMgr;
	}

	public EquipMgr getEquipMgr() {
		return m_EquipMgr;
	}

	public FixNormEquipMgr getFixNormEquipMgr() {
		return m_FixNormEquipMgr;
	}

	public FixExpEquipMgr getFixExpEquipMgr() {
		return m_FixExpEquipMgr;
	}

}
