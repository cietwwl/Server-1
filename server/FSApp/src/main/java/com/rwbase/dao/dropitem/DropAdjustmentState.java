package com.rwbase.dao.dropitem;

public enum DropAdjustmentState {

	/**
	 * 没有触发掉落
	 */
	FAIL,	
	/**
	 * 触发首次掉落
	 */
	FIRST,
	/**
	 * 触发最小几率掉落
	 */
	MIN_RATE,
	/**
	 * 触发增加几率掉落
	 */
	ADD_RATE
}
