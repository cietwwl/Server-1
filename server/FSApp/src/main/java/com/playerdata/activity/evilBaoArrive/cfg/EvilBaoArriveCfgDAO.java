package com.playerdata.activity.evilBaoArrive.cfg;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rw.service.EvilBaoArrive.datamodel.EvilBaoArriveCfgHelper"  init-method="init" />

public class EvilBaoArriveCfgDAO extends CfgCsvDao<EvilBaoArriveCfg> {
	public static EvilBaoArriveCfgDAO getInstance() {
		return SpringContextUtil.getBean(EvilBaoArriveCfgDAO.class);
	}

	@Override
	public Map<String, EvilBaoArriveCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("EvilBaoArrive/EvilBaoArriveCfg.csv",EvilBaoArriveCfg.class);
		Collection<EvilBaoArriveCfg> vals = cfgCacheMap.values();
		for (EvilBaoArriveCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
