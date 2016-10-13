package com.playerdata.groupcompetition.battle;

/**
 * @Author HC
 * @date 2016年10月10日 下午2:56:36
 * @desc
 **/

public interface EventsStatusForBattleHandler {

	/**
	 * 当事件发生改变时，这里要做的处理
	 * 
	 * @param t
	 */
	public void handler(GCompMatchBattleCheckTask t);
}