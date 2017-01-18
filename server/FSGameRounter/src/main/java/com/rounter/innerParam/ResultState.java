package com.rounter.innerParam;

import com.rounter.state.UCStateCode;

public enum ResultState {
	
	/**
	 * 成功
	 */
	SUCCESS(UCStateCode.STATE_OK),
	
	/**
	 * 服务器异常或者数据异常
	 */
	EXCEPTION(UCStateCode.STATE_SERVER_ERROR),
	
	/**
	 * 参数错误
	 */
	PARAM_ERROR(UCStateCode.STATE_PARAM_ERROR),
	
	/**
	 * 礼包编号错误
	 */
	GIFT_ID_ERROR(UCStateCode.STATE_GIFTID_ERROR),
	
	/**
	 * 礼包数量错误
	 */
	GIFT_COUNT_ERROR(UCStateCode.STATE_GIFT_NUM_ERROR),
	
	/**
	 * 角色不存在
	 */
	NO_ROLE(UCStateCode.STATE_ROLE_NOT_EXIST),
	
	/**
	 * 账号不存在
	 */
	NO_ACCOUNT(UCStateCode.STATE_ACCOUNT_ERROR),
	
	/**
	 * 礼包重复领取
	 */
	REPEAT_GET(UCStateCode.STATE_GIFT_RECV)
	;
	
	private UCStateCode ucState;
	
	ResultState(UCStateCode ucState){
		this.ucState = ucState;
	}
	
	public UCStateCode getUCStateCode(){
		return ucState;
	}
}
