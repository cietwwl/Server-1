package com.rwbase.common.timer.core;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.rwbase.common.timer.IPlayerGatherer;

public class FSGamePlayerOperationDailyTask extends FSGamePlayerOperationTask {

	private boolean _manualExecuteFinish = false;
	private int _hourOfDay;
	private int _minute;
	
	FSGamePlayerOperationDailyTask(int hourOfDay, int minute, IPlayerGatherer playerGatherer, boolean needRecordData) {
		super(playerGatherer, needRecordData);
		this._hourOfDay = hourOfDay;
		this._minute = minute;
	}
	
	private int calculateExecuteTimes(Calendar lastShutdownCalendar) {
		// 計算上一次停服到現在需要補充執行的次數
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, _hourOfDay);
		c.set(Calendar.MINUTE, _minute);
		long time = c.getTimeInMillis(); // 今天最近一次執行時間
		int executeTimes = 0;
		/*
		 * 以下情況需要執行： 
		 * 1、停服時間與最近一次執行時間在同一日內，並且停服時此任務未執行，但當前時間已經過了最近一次的執行時間；
		 * 2、停服超過一天，停服時此任務未執行；如果當前時間已經超過了最近一次執行時間，則需要額外執行一次。
		 */
		int dayOfYearShutdown = lastShutdownCalendar.get(Calendar.DAY_OF_YEAR);
		int dayOfYearNearbyExecute = c.get(Calendar.DAY_OF_YEAR);
		if (dayOfYearShutdown == dayOfYearNearbyExecute) {
			if (lastShutdownCalendar.getTimeInMillis() < time && time < System.currentTimeMillis()) {
				executeTimes = 1;
			}
		} else {
			int subDay = dayOfYearNearbyExecute - dayOfYearShutdown;
			executeTimes = subDay;
			if (c.getTimeInMillis() < System.currentTimeMillis()) {
				executeTimes++;
			}
			c.add(Calendar.DATE, -subDay);
			if (c.getTimeInMillis() < lastShutdownCalendar.getTimeInMillis()) {
				executeTimes--;
			}
		}
		return executeTimes;
	}
	
	void manualExecute(Calendar lastShutdownCalendar, long asumeTime) {
		if (_manualExecuteFinish) {
			return;
		}
		int executeTimes = this.calculateExecuteTimes(lastShutdownCalendar);
		if (executeTimes > 0) {
//			List<FSGamePlayerOperationSubTask> list = new ArrayList<FSGamePlayerOperationSubTask>();
//			for (Iterator<FSGamePlayerOperationSubTask> itr = operationList.values().iterator(); itr.hasNext();) {
//				FSGamePlayerOperationSubTask subTask = itr.next();
//				long lastExecuteTime = FSGameTimerSaveData.getInstance().getLastExecuteTime(subTask.getOperatorType());
//				if (lastExecuteTime > 0) {
//					// 沒有記錄的不用管
//					list.add(subTask);
//				}
//			}
			while (executeTimes > 0) {
//				List<FSGameTimeSignal> timeSignalList = this.execute(list);
				List<FSGameTimeSignal> timeSignalList = this.execute(operationList.values());
				executeTimes--;
				while (timeSignalList.size() > 0) {
					for (Iterator<FSGameTimeSignal> itr = timeSignalList.iterator(); itr.hasNext();) {
						FSGameTimeSignal timeSignal = itr.next();
						if (timeSignal.isDone()) {
							itr.remove();
						}
					}
					if (timeSignalList.size() > 0) {
						try {
							TimeUnit.MILLISECONDS.sleep(100);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		_manualExecuteFinish = true;
	}

	public int getHourOfDay() {
		return _hourOfDay;
	}
	
	public int getMinute() {
		return _minute;
	}
}
