package com.rwbase.dao.fighting;

import com.rw.fsutil.util.SpringContextUtil;
import com.rwbase.dao.fighting.pojo.FixEquipQualityFightingCfg;

public class FixEquipQualityFightingCfgDAO extends FightingCfgCsvDAOBase<FixEquipQualityFightingCfg>{

	public static FixEquipQualityFightingCfgDAO getInstance() {
		return SpringContextUtil.getBean(FixEquipQualityFightingCfgDAO.class);
	}
	
	@Override
	protected String getFileName() {
		return "FixEquipQualityFighting.csv";
	}

	@Override
	protected Class<FixEquipQualityFightingCfg> getCfgClazz() {
		return FixEquipQualityFightingCfg.class;
	}

}
