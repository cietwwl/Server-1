package com.rwbase.common.timer.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.common.DetectionTool;
import com.log.GameLog;
import com.rw.fsutil.shutdown.IShutdownHandler;
import com.rw.fsutil.util.jackson.JsonUtil;
import com.rwbase.common.timer.IGameTimerDelegate;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;

/**
 * 
 * 时效任务管理器
 * 
 * @author CHEN.P
 *
 */
public class FSGameTimer implements IShutdownHandler {

	public static final String CONFIG_KEY_NAME_CORE_POOL_SIZE = "fs.game.timer.core-pool-size";
	public static final String CONFIG_KEY_NAME_TIME_INTERVAL_BETWEEN_TICK = "fs.game.timer.time-interval-of-tick";
	public static final String CONFIG_KEY_NAME_TICKS_PER_WHEEL = "fs.game.timer.ticks-per-wheel";
	public static final String CONFIT_KEY_NAME_START_DIRECTLY = "fs.game.timer.start-directly";
	public static final String CONFIG_KEY_NAME_PRE_ADD_TASKS = "fs.game.timer.pre-add-daily-tasks";
	
	public static final TimeUnit STANDARD_UNIT_OF_TIMER = TimeUnit.MILLISECONDS; // 时效任务的基准时间单位
	public static final DateFormat FORMAT_DEBUG = new SimpleDateFormat("MMdd:HH:mm:ss.SSS");
	
