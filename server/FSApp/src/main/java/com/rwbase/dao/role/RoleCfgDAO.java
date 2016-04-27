package com.rwbase.dao.role;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.role.pojo.RoleCfg;
import com.rwbase.dao.skill.pojo.Skill;

public class RoleCfgDAO extends CfgCsvDao<RoleCfg> {

	public static RoleCfgDAO getInstance() {
		return SpringContextUtil.getBean(RoleCfgDAO.class);
	}

	protected HashMap<String, RoleCfg> cfgModeMap;// 保存唯一召唤的英雄配置信息
	@Override
	public Map<String, RoleCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("role/RoleCfg.csv", RoleCfg.class);
		initData();
		return cfgCacheMap;
	}

	/**
	 * 获取佣兵配置拷贝
	 * 
	 * @return
	 */
	public Map<String, RoleCfg> getAllRoleCfgCopy() {
		if (CollectionUtils.isEmpty(cfgModeMap)) {
			initData();
		}
		return new HashMap<String, RoleCfg>(this.cfgModeMap);
	}

	public RoleCfg getConfig(String roleId) {
		// List<RoleCfg> allCfgs = super.getAllCfg();
		// if (allCfgs != null && allCfgs.size() > 0) {
		// RoleCfg playerCfg = null;
		// for (int i = 0; i < allCfgs.size(); i++) {
		// playerCfg = allCfgs.get(i);
		// if (playerCfg.getRoleId().equals(roleId)) {
		// return playerCfg;
		// }
		// }
		// }
		// return null;

		return (RoleCfg) getCfgById(roleId);
	}

	public RoleCfg GetConfigBySexCareer(int sex, int careerType, int star) {
		List<RoleCfg> allCfgs = super.getAllCfg();
		RoleCfg playerCfg = null;
		for (int i = 0; i < allCfgs.size(); i++) {
			RoleCfg playerCfgTmp = allCfgs.get(i);
			if (playerCfgTmp.getRoleType() == 1 && playerCfgTmp.getCareerType() == careerType && playerCfgTmp.getSex() == sex && playerCfgTmp.getStarLevel() == star) {
				playerCfg = playerCfgTmp;
				break;
			}
		}
		return playerCfg;
	}

	/*
	 * 根据模型Id和星级获取RoleCfg
	 */
	public RoleCfg getConfig(int modelId, int starLevel) {
		// List<RoleCfg> allCfgs = super.getAllCfg();
		// for (int i = 0; i < allCfgs.size(); i++) {
		// RoleCfg tmpRoleCfg = allCfgs.get(i);
		// if (tmpRoleCfg.getModelId() == modelId && tmpRoleCfg.getStarLevel() == starLevel)
		// return tmpRoleCfg;
		// }
		String templateId = modelId + "_" + starLevel;// 模版Id
		return this.getConfig(templateId);
	}

	public List<Skill> getSkill(String playerId) {
		RoleCfg pPlayerCfg = getConfig(playerId);
		if (pPlayerCfg == null) {
			return null;
		}
		List<Skill> arr = new ArrayList<Skill>();

		if (StringUtils.isNotBlank(pPlayerCfg.getSkillId01())) {
			Skill pSkill = new Skill();
			pSkill.setSkillId(pPlayerCfg.getSkillId01());
			pSkill.setOrder(0);
			pSkill.setLevel(1);
			arr.add(pSkill);
		}
		if (StringUtils.isNotBlank(pPlayerCfg.getSkillId02())) {
			Skill pSkill = new Skill();
			pSkill.setSkillId(pPlayerCfg.getSkillId02());
			pSkill.setOrder(1);
			pSkill.setLevel(0);
			arr.add(pSkill);
		}
		if (StringUtils.isNotBlank(pPlayerCfg.getSkillId03())) {
			Skill pSkill = new Skill();
			pSkill.setSkillId(pPlayerCfg.getSkillId03());
			pSkill.setOrder(2);
			pSkill.setLevel(0);
			arr.add(pSkill);
		}
		if (StringUtils.isNotBlank(pPlayerCfg.getSkillId04())) {
			Skill pSkill = new Skill();
			pSkill.setSkillId(pPlayerCfg.getSkillId04());
			pSkill.setOrder(3);
			pSkill.setLevel(0);
			arr.add(pSkill);
		}
		if (StringUtils.isNotBlank(pPlayerCfg.getSkillId05())) {
			Skill pSkill = new Skill();
			pSkill.setSkillId(pPlayerCfg.getSkillId05());
			pSkill.setOrder(4);
			pSkill.setLevel(-1);
			arr.add(pSkill);
		}
		if (StringUtils.isNotBlank(pPlayerCfg.getDieSkillId())) {
			Skill pSkill = new Skill();
			pSkill.setSkillId(pPlayerCfg.getDieSkillId());
			pSkill.setOrder(5);
			pSkill.setLevel(-2);// 死亡技能等级设为-2
			arr.add(pSkill);
		}
		return arr;
	}

	public String getAttackId(String roleId) {
		RoleCfg config = getConfig(roleId);
		if (config == null) {
			return null;
		}
		return config.getAttackId();
	}

	// 玩家可拥有的唯一英雄
	public RoleCfg getCfgByModeID(String modeID) {
		RoleCfg heroCfg = cfgModeMap.get(modeID);
		return heroCfg;
	}

	private void initData() {
		List<RoleCfg> list = getAllCfg();
		HashMap<String, RoleCfg> cfgModeMap_ = new HashMap<String, RoleCfg>();
		int count = list.size();
		for (int i = 0; i < count; i++) {
			RoleCfg cfg = list.get(i);
			if (cfg.getSummonFlag() == 1 && cfg.getCanShow() == 1) {
				String modeId = String.valueOf(cfg.getModelId());
				cfgModeMap_.put(modeId, cfg);
			}
		}
		this.cfgModeMap = cfgModeMap_;
	}
}
