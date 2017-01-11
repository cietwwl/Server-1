package com.rw.service.dropitem;

public enum DropGuaranteeState {

	/**
	 * 增加一次不掉落记录
	 */
	ADD_MISS,
	/**
	 * 增加一次掉落记录
	 */
	ADD_DROP,
	/**
	 * 触发保底掉落，或者从不掉落变成掉落(重置为掉落1次)
	 */
	DROP_RESET,
	/**
	 * 触发保底不掉落，或者从掉落变成不掉落(重置为不掉落1次)
	 */
	MISS_RESET
	
}
