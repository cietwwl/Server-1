package com.playerdata.fixEquip.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;


public final class RoleFixEquipCfgDAO extends CfgCsvDao<RoleFixEquipCfg> {


	public static RoleFixEquipCfgDAO getInstance() {
		return SpringContextUtil.getBean(RoleFixEquipCfgDAO.class);
	}

	
	@Override
	public Map<String, RoleFixEquipCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fixEquip/RoleFixEquipCfg.csv", RoleFixEquipCfg.class);
		return cfgCacheMap;
	}
	
	public RoleFixEquipCfg getConfig(String id){
		RoleFixEquipCfg cfg = getCfgById(id);
		return cfg;
	}
	


}