package com.rwbase.dao.groupcompetition;

import com.rw.fsutil.util.SpringContextUtil;

public class GCompKillRewardCfgDAO extends GCompCommonRankRewardCfgBaseDAO {

	public static GCompKillRewardCfgDAO getInstance() {
		return SpringContextUtil.getBean(GCompKillRewardCfgDAO.class);
	}
	
	@Override
	protected String getFileName() {
		return "GCompWinRewardCfg.csv";
	}

}
