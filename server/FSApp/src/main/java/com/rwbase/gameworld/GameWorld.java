package com.rwbase.gameworld;

import com.rw.fsutil.common.TaskExceptionHandler;
import com.rwbase.common.PlayerTaskListener;

public interface GameWorld {

	/**
	 * 执行异步任务
	 * 
	 * @param task
	 */
	public void asynExecute(Runnable task);

	/**
	 * 异步执行指定角色的任务，保证同一个角色任务的执行是线程安全
	 * 
	 * @param key
	 * @param task
	 */
	public void asyncExecute(String userId, PlayerTask task);

	/**
	 * <pre>
	 * 异步执行指定主键的任务
	 * </pre>
	 * 
	 * @param key
	 * @param task
	 * @param handler
	 */
	public void asyncExecute(String key, PlayerTask task, TaskExceptionHandler handler);

	/**
	 * 异步执行账号相关的任务
	 * 
	 * @param accountId
	 * @param task
	 */
	public void executeAccountTask(String accountId, Runnable task);

	/**
	 * 获取某个账号当前正在执行的任务数量
	 * 
	 * @param accountId
	 * @return
	 */
	public int getAccountTaskCount(String accountId);

	/**
	 * 获取属性
	 * 
	 * @param key
	 * @return
	 */
	public String getAttribute(GameWorldKey key);

	/**
	 * 更新属性
	 * 
	 * @param key
	 * @param attribute
	 * @return
	 */
	public boolean updateAttribute(GameWorldKey key, String attribute);

	/**
	 * 注册一个数据变化的监听者
	 * 
	 * @param listener
	 */
	public void registerPlayerDataListener(PlayerTaskListener listener);
}
