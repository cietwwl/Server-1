package com.rwbase.dao.groupsecret.pojo.cfg.dao;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretResourceCfg;

/*
 * @author HC
 * @date 2016年5月25日 下午6:18:39
 * @Description 
 */
public class GroupSecretResourceCfgDAO extends CfgCsvDao<GroupSecretResourceCfg> {

	public static GroupSecretResourceCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(GroupSecretResourceCfgDAO.class);
	}

	// /** 对应的秘境类型配置表 */
	// private Map<Integer, GroupSecretResourceTemplate> tmpMap = new HashMap<Integer, GroupSecretResourceTemplate>();

	@Override
	protected Map<String, GroupSecretResourceCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("GroupSecret/GroupSecretResourceCfg.csv", GroupSecretResourceCfg.class);

		// if (cfgCacheMap != null && !cfgCacheMap.isEmpty()) {
		// HashMap<Integer, GroupSecretResourceTemplate> tmpMap = new HashMap<Integer, GroupSecretResourceTemplate>();
		// for (Entry<String, GroupSecretResourceCfg> e : cfgCacheMap.entrySet()) {
		// GroupSecretResourceCfg cfg = e.getValue();
		// if (cfg == null) {
		// continue;
		// }
		//
		// tmpMap.put(cfg.getId(), new GroupSecretResourceTemplate(cfg));
		// }
		//
		// this.tmpMap = Collections.unmodifiableMap(tmpMap);
		// }

		return cfgCacheMap;
	}

	/**
	 * 获取对应的秘境类型配置
	 *
	 * @param cfgId
	 * @return
	 */
	public GroupSecretResourceCfg getGroupSecretResourceTmp(int cfgId) {
		return getCfgById(String.valueOf(cfgId));
	}

	// /**
	// * 获取掉落钻石的Id
	// *
	// * @param cfgId
	// * @param minutes
	// * @return
	// */
	// public int getDropIdBasedOnJoinTime(int cfgId, int minutes) {
	// GroupSecretResourceTemplate cfg = getGroupSecretResourceTmp(cfgId);
	// if (cfg == null) {
	// return -1;
	// }
	//
	// int dropId = -1;
	// int lastMinutes = 0;
	// List<GroupSecretLevelGetResTemplate.Drop> list = cfg.getDropIdBasedOnJoinTimeList();
	// for (int i = 0, size = list.size(); i < size; i++) {
	// GroupSecretLevelGetResTemplate.Drop drop = list.get(i);
	// if (drop == null) {
	// continue;
	// }
	//
	// int leftMinutes = drop.leftMinutes;
	// if (minutes < leftMinutes && dropId == -1) {
	// dropId = drop.dropId;
	// lastMinutes = leftMinutes;
	// } else if (dropId != -1 && minutes < leftMinutes && leftMinutes < lastMinutes) {
	// dropId = drop.dropId;
	// lastMinutes = leftMinutes;
	// }
	// }
	//
	// return dropId;
	// }
}