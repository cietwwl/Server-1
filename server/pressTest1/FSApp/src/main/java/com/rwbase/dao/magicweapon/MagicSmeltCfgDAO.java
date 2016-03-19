package com.rwbase.dao.magicweapon;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.magicweapon.pojo.MagicSmeltCfg;

public class MagicSmeltCfgDAO extends CfgCsvDao<MagicSmeltCfg> {

	private static MagicSmeltCfgDAO instance = new MagicSmeltCfgDAO();
	private MagicSmeltCfgDAO(){}
	public static MagicSmeltCfgDAO getInstance(){
		return instance;
	}
	
	@Override
	public Map<String, MagicSmeltCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("MagicWeapon/MagicSmelt.csv", MagicSmeltCfg.class);
		return cfgCacheMap;
	}

}
