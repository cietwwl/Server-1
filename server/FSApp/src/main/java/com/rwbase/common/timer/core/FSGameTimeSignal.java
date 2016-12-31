package com.rwbase.common.timer.core;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

import com.log.GameLog;
import com.rw.fsutil.log.GmLog;
import com.rwbase.common.timer.FSDailyTaskType;
import com.rwbase.common.timer.IGameTimerDelegate;
import com.rwbase.common.timer.IGameTimerTask;

/**
 * 
 * @author CHEN.P
 *
 */
public class FSGameTimeSignal implements RunnableFuture<Object> {

	private IGameTimerTask _task;
	
	public final long deadline; // 执行的时间
	private long _assumeExecuteTime; // 预定的执行时间
	private volatile int _stopIndex;
	private volatile int _remainingRounds;
	
	private final long _createTimeMillis; // 时效任务创建的时间
	
	private Sync _sync;
	
	private IGameTimerDelegate _timerDelegate;
	
	private long _interval; // 间隔
	
	private boolean _isDailyTask; // 是否天时效
	
	public FSGameTimeSignal(IGameTimerDelegate pDelegate, IGameTimerTask pTask, long pInterval, boolean isDayTask) {
		if(pTask == null) {
			throw new NullPointerException("task不能为null！");
		}
		long currentTime = System.currentTimeMillis();
		this._timerDelegate = pDelegate;
		this.deadline = pInterval + currentTime;
		this._assumeExecuteTime = deadline;
		this._createTimeMillis = currentTime;
		this._task = pTask;
		this._interval = pInterval;
		this._sync = new Sync(_task);
		this._isDailyTask = isDayTask;
//		System.out.println("task:" + _task.getName() + ", deadline:" + deadline);
	}
	
	private void checkChildTasks() {
		List<FSGameTimerTaskSubmitInfoImpl> childTasks = this._task.getChildTasks();
		if (childTasks != null && childTasks.size() > 0) {
			for (FSGameTimerTaskSubmitInfoImpl submitInfo : childTasks) {
				_timerDelegate.submitNewTask(submitInfo.getTask(), submitInfo.getInterval(), submitInfo.getTimeUnitOfInterval(), submitInfo.isDayTask());
			}
		}
	}
	
	void release() {
		this._timerDelegate = null;
		this._task = null;
		this._sync.release();
//		this._sync = null;
	}
	
	void setStopIndex(int pStopIndex) {
		this._stopIndex = pStopIndex;
	}
	
	void setRemainingRounds(int pRemainingRounds) {
		this._remainingRounds = pRemainingRounds;
	}
	
	void decreaseRemainingRounds(int offset) {
		this._remainingRounds -= offset;
	}
	
	void updateInterval(long pNewInterval) {
		this._interval = pNewInterval;
	}
	
	void updateAssumeTime(long value) {
		this._assumeExecuteTime = value;
	}
	
	public int getRemainingRounds() {
		return _remainingRounds;
	}
	
	public int getStopIndex() {
		return _stopIndex;
	}
	
	protected void done() {
		_task.afterOneRoundExecuted(this);
	}
	
	/**
	 * 
	 * 获取预设的执行时间
	 * 
	 * @return
	 */
	public long getAssumeExecuteTime() {
		return _assumeExecuteTime;
	}
	
	public IGameTimerTask getTask() {
		return _task;
	}
	
	@Override
	public String toString() {
		long currentTime = System.currentTimeMillis();
		long remaining = this.deadline - currentTime;
		
		StringBuilder buf = new StringBuilder(300);
		buf.append(_task.getName()).append("@");
		buf.append(FSGameTimer.FORMAT_DEBUG.format(new Date(_createTimeMillis)));
		buf.append("->");
		buf.append(FSGameTimer.FORMAT_DEBUG.format(new Date(deadline)));
		buf.append("(");
		buf.append("deadline: ");
		if(remaining > 0) {
			buf.append(remaining);
			buf.append(" ms early, ");
		} else if (remaining < 0) {
			buf.append(-remaining);
			buf.append(" ms later, ");
		} else {
			buf.append("now, ");
		}
		buf.append("now: ").append(FSGameTimer.FORMAT_DEBUG.format(new Date(currentTime)));
		if(this.isCancelled()) {
			buf.append(", cancelled");
		}
		buf.append(")");
		return buf.toString();
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		if(this._sync.innerCancel(mayInterruptIfRunning)) {
			_timerDelegate.cancel(this);
			return true;
		}
		return false;
	}

	@Override
	public boolean isCancelled() {
		return this._sync.innerIsCancelled();
	}

	@Override
	public boolean isDone() {
		return this._sync.innerIsDone();
	}

	@Override
	public Object get() throws InterruptedException, ExecutionException {
		return _sync.innerGet();
	}

