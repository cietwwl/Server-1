package com.rwbase.common.timer.test;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.common.timer.core.FSGameTimeSignal;
import com.rwbase.common.timer.core.FSGameTimer;
import com.rwbase.common.timer.core.FSGameTimerMgr;
import com.rwbase.common.timer.core.FSGameTimerTaskSubmitInfoImpl;

public class FSGameTimerTest {
	
	private static final DateFormat _DATE_FORMATTER = new SimpleDateFormat("MMdd:HH:mm:ss.SSS");
	private static final Field _FIELD_CURRENT_CURSOR_OF_WHEEL;
	private static FSGameTimer _timer;
	
	static {
		Field f = null;
		try {
			f = FSGameTimer.class.getDeclaredField("_currentCursorOfWheel");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		_FIELD_CURRENT_CURSOR_OF_WHEEL = f;
		_FIELD_CURRENT_CURSOR_OF_WHEEL.setAccessible(true);
	}
	
	static final String _format = "[%s][%s]-%s";
	
	static class Printer implements Runnable {

		static final Queue<Object[]> _msgQueue = new ConcurrentLinkedQueue<Object[]>();
		
		@Override
		public void run() {
			while(true) {
				Object[] msg;
				while((msg = _msgQueue.poll()) != null) {
					System.out.println(String.format(_format, msg[0], _DATE_FORMATTER.format(new Date((Long)msg[2])), msg[1]));
				}
			}
		}
		
	}

	static void log(String msg) {
		Printer._msgQueue.add(new Object[] { Thread.currentThread().getName(), msg, System.currentTimeMillis() });
	}

	static void submitSecondTask() {
		for (int i = 0; i < 10; i++) {
			new FSGameSecondTask(i + 1);
		}		
	}
	
	static void submitMinuteTask() {
		for (int i = 0; i < 10; i++) {
			new FSGameMinuteTask(1);
		}
	}
	
	public static void main(String[] args) throws Exception {
		Thread printerThread = Executors.defaultThreadFactory().newThread(new Printer());
		printerThread.start();
		FSGameTimerMgr.getInstance().init();
		Field fTimer =FSGameTimerMgr.class.getDeclaredField("_timerInstance");
		fTimer.setAccessible(true);
		_timer = (FSGameTimer)fTimer.get(FSGameTimerMgr.getInstance());
		fTimer.setAccessible(false);
		new FSGameFixedMinuteTask();
		FSGameTimerMgr.getInstance().submitDayTask(new TaskDemoImpl(), 14, 50);
//		new FSGameHourTask(true, 1);
//		new FSGameHourTask(false, 1);
//		new FSGameHourTask(false, 2);
//		submitSecondTask();
//		submitMinuteTask();
	}
	
	static class FSGameMillisecondTask implements IGameTimerTask {

		@Override
		public String getName() {
			return FSGameSecondTask.class.getSimpleName();
		}

		@Override
		public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
			log("FSGameSecondTask#onTimeSignal");
			return "SUCCESS";
		}

		@Override
		public void afterOneRoundExecuted(FSGameTimeSignal timeSignal) {
			
		}

		@Override
		public void rejected(RejectedExecutionException e) {
			
		}
		
		@Override
		public boolean isContinue() {
			return true;
		}
		
		@Override
		public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
			return null;
		}
		
	}
	
	static abstract class FSGameTaskBase implements IGameTimerTask {
		
		protected long lastExecuteTime;
		protected long delayMillis;
		protected int counter;
		protected final long id;
		protected final boolean printNormalContent;
		protected final int warningDelayMillis;
		
		static final AtomicLong _ID_GENERATOR = new AtomicLong();

		protected FSGameTaskBase(long delay, TimeUnit unit, boolean printNormalContent, int warningDelayMillis) {
			this.id = _ID_GENERATOR.incrementAndGet();
			this.printNormalContent = printNormalContent;
			this.warningDelayMillis = warningDelayMillis;
			switch (unit) {
			case HOURS:
				FSGameTimerMgr.getInstance().submitHourTask(this, (int)delay);
				break;
			case MINUTES:
				FSGameTimerMgr.getInstance().submitSecondTask(this, (int)delay);
				break;
			case SECONDS:
				FSGameTimerMgr.getInstance().submitSecondTask(this, (int)delay);
				break;
			default:
				break;
			}
			this.delayMillis = TimeUnit.MILLISECONDS.convert(delay, unit);
			log("submit " + unit.name() + " task :" + this.toString() + "!");
		}
		
		@Override
		public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
			counter++;
			if (printNormalContent) {
				log(this.getName() + "@" + id + "#onTimeSignal");
			}
			if (lastExecuteTime > 0) {
				long current = System.currentTimeMillis();
				long sub = current - lastExecuteTime - delayMillis;
				if (sub > 0) {
					if (sub >= this.warningDelayMillis) {
						log(this.getName() + "@" + id + ", " + (sub) + " millis delay! counter=" + counter + ", lastExecuteTime:" + lastExecuteTime + ", currentTime=" + current);
					}
				} else if (sub < 0) {
					sub = Math.abs(sub);
					if (sub > warningDelayMillis) {
						log(this.getName() + "@" + id + ", " + sub + " millis ealier! counter=" + counter + ", lastExecuteTime:" + lastExecuteTime + ", currentTime=" + current
								+ ", current cursor of wheel: " + _FIELD_CURRENT_CURSOR_OF_WHEEL.getInt(_timer));
					}
				}
				lastExecuteTime = current;
			} else {
				lastExecuteTime = System.currentTimeMillis();
			}
			return "SUCCESS";
		}
		
		@Override
		public void afterOneRoundExecuted(FSGameTimeSignal timeSignal) {
			
		}

		@Override
		public void rejected(RejectedExecutionException e) {
			
		}
		
		@Override
		public boolean isContinue() {
			return true;
		}

		@Override
		public String toString() {
			return "[" + id + ", " + this.getName() + "] @ " + this.hashCode();
		}
	}
	
	static class FSGameSecondTask extends FSGameTaskBase implements IGameTimerTask {
		
		FSGameSecondTask(int delay) {
			super(delay, TimeUnit.SECONDS, false, 2);
		}

		@Override
		public String getName() {
			return FSGameSecondTask.class.getSimpleName();
		}

		@Override
		public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
			return null;
		}

	}
	
	static class FSGameAnotherSecondTask implements IGameTimerTask {

		private int _counter;
		@Override
		public String getName() {
			return FSGameAnotherSecondTask.class.getSimpleName();
		}

		@Override
		public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
			_counter++;
			log(this.getName() + " 第 " + _counter + " 次被调用！");
			return "SUCCESS";
		}

		@Override
		public void afterOneRoundExecuted(FSGameTimeSignal timeSignal) {
			
		}

		@Override
		public void rejected(RejectedExecutionException e) {
			
		}

		@Override
		public boolean isContinue() {
			return _counter < 10;
		}

		@Override
		public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
			return Collections.emptyList();
		}
		
	}
	
	static class FSGameMinuteTask extends FSGameTaskBase implements IGameTimerTask {

		FSGameMinuteTask(int delay) {
			super(delay, TimeUnit.MINUTES, false, 6);
		}
		
		@Override
		public String getName() {
			return FSGameMinuteTask.class.getSimpleName();
		}
		
		@Override
		public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
			return null;
		}
	}
	
	static class FSGameFixedMinuteTask implements IGameTimerTask {
		
		private int _counter;
		public FSGameFixedMinuteTask() {
			_counter = 0;
			log("submit " + this.getName());
			FSGameTimerMgr.getInstance().submitFixedMinuteTask(this, 1);
		}

		@Override
		public String getName() {
			return FSGameFixedMinuteTask.class.getSimpleName();
		}

		@Override
		public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
			_counter++;
//			if (_counter % 5 == 0) {
				log(this.getName() + " has been called " + (_counter) + " times");
//			}
			return "SUCCESS:" + _counter;
		}

		@Override
		public void afterOneRoundExecuted(FSGameTimeSignal timeSignal) {
			try {
				if (_counter % 5 == 0) {
					log("result of " + _counter + " times:" + timeSignal.get());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void rejected(RejectedExecutionException e) {
			
		}

		@Override
		public boolean isContinue() {
			return true;
		}
		
		@Override
		public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
			return Arrays.asList(FSGameTimerMgr.getInstance().createSecondTaskSubmitInfo(new FSGameAnotherSecondTask(), 1));
		}
		
	}
	
	static class FSGameHourTask implements IGameTimerTask {
		
		private int _counter;
		private String _uuid;
		
		public FSGameHourTask(boolean fixedHour, int interval) {
			this._uuid = java.util.UUID.randomUUID().toString();
			log("submit " + this.getName());
			if(fixedHour) {
				FSGameTimerMgr.getInstance().submitFixedHourTask(this, interval);
			} else {
				FSGameTimerMgr.getInstance().submitHourTask(this, interval);
			}
		}

		@Override
		public String getName() {
			return FSGameHourTask.class.getSimpleName() + "@" + _uuid;
		}

		@Override
		public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
			log(this.getName() + " has been called " + (++_counter) + " times");
			return new FSGameHourTaskResult();
		}

		@Override
		public void afterOneRoundExecuted(FSGameTimeSignal timeSignal) {
			try {
				FSGameHourTaskResult result = (FSGameHourTaskResult) timeSignal.get();
				System.out.println("result of " + _counter + " round is : " + result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void rejected(RejectedExecutionException e) {
			
		}

		@Override
		public boolean isContinue() {
			return true;
		}
		
		@Override
		public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
			return null;
		}
		
		
		static class FSGameHourTaskResult {
			private Date _createDate;
			private String _uuid;
			
			FSGameHourTaskResult() {
				this._createDate = new Date(System.currentTimeMillis());
				this._uuid = java.util.UUID.randomUUID().toString();
			}
			
			@Override
			public String toString() {
				return this.getClass().getSimpleName() + " [ UUID = " + _uuid + ", create @ " + _DATE_FORMATTER.format(_createDate) + "]";
			}
		}
	}
	
	static class TaskDemoImpl implements IGameTimerTask {
		
		private String _uuid;
		private int _counter;
		
		public TaskDemoImpl() {
			_uuid = java.util.UUID.randomUUID().toString();
		}

		@Override
		public String getName() {
			return TaskDemoImpl.class.getSimpleName() + "@" + _uuid;
		}

		@Override
		public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
			_counter++;
			log(this.getName() + "#onTimeSignal " + _counter + " times");
			return "SUCCESS";
		}

		@Override
		public void afterOneRoundExecuted(FSGameTimeSignal timeSignal) {
			
		}

		@Override
		public void rejected(RejectedExecutionException e) {
			
		}

		@Override
		public boolean isContinue() {
			return true;
		}

		@Override
		public List<FSGameTimerTaskSubmitInfoImpl> getChildTasks() {
			return null;
		}
		
	}
}
