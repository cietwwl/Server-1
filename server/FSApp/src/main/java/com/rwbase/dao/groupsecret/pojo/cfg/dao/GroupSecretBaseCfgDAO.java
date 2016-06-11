package com.rwbase.dao.groupsecret.pojo.cfg.dao;

import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretBaseCfg;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretBaseTemplate;

/*
 * @author HC
 * @date 2016年5月25日 下午5:51:01
 * @Description 
 */
public class GroupSecretBaseCfgDAO extends CfgCsvDao<GroupSecretBaseCfg> {

	public static GroupSecretBaseCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(GroupSecretBaseCfgDAO.class);
	}

	private GroupSecretBaseTemplate uniqueCfg;// 唯一的基础配置表

	@Override
	protected Map<String, GroupSecretBaseCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("GroupSecret/GroupSecretBaseCfg.csv", GroupSecretBaseCfg.class);

		if (cfgCacheMap != null && !cfgCacheMap.isEmpty()) {
			for (Entry<String, GroupSecretBaseCfg> e : cfgCacheMap.entrySet()) {
				uniqueCfg = new GroupSecretBaseTemplate(e.getValue());
				break;
			}
		}

		return cfgCacheMap;
	}

	/**
	 * 获取帮派秘境的唯一基础配置表
	 * 
	 * @return
	 */
	public GroupSecretBaseTemplate getUniqueCfg() {
		return uniqueCfg;
	}
}