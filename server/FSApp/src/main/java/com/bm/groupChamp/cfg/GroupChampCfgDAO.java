package com.bm.groupChamp.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class GroupChampCfgDAO extends CfgCsvDao<GroupChampCfg> {

	public static GroupChampCfgDAO getInstance() {
		return SpringContextUtil.getBean(GroupChampCfgDAO.class);
	}

	
	@Override
	public Map<String, GroupChampCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("groupChamp/GroupChampCfg.csv", GroupChampCfg.class);
		return cfgCacheMap;
	}
	





}