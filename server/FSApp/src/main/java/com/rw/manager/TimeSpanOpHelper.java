package com.rw.manager;

import com.log.GameLog;
import com.log.LogModule;

public class TimeSpanOpHelper {

	private long lastExecuteFinishTime = 0;

	private long timeSpan;

	private ITimeOp timeOp;

	private boolean isRunning;

	public TimeSpanOpHelper(ITimeOp timeOpP, long timeSpanP) {
		timeOp = timeOpP;
		timeSpan = timeSpanP;
		lastExecuteFinishTime = System.currentTimeMillis();
	}

	public synchronized void tryRun() {
		if (isRunning) {
			return;
		}

		long currentTimeMillis = System.currentTimeMillis();
		if (lastExecuteFinishTime + timeSpan <= currentTimeMillis) {
			isRunning = true;
			try {
				timeOp.doTask();
			} catch (Exception e) {
				GameLog.error(LogModule.COMMON.getName(), "TimeSpanOpHelper", "TimeSpanOpHelper[tryRun]", e);
			} finally {
				lastExecuteFinishTime = System.currentTimeMillis();
				isRunning = false;
			}
		}
	}
}