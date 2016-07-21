package com.rwbase.dao.item;

import java.util.Map;
import java.util.Map.Entry;

import com.rw.fsutil.cacheDao.CfgCsvDao;
import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.common.config.CfgCsvHelper;
import com.rwbase.dao.item.pojo.HeroEquipCfg;

public class HeroEquipCfgDAO extends CfgCsvDao<HeroEquipCfg> {
	public static HeroEquipCfgDAO getInstance() {
		return SpringContextUtil.getBean(HeroEquipCfgDAO.class);
	}

	@Override
	public Map<String, HeroEquipCfg> initJsonCfg() {
		Map<String, HeroEquipCfg> readCsv2Map = CfgCsvHelper.readCsv2Map("item/HeroEquip.csv", HeroEquipCfg.class);
		if (readCsv2Map == null) {
			return cfgCacheMap;
		}

		for (Entry<String, HeroEquipCfg> e : readCsv2Map.entrySet()) {
			e.getValue().initData();
		}

		return cfgCacheMap = readCsv2Map;
	}

	public HeroEquipCfg getConfig(int id) {
		return (HeroEquipCfg) getCfgById(String.valueOf(id));
	}
}
