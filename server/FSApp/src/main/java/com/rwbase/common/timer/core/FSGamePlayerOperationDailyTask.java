package com.rwbase.common.timer.core;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.rw.fsutil.common.Pair;
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
	
//	void manualExecute(Calendar lastShutdownCalendar, long asumeTime) {
	void manualExecute(Calendar lastShutdownCalendar) {
		if (_manualExecuteFinish) {
			return;
		}
		Pair<Long, Integer> pair = FSGameTimerMgr.getInstance().calculateExecuteTimes(lastShutdownCalendar, _hourOfDay, _minute);
		int executeTimes = pair.getT2();
		if (executeTimes > 0) {
			while (executeTimes > 0) {
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
