package com.playerdata.charge;

import com.playerdata.charge.dao.ChargeRecord;

public interface IChargeCallbackChecker<T> {

	/**
	 * 
	 *  检查订单是否合法
	 *  
	 * @param content
	 * @return
	 */
	public boolean checkChargeCallback(T content);
	
	/**
	 * 
	 * @param content
	 * @return
	 */
	public ChargeRecord generateChargeRecord(T content);
}
