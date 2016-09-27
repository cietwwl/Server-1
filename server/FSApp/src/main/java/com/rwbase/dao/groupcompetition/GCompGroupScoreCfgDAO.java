package com.rwbase.dao.groupcompetition;

import com.rw.fsutil.util.SpringContextUtil;

public class GCompGroupScoreCfgDAO extends GCompScoreCfgBaseDAO {

	public static GCompGroupScoreCfgDAO getInstance() {
		return SpringContextUtil.getBean(GCompGroupScoreCfgDAO.class);
	}
	
	@Override
	protected String getFileName() {
		return "GCompGroupScoreCfg.csv";
	}

}
