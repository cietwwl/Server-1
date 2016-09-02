package com.rwbase.dao.fighting;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.fighting.pojo.FightingWeightCfg;

public class FightingWeightCfgDAO  extends CfgCsvDao<FightingWeightCfg> {
	
	private Map<String, Float> _weightOfAttrName;
	
	public static FightingWeightCfgDAO getInstance() {
		return SpringContextUtil.getBean(FightingWeightCfgDAO.class);
	}

	
	@Override
	public Map<String, FightingWeightCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fighting/FightingWeightCfg.csv", FightingWeightCfg.class);
		Map<String, Float> map = new HashMap<String, Float>();
		for (Iterator<FightingWeightCfg> itr = cfgCacheMap.values().iterator(); itr.hasNext();) {
			FightingWeightCfg cfg = itr.next();
			map.put(cfg.getAttrName(), cfg.getWeight());
		}
		_weightOfAttrName = Collections.unmodifiableMap(map);
		return cfgCacheMap;
	}
	
	public Map<String, Float> getWeightsOfAttrName() {
		return _weightOfAttrName;
	}
}
