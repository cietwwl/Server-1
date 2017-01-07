package com.playerdata.activity.exChangeType.cfg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playerdata.activity.ActivityTypeHelper;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class ActivityExchangeTypeCfgDAO extends CfgCsvDao<ActivityExchangeTypeCfg> {
	public static ActivityExchangeTypeCfgDAO getInstance() {
		return SpringContextUtil.getBean(ActivityExchangeTypeCfgDAO.class);
	}

	private HashMap<String, List<ActivityExchangeTypeCfg>> cfgListMap;

	@Override
	public Map<String, ActivityExchangeTypeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/ActivityExchangeTypeCfg.csv", ActivityExchangeTypeCfg.class);
		HashMap<String, List<ActivityExchangeTypeCfg>> cfgListMapTmp = new HashMap<String, List<ActivityExchangeTypeCfg>>();
		for (ActivityExchangeTypeCfg cfg : cfgCacheMap.values()) {
			cfg.ExtraInitAfterLoad();
			ActivityTypeHelper.add(cfg, cfg.getEnumId(), cfgListMapTmp);
		}
		this.cfgListMap = cfgListMapTmp;
		return cfgCacheMap;
	}

	public List<ActivityExchangeTypeCfg> isCfgByEnumIdEmpty(String enumId) {
		List<ActivityExchangeTypeCfg> typeCfgList = cfgListMap.get(enumId);
		return typeCfgList;
	}
}