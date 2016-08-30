package com.rwbase.dao.fighting;

import java.util.Map;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.dao.fighting.pojo.GroupSkillFightingCfg;

public class GroupSkillFightingCfgDAO extends FightingCfgCsvDAOBase<GroupSkillFightingCfg> {

	public static GroupSkillFightingCfgDAO getInstance() {
		return SpringContextUtil.getBean(GroupSkillFightingCfgDAO.class);
	}
	
	@Override
	protected Map<String, GroupSkillFightingCfg> initJsonCfg() {
		this.cfgCacheMap = readFightingCfgBaseType("GroupSkillFighting.csv", GroupSkillFightingCfg.class);
		return cfgCacheMap;
	}

}
