package com.rwbase.dao.groupsecret.pojo.cfg.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretMainRoleRecoveryCfg;

/*
 * @author HC
 * @date 2016年6月3日 下午2:22:37
 * @Description 
 */
public class GroupSecretMainRoleRecoveryCfgDAO extends CfgCsvDao<GroupSecretMainRoleRecoveryCfg> {

	public static GroupSecretMainRoleRecoveryCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(GroupSecretMainRoleRecoveryCfgDAO.class);
	}

	private Map<Integer, Integer> recoveryMap = new HashMap<Integer, Integer>();

	@Override
	protected Map<String, GroupSecretMainRoleRecoveryCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("GroupSecret/GroupSecretMainRoleRecovery.csv", GroupSecretMainRoleRecoveryCfg.class);

		if (cfgCacheMap != null && !cfgCacheMap.isEmpty()) {
			Map<Integer, Integer> recoveryMap = new HashMap<Integer, Integer>();

			for (Entry<String, GroupSecretMainRoleRecoveryCfg> e : cfgCacheMap.entrySet()) {
				GroupSecretMainRoleRecoveryCfg cfg = e.getValue();
				if (cfg == null) {
					continue;
				}

				recoveryMap.put(cfg.getId(), cfg.getRecoveryRatio());
			}

			this.recoveryMap = Collections.unmodifiableMap(recoveryMap);
		}

		return cfgCacheMap;
	}

	/**
	 * 获取某个对应的角色死亡之后恢复血量的百分比
	 * 
	 * @param heroModelId
	 * @return
	 */
	public int getRecoveryRatio(int heroModelId) {
		Integer hasValue = recoveryMap.get(heroModelId);
		return hasValue == null ? 0 : hasValue.intValue();
	}
}