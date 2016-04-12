package com.bm.arena;

import java.util.Map;

import com.common.HPCUtil;

public class ArenaScoreTemplate {

	private final int socre;
	private final Map<Integer, Integer> rewards;

	public ArenaScoreTemplate(int socre, String rewardTips) {
		this.socre = socre;
		this.rewards = HPCUtil.parseIntegerMap(rewardTips, ",", "_");
	}

	public int getSocre() {
		return socre;
	}

	public Map<Integer, Integer> getRewards() {
		return rewards;
	}

}
