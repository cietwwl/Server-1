package com.rwbase.dao.arena;

import com.bm.arena.ArenaScoreCfgDAO;

public class PeakArenaScoreRewardCfgDAO extends ArenaScoreCfgDAO {

	@Override
	protected String getFilePath() {
		return "PeakArena/peakArenaScoreReward.csv";
	}
}
