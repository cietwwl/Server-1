package com.rwbase.common.timer;

import java.util.concurrent.TimeUnit;

import com.rwbase.common.timer.core.FSGameTimeSignal;

public interface FSGameTimerDelegate {

	/**
	 * 
	 * @param target
	 */
	public void cancel(FSGameTimeSignal target);
	
	/**
	 * 
	 * @param task
	 * @param delay
	 * @param unit
	 */
	public FSGameTimeSignal submitNewTask(FSGameTimerTask task, long delay, TimeUnit unit);
}
