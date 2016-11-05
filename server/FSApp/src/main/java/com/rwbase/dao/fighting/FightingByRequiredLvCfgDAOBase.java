package com.rwbase.dao.fighting;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.fighting.pojo.FightingByRequiredLv;

public abstract class FightingByRequiredLvCfgDAOBase<T extends FightingByRequiredLv> extends CfgCsvDao<T> {

	protected final String basePathFormat = "SystemFightingExpected/%s";

	private TreeMap<Integer, T> _lvMap;

	protected abstract String getFileName();

	protected abstract Class<T> getElementClass();

	@Override
	protected Map<String, T> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map(String.format(this.basePathFormat, getFileName()), getElementClass());
		this._lvMap = new TreeMap<Integer, T>();
		for (Iterator<String> itr = this.cfgCacheMap.keySet().iterator(); itr.hasNext();) {
			T cfg = this.cfgCacheMap.get(itr.next());
			_lvMap.put(cfg.getRequiredLv(), cfg);
		}
		return this.cfgCacheMap;
	}

	public T getByRequiredLv(int lv) {
		Entry<Integer, T> entry = _lvMap.floorEntry(lv);
		return entry.getValue();
	}
}
