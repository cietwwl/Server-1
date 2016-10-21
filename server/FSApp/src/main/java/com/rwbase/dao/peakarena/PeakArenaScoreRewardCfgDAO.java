package com.rwbase.dao.peakarena;

import com.bm.arena.ArenaScoreCfgBaseDAO;
import com.rw.fsutil.util.SpringContextUtil;

public class PeakArenaScoreRewardCfgDAO extends ArenaScoreCfgBaseDAO {
	
	public static PeakArenaScoreRewardCfgDAO getInstance() {
		return SpringContextUtil.getBean(PeakArenaScoreRewardCfgDAO.class);
	}

	@Override
	protected String getFilePath() {
		return "PeakArena/peakArenaScoreReward.csv";
	}
}
