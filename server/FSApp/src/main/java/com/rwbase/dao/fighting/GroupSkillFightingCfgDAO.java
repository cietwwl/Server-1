package com.rwbase.dao.fighting;

import com.rw.fsutil.util.SpringContextUtil;

public class GroupSkillFightingCfgDAO extends FightingCfgCsvDAOOneToOneBase {

	public static GroupSkillFightingCfgDAO getInstance() {
		return SpringContextUtil.getBean(GroupSkillFightingCfgDAO.class);
	}
	
	@Override
	protected String getFileName() {
		return "GroupSkillFighting.csv";
	}

}
