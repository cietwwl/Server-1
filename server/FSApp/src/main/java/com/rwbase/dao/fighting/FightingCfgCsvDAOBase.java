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

	protected Map<String, T> readFightingCfgBaseType(String fileName, Class<T> clazz) {
		String path = String.format(basePathFormat, fileName);
		Map<String, T> map = CfgCsvHelper.readCsv2Map(path, clazz);
		for (Iterator<T> itr = map.values().iterator(); itr.hasNext();) {
			itr.next().afterInit();
		}
		_sortByRequiredLvList = new ArrayList<T>(map.size());
		for(T t : map.values()) {
			_sortByRequiredLvList.add(t);
		}
		Collections.sort(_sortByRequiredLvList, _comparator);
		return map;
	}
	
	public T getByLevel(int lv) {
		T pre = null;
		for (int i = 0; i < _sortByRequiredLvList.size(); i++) {
			T t = _sortByRequiredLvList.get(i);
			if (t.getRequiredLv() > lv) {
				if (pre == null) {
					pre = t;
					break;
				}
			} else {
				pre = t;
			}
		}
		return pre;
	}
	
	private static class FightingCfgBaseComparator implements Comparator<FightingCfgBase> {

		@Override
		public int compare(FightingCfgBase o1, FightingCfgBase o2) {
			return o1.getRequiredLv() < o2.getRequiredLv() ? -1 : (o1.getAllFighting() < o2.getAllFighting() ? -1 : 1);
		}
		
	}

}
