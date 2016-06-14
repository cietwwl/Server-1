package com.rwbase.dao.magicweapon;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.magicweapon.pojo.MagicSmeltCfg;

public class MagicSmeltCfgDAO extends CfgCsvDao<MagicSmeltCfg> {

	public static MagicSmeltCfgDAO getInstance() {
		return SpringContextUtil.getBean(MagicSmeltCfgDAO.class);
	}
	
	@Override
	public Map<String, MagicSmeltCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("MagicWeapon/MagicSmelt.csv", MagicSmeltCfg.class);
		return cfgCacheMap;
	}

}
