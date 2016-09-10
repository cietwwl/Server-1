package com.playerdata.groupcompetition.cfg;

import java.util.Arrays;
import java.util.List;

public class CompetitionCommonCfg {

	private int minutesPerCompetitionEvents = 32; // 每一轮争霸赛持续的时间
	private List<Integer> fightingStartTimeInfos = Arrays.asList(19, 0); // 赛事阶段，每一轮赛事开始的时间
	
	public int getMinutesPerCompetition() {
		return minutesPerCompetitionEvents;
	}
	
	public List<Integer> getFightingStartTimeInfos() {
		return fightingStartTimeInfos;
	}
}
