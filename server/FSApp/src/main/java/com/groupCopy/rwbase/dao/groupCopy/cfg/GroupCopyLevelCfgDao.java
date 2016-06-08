package com.groupCopy.rwbase.dao.groupCopy.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class GroupCopyLevelCfgDao extends CfgCsvDao<GroupCopyLevelCfg> {
	public static GroupCopyLevelCfgDao getInstance() {
		return SpringContextUtil.getBean(GroupCopyLevelCfgDao.class);
	}
	
	@Override
	public Map<String, GroupCopyLevelCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("GroupCopy/GroupCopyLevelCfg.csv", GroupCopyLevelCfg.class);
		return cfgCacheMap;
	}
	
	public GroupCopyLevelCfg getConfig(String id){
		GroupCopyLevelCfg cfg = (GroupCopyLevelCfg)getCfgById(id);
		return cfg;
	}
	
	
}
