package com.rwbase.dao.groupSkill.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class GroupSkillCfgDao extends CfgCsvDao<GroupSkillCfg> {
	private static GroupSkillCfgDao instance = new GroupSkillCfgDao();
	private GroupSkillCfgDao(){}
	public static GroupSkillCfgDao getInstance(){
		return instance;
	}
	
	@Override
	public Map<String, GroupSkillCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("group/GroupSkillCfg.csv", GroupSkillCfg.class);
		return cfgCacheMap;
	}
	
	public GroupSkillCfg getConfig(String id){
		GroupSkillCfg cfg = (GroupSkillCfg)getCfgById(id);
		return cfg;
	}
	
	
}
