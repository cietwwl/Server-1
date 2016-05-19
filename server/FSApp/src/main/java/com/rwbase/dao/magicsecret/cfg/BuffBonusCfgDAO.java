package com.rwbase.dao.magicsecret.cfg;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class BuffBonusCfgDAO extends CfgCsvDao<BuffBonusCfg> {
	public static BuffBonusCfgDAO getInstance(){
		return SpringContextUtil.getBean(BuffBonusCfgDAO.class);
	}
	
	@Override
	public Map<String, BuffBonusCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("magicSecret/buffBonus.csv", BuffBonusCfg.class);
		Set<Entry<String, BuffBonusCfg>> entrySet = cfgCacheMap.entrySet();
		for (Entry<String, BuffBonusCfg> entry : entrySet) {
			if(entry != null){
				BuffBonusCfg cfg = entry.getValue();
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
