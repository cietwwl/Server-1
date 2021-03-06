package com.rwbase.dao.groupcompetition;

import com.rw.fsutil.util.SpringContextUtil;

public class GCompGroupScoreCfgDAO extends GCompScoreCfgBaseDAO {
	
	public static final int KEY_TEAM_WIN = 1;
	public static final int KEY_TEAM_DRAW = 0;
	public static final int KEY_TEAM_LOSE = -1;

	public static GCompGroupScoreCfgDAO getInstance() {
		return SpringContextUtil.getBean(GCompGroupScoreCfgDAO.class);
	}
	
	@Override
	protected String getFileName() {
		return "GCompGroupScoreCfg.csv";
	}

}
