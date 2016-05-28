package com.rwbase.dao.groupsecret.pojo.cfg.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretResourceCfg;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretResourceTemplate;

/*
 * @author HC
 * @date 2016年5月25日 下午6:18:39
 * @Description 
 */
public class GroupSecretResourceCfgDAO extends CfgCsvDao<GroupSecretResourceCfg> {

	public static GroupSecretResourceCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(GroupSecretResourceCfgDAO.class);
	}

	/** 对应的秘境类型配置表 */
	private Map<Integer, GroupSecretResourceTemplate> tmpMap = new HashMap<Integer, GroupSecretResourceTemplate>();

	@Override
	protected Map<String, GroupSecretResourceCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("GroupSecret/GroupSecretResourceCfg.csv", GroupSecretResourceCfg.class);

		if (cfgCacheMap != null && !cfgCacheMap.isEmpty()) {
			HashMap<Integer, GroupSecretResourceTemplate> tmpMap = new HashMap<Integer, GroupSecretResourceTemplate>();
			for (Entry<String, GroupSecretResourceCfg> e : cfgCacheMap.entrySet()) {
				GroupSecretResourceCfg cfg = e.getValue();
				if (cfg == null) {
					continue;
				}

				tmpMap.put(cfg.getId(), new GroupSecretResourceTemplate(cfg));
			}

			this.tmpMap = Collections.unmodifiableMap(tmpMap);
		}

		return cfgCacheMap;
	}

	/**
	 * 获取对应的秘境类型配置
	 * 
	 * @param id
	 * @return
	 */
	public GroupSecretResourceTemplate getGroupSecretResourceTmp(int id) {
		return tmpMap.get(id);
	}
}