package com.playerdata.fixEquip.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;


public final class FixEquipCfgDAO extends CfgCsvDao<FixEquipCfg> {


	public static FixEquipCfgDAO getInstance() {
		return SpringContextUtil.getBean(FixEquipCfgDAO.class);
	}

	
	@Override
	public Map<String, FixEquipCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("fixEquip/FixEquipCfg.csv", FixEquipCfg.class);
		return cfgCacheMap;
	}
	
	public FixEquipCfg getConfig(String id){
		FixEquipCfg cfg = getCfgById(id);
		return cfg;
	}
	


}