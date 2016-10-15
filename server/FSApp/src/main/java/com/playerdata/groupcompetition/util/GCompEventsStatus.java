package com.playerdata.groupcompetition.util;

import java.util.concurrent.TimeUnit;

import com.rwbase.dao.groupcompetition.pojo.GCompTimeCfg;

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
	NONE(0, 0, "未开始"),
	/**
	 * 比赛准备阶段
	 */
	PREPARE(1, 30, "准备阶段"),
	/**
	 * 组队赛阶段
	 */
	TEAM_EVENTS(2, 15, "组队战阶段"),
	/**
	 * 中场休息阶段
	 */
	REST(3, 3, "中场休息阶段"),
	/**
	 * 个人赛阶段
	 */
	PERSONAL_EVENTS(4, 15, "个人战阶段"),
	/**
	 * 结束
	 */
	FINISH(5, 0, "结束");
	
	public final int sign;
	private int _lastMinutes; // 持续的时间（分钟）
	private GCompEventsStatus _nextStatus;
	private String displayName;
	
	private static int _totalMinutes;

	private static void calculateTotalMinutes() {
		_totalMinutes = 0;
		GCompEventsStatus[] all = values();
		GCompEventsStatus currentStatus;
		for (int i = 0, length = all.length, next = 1; i < length && next != length; i++, next++) {
			currentStatus = all[i];
			currentStatus._nextStatus = all[next];
			_totalMinutes += currentStatus._lastMinutes;
		}
	}
	
	static {
		calculateTotalMinutes();
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
	
	public static void setTimeInfo(GCompTimeCfg cfg) {
		if (cfg.getPrepareTime() > 0) {
			PREPARE._lastMinutes = (int) TimeUnit.SECONDS.toMinutes(cfg.getPrepareTime());
		}
		if (cfg.getFirstRoundTime() > 0) {
			TEAM_EVENTS._lastMinutes = (int) TimeUnit.SECONDS.toMinutes(cfg.getFirstRoundTime());
		}
		if (cfg.getRest() > 0) {
			REST._lastMinutes = (int) TimeUnit.SECONDS.toMinutes(cfg.getRest());
		}
		if (cfg.getPrepareTime() > 0) {
			PERSONAL_EVENTS._lastMinutes = (int) TimeUnit.SECONDS.toMinutes(cfg.getPrepareTime());
		}
		calculateTotalMinutes();
	}
	
	private GCompEventsStatus(int sign, int pLastMinutes, String pDisplayName) {
		this.sign = sign;
		this._lastMinutes = pLastMinutes;
		this.displayName = pDisplayName;
	}
	
	public int getLastMinutes() {
		return _lastMinutes;
	}
	
	public GCompEventsStatus getNextStatus() {
		return this._nextStatus;
	}
	
	public int getSign(){
		return this.sign;
	}

	public String getDisplayName() {
		return displayName;
	}
}
