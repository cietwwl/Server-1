package com.rw.service.TaoistMagic.datamodel;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rw.service.TaoistMagic.datamodel.TaoistCriticalPlanCfgHelper"  init-method="init" />

public class TaoistCriticalPlanCfgHelper extends CfgCsvDao<TaoistCriticalPlanCfg> {
	public static TaoistCriticalPlanCfgHelper getInstance() {
		return SpringContextUtil.getBean(TaoistCriticalPlanCfgHelper.class);
	}

	@Override
	public Map<String, TaoistCriticalPlanCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("TaoistMagic/TaoistCriticalPlanCfg.csv",TaoistCriticalPlanCfg.class);
		Collection<TaoistCriticalPlanCfg> vals = cfgCacheMap.values();
		for (TaoistCriticalPlanCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}//解析暴击序列，看看是否正确
		return cfgCacheMap;
	}
}
