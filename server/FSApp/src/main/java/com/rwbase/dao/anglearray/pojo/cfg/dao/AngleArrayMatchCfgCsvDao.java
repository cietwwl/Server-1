package com.rwbase.dao.anglearray.pojo.cfg.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.anglearray.pojo.cfg.AngleArrayMatchCfg;

/*
 * @author HC
 * @date 2015年11月13日 下午2:56:49
 * @Description 万仙阵匹配规则数据Dao，不允许外部构造和继承。必须使用本类中的静态方法
 */
public class AngleArrayMatchCfgCsvDao extends CfgCsvDao<AngleArrayMatchCfg> {
	// 内部使用的类实例，坚决不开给外部使用
	public static AngleArrayMatchCfgCsvDao getCfgDAO() {
		return SpringContextUtil.getBean(AngleArrayMatchCfgCsvDao.class);
	}

	/**
	 * 匹配规则的缓存。所有的Key依次代表<匹配的最低等级,<万仙阵层数,对应的唯一Json记录Id>>
	 */
	private TreeMap<Integer, Map<Integer, Integer>> matchMap;

	private AngleArrayMatchCfgCsvDao() {
	}

	@Override
	public Map<String, AngleArrayMatchCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("tower/Match.csv", AngleArrayMatchCfg.class);

		if (cfgCacheMap != null) {
			TreeMap<Integer, Map<Integer, Integer>> matchMap = new TreeMap<Integer, Map<Integer, Integer>>();// 初始化
			for (Entry<String, AngleArrayMatchCfg> e : cfgCacheMap.entrySet()) {
				AngleArrayMatchCfg cfg = e.getValue();// Value

				Map<Integer, Integer> map = matchMap.get(cfg.getLevel());
				if (map == null) {
					map = new HashMap<Integer, Integer>();
					matchMap.put(cfg.getLevel(), map);
				}

				map.put(cfg.getFloor(), cfg.getUniqueId());
			}
			this.matchMap = matchMap;
		}

		return cfgCacheMap;
	}

	/**
	 * 获取对应的匹配规则
	 * 
	 * @param level
	 * @param floor
	 * @return
	 */
	public AngleArrayMatchCfg getMatchCfg(int level, int floor) {
		if (matchMap == null) {
			return null;
		}

		Entry<Integer, Map<Integer, Integer>> floorEntry = matchMap.floorEntry(level);
		if (floorEntry == null) {
			return null;
		}

		Map<Integer, Integer> valueMap = floorEntry.getValue();
		if (valueMap == null) {
			return null;
		}

		Integer uniqueId = valueMap.get(floor);
		if (uniqueId == null) {
			return null;
		}

		return (AngleArrayMatchCfg) this.getCfgById(uniqueId.toString());
	}

	/**
	 * 获取匹配规则中要用到的等级分段
	 * 
	 * @return
	 */
	public List<Integer> getAngleArrayMatchKeys() {
		if (matchMap == null) {
			return Collections.EMPTY_LIST;
		}

		List<Integer> keyList = new ArrayList<Integer>();
		Iterator<Integer> itr = matchMap.keySet().iterator();
		while (itr.hasNext()) {
			Integer next = itr.next();
			keyList.add(next);
		}

		return keyList;
	}
}