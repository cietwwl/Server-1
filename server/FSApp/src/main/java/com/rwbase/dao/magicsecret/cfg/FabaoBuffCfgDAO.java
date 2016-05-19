package com.rwbase.dao.magicsecret.cfg;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class FabaoBuffCfgDAO extends CfgCsvDao<FabaoBuffCfg> {
	public static FabaoBuffCfgDAO getInstance(){
		return SpringContextUtil.getBean(FabaoBuffCfgDAO.class);
	}
	
	@Override
	public Map<String, FabaoBuffCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("magicSecret/fabaoBuff.csv", FabaoBuffCfg.class);
		Set<Entry<String, FabaoBuffCfg>> entrySet = cfgCacheMap.entrySet();
		for (Entry<String, FabaoBuffCfg> entry : entrySet) {
			if(entry != null){
				FabaoBuffCfg cfg = entry.getValue();
				if (cfg != null) {
					//cfg.ExtraInit();
				}else{
					//GameLog.error("法宝", "CriticalEnhance.csv", "invalid cfg");
				}
			}
		}

		return cfgCacheMap;
	}
}
