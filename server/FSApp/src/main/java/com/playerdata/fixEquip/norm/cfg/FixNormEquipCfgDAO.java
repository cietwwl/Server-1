package com.playerdata.fixEquip.norm.cfg;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

/*
 * @author HC
 * @date 2016年1月16日 下午5:42:44
 * @Description 帮派的基础配置表Dao
 */
public final class FixNormEquipCfgDAO extends CfgCsvDao<FixNormEquipCfg> {


	public static FixNormEquipCfgDAO getInstance() {
		return SpringContextUtil.getBean(FixNormEquipCfgDAO.class);
	}

	
	@Override
	public Map<String, FixNormEquipCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("FixEquip/FixNormEquipCfg.csv", FixNormEquipCfg.class);
		return cfgCacheMap;
	}
	
	public FixNormEquipCfg getConfig(String id){
		FixNormEquipCfg cfg = getCfgById(id);
		return cfg;
	}
	


}