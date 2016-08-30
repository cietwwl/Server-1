package com.rwbase.dao.fighting;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.fighting.pojo.OneToOneTypeFightingCfg;

public abstract class FightingCfgCsvDAOOneToOneBase extends CfgCsvDao<OneToOneTypeFightingCfg> {

	protected final String basePathFormat = "SystemFightingExpected/%s";
	
	protected abstract String getFileName();
	
	@Override
	protected final Map<String, OneToOneTypeFightingCfg> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map(String.format(this.basePathFormat, getFileName()), OneToOneTypeFightingCfg.class);
		return cfgCacheMap;
	}

}
