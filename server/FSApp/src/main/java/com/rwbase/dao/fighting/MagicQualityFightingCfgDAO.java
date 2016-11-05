package com.rwbase.dao.fighting;

import com.rw.fsutil.util.SpringContextUtil;

public class MagicQualityFightingCfgDAO extends AbsMagicSkillFightingCfgDAO {

	public static MagicQualityFightingCfgDAO getInstance() {
		return SpringContextUtil.getBean(MagicQualityFightingCfgDAO.class);
	}
	
	@Override
	protected String getFileName() {
		return "MagicQualityFighting.csv";
	}
	

}
