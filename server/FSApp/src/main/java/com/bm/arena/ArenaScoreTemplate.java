package com.bm.arena;

import java.util.Map;

import com.common.HPCUtil;

public class ArenaScoreTemplate {

	private final int socre;
	private final Map<Integer, Integer> rewards;
	private final int minLevel;
	private final int maxLevel;

	public ArenaScoreTemplate(int socre, String rewardTips, int minLevel, int maxLevel) {
		this.socre = socre;
		this.rewards = HPCUtil.parseIntegerMap(rewardTips, ",", "_");
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
	}

	public int getScore() {
		return socre;
	}

	public Map<Integer, Integer> getRewards() {
		return rewards;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

}
