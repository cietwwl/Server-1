package com.rwbase.dao.angelarray.pojo.cfg.dao;

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
import com.rwbase.dao.angelarray.pojo.cfg.AngelArrayMatchCfg;

/*
 * @author HC
 * @date 2015年11月13日 下午2:56:49
 * @Description 万仙阵匹配规则数据Dao，不允许外部构造和继承。必须使用本类中的静态方法
 */
public class AngelArrayMatchCfgCsvDao extends CfgCsvDao<AngelArrayMatchCfg> {
	// 内部使用的类实例，坚决不开给外部使用
	public static AngelArrayMatchCfgCsvDao getCfgDAO() {
		return SpringContextUtil.getBean(AngelArrayMatchCfgCsvDao.class);
	}

	/**
	 * 匹配规则的缓存。所有的Key依次代表<匹配的最低等级,<万仙阵层数,对应的唯一Json记录Id>>
	 */
	private TreeMap<Integer, Map<Integer, Integer>> matchMap;
	/** 匹配的等级分段<最低等级,最高等级> */
	private TreeMap<Integer, Integer> levelMap;// 等级配置

	private AngelArrayMatchCfgCsvDao() {
	}

	@Override
	public Map<String, AngelArrayMatchCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("tower/Match.csv", AngelArrayMatchCfg.class);

		if (cfgCacheMap != null) {
			List<Integer> uniqueIdList = new ArrayList<Integer>(cfgCacheMap.size());

			TreeMap<Integer, Map<Integer, Integer>> matchMap = new TreeMap<Integer, Map<Integer, Integer>>();// 初始化
			TreeMap<Integer, Integer> levelMap = new TreeMap<Integer, Integer>();// 初始化
			for (Entry<String, AngelArrayMatchCfg> e : cfgCacheMap.entrySet()) {
				AngelArrayMatchCfg cfg = e.getValue();// Value

				int minLevel = cfg.getLevel();
				Map<Integer, Integer> map = matchMap.get(minLevel);
				if (map == null) {
					map = new HashMap<Integer, Integer>();
					matchMap.put(minLevel, map);
				}

				map.put(cfg.getFloor(), cfg.getUniqueId());

				Integer hasLevel = levelMap.get(minLevel);
				if (hasLevel == null) {
					levelMap.put(minLevel, cfg.getMaxLevel());
				}

				if (uniqueIdList.contains(cfg.getUniqueId())) {
					throw new ExceptionInInitializerError("万仙阵表有重复的唯一Id");
				} else {
					uniqueIdList.add(cfg.getUniqueId());
				}
			}
			this.matchMap = matchMap;
			this.levelMap = levelMap;
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
	public AngelArrayMatchCfg getMatchCfg(int level, int floor) {
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

		return (AngelArrayMatchCfg) this.getCfgById(uniqueId.toString());
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

	/**
	 * 获取当前等级所在的分段，如果等级低于最低分段起始位置，返回-1，如果高于最后一个返回1
	 * 
	 * @param level
	 * @return
	 */
	public int getLevelLimit(int level) {
		Entry<Integer, Integer> floorEntry = levelMap.floorEntry(level);
		if (floorEntry == null) {
			return -1;
		}

		return floorEntry.getKey().intValue();
	}
}