package com.rwbase.dao.groupcompetition;

import com.rw.fsutil.util.SpringContextUtil;

public class GCompWinRewardCfgDAO extends GCompCommonRankRewardCfgBaseDAO {

	public static GCompCommonRankRewardCfgBaseDAO getInstance() {
		return SpringContextUtil.getBean(GCompCommonRankRewardCfgBaseDAO.class);
	}
	
	@Override
	protected String getFileName() {
		return "GCompWinRewardCfg.csv";
	}

}
