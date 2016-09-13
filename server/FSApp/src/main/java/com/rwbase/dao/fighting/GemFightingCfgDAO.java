package com.rwbase.dao.fighting;

import com.rw.fsutil.util.SpringContextUtil;

public class GemFightingCfgDAO extends FightingCfgCsvDAOOneToOneBase {

	
	public static GemFightingCfgDAO getInstance() {
		return SpringContextUtil.getBean(GemFightingCfgDAO.class);
	}
	
	@Override
	protected String getFileName() {
		return "GemFighting.csv";
	}

}
