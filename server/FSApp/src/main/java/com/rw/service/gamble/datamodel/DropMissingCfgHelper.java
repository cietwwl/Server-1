package com.rw.service.gamble.datamodel;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rw.service.gamble.datamodel.DropMissingCfgHelper"  init-method="init" />

public class DropMissingCfgHelper extends CfgCsvDao<DropMissingCfg> {
	public static DropMissingCfgHelper getInstance() {
		return SpringContextUtil.getBean(DropMissingCfgHelper.class);
	}

	@Override
	public Map<String, DropMissingCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("gamble/DropMissingCfg.csv",DropMissingCfg.class);
		Collection<DropMissingCfg> vals = cfgCacheMap.values();
		for (DropMissingCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
	
	public boolean isDropMissingId(String id){
		DropMissingCfg cfg = cfgCacheMap.get(id);
		return cfg != null;
	}
}
