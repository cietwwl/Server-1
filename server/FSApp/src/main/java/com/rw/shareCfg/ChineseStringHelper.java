package com.rw.shareCfg;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

//	<bean class="com.rw.shareCfg.ChineseStringHelper"  init-method="init" />

public class ChineseStringHelper extends CfgCsvDao<ChineseString> {
	public static ChineseStringHelper getInstance() {
		return SpringContextUtil.getBean(ChineseStringHelper.class);
	}

	@Override
	public Map<String, ChineseString> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("chineseString/ChineseString.csv",ChineseString.class);
		Collection<ChineseString> vals = cfgCacheMap.values();
		for (ChineseString cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
	
	public String getLanguageString(String key,String defaultVal){
		ChineseString cfg = getCfgById(key);
		if (cfg == null){
			return defaultVal;
		}
		return cfg.getValue();
	}
}
