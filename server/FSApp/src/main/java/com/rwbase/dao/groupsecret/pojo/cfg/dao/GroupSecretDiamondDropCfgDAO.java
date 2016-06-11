package com.rwbase.dao.groupsecret.pojo.cfg.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.common.Weight;
import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretDiamondDropCfg;

/*
 * @author HC
 * @date 2016年5月25日 下午6:26:38
 * @Description 
 */
public class GroupSecretDiamondDropCfgDAO extends CfgCsvDao<GroupSecretDiamondDropCfg> {

	public static GroupSecretDiamondDropCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(GroupSecretDiamondDropCfgDAO.class);
	}

	/** 帮派秘境权重Map */
	private Map<Integer, Weight<Integer>> weightMap = new HashMap<Integer, Weight<Integer>>();

	@Override
	protected Map<String, GroupSecretDiamondDropCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("GroupSecret/GroupSecretDiamondDropCfg.csv", GroupSecretDiamondDropCfg.class);

		if (cfgCacheMap != null && !cfgCacheMap.isEmpty()) {

			HashMap<Integer, Map<Integer, Integer>> proMap = new HashMap<Integer, Map<Integer, Integer>>();

			for (Entry<String, GroupSecretDiamondDropCfg> e : cfgCacheMap.entrySet()) {
				GroupSecretDiamondDropCfg cfg = e.getValue();
				if (cfg == null) {
					continue;
				}

				int dropId = cfg.getDropId();
				Map<Integer, Integer> map = proMap.get(dropId);
				if (map == null) {
					map = new HashMap<Integer, Integer>();
					proMap.put(dropId, map);
				}

				int pro = cfg.getDropRate();
				if (pro > 0) {
					map.put(cfg.getDropNum(), pro);
				}
			}

			Map<Integer, Weight<Integer>> weightMap = new HashMap<Integer, Weight<Integer>>(proMap.size());
			for (Entry<Integer, Map<Integer, Integer>> e : proMap.entrySet()) {
				Map<Integer, Integer> value = e.getValue();
				if (value == null || value.isEmpty()) {
					continue;
				}

				weightMap.put(e.getKey(), new Weight<Integer>(value));
			}

			this.weightMap = Collections.unmodifiableMap(weightMap);
		}

		return cfgCacheMap;
	}

	/**
	 * 获取钻石掉落的数量
	 * 
	 * @param dropId
	 * @return
	 */
	public int getDiamondDropNum(int dropId) {
		Weight<Integer> weight = this.weightMap.get(dropId);
		if (weight == null) {
			return 0;
		}

		Integer ranResult = weight.getRanResult();
		return ranResult == null ? 0 : ranResult.intValue();
	}
}