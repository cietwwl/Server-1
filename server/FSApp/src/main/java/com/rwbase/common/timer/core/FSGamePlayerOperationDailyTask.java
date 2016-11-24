package com.rwbase.common.timer.core;

import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import com.log.GameLog;
import com.playerdata.Player;
import com.playerdata.dailyreset.DailyResetReccordDao;
import com.playerdata.dailyreset.DailyResetRecord;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.util.DateUtils;
import com.rwbase.common.timer.IPlayerGatherer;
import com.rwbase.common.timer.IPlayerOperable;

public class FSGamePlayerOperationDailyTask extends FSGamePlayerOperationTask {

//	private boolean _manualExecuteFinish = false;
	private int _hourOfDay;
	private int _minute;
	
	FSGamePlayerOperationDailyTask(int hourOfDay, int minute, IPlayerGatherer playerGatherer, boolean needRecordData) {
		super(playerGatherer, needRecordData);
		this._hourOfDay = hourOfDay;
		this._minute = minute;
	}
	
	void manualExecute(Calendar lastExecuteCalendar, int type) {
		Pair<Long, Integer> pair = FSGameTimerMgr.getInstance().calculateExecuteTimes(lastExecuteCalendar, _hourOfDay, _minute);
		int executeTimes = pair.getT2();
		FSGamePlayerOperationSubTask subTask = operationList.get(type);
		long lastExecuteTime = 0;
		if (executeTimes > 0) {
			while (executeTimes > 0) {
				FSGameTimeSignal signal = this.execute(Arrays.asList(subTask)).get(0);
				executeTimes--;
				while (!signal.isDone()) {
					try {
						TimeUnit.MILLISECONDS.sleep(100);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			lastExecuteTime = pair.getT1() + TimeUnit.DAYS.toMillis(executeTimes);
		} else {
			lastExecuteTime = lastExecuteCalendar.getTimeInMillis();
		}
		subTask.setLastExecuteTime(lastExecuteTime);
		Calendar instance = Calendar.getInstance();
		instance.setTimeInMillis(lastExecuteTime);
		subTask.dayOfYearNow = instance.get(Calendar.DAY_OF_YEAR);
	}
	
	public void setLastExecuteTime(int operatorType, long time) {
		FSGamePlayerOperationSubTask subTask = operationList.get(operatorType);
		if(subTask != null) {
			subTask.setLastExecuteTime(time);
		}
	}
	
	@Override
	protected void addOperator(int operatorType, IPlayerOperable operator) {
		if (operator == null) {
			throw new NullPointerException("operator不能為null！類型：" + operatorType);
		}
		// 添加一個PlayerOperable到列表當中
		synchronized(operationList) {
			FSGamePlayerOperationSubTask pre = this.operationList.put(operatorType, new FSGamePlayerDailyOperationSubTask(operator, operatorType, this.needRecordData));
			if(pre != null) {
				throw new RuntimeException("重複的operatorType：" + operatorType + "，上一個實例是：" + pre.getOperator() + "，當前實例是：" + operator);
			}
		}
	}

	public int getHourOfDay() {
		return _hourOfDay;
	}
	
	public int getMinute() {
		return _minute;
	}
	
	protected static class FSGamePlayerDailyOperationSubTask extends FSGamePlayerOperationSubTask {

		public FSGamePlayerDailyOperationSubTask(IPlayerOperable pOperator, int operatorType, boolean pNeedRecordData) {
			super(pOperator, operatorType, pNeedRecordData);
		}
		
		private boolean operateOne(Player player) {
			if(player.isRobot()) {
				return false;
			}
			try {
				operator.operate(player);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				GameLog.error("FSGamePlayerOperationSubTask", "executeSingle", "执行出现错误！playerId：" + player.getUserId() + ", operator=" + operator.getClass());
				return false;
			}
		}
		
		private void afterOperate(DailyResetRecord record) {
			record.updateDailyResetDay(getOperatorType(), dayOfYearNow);
			DailyResetReccordDao.getInstance().update(record);
		}
		
		@Override
		protected void executeSingle(Player player) {
			if (this.operateOne(player)) {
				DailyResetRecord record = DailyResetReccordDao.getInstance().get(player.getUserId());
				afterOperate(record);
			}
		}
		
		@Override
		protected void playerLogin(Player player) {
			if (this.operator.isInterestingOn(player)) {
				if (isExecuting()) {
					this.tempPlayers.add(player);
				} else if (this.getLaseExecuteTime() > 0) {
					DailyResetRecord record = DailyResetReccordDao.getInstance().get(player.getUserId());
					if (record.getLastResetDay(getOperatorType()) != dayOfYearNow) {
						if (this.operateOne(player)) {
							afterOperate(record);
						}
					}
				}
			}
		}
	}
}
