package com.rwbase.common.timer;

import java.util.concurrent.TimeUnit;

/**
 * 
 * 时效任务的子任务的接口规范
 * 
 * @author CHEN.P
 *
 */
public interface IGameTimerTaskSubmitInfo {

	/**
	 * 
	 * 获取任务
	 * 
	 * @return
	 */
	public IGameTimerTask getTask();
	
	/**
	 * 
	 * 获取时间间隔
	 * 
	 * @return
	 */
	public int getInterval();
	
	/**
	 * 
	 * 获取延迟的时间单位
	 * 
	 * @return
	 */
	public TimeUnit getTimeUnitOfInterval();
}
