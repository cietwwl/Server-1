package com.playerdata.mgcsecret.cfg;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class MagicChapterCfgDAO extends CfgCsvDao<MagicChapterCfg> {
	public static MagicChapterCfgDAO getInstance(){
		return SpringContextUtil.getBean(MagicChapterCfgDAO.class);
	}
	
	@Override
	public Map<String, MagicChapterCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("magicSecret/magicChapterCfg.csv", MagicChapterCfg.class);
		Set<Entry<String, MagicChapterCfg>> entrySet = cfgCacheMap.entrySet();
		for (Entry<String, MagicChapterCfg> entry : entrySet) {
			if(entry != null){
				MagicChapterCfg cfg = entry.getValue();
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
