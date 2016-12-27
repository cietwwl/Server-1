package com.rwbase.common.timer.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.playerdata.Player;
import com.rw.fsutil.common.Pair;
import com.rw.fsutil.shutdown.ShutdownService;
import com.rwbase.common.timer.FSDailyTaskType;
import com.rwbase.common.timer.FSHourTaskType;
import com.rwbase.common.timer.FSMinuteTaskType;
import com.rwbase.common.timer.IGameTimerTask;
import com.rwbase.gameworld.GameWorldFactory;
import com.rwbase.gameworld.GameWorldKey;

/**
 * 
 * 时效任务工具类
 * 
 * @author CHEN.P
 *
 */
public class FSGameTimerMgr {

	private FSGameTimer _timerInstance; // FSGameTimerDelegate的实例
	private FSGamePlayerOperationTaskMgr _commonTaskSupport = FSGamePlayerOperationTaskMgr.getInstance(); // 预设的Player操作时效任务的实例
	
	private static FSGameTimerMgr _instance = new FSGameTimerMgr(); // 自身的实例，单实例
	
	public static FSGameTimerMgr getInstance() {
		return _instance;
	}
	
	static final FSGameTimer getTimerInstance() {
		return _instance._timerInstance;
	}
	
	private FSGameTimeSignal manualExecute(Calendar lastShutdownCalendar, IGameTimerTask task ,int hourOfDay, int minute) throws Exception {
		Pair<Long, Integer> pair = this.calculateExecuteTimes(lastShutdownCalendar, hourOfDay, minute);
		int executeTimes = pair.getT2();
		if (executeTimes > 0) {
			return _timerInstance.newTimeSignal(new FSGameManualExecuteTask(executeTimes, task, pair.getT1()), 0, TimeUnit.MILLISECONDS, false);
		}
		return null;
	}
	
	private void loadAndCheckDailyTask() throws Exception {
		FSDailyTaskType[] allTypes = FSDailyTaskType.values();
		List<Integer> alreadyAddTypes = new ArrayList<Integer>();
		List<FSGameTimeSignal> list = new ArrayList<FSGameTimeSignal>();
		List<Object[]> submitInfos = new ArrayList<Object[]>();
		for (int i = 0; i < allTypes.length; i++) {
			FSDailyTaskType type = allTypes[i];
			if (alreadyAddTypes.contains(type.getType())) {
				// 判斷任務類型是否重複
				throw new RuntimeException("重复的每日任务类型：" + type.getType());
			}
			alreadyAddTypes.add(type.getType());
			Class<? extends IGameTimerTask> clazz = type.getClassOfTask();
			IGameTimerTask instance = clazz.newInstance();
			submitInfos.add(new Object[] { instance, type.getHourOfDay(), type.getMinute(), type.getType() });
		}
		if (FSGameTimerSaveData.getInstance().getLastServerShutdownTimeMillis() > 0) {
			// 檢查所有超時的任務
			Calendar lastShutdownCalendar = Calendar.getInstance();
			lastShutdownCalendar.setTimeInMillis(FSGameTimerSaveData.getInstance().getLastServerShutdownTimeMillis());
			for (int i = 0; i < submitInfos.size(); i++) {
				Object[] temp = submitInfos.get(i);
				FSGameTimeSignal timeSignal = this.manualExecute(lastShutdownCalendar, (IGameTimerTask) temp[0], (Integer) temp[1], (Integer) temp[2]);
				if (timeSignal != null) {
					list.add(timeSignal);
				}
			}
			while (list.size() > 0) {
				for (Iterator<FSGameTimeSignal> itr = list.iterator(); itr.hasNext();) {
					if (itr.next().isDone()) {
						itr.remove();
					}
				}
			}
		}
		for (int i = 0; i < submitInfos.size(); i++) {
			Object[] temp = submitInfos.get(i);
			this.submitDayTask((IGameTimerTask) temp[0], (Integer) temp[1], (Integer) temp[2]);
		}
	}
	
	private void loadHourTask() throws Exception {
		FSHourTaskType[] allTypes = FSHourTaskType.values();
		for (int i = 0; i < allTypes.length; i++) {
			FSHourTaskType type = allTypes[i];
			if (type.getIntervalHours() > FSGameTimer.MAX_HOUR_OF_DAY || type.getIntervalHours() < 1) {
				throw new RuntimeException("时间间隔不合法！");
			}
			Class<? extends IGameTimerTask> clazz = type.getClassOfTask();
			IGameTimerTask instance = clazz.newInstance();
			if (type.isFixed()) {
				this.submitFixedHourTask(instance, type.getIntervalHours());
			} else {
				this.submitHourTask(instance, type.getIntervalHours());
			}
		}
	}
	
