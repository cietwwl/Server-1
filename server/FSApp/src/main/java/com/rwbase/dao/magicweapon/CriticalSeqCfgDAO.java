package com.rwbase.dao.magicweapon;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.log.GameLog;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.magicweapon.pojo.CriticalEnhanceCfg;
import com.rwbase.dao.magicweapon.pojo.CriticalSeqCfg;

public class CriticalSeqCfgDAO extends CfgCsvDao<CriticalSeqCfg> {
		private static CriticalSeqCfgDAO instance = new CriticalSeqCfgDAO();
		
		private CriticalSeqCfgDAO(){}
		public static CriticalSeqCfgDAO getInstance(){
			return instance;
		}
		
		@Override
		public Map<String, CriticalSeqCfg> initJsonCfg() {
			cfgCacheMap = CfgCsvHelper.readCsv2Map("MagicWeapon/CriticalSeq.csv", CriticalSeqCfg.class);
			Set<Entry<String, CriticalSeqCfg>> entrySet = cfgCacheMap.entrySet();
			for (Entry<String, CriticalSeqCfg> entry : entrySet) {
				if(entry != null){
					CriticalSeqCfg cfg = entry.getValue();
					if (cfg != null) {
						cfg.ExtraInit();
					}else{
						GameLog.error("法宝", "CriticalSeq.csv", "invalid cfg");
					}
				}
			}

			return cfgCacheMap;
		}
		
}
