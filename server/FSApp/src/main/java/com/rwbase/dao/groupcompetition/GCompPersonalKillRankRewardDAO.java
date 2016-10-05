package com.rwbase.dao.groupcompetition;

import com.rw.fsutil.util.SpringContextUtil;

/**
 * 
 * 个人击杀排行榜奖励配置
 * 
 * @author CHEN.P
 *
 */
public class GCompPersonalKillRankRewardDAO extends GCompCommonRankRewardCfgBaseDAO {

	public static GCompPersonalKillRankRewardDAO getInstance() {
		return SpringContextUtil.getBean(GCompPersonalKillRankRewardDAO.class);
	}
	
	@Override
	protected String getFileName() {
		return "PersonalKillRankReward.csv";
	}

}
