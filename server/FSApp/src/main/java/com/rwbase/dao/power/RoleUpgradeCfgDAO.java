package com.rwbase.dao.power;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.power.pojo.RoleUpgradeCfg;

public class RoleUpgradeCfgDAO extends CfgCsvDao<RoleUpgradeCfg> {

	public static RoleUpgradeCfgDAO getInstance(){
		return SpringContextUtil.getBean(RoleUpgradeCfgDAO.class);
	}
	
	@Override
	public Map<String, RoleUpgradeCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("roleUpgrade/roleUpgrade.csv", RoleUpgradeCfg.class);
		return cfgCacheMap;
	}

	public RoleUpgradeCfg getCfg(int level){
		RoleUpgradeCfg cfg = (RoleUpgradeCfg)getCfgById(String.valueOf(level));
		return cfg;
	}
}
