package com.rwbase.dao.groupSkill.cfg;

import java.util.Map;

import com.groupCopy.rwbase.dao.groupCopy.cfg.GroupCopyMapCfgDao;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class GroupSkillCfgDao extends CfgCsvDao<GroupSkillCfg> {
	public static GroupSkillCfgDao getInstance() {
		return SpringContextUtil.getBean(GroupSkillCfgDao.class);
	}
	
	@Override
	public Map<String, GroupSkillCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Group/GroupSkillCfg.csv", GroupSkillCfg.class);
		return cfgCacheMap;
	}
	
	public GroupSkillCfg getConfig(String id){
		GroupSkillCfg cfg = (GroupSkillCfg)getCfgById(id);
		return cfg;
	}
	
	
}
