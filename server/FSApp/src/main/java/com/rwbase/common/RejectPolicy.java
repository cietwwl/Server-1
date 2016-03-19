package com.rwbase.common;

public enum RejectPolicy {

	/**
	 * 丢弃最旧的请求
	 */
	DISCARD_OLDEST,
	/**
	 * 拒绝当前请求
	 */
	ABORT
}