	private static final long _A_DAY_OF_STANDARD_UNIT = STANDARD_UNIT_OF_TIMER.convert(1, TimeUnit.DAYS);
	private static final long _MILLISECONDS_OF_ONE_DAY = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);
	
	private static final int _MAX_TICKS_PER_WHEEL = (int) Math.pow(2, 30); // 最大的轮数量
	private static final String _MODULE_NAME_FOR_LOG = FSGameTimer.class.getSimpleName(); // Log需要用到的模块名字
	
	public static final int MAX_HOUR_OF_DAY; // hourOfDay的最大值
	public static final int MIN_HOUR_OF_DAY; // hourOfDay的最小值
	public static final int MAX_MINUTE; // Minute的最大值
	public static final int MIN_MINUTE; // Minute的最小值
	
	private final int _corePoolSize; // 时效任务线程池的size
	private final ExecutorService _scheduledThreadPool; // 时效任务线程池
	
	private final TickerWorker _worker = new TickerWorker(); // 扫描是否有时效任务到时的线程
	private final Thread _workerThread; // 工作线程
	private final AtomicBoolean _shutdown = new AtomicBoolean(); // 关闭的标记
	private final AtomicBoolean _alreadyStart = new AtomicBoolean(); // 是否已经启动
	
	private final int _timeIntervalBetweenTicks; // tick与tick之间的最小间隔（单位：毫秒）
	private final long _totalTimeOfOneRound; // 每一轮的总时间间隔（扫描完整个时间轮的总耗时）
	private final Set<FSGameTimeSignal>[] _wheel; // 将时间看作一个轮状，轮被分割成N个区间，就好像一个钟
	private final ReusableIterator<FSGameTimeSignal>[] _iteratorsOfElementInWheel; // 时间轮上的set的iterator
	private final IGameTimerDelegate _delegate = new FSGameTimerDelegateImpl();
	
	private int _mask;
	private volatile int _currentCursorOfWheel; // 当前的指针位置
	
	static {
		Calendar c = Calendar.getInstance();
		MAX_HOUR_OF_DAY = c.getActualMaximum(Calendar.HOUR_OF_DAY);
		MIN_HOUR_OF_DAY = c.getActualMinimum(Calendar.HOUR_OF_DAY);
		MAX_MINUTE = c.getActualMaximum(Calendar.MINUTE);
		MIN_MINUTE = c.getActualMinimum(Calendar.MINUTE);
	}
	
	private static void logInfo(String module, String id, String message) {
		GameLog.info(_MODULE_NAME_FOR_LOG, id, message);
//		StringBuilder logContent = new StringBuilder();
//		logContent.append(module).append("|")
//					.append(id).append("|")
//					.append(message).append("|");
//		System.out.println(logContent.toString());
	}
	
	private static Set<FSGameTimeSignal>[] createWheel(int ticksPerWheel) {
		if (ticksPerWheel <= 0) {
			throw new IllegalArgumentException("ticksPerWheel must be greater than 0: " + ticksPerWheel);
		}
		if (ticksPerWheel > _MAX_TICKS_PER_WHEEL) {
			throw new IllegalArgumentException("ticksPerWheel may not be greater than 2^30: " + ticksPerWheel);
		}
		@SuppressWarnings("unchecked")
		Set<FSGameTimeSignal>[] wheel = new Set[ticksPerWheel];
		for (int i = 0; i < ticksPerWheel; i++) {
			wheel[i] = new MapBackedSet<FSGameTimeSignal>(new ConcurrentIdentityHashMap<FSGameTimeSignal, Boolean>(16, 0.95f, 4));
		}
		return wheel;
	}
	
	private static ReusableIterator<FSGameTimeSignal>[] createIterators(Set<FSGameTimeSignal>[] wheel) {
		@SuppressWarnings("unchecked")
		ReusableIterator<FSGameTimeSignal>[] iterators = new ReusableIterator[wheel.length];
		for(int i = 0; i < wheel.length; i++) {
			iterators[i] = (ReusableIterator<FSGameTimeSignal>) wheel[i].iterator();
		}
		return iterators;
	}
	
	private static int normalizeTicksPerWheel(int ticksPerWheel) {
		// 确保wheel上面的刻度数量是2的N次方
		int normalizedTicksPerWheel = 1;
		while (normalizedTicksPerWheel < ticksPerWheel) {
			normalizedTicksPerWheel <<= 1;
		}
		return normalizedTicksPerWheel;
	}
	
	public FSGameTimer(Properties config) {
		boolean startDirectly = Boolean.parseBoolean(config.getProperty(CONFIT_KEY_NAME_START_DIRECTLY));
		this._corePoolSize = Integer.parseInt(config.getProperty(CONFIG_KEY_NAME_CORE_POOL_SIZE));
		this._scheduledThreadPool = Executors.newFixedThreadPool(this._corePoolSize);
		this._timeIntervalBetweenTicks = Integer.parseInt(config.getProperty(CONFIG_KEY_NAME_TIME_INTERVAL_BETWEEN_TICK));
		int ticksPerWheel = normalizeTicksPerWheel(Integer.parseInt(config.getProperty(CONFIG_KEY_NAME_TICKS_PER_WHEEL))); // 这个时间轮上有多少刻度（确保wheel上面的刻度是2的n次方）
		this._totalTimeOfOneRound = _timeIntervalBetweenTicks * ticksPerWheel; // 计算扫描完这个轮所需要的总耗时
		this._wheel = createWheel(ticksPerWheel); // 创建时间轮
		this._iteratorsOfElementInWheel = createIterators(_wheel);
		this._mask = _wheel.length - 1;
		this._workerThread = Executors.defaultThreadFactory().newThread(_worker);
		if (startDirectly) {
			this.start(); // 直接开始
		}
		try {
			// 等待线程初始化的时间
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			logInfo(_MODULE_NAME_FOR_LOG, "_init_", "等待线程初始化收到一个Interrupt异常！");
		}
	}
	
	private int normalizeIndex(int originalIndex) {
		return originalIndex & _mask;
	}
	
	private void moveWheelCursor() {
		this._currentCursorOfWheel = this.normalizeIndex((this._currentCursorOfWheel + 1));
	}
	
	private void scheduleTimeSignal(FSGameTimeSignal timeSignal, long delay) {

		if (delay < _timeIntervalBetweenTicks) {
			delay = _timeIntervalBetweenTicks;
		}

		final int delayInRound = (int) (delay % _totalTimeOfOneRound); // 在时间轮上面的delay时间

		int relativeIndex = delayInRound / _timeIntervalBetweenTicks; // 在时间轮上的相对位置
		if (delay % _timeIntervalBetweenTicks != 0) {
			relativeIndex++;
		}

		int roundOffset = (int) (delay / _totalTimeOfOneRound); // 剩余多少轮
		if (roundOffset > 0 && delayInRound == 0) {
			roundOffset--;
		}
		int stopIndex = this.normalizeIndex((_currentCursorOfWheel + relativeIndex)); // 在时间轮上的实际位置
		timeSignal.setStopIndex(stopIndex);
		timeSignal.setRemainingRounds(roundOffset);
		Set<FSGameTimeSignal> setInWheel = _wheel[stopIndex];
		synchronized (setInWheel) {
			// 加到时间轮的集合上面
			setInWheel.add(timeSignal);
		}
	}
	
	private void cancelTask(FSGameTimeSignal target) {
		Set<FSGameTimeSignal> set = _wheel[target.getStopIndex()];
		synchronized (set) {
			set.remove(target);
		}
	}
	
	private boolean shutdownAndAwaitTermination(ExecutorService pool, int awaitSeconds) {
		pool.shutdown();
		try {
			if(!pool.awaitTermination(awaitSeconds, TimeUnit.SECONDS)) {
				GameLog.error(_MODULE_NAME_FOR_LOG, "shutdownAndAwaitTermination", "service did not terminate, await " + awaitSeconds + " s");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			pool.shutdownNow();
			Thread.currentThread().interrupt();
		}
		return pool.isTerminated();
	}
	
	/**
	 * 
	 * 停止计时器。调用本方法后内部的计时线程先被关闭，然后关闭处理线程池（提交了还没执行的任务直接取消，运行中的则等待Termination）
	 * 
	 * @return 提交了但还没开始执行的任务的集合，如果是很重要的任务，使用者可以对此做保存或其他处理
	 */
	private Set<FSGameTimeSignal> stop() {
		if (Thread.currentThread() == _workerThread) {
			// 不允许计时线程调用本方法
			throw new IllegalStateException(FSGameTimer.class.getName() + ".stop() cannot be called from his own workerThread!");
		}
		if (_shutdown.compareAndSet(false, true)) {
			// 确保只调用一次
			GameLog.info(_MODULE_NAME_FOR_LOG, "stop()", "时效任务管理器开始停止...");
			boolean interrupted = false;
			while(_workerThread.isAlive()) {
				_workerThread.interrupt();
				try {
					_workerThread.join(100);
				} catch (InterruptedException e) {
					interrupted = true;
				}
			}
			logInfo(_MODULE_NAME_FOR_LOG, "stop()", "woker线程已经停止！是否interrupted?" + interrupted);
			if(interrupted) {
				Thread.currentThread().interrupt();
			}
			boolean isTerminated = false;
			try {
				isTerminated = shutdownAndAwaitTermination(_scheduledThreadPool, 60);
			} catch (Exception e) {
				e.printStackTrace();
			}
			logInfo(_MODULE_NAME_FOR_LOG, "stop()", "时效任务管理器已停止！线程池是否关闭？" + _scheduledThreadPool.isShutdown() + ", 是否终止？" + isTerminated);
			
			Set<FSGameTimeSignal> unprocessedTimeSignals = new HashSet<FSGameTimeSignal>();
			for (Set<FSGameTimeSignal> bucket : _wheel) {
				unprocessedTimeSignals.addAll(bucket);
				bucket.clear();
			}
			
			return Collections.unmodifiableSet(unprocessedTimeSignals);
		} else {
			logInfo(_MODULE_NAME_FOR_LOG, "stop()", "重复调用停止！");
			return Collections.emptySet();
		}
	}
	
	@Override
	public void notifyShutdown() {
		this.stop();
		FSGameTimerSaveData.getInstance().setServerShutdownTime(System.currentTimeMillis());
		String attribute = JsonUtil.writeValue(FSGameTimerSaveData.getInstance());
		System.out.println(GameWorldKey.TIMER_DATA + "=" + attribute);
		GameWorldFactory.getGameWorld().updateAttribute(GameWorldKey.TIMER_DATA, attribute);
	}
	
	public void start() {
		if (_shutdown.get()) {
			throw new IllegalStateException("cannot be started once shutdown");
		}
		if (!_workerThread.isAlive() && this._alreadyStart.compareAndSet(false, true)) {
			_workerThread.start();
		}
	}
	
	/**
	 * 
	 * 提交一个时效任务
	 * 
	 * @param task 时效任务的执行逻辑
	 * @param delay 延迟的时间
	 * @param unit 延迟时间的单位
	 * @return
	 */
	public FSGameTimeSignal newTimeSignal(IGameTimerTask task, long delay, TimeUnit unit) {
		if (task == null) {
			throw new NullPointerException("task不能为null");
		}
		if (unit == null) {
			throw new NullPointerException("unit不能为null");
		}
		if (STANDARD_UNIT_OF_TIMER != unit) {
			delay = STANDARD_UNIT_OF_TIMER.convert(delay, unit); // 转为毫秒（时效任务的时间单位是毫秒）
		}
		FSGameTimeSignal timeSignal = new FSGameTimeSignal(_delegate, task, delay); // 实例化时间信号
		scheduleTimeSignal(timeSignal, delay); // 提交到时效任务管理器
		return timeSignal;
	}
	
	public FSGameTimeSignal newSecondTimeSignal(IGameTimerTask task, long delay) {
		return this.newTimeSignal(task, delay, TimeUnit.SECONDS);
	}

	public FSGameTimeSignal newMinuteTimeSignal(IGameTimerTask task, long delay) {
		return this.newTimeSignal(task, delay, TimeUnit.MINUTES);
	}

	public FSGameTimeSignal newHourTimeSignal(IGameTimerTask task, long delay) {
		return this.newTimeSignal(task, delay, TimeUnit.HOURS);
	}
	
	/**
	 * 
	 * <pre>
	 * 提交一个整点任务(e.g: 10->12->14 )。
	 * 对于第一次的执行时间，会fixed到最近的整点。
	 * 第一次执行以后，则按照intervalHours的间隔去执行。
	 * 如果不需要fixed，则使用{@link #newHourTimeSignal(IGameTimerTask, long)}
	 * </pre>
	 * 
	 * @param task 整点执行的task
	 * @param intervalHours 间隔
	 * @return
	 */
	public FSGameTimeSignal newFixedHourTimeSignal(IGameTimerTask task, int intervalHours) {
		if(intervalHours <= 0) {
			throw new IllegalArgumentException("整点任务时间间隔必须大于0！");
		}
		long intervalOfStandardUnit = STANDARD_UNIT_OF_TIMER.convert(intervalHours, TimeUnit.HOURS);
		Calendar comingTime = Calendar.getInstance();
		long current = comingTime.getTimeInMillis();
		comingTime.set(Calendar.MINUTE, 0);
		comingTime.set(Calendar.SECOND, 0);
		comingTime.set(Calendar.MILLISECOND, 0);
		comingTime.add(Calendar.HOUR_OF_DAY, intervalHours);
		long firstDelay = comingTime.getTimeInMillis() - current; // 计算第一次偏移数值
		FSGameTimeSignal timeSignal = this.newTimeSignal(task, firstDelay, TimeUnit.MILLISECONDS);
		timeSignal.updateInterval(intervalOfStandardUnit);
		logInfo(_MODULE_NAME_FOR_LOG, "newFixedHourTimeSignal",
				"提交整点任务：" + task.getName() + ", first delay : " + firstDelay + ", deadline:" + timeSignal.deadline + ", deadlineDate:" + FORMAT_DEBUG.format(new java.util.Date(timeSignal.deadline)));
		return timeSignal;
	}
	
	/**
	 * 提交一个整分任务(e.g: 12:04->12:06->12:08)
	 * @param task
	 * @param intervalMinutes
	 * @return
	 */
	FSGameTimeSignal newFixedMinuteTimeSignal(IGameTimerTask task, int intervalMinutes) {
		if(intervalMinutes <= 0) {
			throw new IllegalArgumentException("整分任务时间间隔必须大于0！");
		}
		long intervalOfStandardUnit = STANDARD_UNIT_OF_TIMER.convert(intervalMinutes, TimeUnit.MINUTES);
		Calendar comingTime = Calendar.getInstance();
		comingTime.set(Calendar.SECOND, 0);
		comingTime.set(Calendar.MILLISECOND, 0);
		comingTime.add(Calendar.MINUTE, intervalMinutes);
		long firstDelay = comingTime.getTimeInMillis() - System.currentTimeMillis(); // 计算第一次偏移数值
		FSGameTimeSignal timeSignal = this.newTimeSignal(task, firstDelay, TimeUnit.MILLISECONDS);
		timeSignal.updateInterval(intervalOfStandardUnit);
		logInfo(_MODULE_NAME_FOR_LOG, "newFixedMinuteTimeSignal", "提交整分任务：" + task.getName() + ", first delay : " + firstDelay + ", deadline:" + timeSignal.deadline + ", deadlineDate:" + FORMAT_DEBUG.format(new java.util.Date(timeSignal.deadline)));
		return timeSignal;
	}
	
	FSGameTimeSignal newDayTimeSignal(IGameTimerTask task, int hourOfDay, int minutes) {
		if (hourOfDay < MIN_HOUR_OF_DAY || hourOfDay > MAX_HOUR_OF_DAY) {
			throw new IllegalArgumentException(String.format("hour的值需要在%d~%d之间", MIN_HOUR_OF_DAY, MAX_HOUR_OF_DAY));
		}
		if (minutes < MIN_MINUTE || minutes > MAX_MINUTE) {
			throw new IllegalArgumentException(String.format("minute的值需要在%d~%d之间", MIN_MINUTE, MAX_MINUTE));
		}
		Calendar comingTime = Calendar.getInstance();
		comingTime.set(Calendar.SECOND, 0);
		comingTime.set(Calendar.MILLISECOND, 0);
		comingTime.set(Calendar.MINUTE, minutes);
		comingTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
		long comingTimeMillis = comingTime.getTimeInMillis();
		long currentTime = System.currentTimeMillis();
		if(comingTimeMillis < currentTime) {
			// 过了
			comingTimeMillis += _MILLISECONDS_OF_ONE_DAY;
		}
		long firstDelay = comingTimeMillis - System.currentTimeMillis(); // 计算第一次偏移数值
		FSGameTimeSignal timeSignal = this.newTimeSignal(task, firstDelay, TimeUnit.MILLISECONDS);
		timeSignal.updateInterval(_A_DAY_OF_STANDARD_UNIT);
		logInfo(_MODULE_NAME_FOR_LOG, "newDayTimeSignal", "提交天任务：" + task.getName() + ", first delay : " + firstDelay + ", deadline:" + timeSignal.deadline + ", deadlineDate:" + FORMAT_DEBUG.format(new java.util.Date(timeSignal.deadline)));
		return timeSignal;
	}
	
	/**
	 * 
	 * 时效任务的计时器
	 * 
	 * @author CHEN.P
	 *
	 */
	private final class TickerWorker implements Runnable {
		
		private final String _MODULE_NAME_FOR_LOG = TickerWorker.class.getSimpleName();

		private long _startTime; // 开始的时间
		private long _currentTick; // 当前的时针位置
		
		private long waitForNextTick() {
			
			for(;;) {
				final long currentTime = System.currentTimeMillis();
				long sleepTime = FSGameTimer.this._timeIntervalBetweenTicks * _currentTick - (currentTime - _startTime);
				
				// Check if we run on windows, as if thats the case we will need
				// to round the sleepTime as workaround for a bug that only
				// affect
				// the JVM if it runs on windows.
				//
				// See https://github.com/netty/netty/issues/356
				if (DetectionTool.isWindows() && sleepTime > 0) {
					sleepTime = sleepTime / 10 * 10;
				}
				
				if (sleepTime > 0) {
					try {
						TimeUnit.MILLISECONDS.sleep(sleepTime);
					} catch (InterruptedException e) {
						if (FSGameTimer.this._shutdown.get()) {
							return -1;
						}
						GameLog.error(_MODULE_NAME_FOR_LOG, "waitForNextTick()", "捕捉到一个InterruptedException！", e);
					}
				} else {
					break;
				}
			}
			
			this._currentTick++;
			long deadline = _startTime + FSGameTimer.this._timeIntervalBetweenTicks * this._currentTick;
			return deadline;
		}
		
		private void fetchExpiredTimeSignals(List<FSGameTimeSignal> outList, ReusableIterator<FSGameTimeSignal> i, long deadline) {
			List<FSGameTimeSignal> slipped = null;
			i.rewind();
			FSGameTimeSignal timeSignal;
			while (i.hasNext()) {
				timeSignal = i.next();
				if (timeSignal.getRemainingRounds() > 0) {
					// not your round，take it easy
					timeSignal.decreaseRemainingRounds(1);
				} else {
					// it's your round
					i.remove();
					if (timeSignal.deadline > deadline) {
						// not your tick, take it easy
						if (slipped == null) {
							slipped = new ArrayList<FSGameTimeSignal>();
						}
						slipped.add(timeSignal);
					} else {
						// it's your tick
						outList.add(timeSignal);
					}
				}
			}
			if (slipped != null) {
				// 时间未到的，重新放到下一轮的计划里面
				for (FSGameTimeSignal tempTimeSignal : slipped) {
//					System.out.println("earlier task : " + tempTimeSignal + ", preStopIndex=" + tempTimeSignal.getStopIndex() + ", preRemainingRounds=" + tempTimeSignal.getRemainingRounds());
					FSGameTimer.this.scheduleTimeSignal(tempTimeSignal, tempTimeSignal.deadline - deadline);
//					System.out.println("now stop index : " + tempTimeSignal.getStopIndex() + ", nowRemainingRounds=" + tempTimeSignal.getRemainingRounds());
				}
			}
		}
		
		private void fetchExpiredTimeSignals(List<FSGameTimeSignal> outList, long deadline) {
			FSGameTimer.this.moveWheelCursor();
			int wheelCursor = FSGameTimer.this._currentCursorOfWheel;
			synchronized (FSGameTimer.this._wheel[wheelCursor]) {
				// 同步块，fetch的时候需要独占
				ReusableIterator<FSGameTimeSignal> i = FSGameTimer.this._iteratorsOfElementInWheel[wheelCursor];
				fetchExpiredTimeSignals(outList, i, deadline);
			}
		}
		
		private void executeExpiredTimeSignals(List<FSGameTimeSignal> expiredTimeSignals) {
			for (FSGameTimeSignal timeSignal : expiredTimeSignals) {
				try {
//					System.out.println("execute:" + timeSignal.getTask().getName() + ", currentTimeMillis=" + System.currentTimeMillis());
					FSGameTimer.this._scheduledThreadPool.execute(timeSignal);
				} catch (NullPointerException e) {
					continue;
				} catch (RejectedExecutionException e) {
					if (timeSignal != null) {
						timeSignal.getTask().rejected(e);
					}
				}
			}
		}
		
		@Override
		public void run() {
			
			List<FSGameTimeSignal> expiredTimeSignals = new ArrayList<FSGameTimeSignal>();
			_currentTick = 0; // 时针的起始位置
			Calendar c = Calendar.getInstance();
			_startTime = c.getTimeInMillis();
			long fixedMillisecondToZeroMillis = TimeUnit.SECONDS.toMillis(1) - c.get(Calendar.MILLISECOND) + 10;

			logInfo(_MODULE_NAME_FOR_LOG, "run()", "FSGameTimer's TickerWorker started @ " + FORMAT_DEBUG.format(c.getTime()));
			
			if (fixedMillisecondToZeroMillis > 0 && fixedMillisecondToZeroMillis < 1000) {
				_startTime += fixedMillisecondToZeroMillis;
				try {
					TimeUnit.MILLISECONDS.sleep(fixedMillisecondToZeroMillis);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			while (!_shutdown.get()) {
				final long deadline = waitForNextTick();
				if (deadline > 0) {
//					System.out.println("deadline:" + deadline);
					fetchExpiredTimeSignals(expiredTimeSignals, deadline);
					executeExpiredTimeSignals(expiredTimeSignals);
					expiredTimeSignals.clear();
				}
			}
		}
		
	}
	
	/**
	 * 
	 * FSGameTimeSignal状态callback的内部实现
	 * 
	 * @author CHEN.P
	 *
	 */
	private final class FSGameTimerDelegateImpl implements IGameTimerDelegate {

		@Override
		public void cancel(FSGameTimeSignal target) {
			FSGameTimer.this.cancelTask(target);
		}

		@Override
		public FSGameTimeSignal submitNewTask(IGameTimerTask task, long delay, TimeUnit unit) {
//			if (task.getName().contains("Hour")) {
//				System.out.println(Thread.currentThread().getName() + ", submit again : " + task + ", delay=" + delay + ", unit=" + unit);
//			}
			return FSGameTimer.this.newTimeSignal(task, delay, unit);
		}

	}
}
