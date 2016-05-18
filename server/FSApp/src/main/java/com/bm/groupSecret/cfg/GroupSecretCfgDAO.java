package com.bm.groupSecret.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class GroupSecretCfgDAO extends CfgCsvDao<GroupSecretCfg> {


	public static GroupSecretCfgDAO getInstance() {
		return SpringContextUtil.getBean(GroupSecretCfgDAO.class);
	}

	
	@Override
	public Map<String, GroupSecretCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("groupSecret/GroupSecret.csv", GroupSecretCfg.class);
		
		return cfgCacheMap;
	}
	
	
	
	public GroupSecretCfg getConfig(String id){
		GroupSecretCfg cfg = getCfgById(id);
		return cfg;
	}
	
	
	


}