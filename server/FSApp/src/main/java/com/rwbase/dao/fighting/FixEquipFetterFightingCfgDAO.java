package com.rwbase.dao.fighting;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.dao.fighting.pojo.FixEquipFetterFightingCfg;

public class FixEquipFetterFightingCfgDAO extends FightingCfgCsvDAOBase<FixEquipFetterFightingCfg> {
	
	public static FixEquipFetterFightingCfgDAO getInstnce() {
		return SpringContextUtil.getBean(FixEquipFetterFightingCfgDAO.class);
	}

	@Override
	protected String getFileName() {
		return "FixEquipFetterFighting.csv";
	}

	@Override
	protected Class<FixEquipFetterFightingCfg> getCfgClazz() {
		return FixEquipFetterFightingCfg.class;
	}

}
