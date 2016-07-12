package com.rwbase.common.timer.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.playerdata.Player;
import com.rwbase.common.timer.FSGameTimerTask;
import com.rwbase.common.timer.test.FSGamePlayerOperableDemo;
import com.rwbase.common.timer.test.FSGamePlayerOperableMinuteDemo;
import com.rwbase.common.timer.test.FSGamePlayerOperationHeavyWeightMinuteDemo;

/**
 * 
 * 时效任务工具类
 * 
 * @author CHEN.P
 *
 */
public class FSGameTimerMgr {

	private static FSGameTimer _TIMER;
	private static FSGamePlayerOperationTaskMgr _COMMON_TASK_SUPPORT = FSGamePlayerOperationTaskMgr.getInstance();
	
	private static FSGameTimerMgr _instance = new FSGameTimerMgr();
	
	public static FSGameTimerMgr getInstance() {
		return _instance;
	}
	
	public void init() throws Exception {
		
		Resource r = new ClassPathResource("serverparam.properties");
		Properties p = PropertiesLoaderUtils.loadProperties(r);
		FSGameTimer timer = new FSGameTimer(p);
		_TIMER = timer;

		String strPreAddTasks = p.getProperty(FSGameTimer.CONFIG_KEY_NAME_PRE_ADD_TASKS);
		List<int[]> dailyTaskInfos;
		if (strPreAddTasks != null && strPreAddTasks.length() > 0) {
			String[] allPreAddTasks = strPreAddTasks.split(";");
			dailyTaskInfos = new ArrayList<int[]>(allPreAddTasks.length);
			for (int i = 0; i < allPreAddTasks.length; i++) {
				String[] singleTasks = allPreAddTasks[i].split(":");
				dailyTaskInfos.add(new int[] { Integer.parseInt(singleTasks[0]), Integer.parseInt(singleTasks[1]) });
			}
		} else {
			dailyTaskInfos = Collections.emptyList();
		}
		_COMMON_TASK_SUPPORT.init(dailyTaskInfos);
		_COMMON_TASK_SUPPORT.addOperatorToDailyTask(21, 0, new FSGamePlayerOperableDemo());
		_COMMON_TASK_SUPPORT.addOperatorToMinuteTask(new FSGamePlayerOperableMinuteDemo());
		_COMMON_TASK_SUPPORT.addOperatorToMinuteTask(new FSGamePlayerOperationHeavyWeightMinuteDemo());
	}
	
	public void playerLogin(Player player) {
		FSGamePlayerOperationTaskMgr.getInstance().playerLogin(player);
	}
	
	public final FSGameTimer getTimer() {
		return _TIMER;
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
	public FSGameTimeSignal submitSecondTask(FSGameTimerTask task, int interval) {
		return _TIMER.newSecondTimeSignal(task, interval);
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
	public FSGameTimeSignal submitMinuteTask(FSGameTimerTask task, int interval) {
		return _TIMER.newMinuteTimeSignal(task, interval);
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
	public FSGameTimeSignal submitFixedMinuteTask(FSGameTimerTask task, int interval) {
		return _TIMER.newFixedMinuteTimeSignal(task, interval);
	}
	
	/**
	 * 
	 * <pre>
	 * 提交一个小时时效任务。
	 * 此任务的执行间隔是<font color="ff0000"><b>与提交时间相间隔的interval小时</b></font>。
	 * 例如，提交一个一小时执行一次的时效，则当提交时间是11:48分时，第一次执行时间是12:48，以此类推）
	 * </pre>
	 * 
	 * @see {@link #submitFixedHourTask(FSGameTimerTask, int)}
	 * @param task
	 * @param interval
	 * @return
	 */
	public FSGameTimeSignal submitHourTask(FSGameTimerTask task, int interval) {
		return _TIMER.newHourTimeSignal(task, interval);
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
	public FSGameTimeSignal submitFixedHourTask(FSGameTimerTask task, int interval) {
		return _TIMER.newFixedHourTimeSignal(task, interval);
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
	public FSGameTimeSignal submitDayTask(FSGameTimerTask task, int hourOfDay) {
		return _TIMER.newDayTimeSignal(task, hourOfDay, 0);
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
	public FSGameTimeSignal submitDayTask(FSGameTimerTask task, int hourOfDay, int minute) {
		return _TIMER.newDayTimeSignal(task, hourOfDay, minute);
	}
}
