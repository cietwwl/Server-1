package com.rwbase.dao.fighting;

import java.util.Map;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.dao.fighting.pojo.FixExpEquipQualityFightingCfg;

public class FixExpEquipQualityFightingCfgDAO extends FightingCfgCsvDAOBase<FixExpEquipQualityFightingCfg>{

	public static FixExpEquipQualityFightingCfgDAO getInstance() {
		return SpringContextUtil.getBean(FixExpEquipQualityFightingCfgDAO.class);
	}
	
	@Override
	protected Map<String, FixExpEquipQualityFightingCfg> initJsonCfg() {
		this.cfgCacheMap = this.readFightingCfgBaseType("FixExpEquipQualityFighting.csv", FixExpEquipQualityFightingCfg.class);
		return cfgCacheMap;
	}

}
