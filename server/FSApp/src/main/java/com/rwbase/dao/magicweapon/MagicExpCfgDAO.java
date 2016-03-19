package com.rwbase.dao.magicweapon;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.magicweapon.pojo.MagicExpCfg;

public class MagicExpCfgDAO extends CfgCsvDao<MagicExpCfg> {

	private static MagicExpCfgDAO instance = new MagicExpCfgDAO();
	private MagicExpCfgDAO(){}
	public static MagicExpCfgDAO getInstance(){
		return instance;
	}
	
	@Override
	public Map<String, MagicExpCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("MagicWeapon/MagicExp.csv", MagicExpCfg.class);
		return cfgCacheMap;
	}

}
