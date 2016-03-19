package com.rwbase.dao.groupSkill.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class GroupSkillLevelCfgDao extends CfgCsvDao<GroupSkillLevelCfg> {
	private static GroupSkillLevelCfgDao instance = new GroupSkillLevelCfgDao();
	private GroupSkillLevelCfgDao(){}
	public static GroupSkillLevelCfgDao getInstance(){
		return instance;
	}
	
	@Override
	public Map<String, GroupSkillLevelCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("group/GroupSkillLevelCfg.csv", GroupSkillLevelCfg.class);
		return cfgCacheMap;
	}
	
	public GroupSkillLevelCfg getConfig(String id){
		GroupSkillLevelCfg cfg = (GroupSkillLevelCfg)getCfgById(id);
		return cfg;
	}
	
	
}
