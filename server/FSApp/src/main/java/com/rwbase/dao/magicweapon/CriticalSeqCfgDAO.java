package com.rwbase.dao.magicweapon;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.log.GameLog;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.magicweapon.pojo.CriticalSeqCfg;

public class CriticalSeqCfgDAO extends CfgCsvDao<CriticalSeqCfg> {
		public static CriticalSeqCfgDAO getInstance(){
			return SpringContextUtil.getBean(CriticalSeqCfgDAO.class);
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
