package com.rwbase.dao.fighting;

import java.util.Map;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.dao.fighting.pojo.FixExpEquipLevelFightingCfg;

public class FixExpEquipLevelFightingCfgDAO extends FightingCfgCsvDAOBase<FixExpEquipLevelFightingCfg> {

	public static FixExpEquipLevelFightingCfgDAO getInstance() {
		return SpringContextUtil.getBean(FixExpEquipLevelFightingCfgDAO.class);
	}
	
	@Override
	protected Map<String, FixExpEquipLevelFightingCfg> initJsonCfg() {
		this.cfgCacheMap = readFightingCfgBaseType("FixExpEquipFighting.csv", FixExpEquipLevelFightingCfg.class);
		return cfgCacheMap;
	}

}
