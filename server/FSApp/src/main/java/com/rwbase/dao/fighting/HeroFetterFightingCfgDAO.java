package com.rwbase.dao.fighting;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.dao.fighting.pojo.HeroFetterFightingCfg;

public class HeroFetterFightingCfgDAO extends FightingCfgCsvDAOBase<HeroFetterFightingCfg> {
	
	public static HeroFetterFightingCfgDAO getInstance() {
		return SpringContextUtil.getBean(HeroFetterFightingCfgDAO.class);
	}
	
	@Override
	protected String getFileName() {
		return "HeroFetterFighting.csv";
	}

	@Override
	protected Class<HeroFetterFightingCfg> getCfgClazz() {
		return HeroFetterFightingCfg.class;
	}
}
