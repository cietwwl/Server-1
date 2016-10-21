package com.bm.arena;

import com.rw.fsutil.util.SpringContextUtil;

public class ArenaScoreCfgDAO extends ArenaScoreCfgBaseDAO {
	
	public static ArenaScoreCfgDAO getInstance() {
		return SpringContextUtil.getBean(ArenaScoreCfgDAO.class);
	}
	
	protected String getFilePath() {
		return "arena/arenaScore.csv";
	}
}
