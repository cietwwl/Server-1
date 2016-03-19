package com.rwbase.dao.magicweapon;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.log.GameLog;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.magicweapon.pojo.CriticalEnhanceCfg;

public class CriticalEnhanceCfgDAO extends CfgCsvDao<CriticalEnhanceCfg> {
			private static CriticalEnhanceCfgDAO instance = new CriticalEnhanceCfgDAO();
			
			private CriticalEnhanceCfgDAO(){}
			public static CriticalEnhanceCfgDAO getInstance(){
				return instance;
			}
			
			@Override
			public Map<String, CriticalEnhanceCfg> initJsonCfg() {
				cfgCacheMap = CfgCsvHelper.readCsv2Map("MagicWeapon/CriticalEnhance.csv", CriticalEnhanceCfg.class);
				Set<Entry<String, CriticalEnhanceCfg>> entrySet = cfgCacheMap.entrySet();
				for (Entry<String, CriticalEnhanceCfg> entry : entrySet) {
					if(entry != null){
						CriticalEnhanceCfg cfg = entry.getValue();
						if (cfg != null) {
							cfg.ExtraInit();
						}else{
							GameLog.error("法宝", "CriticalEnhance.csv", "invalid cfg");
						}
					}
				}

				return cfgCacheMap;
			}
}
