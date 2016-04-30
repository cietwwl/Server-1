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
	 * 如果允许多种类型叠加，就按照优先级从低到高返回
	 * @param sources
	 * @return
	 */
	public int getPrivilegeIndex(String[] sources);
	public String getCurrentPrivilege();
}
