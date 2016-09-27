package com.rwbase.dao.groupcompetition;

import com.rw.fsutil.util.SpringContextUtil;

public class GCompGroupRewardCfgDAO extends GCompCommonRankRewardCfgBaseDAO {
	
	public static GCompGroupRewardCfgDAO getInstance() {
		return SpringContextUtil.getBean(GCompGroupRewardCfgDAO.class);
	}

	@Override
	protected String getFileName() {
		return "GCompGroupRewardCfg.csv";
	}
	
}
