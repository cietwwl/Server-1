package com.playerdata.mgcsecret.cfg;

import java.util.Collection;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class DungeonsDataCfgDAO extends CfgCsvDao<DungeonsDataCfg> {
	public static DungeonsDataCfgDAO getInstance(){
		return SpringContextUtil.getBean(DungeonsDataCfgDAO.class);
	}
	
	@Override
	public Map<String, DungeonsDataCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("magicSecret/dungeonsData.csv", DungeonsDataCfg.class);
		Collection<DungeonsDataCfg> vals = cfgCacheMap.values();
		for (DungeonsDataCfg cfg : vals) {
			cfg.ExtraInitAfterLoad();
		}
		return cfgCacheMap;
	}
}
