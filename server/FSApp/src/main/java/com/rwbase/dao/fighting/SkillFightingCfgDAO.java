package com.rwbase.dao.fighting;

import java.util.Map;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.dao.fighting.pojo.SkillFightingCfg;

public class SkillFightingCfgDAO extends FightingCfgCsvDAOBase<SkillFightingCfg> {

	public static SkillFightingCfgDAO getInstance() {
		return SpringContextUtil.getBean(SkillFightingCfgDAO.class);
	}
	
	@Override
	protected Map<String, SkillFightingCfg> initJsonCfg() {
		this.cfgCacheMap = readFightingCfgBaseType("SkillFighting.csv", SkillFightingCfg.class);
		return cfgCacheMap;
	}

}
