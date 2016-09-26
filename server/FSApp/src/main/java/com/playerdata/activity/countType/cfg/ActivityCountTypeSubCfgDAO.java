package com.playerdata.activity.countType.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.playerdata.activity.ActivityTypeHelper;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class ActivityCountTypeSubCfgDAO extends
		CfgCsvDao<ActivityCountTypeSubCfg> {

	public static ActivityCountTypeSubCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityCountTypeSubCfgDAO.class);
	}

	private HashMap<String, List<ActivityCountTypeSubCfg>> parentCfgMapping = new HashMap<String, List<ActivityCountTypeSubCfg>>();

	@Override
	public Map<String, ActivityCountTypeSubCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map(
				"Activity/ActivityCountTypeSubCfg.csv",
				ActivityCountTypeSubCfg.class);
		HashMap<String, List<ActivityCountTypeSubCfg>> parentCfgMappingTmp = new HashMap<String, List<ActivityCountTypeSubCfg>>();
		for (ActivityCountTypeSubCfg subCfg : cfgCacheMap.values()) {
			ActivityTypeHelper.add(subCfg,subCfg.getParentCfg(), parentCfgMappingTmp);
		}
		this.parentCfgMapping = parentCfgMappingTmp;
		return cfgCacheMap;
	}

	public List<ActivityCountTypeSubCfg> getByParentCfgId(String parentCfgId) {
		List<ActivityCountTypeSubCfg> targetList = new ArrayList<ActivityCountTypeSubCfg>();
		List<ActivityCountTypeSubCfg> parentCfgList = parentCfgMapping
				.get(parentCfgId);
		if (parentCfgList == null || parentCfgList.isEmpty()) {
			return targetList;
		}
		for (ActivityCountTypeSubCfg subCfg : parentCfgList) {
			if (StringUtils.equals(subCfg.getParentCfg(), parentCfgId)) {
				targetList.add(subCfg);
			}
		}
		return targetList;
	}

	public ActivityCountTypeSubCfg getById(String subId) {
		ActivityCountTypeSubCfg target = cfgCacheMap.get(subId);
		return target;

	}

}