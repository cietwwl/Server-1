package com.rwbase.dao.fighting;

import com.rw.fsutil.util.SpringContextUtil;

public class FashionFightingCfgDAO extends FightingCfgCsvDAOOneToOneBase {

	public static final FashionFightingCfgDAO getInstance() {
		return SpringContextUtil.getBean(FashionFightingCfgDAO.class);
	}
	
	@Override
	protected String getFileName() {
		return "FashionFighting.csv";
	}

}
