package com.rwbase.dao.groupCopy.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class GroupCopyLevelCfgDao extends CfgCsvDao<GroupCopyLevelCfg> {
	private static GroupCopyLevelCfgDao instance = new GroupCopyLevelCfgDao();
	private GroupCopyLevelCfgDao(){}
	public static GroupCopyLevelCfgDao getInstance(){
		return instance;
	}
	
	@Override
	public Map<String, GroupCopyLevelCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Group/GroupSkillCfg.csv", GroupCopyLevelCfg.class);
		return cfgCacheMap;
	}
	
	public GroupCopyLevelCfg getConfig(String id){
		GroupCopyLevelCfg cfg = (GroupCopyLevelCfg)getCfgById(id);
		return cfg;
	}
	
	
}
