package com.rw.service.TaoistMagic.datamodel;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rw.service.TaoistMagic.datamodel.TaoistMagiclCfgHelper"  init-method="init" />

public class TaoistMagiclCfgHelper extends CfgCsvDao<TaoistMagicCfg> {
	public static TaoistMagiclCfgHelper getInstance() {
		return SpringContextUtil.getBean(TaoistMagiclCfgHelper.class);
	}

	@Override
	public Map<String, TaoistMagicCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("TaoistMagic/TaoistMagicCfg.csv",TaoistMagicCfg.class);
		Collection<TaoistMagicCfg> vals = cfgCacheMap.values();
		for (TaoistMagicCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}//检查开放等级与分页的关系，检查序号与分页的关系，检查属性是否存在，检查属性类型是否正确，检查公式参数是否有效，
		//跨表检查，consumeId是否在TaoistConsumeCfg有定义
		return cfgCacheMap;
	}
}
