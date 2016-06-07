package com.rwbase.dao.groupsecret.pojo.cfg.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretLevelGetResCfg;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretLevelGetResTemplate;

/*
 * @author HC
 * @date 2016年6月7日 下午3:06:40
 * @Description 
 */
public class GroupSecretLevelGetResCfgDAO extends CfgCsvDao<GroupSecretLevelGetResCfg> {

	public static GroupSecretLevelGetResCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(GroupSecretLevelGetResCfgDAO.class);
	}

	/** 对应的获取或者掠夺的概率表<等级组Id,<等级,概率表>> */
	private Map<Integer, TreeMap<Integer, GroupSecretLevelGetResTemplate>> tmpMap = new HashMap<Integer, TreeMap<Integer, GroupSecretLevelGetResTemplate>>();

	@Override
	protected Map<String, GroupSecretLevelGetResCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("GroupSecret/GroupSecretLevelGetResCfg.csv", GroupSecretLevelGetResCfg.class);

		if (cfgCacheMap != null && !cfgCacheMap.isEmpty()) {
			Map<Integer, TreeMap<Integer, GroupSecretLevelGetResTemplate>> tmpMap = new HashMap<Integer, TreeMap<Integer, GroupSecretLevelGetResTemplate>>();
			for (Entry<String, GroupSecretLevelGetResCfg> e : cfgCacheMap.entrySet()) {
				GroupSecretLevelGetResCfg cfg = e.getValue();
				if (cfg == null) {
					continue;
				}

				int levelGroupId = cfg.getLevelGroupId();
				TreeMap<Integer, GroupSecretLevelGetResTemplate> treeMap = tmpMap.get(levelGroupId);
				if (treeMap == null) {
					treeMap = new TreeMap<Integer, GroupSecretLevelGetResTemplate>();
					tmpMap.put(levelGroupId, treeMap);
				}

				treeMap.put(cfg.getLevel(), new GroupSecretLevelGetResTemplate(cfg));
			}

			this.tmpMap = Collections.unmodifiableMap(tmpMap);
		}

		return cfgCacheMap;
	}

	/**
	 * 获取等级对应的获取或者掠夺的概率表
	 * 
	 * @param levelGroupId
	 * @param level
	 * @return
	 */
	public GroupSecretLevelGetResTemplate getLevelGetResTemplate(int levelGroupId, int level) {
		TreeMap<Integer, GroupSecretLevelGetResTemplate> treeMap = tmpMap.get(levelGroupId);
		if (treeMap == null) {
			return null;
		}

		Entry<Integer, GroupSecretLevelGetResTemplate> floorEntry = treeMap.floorEntry(level);
		if (floorEntry == null) {
			return null;
		}

		return floorEntry.getValue();
	}

	/**
	 * 获取掉落钻石的Id
	 * 
	 * @param cfgId
	 * @param minutes
	 * @return
	 */
	public int getDropIdBasedOnJoinTime(int levelGroupId, int level, int minutes) {
		GroupSecretLevelGetResTemplate cfg = getLevelGetResTemplate(levelGroupId, level);
		if (cfg == null) {
			return -1;
		}

		int dropId = -1;
		int lastMinutes = 0;
		List<GroupSecretLevelGetResTemplate.Drop> list = cfg.getDropIdBasedOnJoinTimeList();
		for (int i = 0, size = list.size(); i < size; i++) {
			GroupSecretLevelGetResTemplate.Drop drop = list.get(i);
			if (drop == null) {
				continue;
			}

			int leftMinutes = drop.leftMinutes;
			if (minutes < leftMinutes && dropId == -1) {
				dropId = drop.dropId;
				lastMinutes = leftMinutes;
			} else if (dropId != -1 && minutes < leftMinutes && leftMinutes < lastMinutes) {
				dropId = drop.dropId;
				lastMinutes = leftMinutes;
			}
		}

		return dropId;
	}
}