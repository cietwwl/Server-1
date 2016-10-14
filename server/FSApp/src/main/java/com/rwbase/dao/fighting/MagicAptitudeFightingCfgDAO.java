package com.rwbase.dao.fighting;

import com.rw.fsutil.util.SpringContextUtil;

public class MagicAptitudeFightingCfgDAO extends FightingCfgCsvDAOOneToOneBase{

	public static MagicAptitudeFightingCfgDAO getInstance() {
		return SpringContextUtil.getBean(MagicAptitudeFightingCfgDAO.class);
	}
	
	@Override
	protected String getFileName() {
		return "MagicAptitudeFighting.csv";
	}
}
