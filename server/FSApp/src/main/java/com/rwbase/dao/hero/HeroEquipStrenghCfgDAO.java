package com.rwbase.dao.hero;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;

public class HeroEquipStrenghCfgDAO extends CfgCsvDao<HeroEquipStrenghCfg> {

	public static HeroEquipStrenghCfgDAO getInstance() {
		return SpringContextUtil.getBean(HeroEquipStrenghCfgDAO.class);
	}
	
	
	@Override
	public Map<String, HeroEquipStrenghCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("hero/heroEquipStrengh.csv",HeroEquipStrenghCfg.class);
		return cfgCacheMap;
	}

}
