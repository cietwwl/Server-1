package com.rw.handler.groupCompetition.util;

/**
 * @Author HC
 * @date 2016年9月23日 上午10:24:01
 * @desc
 **/

public enum GCompBattleResult {
	/**
	 * 未开站
	 */
	NonStart(0, 0),
	/**
	 * 战斗中
	 */
	Fighting(0, 0),
	/**
	 * 胜利
	 */
	Win(3, 1),
	/**
	 * 失败
	 */
	Lose(1, 3),
	/**
	 * 平局
	 */
	Draw(2, 2);

	public final int myAdd;
	public final int enemyAdd;

	private GCompBattleResult(int myAdd, int enemyAdd) {
		this.myAdd = myAdd;
		this.enemyAdd = enemyAdd;
	}
}