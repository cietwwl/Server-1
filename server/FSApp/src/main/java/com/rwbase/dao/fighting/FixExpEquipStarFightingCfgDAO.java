package com.rwbase.dao.fighting;

import java.util.Map;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.dao.fighting.pojo.FixExpEquipStarFightingCfg;

public class FixExpEquipStarFightingCfgDAO extends FightingCfgCsvDAOBase<FixExpEquipStarFightingCfg> {

	public static FixExpEquipStarFightingCfgDAO getInstance() {
		return SpringContextUtil.getBean(FixExpEquipStarFightingCfgDAO.class);
	}
	
	@Override
	protected Map<String, FixExpEquipStarFightingCfg> initJsonCfg() {
		this.cfgCacheMap = this.readFightingCfgBaseType("FixExpEquipStarFighting.csv", FixExpEquipStarFightingCfg.class);
		return this.cfgCacheMap;
	}

}
