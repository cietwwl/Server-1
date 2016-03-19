package com.rwbase.dao.groupCopy.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class GroupCopyMapCfgDao extends CfgCsvDao<GroupCopyMapCfg> {
	private static GroupCopyMapCfgDao instance = new GroupCopyMapCfgDao();
	private GroupCopyMapCfgDao(){}
	public static GroupCopyMapCfgDao getInstance(){
		return instance;
	}
	
	@Override
	public Map<String, GroupCopyMapCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("group/GroupSkillLevelCfg.csv", GroupCopyMapCfg.class);
		return cfgCacheMap;
	}
	
	public GroupCopyMapCfg getConfig(String id){
		GroupCopyMapCfg cfg = (GroupCopyMapCfg)getCfgById(id);
		return cfg;
	}
	
	
}
