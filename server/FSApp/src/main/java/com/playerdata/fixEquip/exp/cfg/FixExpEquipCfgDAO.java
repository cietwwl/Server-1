package com.playerdata.fixEquip.exp.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class FixExpEquipCfgDAO extends CfgCsvDao<FixExpEquipCfg> {


	public static FixExpEquipCfgDAO getInstance() {
		return SpringContextUtil.getBean(FixExpEquipCfgDAO.class);
	}

	
	@Override
	public Map<String, FixExpEquipCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("FixEquip/FixExpEquipCfg.csv", FixExpEquipCfg.class);
		return cfgCacheMap;
	}
	
	public FixExpEquipCfg getConfig(String id){
		FixExpEquipCfg cfg = getCfgById(id);
		return cfg;
	}
	


}