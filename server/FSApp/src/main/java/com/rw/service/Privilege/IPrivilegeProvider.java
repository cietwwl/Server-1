package com.rw.service.Privilege;

import com.rw.fsutil.common.stream.IStream;

/**
 * 特权管理器向具体提供特权的模块（VIP，月卡等）询问当前玩家在已知特权中哪些是有效的
 * 特权管理器向特权提供者注册特权发生变化的通知
 */
public interface IPrivilegeProvider {
	/**
	 * 数据更新的时候特权提供者将自己传递到管理器，管理器由此知道哪个模块产生的特权发生了变化
	 * @return
	 */
	public IStream<IPrivilegeProvider> getPrivilegeProvider();
	
	/**
	 * 返回最佳匹配充值类型，如果没有应该返回-1，名字全部小写
	 * @param chargeSources
	 * @return
	 */
	public int getBestMatchCharge(String[] chargeSources);
	
	/**
	 * 返回当前充值或者月卡的类型名称
	 * @return
	 */
	public String getCurrentChargeType();

	/**
	 * 当前充值等级是否已经达到或者超过chargeType对应的充值等级
	 * @param chargeType
	 * @return
	 */
	public boolean reachChargeLevel(String chargeType);

	public boolean hasChargeType(String chargeType);
}
