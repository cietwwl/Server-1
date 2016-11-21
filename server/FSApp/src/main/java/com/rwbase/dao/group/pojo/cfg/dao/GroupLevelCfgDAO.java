package com.rwbase.dao.group.pojo.cfg.dao;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.group.pojo.cfg.GroupLevelCfg;

/*
 * @author HC
 * @date 2016年1月18日 上午10:55:28
 * @Description 帮派等级的模版表
 */
public class GroupLevelCfgDAO extends CfgCsvDao<GroupLevelCfg> {

	public static GroupLevelCfgDAO getDAO() {
		return SpringContextUtil.getBean(GroupLevelCfgDAO.class);
	}

	protected GroupLevelCfgDAO() {
	}

	@Override
	public Map<String, GroupLevelCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Group/GroupLevelCfg.csv", GroupLevelCfg.class);
		return cfgCacheMap;
	}

	/**
	 * 获取等级对应的模版信息
	 * 
	 * @param level
	 * @return
	 */
	public GroupLevelCfg getLevelCfg(int level) {
		if (cfgCacheMap == null || cfgCacheMap.isEmpty()) {
			return null;
		}

		return cfgCacheMap.get(String.valueOf(level));
	}

	// /**
	// * 获取帮派的最大等级
	// *
	// * @return
	// */
	// public int getMaxGroupLevel() {
	// return maxGroupLevel;
	// }
}