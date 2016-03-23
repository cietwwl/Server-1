package com.rwbase.dao.item;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.item.pojo.HeroEquipCfg;

public class HeroEquipCfgDAO  extends CfgCsvDao<HeroEquipCfg>{
	public static HeroEquipCfgDAO getInstance() {
		return SpringContextUtil.getBean(HeroEquipCfgDAO.class);
	}

	@Override
	public Map<String, HeroEquipCfg> initJsonCfg() {
		cfgCacheMap = CfgCsvHelper.readCsv2Map("item/HeroEquip.csv",HeroEquipCfg.class);
		return cfgCacheMap;
	}
	
	public HeroEquipCfg getConfig(int id){
		return (HeroEquipCfg)getCfgById(String.valueOf(id));
	}
}
