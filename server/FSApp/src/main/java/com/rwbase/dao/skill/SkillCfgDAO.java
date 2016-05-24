package com.rwbase.dao.skill;

import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.skill.pojo.SkillCfg;

public class SkillCfgDAO extends CfgCsvDao<SkillCfg> {

	public static SkillCfgDAO getInstance() {
		return SpringContextUtil.getBean(SkillCfgDAO.class);
	}

	@Override
	public Map<String, SkillCfg> initJsonCfg() {
		Map<String, SkillCfg> readCsv2Map = CfgCsvHelper.readCsv2Map("skillCfg/SkillCfg.csv", SkillCfg.class);
		if (readCsv2Map == null) {
			return cfgCacheMap;
		}

		for (Entry<String, SkillCfg> e : readCsv2Map.entrySet()) {
			e.getValue().initData();
		}

		return cfgCacheMap = readCsv2Map;
	}

	public SkillCfg getCfg(String skillId) {
		return (SkillCfg) getCfgById(skillId);
	}
}
