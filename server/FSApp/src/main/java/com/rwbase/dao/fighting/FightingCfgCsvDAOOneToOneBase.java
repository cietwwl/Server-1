package com.rwbase.dao.fighting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.fighting.pojo.OneToOneTypeFightingCfg;

public abstract class FightingCfgCsvDAOOneToOneBase extends CfgCsvDao<OneToOneTypeFightingCfg> {

	protected final String basePathFormat = "SystemFightingExpected/%s";
	
	private Map<Integer, OneToOneTypeFightingCfg> _lvMap;
	
	protected abstract String getFileName();
	
	@Override
	protected final Map<String, OneToOneTypeFightingCfg> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map(String.format(this.basePathFormat, getFileName()), OneToOneTypeFightingCfg.class);
		this._lvMap = new LinkedHashMap<Integer, OneToOneTypeFightingCfg>(this.cfgCacheMap.size() + 1, 1.0f);
		List<Integer> list = new ArrayList<Integer>();
		Map<Integer, OneToOneTypeFightingCfg> map = new HashMap<Integer, OneToOneTypeFightingCfg>();
		for (Iterator<String> itr = this.cfgCacheMap.keySet().iterator(); itr.hasNext();) {
			OneToOneTypeFightingCfg cfg = this.cfgCacheMap.get(itr.next());
			list.add(cfg.getRequiredLv());
			map.put(cfg.getRequiredLv(), cfg);
		}
		Collections.sort(list);
		for (int i = 0; i < list.size(); i++) {
			int lv = list.get(i);
			this._lvMap.put(lv, map.get(lv));
		}
		return cfgCacheMap;
	}
	
	public OneToOneTypeFightingCfg getByRequiredLv(int lv) {
		OneToOneTypeFightingCfg cfg = this._lvMap.get(lv);
		if (cfg != null) {
			return cfg;
		} else {
			int tempLv;
			OneToOneTypeFightingCfg tempCfg;
			for (Iterator<Integer> itr = _lvMap.keySet().iterator(); itr.hasNext();) {
				tempLv = itr.next();
				tempCfg = _lvMap.get(tempLv);
				if (tempLv > lv) {
					if (cfg == null) {
						cfg = tempCfg;
					}
					break;
				} else {
					cfg = tempCfg;
				}
			}
			return cfg;
		}
	}

}
