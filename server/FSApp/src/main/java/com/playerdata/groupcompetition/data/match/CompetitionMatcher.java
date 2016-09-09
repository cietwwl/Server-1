package com.playerdata.groupcompetition.data.match;

import com.rwbase.common.timer.IGameTimerTask;

/**
 * 
 * 匹配器（时效任务）
 * 
 * @author CHEN.P
 *
 */
public interface CompetitionMatcher<T extends CompetitionMatchSource> extends IGameTimerTask {


	/**
	 * 
	 * 开始匹配任务
	 * 
	 * @param onMatchedAction
	 *            匹配到之后的行为
	 * @param providerA
	 * @param providerB
	 */
	public void startMatchTask(CompetitionMatchedAction<T> onMatchedAction, CompetitionMatchSourceProvider<T> providerA, CompetitionMatchSourceProvider<T> providerB);
	
}
