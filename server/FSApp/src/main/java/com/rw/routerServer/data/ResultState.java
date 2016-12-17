package com.rw.routerServer.data;

public enum ResultState {
	
	/**
	 * 成功
	 */
	SUCCESS,
	
	/**
	 * 服务器异常或者数据异常
	 */
	EXCEPTION,
	
	/**
	 * 参数错误
	 */
	PARAM_ERROR,
	
	/**
	 * 礼包编号错误
	 */
	GIFT_ID_ERROR,
	
	/**
	 * 礼包数量错误
	 */
	GIFT_COUNT_ERROR,
	
	/**
	 * 角色不存在
	 */
	NO_ROLE,
	
	/**
	 * 账号不存在
	 */
	NO_ACCOUNT,
	
	/**
	 * 礼包重复领取
	 */
	REPEAT_GET
	
}
