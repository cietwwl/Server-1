package com.rwbase.dao.fighting;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.dao.fighting.pojo.FixEquipStarFightingCfg;

public class FixEquipStarFightingCfgDAO extends FightingCfgCsvDAOBase<FixEquipStarFightingCfg> {

	public static FixEquipStarFightingCfgDAO getInstance() {
		return SpringContextUtil.getBean(FixEquipStarFightingCfgDAO.class);
	}

	@Override
	protected String getFileName() {
		return "FixEquipStarFighting.csv";
	}

	@Override
	protected Class<FixEquipStarFightingCfg> getCfgClazz() {
		return FixEquipStarFightingCfg.class;
	}
	
}
