package com.rwbase.dao.groupsecret.pojo.cfg.dao;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.groupsecret.pojo.cfg.GroupSecretMemberAdditionCfg;

public class GroupSecretMemberAdditionCfgDAO extends CfgCsvDao<GroupSecretMemberAdditionCfg>{
	
	public static GroupSecretMemberAdditionCfgDAO getCfgDAO() {
		return SpringContextUtil.getBean(GroupSecretMemberAdditionCfgDAO.class);
	}

	@Override
	protected Map<String, GroupSecretMemberAdditionCfg> initJsonCfg() {
		this.cfgCacheMap = CfgCsvHelper.readCsv2Map("GroupSecret/GroupSecretMemberAddition.csv", GroupSecretMemberAdditionCfg.class);
		return this.cfgCacheMap;
	}

	public int getAdditional(int count) {
		GroupSecretMemberAdditionCfg cfg = this.cfgCacheMap.get(String.valueOf(count));
		return cfg.getAddition();
	}
}
