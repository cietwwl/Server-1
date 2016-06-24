package com.groupCopy.rwbase.dao.groupCopy.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class GroupCopyDonateCfgDao extends CfgCsvDao<GroupCopyDonateCfg>{

	
	public static GroupCopyDonateCfgDao getInstance(){
		return SpringContextUtil.getBean(GroupCopyDonateCfgDao.class);
	}
	
	@Override
	protected Map<String, GroupCopyDonateCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("GroupCopy/GroupCopyDonateCfg.csv", GroupCopyDonateCfg.class);
		return cfgCacheMap;
	}

	public GroupCopyDonateCfg getConfig(String id){
		GroupCopyDonateCfg cfg = getCfgById(id);
		return cfg;
	}
}
