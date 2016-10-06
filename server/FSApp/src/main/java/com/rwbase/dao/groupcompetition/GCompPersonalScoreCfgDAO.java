package com.rwbase.dao.groupcompetition;

import com.rw.fsutil.util.SpringContextUtil;

public class GCompPersonalScoreCfgDAO extends GCompScoreCfgBaseDAO {
	
	public static GCompPersonalScoreCfgDAO getInstance() {
		return SpringContextUtil.getBean(GCompPersonalScoreCfgDAO.class);
	}

	@Override
	protected String getFileName() {
		return "GCompPersonalScoreCfg.csv";
	}

}
