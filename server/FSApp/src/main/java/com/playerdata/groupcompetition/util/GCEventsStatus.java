package com.playerdata.groupcompetition.util;

/**
 * 
 * 单场赛事的状态
 * 
 * @author CHEN.P
 *
 */
public enum GCEventsStatus {
	

	/**
	 * 未开始
	 */
	NONE(0, 0),
	/**
	 * 比赛准备阶段
	 */
	PREPARE(1, 3),
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
	private GCEventsStatus _nextStatus;
	
	static {
		GCEventsStatus[] all = values();
		for(int i = 0, length = all.length, next = 1; i < length && next != length; i++, next++) {
			all[i]._nextStatus = all[next];
		}
	}
	
	private GCEventsStatus(int sign, int pLastMinutes) {
		this.sign = sign;
		this._lastMinutes = pLastMinutes;
	}
	
	public int getLastMinutes() {
		return _lastMinutes;
	}
	
	public GCEventsStatus getNextStatus() {
		return this._nextStatus;
	}
}
