package com.rw.service.TaoistMagic.datamodel;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rw.service.TaoistMagic.datamodel.TaoistConsumeCfgHelper"  init-method="init" />

public class TaoistConsumeCfgHelper extends CfgCsvDao<TaoistConsumeCfg> {
	public static TaoistConsumeCfgHelper getInstance() {
		return SpringContextUtil.getBean(TaoistConsumeCfgHelper.class);
	}

	@Override
	public Map<String, TaoistConsumeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("TaoistMagic/TaoistConsumeCfg.csv",TaoistConsumeCfg.class);
		Collection<TaoistConsumeCfg> vals = cfgCacheMap.values();
		for (TaoistConsumeCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}//检查技能等级是否连续,检查货币类型是否正确，数量是否为正数
		//跨表检查：暴击组合序列是否存在TaoistCriticalPlanCfg
		return cfgCacheMap;
	}
}
