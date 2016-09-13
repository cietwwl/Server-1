package com.rwbase.dao.fighting;

import com.rw.fsutil.util.SpringContextUtil;

public class MagicLevelFightingCfgDAO extends FightingCfgCsvDAOOneToOneBase {

	public static MagicLevelFightingCfgDAO getInstance() {
		return SpringContextUtil.getBean(MagicLevelFightingCfgDAO.class);
	}
	
	@Override
	protected String getFileName() {
		return "MagicLevelFighting.csv";
	}
}
