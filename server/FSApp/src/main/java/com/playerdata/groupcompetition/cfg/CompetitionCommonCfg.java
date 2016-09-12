package com.playerdata.groupcompetition.cfg;

import com.rw.fsutil.common.IReadOnlyPair;
import com.rw.fsutil.common.Pair;

public class CompetitionCommonCfg {

	private int minutesPerCompetitionEvents = 32; // 每一轮争霸赛持续的时间
	private IReadOnlyPair<Integer, Integer> fightingStartTime = Pair.CreateReadonly(19, 0); // 赛事阶段，每一轮赛事开始的时间
	private IReadOnlyPair<Integer, Integer> fightingStageEndTime = Pair.CreateReadonly(21, 0); // 赛事阶段结束的时间（最后一轮赛事的当天，几点结束）
	private IReadOnlyPair<Integer, IReadOnlyPair<Integer, Integer>> selectionStageEndDateTime = Pair.CreateReadonly(4, Pair.CreateReadonly(21, 0)); // 海选阶段的结束时间信息，t1=周几结束，t2=当日结束的时间
	
	public int getMinutesPerCompetition() {
		return minutesPerCompetitionEvents;
	}
	
	/**
	 * 
	 * 获取赛事阶段，每一轮赛事开始的时间
	 * {@link IReadOnlyPair#getT1()}表示小时
	 * {@link IReadOnlyPair#getT2()}表示分钟
	 * 
	 * @return
	 */
	public IReadOnlyPair<Integer, Integer> getFightingStartTime() {
		return fightingStartTime;
	}
	
	/**
	 * 
	 * 获取赛事阶段结束的时间（最后一轮赛事的当天，几点结束）
	 * {@link IReadOnlyPair#getT1()}表示小时
	 * {@link IReadOnlyPair#getT2()}表示分钟
	 * 
	 * @return
	 */
	public IReadOnlyPair<Integer, Integer> getFightingStageEndTime() {
		return fightingStageEndTime;
	}
	
	/**
	 * 
	 * <pre>
	 * 获取海选阶段的结束时间信息:
	 * {@link IReadOnlyPair#getT1()}表示结束时间是星期几（0=星期天，1=星期一，2=星期二 ... ）
	 * {@link IReadOnlyPair#getT2()}表示结束时间是几点
	 * </pre>
	 * @return
	 */
	public IReadOnlyPair<Integer, IReadOnlyPair<Integer, Integer>> getSelectionStageEndTimeInfos() {
		return selectionStageEndDateTime;
	}
}
