package com.playerdata.groupcompetition.util;

/**
 * 
 * 单场赛事的状态
 * 
 * @author CHEN.P
 *
 */
public enum GCompEventsStatus {
	

	/**
	 * 未开始
	 */
	NONE(0, 0),
	/**
	 * 比赛准备阶段
	 */
	PREPARE(1, 30),
	/**
	 * 组队赛阶段
	 */
	TEAM_EVENTS(2, 15),
	/**
	 * 中场休息阶段
	 */
	REST(3, 3),
	/**
	 * 个人赛阶段
	 */
	PERSONAL_EVENTS(4, 15),
	/**
	 * 结束
	 */
	FINISH(5, 0);
	
	public final int sign;
	private int _lastMinutes; // 持续的时间（分钟）
	private GCompEventsStatus _nextStatus;
	
	private static int _totalMinutes;
	
	static {
		GCompEventsStatus[] all = values();
		GCompEventsStatus currentStatus;
		for (int i = 0, length = all.length, next = 1; i < length && next != length; i++, next++) {
			currentStatus = all[i];
			currentStatus._nextStatus = all[next];
			_totalMinutes += currentStatus._lastMinutes;
		}
	}
	
	/**
	 * 
	 * 获取一共的持续时间
	 * 
	 * @return
	 */
	public static int getTotalLastMinutes() {
		return _totalMinutes;
	}
	
	private GCompEventsStatus(int sign, int pLastMinutes) {
		this.sign = sign;
		this._lastMinutes = pLastMinutes;
	}
	
	public int getLastMinutes() {
		return _lastMinutes;
	}
	
	public GCompEventsStatus getNextStatus() {
		return this._nextStatus;
	}
}
