package com.bm.worldBoss.cfg;

import java.util.HashMap;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public final class WBExAwardCfgDAO extends CfgCsvDao<WBExAwardCfg> {	


	private Map<Integer, WBExAwardCfg> cfgMap = new HashMap<Integer, WBExAwardCfg>();
	
	public static WBExAwardCfgDAO getInstance() {
		return SpringContextUtil.getBean(WBExAwardCfgDAO.class);
	}

	@Override
	public Map<String, WBExAwardCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("worldBoss/WBExAwardCfg.csv", WBExAwardCfg.class);
		return cfgCacheMap;
	}

	@Override
	public void CheckConfig() {
		String[] key = new String[cfgCacheMap.size()];
		cfgCacheMap.keySet().toArray(key);
		WBExAwardCfg nextCfg, curCfg;
		int curIndex = 0, nextIndex = 0;
		for (int i = 0; i < key.length; i++) {
			curCfg = cfgCacheMap.get(key[i]);
			if (i >= key.length - 1) {
				nextCfg = null;
			} else {
				nextCfg = cfgCacheMap.get(key[i + 1]);
			}
			curIndex = curCfg.getSurvivalCount();

			nextIndex = nextCfg == null ? curIndex + 1 : nextCfg.getSurvivalCount();
			for (int j = curIndex; j < nextIndex; j++) {
				cfgMap.put(j, curCfg);
			}

		}

	}

	public WBExAwardCfg getCfg(int key){
		return cfgMap.get(key);
	}

}