package com.rwbase.dao.hero;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;

public class HeroEquipStrenghCfgDAO extends CfgCsvDao<HeroEquipStrenghCfg> {

	private static HeroEquipStrenghCfgDAO instance = new HeroEquipStrenghCfgDAO();
	private  HeroEquipStrenghCfgDAO() {
		// TODO Auto-generated constructor stub
	}
	public static HeroEquipStrenghCfgDAO getInstance(){
		return instance;
	}
	
	@Override
	public Map<String, HeroEquipStrenghCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("hero/heroEquipStrengh.csv",HeroEquipStrenghCfg.class);
		return cfgCacheMap;
	}

}
