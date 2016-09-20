package com.rwbase.dao.fighting;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.dao.fighting.pojo.MagicFetterFightingCfg;

public class MagicFetterFightingCfgDAO extends FightingCfgCsvDAOBase<MagicFetterFightingCfg> {
	
	public static MagicFetterFightingCfgDAO getInstance() {
		return SpringContextUtil.getBean(MagicFetterFightingCfgDAO.class);
	}

	@Override
	protected String getFileName() {
		return "MagicFetterFighting.csv";
	}

	@Override
	protected Class<MagicFetterFightingCfg> getCfgClazz() {
		return MagicFetterFightingCfg.class;
	}
}
