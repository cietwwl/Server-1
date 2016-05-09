package com.rw.service.TaoistMagic.datamodel;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rw.service.TaoistMagic.datamodel.TaoistMagiclCfgHelper"  init-method="init" />

public class TaoistMagiclCfgHelper extends CfgCsvDao<TaoistMagiclCfg> {
	public static TaoistMagiclCfgHelper getInstance() {
		return SpringContextUtil.getBean(TaoistMagiclCfgHelper.class);
	}

	@Override
	public Map<String, TaoistMagiclCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("TaoistMagic/TaoistMagiclCfg.csv",TaoistMagiclCfg.class);
		Collection<TaoistMagiclCfg> vals = cfgCacheMap.values();
		for (TaoistMagiclCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
