package com.rw.service.guide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.service.guide.datamodel.GuidanceClosure;
import com.rwbase.common.config.CfgCsvHelper;

public class GuidanceClosureCfgDAO extends CfgCsvDao<GuidanceClosure> {

	private TreeMap<Integer, int[]> map;

	public static GuidanceClosureCfgDAO getInstance() {
		return SpringContextUtil.getBean(GuidanceClosureCfgDAO.class);
	}

	@Override
	protected Map<String, GuidanceClosure> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Guidance/GuidanceClosure.csv", GuidanceClosure.class);
		TreeMap<Integer, int[]> map = new TreeMap<Integer, int[]>();
		TreeMap<Integer, GuidanceClosure> levelMap = new TreeMap<Integer, GuidanceClosure>();
		for (GuidanceClosure closure : cfgCacheMap.values()) {
			levelMap.put(closure.getLevel(), closure);
		}
		int last = levelMap.lastKey();
		for (int i = 1; i <= last; i++) {
			GuidanceClosure closure = levelMap.get(i);
			if (closure == null) {
				continue;
			}
			if (closure.getProgress() < 0) {
				map.put(i, new int[] { closure.getProgress() });
				continue;
			}
			ArrayList<Integer> list = new ArrayList<Integer>();
			for (int j = 1; j <= i; j++) {
				closure = levelMap.get(j);
				if (closure == null) {
					continue;
				}
				if (closure.getProgress() > 0) {
					list.add(closure.getProgress());
				}
			}
			int size = list.size();
			int[] array = new int[size];
			int index = 0;
			for (int j = size; --j >= 0;) {
				array[index++] = list.get(j);
			}
			map.put(i, array);
		}
		this.map = map;
		for (Map.Entry<Integer, int[]> entry : map.entrySet()) {
			System.out.println(entry.getKey() + "," + Arrays.toString(entry.getValue()));
		}
		return cfgCacheMap;
	}

	/**
	 * 获取指定等级的GuidanceClosure，若不存在，则向前搜索最接近的等级
	 * 
	 * @param level
	 * @return
	 */
	public int[] getOrPreSearch(int level) {
		Map.Entry<Integer, int[]> entry = this.map.floorEntry(level);
		return entry == null ? null : entry.getValue();
	}

}