	@Override
	public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return _sync.innerGet(unit.toNanos(timeout));
	}

	@Override
	public void run() {
		if (_isDailyTask && this.deadline > System.currentTimeMillis()) {
			long sub = this.deadline - System.currentTimeMillis();
			GmLog.info("FSGameTimeSignal#run()，时效任务提早了：" + sub + "毫秒！");
			if (sub > 0 && sub < 10000) {
				try {
					TimeUnit.MILLISECONDS.sleep(sub);
				} catch (Exception e) {
					GameLog.error("FSGameTimeSignal", "run", "sleep异常，时长：" + sub + "毫秒！", e);
					GmLog.error("sleep异常，时长：" + sub + "毫秒！", e);
				}
			}
		}
		this._sync.innerRun();
		if (this._isDailyTask) {
			int type = FSDailyTaskType.getTypeByClass(_task.getClass());
			if (type > 0) {
				FSGameTimerSaveData.getInstance().updateLastExecuteTimeOfDailyTask(type, System.currentTimeMillis());
			}
		}
		this.checkChildTasks();
		if (this._task.isContinue()) {
			_timerDelegate.submitNewTask(_task, _interval, FSGameTimer.STANDARD_UNIT_OF_TIMER, this._isDailyTask);
			this.release();
		}
	}
	
	protected void runAndReset() {
		this._sync.innerRunAndReset();
	}

	private final class Sync extends AbstractQueuedSynchronizer {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5594102496004082367L;
		
		/** State value representing that task is running */
		private static final int _STATE_RUNNING = 1;
		/** State value representing that task ran */
		private static final int _STATE_RAN = 2;
		/** State value representing that was cancelled */
		private static final int _STATE_CANCELLED = 4;
		
		/** the underlying callable */
		private IGameTimerTask _callable;
		/** the result to return from get() */
		private Object _result;
		/** the exception to throw from get() */
		private Throwable _exception;
		
		private volatile Thread _runner;
		
		Sync(IGameTimerTask pCallable) {
			this._callable = pCallable;
		}
		
		private boolean ranOrCancelled(int state) {
			return (state & (_STATE_RAN | _STATE_CANCELLED)) != 0;
		}
		
		void release() {
			this._callable = null;
		}
		
		/**
		 * 继承AQS的tryAcquireShared方法，如果状态时RAN或者CANCELLED状态，则返回成功
		 */
		protected int tryAcquireShared(int ignore) {
			return innerIsDone() ? 1 : -1;
		}
		
		/**
		 * 继承AQS的release方法
		 */
		protected boolean tryReleaseShared(int ignore) {
			_runner = null;
			return true;
		}
		
		boolean innerIsCancelled() {
			return getState() == _STATE_CANCELLED;
		}
		
		boolean innerIsDone() {
			return ranOrCancelled(getState()) && _runner == null;
		}
		
		/**
		 * 
		 * 获取运行结果
		 * 
		 * @return
		 * @throws InterruptedException
		 * @throws ExecutionException
		 */
		Object innerGet() throws InterruptedException, ExecutionException {
			acquireSharedInterruptibly(0);
			if(getState() == _STATE_CANCELLED) {
				throw new CancellationException();
			}
			if(_exception != null) {
				throw new ExecutionException(_exception);
			}
			return _result;
		}
		
		Object innerGet(long nanosTimeout) throws InterruptedException, ExecutionException, TimeoutException {
			if(!tryAcquireSharedNanos(0, nanosTimeout)) {
				throw new TimeoutException();
			}
			if(getState() == _STATE_CANCELLED) {
				throw new CancellationException();
			}
			if(_exception != null) {
				throw new ExecutionException(_exception);
			}
			return _result;
		}
		
		/**
		 * 
		 * 设置运行结果
		 * 
		 * @param v
		 */
		void innerSet(Object v) {
			for(;;) {
				int s = getState();
				if(s == _STATE_RAN) {
					return;
				}
				if(s == _STATE_CANCELLED) {
					releaseShared(0);
					return;
				}
				if(compareAndSetState(s, _STATE_RAN)) {
					_result = v;
					releaseShared(0);
					FSGameTimeSignal.this.done();
					return;
				}
			}
		}
		
		void innerSetException(Throwable t) {
			for(;;) {
				int s = getState();
				if (s == _STATE_RAN) {
					return;
				}
				if (s == _STATE_CANCELLED) {
					releaseShared(0);
					return;
				}
				if (compareAndSetState(s, _STATE_RAN)) {
					_exception = t;
					_result = null;
					releaseShared(0);
					FSGameTimeSignal.this.done();
					return;
				}
			}
		}
		
		boolean innerCancel(boolean mayInterruptIfRunning) {
			for (;;) {
				int s = getState();
				if (ranOrCancelled(s)) {
					return false;
				}
				if (compareAndSetState(s, _STATE_CANCELLED)) {
					break;
				}
			}
			if (mayInterruptIfRunning) {
				Thread r = _runner;
				if (r != null) {
					r.interrupt();
				}
			}
			releaseShared(0);
			FSGameTimeSignal.this.done();
			return true;
		}
		
		void innerRun() {
			if (!compareAndSetState(0, _STATE_RUNNING)) {
				return;
			}
			try {
				_runner = Thread.currentThread();
				if (getState() == _STATE_RUNNING) {
					innerSet(_callable.onTimeSignal(FSGameTimeSignal.this));
				} else {
					releaseShared(0);
				}
			} catch (Throwable ex) {
				innerSetException(ex);
			}
		}
		
		boolean innerRunAndReset() {
			if(!compareAndSetState(0, _STATE_RUNNING)) {
				return false;
			}
			try {
				_runner = Thread.currentThread();
				if(getState() == _STATE_RUNNING) {
					_callable.onTimeSignal(FSGameTimeSignal.this);
				}
				_runner = null;
				return compareAndSetState(_STATE_RUNNING, 0);
			} catch (Throwable ex) {
				innerSetException(ex);
				return false;
			}
		}
	}
}