	private void loadMinuteTask() throws Exception {
		FSMinuteTaskType[] allTypes = FSMinuteTaskType.values();
		for (int i = 0; i < allTypes.length; i++) {
			FSMinuteTaskType type = allTypes[i];
			if (type.getIntervalMinutes() > FSGameTimer.MAX_MINUTE || type.getIntervalMinutes() < 1) {
				throw new RuntimeException("时间间隔不合法！");
			}
			Class<? extends IGameTimerTask> clazz = type.getClassOfTask();
			IGameTimerTask instance = clazz.newInstance();
			if (type.isFixed()) {
				this.submitFixedMinuteTask(instance, type.getIntervalMinutes());
			} else {
				this.submitMinuteTask(instance, type.getIntervalMinutes());
			}
		}
	}
	
	public Pair<Long, Integer> calculateExecuteTimes(Calendar lastShutdownCalendar, int pHourOfDay, int pMinute) {
		// 計算上一次停服到現在需要補充執行的次數
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, pHourOfDay);
		c.set(Calendar.MINUTE, pMinute);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		long time = c.getTimeInMillis(); // 今天最近一次執行時間
		int executeTimes = 0;
		/*
		 * 以下情況需要執行： 
		 * 1、停服時間與最近一次執行時間在同一日內，並且停服時此任務未執行，但當前時間已經過了最近一次的執行時間；
		 * 2、停服超過一天，停服時此任務未執行；如果當前時間已經超過了最近一次執行時間，則需要額外執行一次。
		 */
		int dayOfYearShutdown = lastShutdownCalendar.get(Calendar.DAY_OF_YEAR);
		int dayOfYearNearbyExecute = c.get(Calendar.DAY_OF_YEAR);
		long assumeTime = 0;
		if (dayOfYearShutdown == dayOfYearNearbyExecute) {
			if (lastShutdownCalendar.getTimeInMillis() < time && time < System.currentTimeMillis()) {
				executeTimes = 1;
				assumeTime = time;
			}
		} else {
			int subDay = dayOfYearNearbyExecute - dayOfYearShutdown;
			executeTimes = subDay;
			if (c.getTimeInMillis() < System.currentTimeMillis()) {
				// 如果开服当天应该执行的时间已经过了
				executeTimes++;
			}
			c.add(Calendar.DATE, -subDay);
			assumeTime = c.getTimeInMillis();
			if (c.getTimeInMillis() < lastShutdownCalendar.getTimeInMillis()) {
				// 如果n天前的时间比当时停服时间要早，证明n天前已经执行过了，所以不用再执行
				executeTimes--;
				assumeTime += TimeUnit.DAYS.toMillis(1); // 从n-1天前开始执行
			}
		}
		return Pair.Create(assumeTime, executeTimes);
	}
	
	public void init() throws Exception {
		
		Resource r = new ClassPathResource("serverparam.properties"); // 加载配置
		Properties p = PropertiesLoaderUtils.loadProperties(r);
		FSGameTimer timer = new FSGameTimer(p);
		_timerInstance = timer;

		String strPreAddTasks = p.getProperty(FSGameTimer.CONFIG_KEY_NAME_PRE_ADD_TASKS);
		List<int[]> dailyTaskInfos;
		if (strPreAddTasks != null && strPreAddTasks.length() > 0) {
			// 解释预设任务的参数，参数格式：HH:mm;HH:mm
			String[] allPreAddTasks = strPreAddTasks.split(";");
			dailyTaskInfos = new ArrayList<int[]>(allPreAddTasks.length);
			for (int i = 0; i < allPreAddTasks.length; i++) {
				String[] singleTasks = allPreAddTasks[i].split(":");
				dailyTaskInfos.add(new int[] { Integer.parseInt(singleTasks[0]), Integer.parseInt(singleTasks[1]) });
			}
		} else {
			dailyTaskInfos = Collections.emptyList();
		}
		_commonTaskSupport.init(dailyTaskInfos); // 角色预设任务
		ShutdownService.registerShutdownService(timer);
//		_COMMON_TASK_SUPPORT.addOperatorToDailyTask(21, 0, com.rwbase.common.timer.test.new FSGamePlayerOperableDemo());
//		_COMMON_TASK_SUPPORT.addOperatorToMinuteTask(new com.rwbase.common.timer.test.FSGamePlayerOperableMinuteDemo());
//		_COMMON_TASK_SUPPORT.addOperatorToMinuteTask(new com.rwbase.common.timer.test.FSGamePlayerOperationHeavyWeightMinuteDemo());
	}
	
	public void serverStartComplete() throws Exception {
		String attribute = GameWorldFactory.getGameWorld().getAttribute(GameWorldKey.TIMER_DATA);
		if(attribute != null && (attribute = attribute.trim()).length() > 0) {
			FSGameTimerSaveData.parseData(attribute);
		}
		this.loadAndCheckDailyTask();
		this.loadHourTask();
		this.loadMinuteTask();
		this._commonTaskSupport.serverStartComplete();
	}
	
	/**
	 * 
	 * 玩家登录事件
	 * 
	 * @param player
	 */
	public void playerLogin(Player player) {
		FSGamePlayerOperationTaskMgr.getInstance().playerLogin(player);
	}
	
	/**
	 * 
	 * <pre>
	 * 提交一个每秒执行的时效任务
	 * </pre>
	 * 
	 * @param task
	 * @param interval
	 * @return
	 */
	public FSGameTimeSignal submitSecondTask(IGameTimerTask task, int interval) {
		return _timerInstance.newSecondTimeSignal(task, interval);
	}
	
	/**
	 * 
	 * <pre>
	 * 提交一个分钟时效任务。
	 * 此任务的执行间隔是与当前时间<font color="ff0000"><b>相间隔的interval分钟</b></font>。
	 * 例如，当提交时间是11:48:04，则第一次执行时间是11:49:04，第二次执行时间是11:50:04，以此类推
	 * </pre>
	 * 
	 * @param task
	 * @param interval
	 * @return
	 */
	public FSGameTimeSignal submitMinuteTask(IGameTimerTask task, int interval) {
		return _timerInstance.newMinuteTimeSignal(task, interval);
	}
	
	/**
	 * 
	 * <pre>
	 * 提交一个整分时效任务。
	 * 此任务的执行间隔是与当前时间<font color="ff0000"><b>不相关的interval分钟</b></font>。
	 * 例如，当提交时间是11:48:04，则第一次执行时间是11:50:00，第二次执行时间是11:51:00，以此类推
	 * </pre>
	 * 
	 * @param task
	 * @param interval
	 * @return
	 */
	public FSGameTimeSignal submitFixedMinuteTask(IGameTimerTask task, int interval) {
		return _timerInstance.newFixedMinuteTimeSignal(task, interval);
	}
	
	/**
	 * 
	 * <pre>
	 * 提交一个小时时效任务。
	 * 此任务的执行间隔是<font color="ff0000"><b>与提交时间相间隔的interval小时</b></font>。
	 * 例如，提交一个一小时执行一次的时效，则当提交时间是11:48分时，第一次执行时间是12:48，以此类推）
	 * </pre>
	 * 
	 * @see {@link #submitFixedHourTask(IGameTimerTask, int)}
	 * @param task
	 * @param interval
	 * @return
	 */
	public FSGameTimeSignal submitHourTask(IGameTimerTask task, int interval) {
		return _timerInstance.newHourTimeSignal(task, interval);
	}
	
	/**
	 * 
	 * <pre>
	 * 提交一个整点时效任务。
	 * 此任务的执行间隔是与提交时间<font color="ff0000"><b>无关的interval小时</b></font>。
	 * 例如，提交一个一小时执行一次的任务，提交时间是11:48，第一次执行时间是12:00，第二次是13:00，以此类推
	 * </pre>
	 * 
	 * @param task
	 * @param interval
	 * @return
	 */
	public FSGameTimeSignal submitFixedHourTask(IGameTimerTask task, int interval) {
		return _timerInstance.newFixedHourTimeSignal(task, interval);
	}
	
	/**
	 * <pre>
	 * 提交一个每天定时执行的时效任务
	 * </pre>
	 * 
	 * @param task
	 * @param hourOfDay
	 * @return
	 */
	public FSGameTimeSignal submitDayTask(IGameTimerTask task, int hourOfDay) {
		return _timerInstance.newDayTimeSignal(task, hourOfDay, 0);
	}
	
	/**
	 * <pre>
	 * 提交一个每天定时执行的时效任务
	 * </pre>
	 * 
	 * @param task
	 * @param hourOfDay
	 * @param minute
	 * @return
	 */
	public FSGameTimeSignal submitDayTask(IGameTimerTask task, int hourOfDay, int minute) {
		return _timerInstance.newDayTimeSignal(task, hourOfDay, minute);
	}
	
	/**
	 * 
	 * <pre>
	 * 创建一个秒任务的submitInfo实例
	 * </pre>
	 * 
	 * @param pTask
	 * @param pDelay
	 * @return
	 */
	public FSGameTimerTaskSubmitInfoImpl createSecondTaskSubmitInfo(IGameTimerTask pTask, int pDelay) {
		return new FSGameTimerTaskSubmitInfoImpl(pTask, pDelay, TimeUnit.SECONDS);
	}
	
	/**
	 * 
	 * <pre>
	 * 创建一个分钟任务的submitInfo
	 * </pre>
	 * 
	 * @param pTask
	 * @param pDelay
	 * @return
	 */
	public FSGameTimerTaskSubmitInfoImpl createMinuteTaskSubmitInfo(IGameTimerTask pTask, int pDelay) {
		return new FSGameTimerTaskSubmitInfoImpl(pTask, pDelay, TimeUnit.MINUTES);
	}
	
	/**
	 * 
	 * <pre>
	 * 创建一个小时任务的submitInfo
	 * </pre>
	 * 
	 * @param pTask
	 * @param pDelay
	 * @return
	 */
	public FSGameTimerTaskSubmitInfoImpl createHourTaskSubmitInfo(IGameTimerTask pTask, int pDelay) {
		return new FSGameTimerTaskSubmitInfoImpl(pTask, pDelay, TimeUnit.HOURS);
	}
}
