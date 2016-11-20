package com.rwbase.common.timer.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.playerdata.Player;
import com.rw.fsutil.shutdown.ShutdownService;
import com.rwbase.common.timer.IGameTimerTask;

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
