package com.rwbase.dao.redpoint;

import io.netty.util.collection.IntObjectHashMap;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rw.service.redpoint.RedPointType;
import com.rwbase.common.config.CfgCsvHelper;

/**
 * @Author HC
 * @date 2016年11月7日 下午5:21:57
 * @desc
 **/

public class RedPointMapCfgDAO extends CfgCsvDao<RedPointMap> {

	public static RedPointMapCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(RedPointMapCfgDAO.class);
	}

	/** 红点对应的类型映射 */
	private EnumMap<RedPointType, Integer> redPointTypeMap = new EnumMap<RedPointType, Integer>(RedPointType.class);
	private IntObjectHashMap<RedPointType> redPointMapKeyFirst;

	@Override
	protected Map<String, RedPointMap> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("RedPoint/RedPointMap.csv", RedPointMap.class);

		if (cfgCacheMap != null && !cfgCacheMap.isEmpty()) {
			int size = cfgCacheMap.size();
			HashMap<String, Integer> map = new HashMap<String, Integer>(size);

			for (Entry<String, RedPointMap> e : cfgCacheMap.entrySet()) {
				RedPointMap value = e.getValue();
				map.put(value.getRedPointType(), value.getRedPointId());
			}

			EnumMap<RedPointType, Integer> redPointTypeMap = new EnumMap<RedPointType, Integer>(RedPointType.class);
			IntObjectHashMap<RedPointType> redPointMapKeyFirst = new IntObjectHashMap<RedPointType>(size);

			// 映射一下枚举和类型
			RedPointType[] values = RedPointType.values();
			for (int i = 0, len = values.length; i < len; i++) {
				RedPointType redPointType = values[i];
				String name = redPointType.name();

				Integer redPointId = map.get(name);
				if (redPointId == null) {
					continue;
				}

				redPointTypeMap.put(redPointType, redPointId);
				redPointMapKeyFirst.put(redPointId, redPointType);
			}

			this.redPointTypeMap = redPointTypeMap;
			this.redPointMapKeyFirst = redPointMapKeyFirst;
		}

		return cfgCacheMap;
	}

	/**
	 * 获取红点类型
	 * 
	 * @param redPointType
	 * @return
	 */
	public int getRedPointType(RedPointType redPointType) {
		if (redPointTypeMap == null || redPointTypeMap.isEmpty()) {
			return 0;
		}

		Integer hasValue = redPointTypeMap.get(redPointType);
		return hasValue == null ? 0 : hasValue.intValue();
	}

	/**
	 * 获取红点对应的枚举
	 * 
	 * @param id
	 * @return
	 */
	public RedPointType getRedPointTypeById(int id) {
		if (redPointMapKeyFirst == null || redPointMapKeyFirst.isEmpty()) {
			return RedPointType.NONE;
		}

		return redPointMapKeyFirst.get(id);
	}
}