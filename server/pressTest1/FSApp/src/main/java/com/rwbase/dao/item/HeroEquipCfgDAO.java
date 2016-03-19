package com.rwbase.dao.item;

import java.util.Map;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.item.pojo.HeroEquipCfg;

public class HeroEquipCfgDAO  extends CfgCsvDao<HeroEquipCfg>{
	private static HeroEquipCfgDAO instance = new HeroEquipCfgDAO();
	private HeroEquipCfgDAO(){}
	public static HeroEquipCfgDAO getInstance(){
		return instance;
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
