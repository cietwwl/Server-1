package com.rw.service.guide;

import java.util.Map;
import java.util.TreeMap;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.service.guide.datamodel.GuidanceClosure;
import com.rwbase.common.config.CfgCsvHelper;

public class GuidanceClosureCfgDAO extends CfgCsvDao<GuidanceClosure> {

	private TreeMap<Integer, GuidanceClosure> map;

	public static GuidanceClosureCfgDAO getInstance() {
		return SpringContextUtil.getBean(GuidanceClosureCfgDAO.class);
	}
	
	@Override
	protected Map<String, GuidanceClosure> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("Guidance/GuidanceClosure.csv", GuidanceClosure.class);
		TreeMap<Integer, GuidanceClosure> map = new TreeMap<Integer, GuidanceClosure>();
		for (GuidanceClosure closure : cfgCacheMap.values()) {
			map.put(closure.getLevel(), closure);
		}
		this.map = map;
		return cfgCacheMap;
	}

	/**
	 * 获取指定等级的GuidanceClosure，若不存在，则向前搜索最接近的等级
	 * @param level
	 * @return
	 */
	public GuidanceClosure getOrPreSearch(int level) {
		Map.Entry<Integer, GuidanceClosure> entry = this.map.floorEntry(level);
		return entry == null ? null : entry.getValue();
	}
	
	
}
