package com.rwbase.dao.fighting;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.dao.fighting.pojo.FixEquipLevelFightingCfg;

public class FixEquipLevelFightingCfgDAO extends FightingCfgCsvDAOBase<FixEquipLevelFightingCfg> {

	public static FixEquipLevelFightingCfgDAO getInstance() {
		return SpringContextUtil.getBean(FixEquipLevelFightingCfgDAO.class);
	}

	@Override
	protected String getFileName() {
		return "FixEquipLevelFighting.csv";
	}

	@Override
	protected Class<FixEquipLevelFightingCfg> getCfgClazz() {
		return FixEquipLevelFightingCfg.class;
	}

}
