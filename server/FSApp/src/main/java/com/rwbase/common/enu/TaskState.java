package com.rwbase.common.enu;

/**
 * 
 * 任务状态枚举
 * 
 * @author CHEN.P
 *
 */
public enum TaskState {
	
	/**
	 * 任务状态：未完成
	 */
	NOT_DONE(0),
	/**
	 * 任务状态：可领取奖励
	 */
	CAN_DRAW(1),
	/**
	 * 任务状态：已领取奖励
	 */
	DRAWED(2),
	;
	public final int sign;
	private TaskState(int pSign) {
		this.sign = pSign;
	}
}
