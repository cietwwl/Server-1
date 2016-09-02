package com.rwbase.dao.fighting;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.dao.fighting.pojo.TaoistFightingCfg;

public class TaoistFightingCfgDAO extends FightingCfgCsvDAOBase<TaoistFightingCfg> {
	
	public static TaoistFightingCfgDAO getInstance() {
		return SpringContextUtil.getBean(TaoistFightingCfgDAO.class);
	}

	@Override
	protected String getFileName() {
		return "TaoistFighting.csv";
	}

	@Override
	protected Class<TaoistFightingCfg> getCfgClazz() {
		return TaoistFightingCfg.class;
	}
}
