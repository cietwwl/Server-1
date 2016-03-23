package com.rwbase.dao.groupCopy.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.gamble.pojo.cfg.GambleRewardCfgDAO;

public class GroupCopyLevelCfgDao extends CfgCsvDao<GroupCopyLevelCfg> {
	public static GroupCopyLevelCfgDao getInstance() {
		return SpringContextUtil.getBean(GroupCopyLevelCfgDao.class);
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
