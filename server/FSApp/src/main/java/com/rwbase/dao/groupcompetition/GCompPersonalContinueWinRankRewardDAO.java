package com.rwbase.dao.groupcompetition;

import com.rw.fsutil.util.SpringContextUtil;

/**
 * 
 * 个人连胜排行榜奖励
 * 
 * @author CHEN.P
 *
 */
public class GCompPersonalContinueWinRankRewardDAO extends GCompCommonRankRewardCfgBaseDAO {

	public static GCompPersonalContinueWinRankRewardDAO getInstance() {
		return SpringContextUtil.getBean(GCompPersonalContinueWinRankRewardDAO.class);
	}
	
	@Override
	protected String getFileName() {
		return "PersonalContinueWinRankReward.csv";
	}

}
