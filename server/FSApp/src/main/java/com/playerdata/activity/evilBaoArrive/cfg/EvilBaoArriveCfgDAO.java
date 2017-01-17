package com.playerdata.activity.evilBaoArrive.cfg;

import java.util.Map;

import com.playerdata.activityCommon.activityType.ActivityCfgIF;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.playerdata.activity.evilBaoArrive.cfg.EvilBaoArriveCfgDAO"  init-method="init" />

public class EvilBaoArriveCfgDAO extends CfgCsvDao<EvilBaoArriveCfg> {
	public static EvilBaoArriveCfgDAO getInstance() {
		return SpringContextUtil.getBean(EvilBaoArriveCfgDAO.class);
	}

	@Override
	public Map<String, EvilBaoArriveCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Activity/EvilBaoArrive/EvilBaoArriveCfg.csv",EvilBaoArriveCfg.class);
		for(ActivityCfgIF cfg : cfgCacheMap.values()){
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
