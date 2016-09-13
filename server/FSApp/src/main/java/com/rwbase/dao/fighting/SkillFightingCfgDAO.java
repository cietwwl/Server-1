package com.rwbase.dao.fighting;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.dao.fighting.pojo.SkillFightingCfg;

public class SkillFightingCfgDAO extends FightingCfgCsvDAOBase<SkillFightingCfg> {

	public static SkillFightingCfgDAO getInstance() {
		return SpringContextUtil.getBean(SkillFightingCfgDAO.class);
	}
	
	@Override
	protected String getFileName() {
		return "SkillFighting.csv";
	}

	@Override
	protected Class<SkillFightingCfg> getCfgClazz() {
		return SkillFightingCfg.class;
	}
}
