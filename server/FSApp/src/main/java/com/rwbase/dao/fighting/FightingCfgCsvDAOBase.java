package com.rwbase.dao.fighting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.fighting.pojo.FightingCfgBase;

public abstract class FightingCfgCsvDAOBase<T extends FightingCfgBase> extends CfgCsvDao<T> {
	
	protected final String basePathFormat = "SystemFightingExpected/%s";
	
	private List<T> _sortByRequiredLvList;
	
	private final Comparator<FightingCfgBase> _comparator = new FightingCfgBaseComparator();
	
	protected abstract String getFileName();
	
	protected abstract Class<T> getCfgClazz();
	
	@Override
	protected final Map<String, T> initJsonCfg() {
		String path = String.format(basePathFormat, getFileName());
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map(path, getCfgClazz());
		for (Iterator<String> itr = cfgCacheMap.keySet().iterator(); itr.hasNext();) {
			cfgCacheMap.get(itr.next()).afterInit();
		}
		_sortByRequiredLvList = new ArrayList<T>(cfgCacheMap.size());
		for (Iterator<String> itr = cfgCacheMap.keySet().iterator(); itr.hasNext();) {
			_sortByRequiredLvList.add(cfgCacheMap.get(itr.next()));
		}
		Collections.sort(_sortByRequiredLvList, _comparator);
		return cfgCacheMap;
	}
	
	public T getByLevel(int lv) {
		if(lv <= 0) {
			return null;
		}
		T target = null;
		for (int i = 0; i < _sortByRequiredLvList.size(); i++) {
			T t = _sortByRequiredLvList.get(i);
			if (t.getRequiredLv() > lv) {
				if (target == null) {
					target = t;
				}
				break;
			} else {
				target = t;
			}
		}
		return target;
	}
	
	private static class FightingCfgBaseComparator implements Comparator<FightingCfgBase> {

		@Override
		public int compare(FightingCfgBase o1, FightingCfgBase o2) {
			return o1.getRequiredLv() < o2.getRequiredLv() ? -1 : (o1.getAllFighting() < o2.getAllFighting() ? -1 : 1);
		}
		
	}

}
