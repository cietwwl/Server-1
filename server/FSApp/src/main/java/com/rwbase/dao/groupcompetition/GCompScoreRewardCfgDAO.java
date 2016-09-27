package com.rwbase.dao.groupcompetition;

import com.rw.fsutil.util.SpringContextUtil;

public class GCompScoreRewardCfgDAO extends GCompCommonRankRewardCfgBaseDAO {
	
	public static GCompScoreRewardCfgDAO getInstance() {
		return SpringContextUtil.getBean(GCompCommonRankRewardCfgBaseDAO.class);
	}

	@Override
	protected String getFileName() {
		return "GCompScoreRewardCfg.csv";
	}

}
