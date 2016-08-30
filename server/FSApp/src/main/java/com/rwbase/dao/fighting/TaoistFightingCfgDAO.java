package com.rwbase.dao.fighting;

import java.util.Map;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.dao.fighting.pojo.TaoistFightingCfg;

public class TaoistFightingCfgDAO extends FightingCfgCsvDAOBase<TaoistFightingCfg> {
	
	public static TaoistFightingCfgDAO getInstance() {
		return SpringContextUtil.getBean(TaoistFightingCfgDAO.class);
	}

	@Override
	protected Map<String, TaoistFightingCfg> initJsonCfg() {
		this.cfgCacheMap = readFightingCfgBaseType("TaoistFighting.csv", TaoistFightingCfg.class);
		return this.cfgCacheMap;
	}

}
