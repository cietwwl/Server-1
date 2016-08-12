package com.rwbase.common.timer;

import java.util.concurrent.TimeUnit;

import com.rwbase.common.timer.core.FSGameTimeSignal;

/**
 * 
 * Timer的委托代理的接口规范
 * 
 * @author CHEN.P
 *
 */
public interface IGameTimerDelegate {

	/**
	 * 
	 * 取消一个任务
	 * 
	 * @param target
	 */
	public void cancel(FSGameTimeSignal target);
	
	/**
	 * 
	 * 提交一个新任务
	 * 
	 * @param task
	 * @param delay
	 * @param unit
	 */
	public FSGameTimeSignal submitNewTask(IGameTimerTask task, long delay, TimeUnit unit);
}
