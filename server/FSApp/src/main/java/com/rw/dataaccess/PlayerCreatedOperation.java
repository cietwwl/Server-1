package com.rw.dataaccess;


/**
 * 玩家创建操作
 * @author Jamaz
 *
 */
public interface PlayerCreatedOperation {

	/**
	 * 执行玩家创建操作
	 * @param param
	 */
	public boolean execute(PlayerCreatedParam param);
}
