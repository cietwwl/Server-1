package com.rwbase.common.timer.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rwbase.common.timer.FSPlayerMinuteTaskType;
import com.rwbase.common.timer.FSPlayerDailyTaskType;
import com.rwbase.common.timer.IPlayerGatherer;
import com.rwbase.common.timer.IPlayerOperable;

public class FSGamePlayerOperationTaskMgr {
	
	private Map<Integer, FSGamePlayerOperationDailyTask> _dailyTaskInstanceMap = new HashMap<Integer, FSGamePlayerOperationDailyTask>(); // 通用的玩家日常任务实例，key=小时与分钟的计算结果，value=日常任务实例
	private FSGamePlayerOperationTask _minuteTaskInstance; // 通用分钟时效任务
	private boolean _inited = false; // 是否已經初始化
	
	private static FSGamePlayerOperationTaskMgr _instance = new FSGamePlayerOperationTaskMgr(); // 單例
	
	private static IPlayerGatherer _allPlayerGatherer = new FSGameAllPlayerGather(); // 所有玩家的收集器
	private static IPlayerGatherer _onlinePlayerGatherer = new FSGameOnlinePlayerGather(); // 在線玩家收集器
	
	public static FSGamePlayerOperationTaskMgr getInstance() {
		return _instance;
	}
	
	private int calculateKey(int hourOfDay, int minute) {
		// 根據小時和分鐘計算key
		int key = (int) TimeUnit.MINUTES.convert(hourOfDay, TimeUnit.HOURS) + minute;
		return key;
	}
	
	/**
	 * 
	 * 獲取daily的時效任務
	 * 
	 * @param hourOfDay 小時
	 * @param minute 分鐘
	 * @param createIfNotExists 不存在的時候是否創建
	 * @param submitIfNew 如果是新創建的任務，是否馬上提交
	 * @return
	 */
	private FSGamePlayerOperationDailyTask getDailyTask(int hourOfDay, int minute, boolean createIfNotExists, boolean submitIfNew) {
		int key = calculateKey(hourOfDay, minute);
		FSGamePlayerOperationDailyTask task = _dailyTaskInstanceMap.get(key);
		if (task == null && createIfNotExists) {
			task = this.createDailyTask(hourOfDay, minute, submitIfNew);
		}
		return task;
	}
	
	/**
	 * 創建并提交每日時效任務
	 * 
	 * @param hourOfDay 小時（0~23）
	 * @param minute 分鐘
	 * @param submit 是否馬上提交
	 * @return
	 */
	private FSGamePlayerOperationDailyTask createDailyTask(int hourOfDay, int minute, boolean submit) {
		if (hourOfDay > FSGameTimer.MAX_HOUR_OF_DAY || hourOfDay < FSGameTimer.MIN_HOUR_OF_DAY) {
			throw new IllegalArgumentException("hourOfDay只能在" + FSGameTimer.MIN_HOUR_OF_DAY + "-" + FSGameTimer.MAX_HOUR_OF_DAY + "之間");
		}
		if (minute > FSGameTimer.MAX_MINUTE || minute < FSGameTimer.MIN_MINUTE) {
			throw new IllegalArgumentException("minute只能在" + FSGameTimer.MIN_HOUR_OF_DAY + "-" + FSGameTimer.MAX_HOUR_OF_DAY + "之間");
		}
		int key = calculateKey(hourOfDay, minute);
		FSGamePlayerOperationDailyTask dailyTask = _dailyTaskInstanceMap.get(key);
		if (dailyTask == null) {
			dailyTask = new FSGamePlayerOperationDailyTask(hourOfDay, minute, _allPlayerGatherer, true);
			if (submit) {
				FSGameTimerMgr.getInstance().submitDayTask(dailyTask, hourOfDay, minute);
			}
			_dailyTaskInstanceMap.put(key, dailyTask);
		}
		return dailyTask;
	}
	
	private void loadPlayerDailyOperator() throws Exception {
		// 加載預設的每日任務
		FSPlayerDailyTaskType[] allValues = FSPlayerDailyTaskType.values();
		List<Integer> alreadyAddTypes = new ArrayList<Integer>();
		for (int i = 0; i < allValues.length; i++) {
			FSPlayerDailyTaskType type = allValues[i];
			if (alreadyAddTypes.contains(type.getType())) {
				// 判斷任務類型是否重複
				throw new RuntimeException("重复的玩家每日任务类型：" + type.getType());
			}
			alreadyAddTypes.add(type.getType());
			Class<? extends IPlayerOperable> clazz = type.getClassOfTask();
			IPlayerOperable instance = clazz.newInstance();
			this.getDailyTask(type.getHourOfDay(), type.getMinute(), true, false).addOperator(type.getType(), instance);
		}
	}
	
	private void loadMinuteTask() throws Exception {
		// 加載預設的整分任務
		FSPlayerMinuteTaskType[] allValues = FSPlayerMinuteTaskType.values();
		List<Integer> alreadyAddTypes = new ArrayList<Integer>();
		for(int i = 0; i < allValues.length; i++) {
			FSPlayerMinuteTaskType type = allValues[i];
			if(alreadyAddTypes.contains(type.getType())) {
				throw new RuntimeException("重複的整分任務類型：" + type.getType());
			}
			alreadyAddTypes.add(type.getType());
			Class<? extends IPlayerOperable> clazz = type.getClassOfTask();
			IPlayerOperable instance = clazz.newInstance();
			_minuteTaskInstance.addOperator(type.getType(), instance);
		}
	}
	
	void init(List<int[]> dailyTasks) throws Exception {
		// 初始化，dailyTasks是一個包含需要預先創建好的時效任務的集合，[0]表示小时，[1]表示分钟
		if (!_inited) {
			_inited = true;
			for (int i = 0; i < dailyTasks.size(); i++) {
				int[] dailyTaskInfo = dailyTasks.get(i);
				createDailyTask(dailyTaskInfo[0], dailyTaskInfo[1], false);
			}
			// minute task 默认只对在线角色进行操作
			_minuteTaskInstance = new FSGamePlayerOperationTask(_onlinePlayerGatherer, false);
			FSGameTimerMgr.getInstance().submitMinuteTask(_minuteTaskInstance, 1);
			loadPlayerDailyOperator();
			loadMinuteTask();
		}
	}
	
	void serverStartComplete() throws Exception {
		// 服務器啟動完成的通知
		if (FSGameTimerSaveData.getInstance().getLastServerShutdownTimeMillis() > 0) {
			// 檢查所有超時的任務
			Calendar lastShutdownCalendar = Calendar.getInstance();
			lastShutdownCalendar.setTimeInMillis(FSGameTimerSaveData.getInstance().getLastServerShutdownTimeMillis());
			for (Iterator<Map.Entry<Integer, FSGamePlayerOperationDailyTask>> itr = _dailyTaskInstanceMap.entrySet().iterator(); itr.hasNext();) {
				Map.Entry<Integer, FSGamePlayerOperationDailyTask> entry = itr.next();
				if (FSGameTimerSaveData.getInstance().getLastExecuteTimeOfPlayerTask(entry.getKey()) > 0) {
					entry.getValue().manualExecute(lastShutdownCalendar);
				}
			}
		}
		for (Iterator<FSGamePlayerOperationDailyTask> itr = _dailyTaskInstanceMap.values().iterator(); itr.hasNext();) {
			FSGamePlayerOperationDailyTask task = itr.next();
			FSGameTimerMgr.getInstance().submitDayTask(task, task.getHourOfDay(), task.getMinute());
		}
	}
	
	void playerLogin(Player player) {
		// 玩家登錄通知
		for(Iterator<FSGamePlayerOperationDailyTask> itr = _dailyTaskInstanceMap.values().iterator(); itr.hasNext();) {
			itr.next().notifyPlayerLogin(player);
		}
		_minuteTaskInstance.notifyPlayerLogin(player);
	}
	
	void changeExecuteTimeOfDailyTask(FSPlayerDailyTaskType type, int newHourOfDay, int newMinute) {
		// 改變某個類型的每日時效任務的執行時間
		if(newHourOfDay > FSGameTimer.MAX_HOUR_OF_DAY || newHourOfDay < FSGameTimer.MIN_HOUR_OF_DAY) {
			throw new RuntimeException("非法的newHourOfDay：" + newHourOfDay);
		}
		if(newMinute > FSGameTimer.MAX_MINUTE || newMinute < FSGameTimer.MIN_MINUTE) {
			throw new RuntimeException("非法的newMinute：" + newMinute);
		}
		int key = this.calculateKey(type.getHourOfDay(), type.getMinute());
		FSGamePlayerOperationTask task = _dailyTaskInstanceMap.get(key);
		if(task == null) {
			throw new NullPointerException("找不到[" + type + "]原來的執行任務！");
		}
		IPlayerOperable operator = task.removeOperator(type.getType()); // 從之前的移除
		task = this.getDailyTask(newHourOfDay, newMinute, true, true); // 尋找新的每日時效
		task.addOperator(type.getType(), operator); // 添加到新的任務中
		
	}
	
	private static class FSGameAllPlayerGather implements IPlayerGatherer {

		private List<Player> _list;
		
		@Override
		public List<Player> gatherPlayers() {
			Map<String, Player> map = PlayerMgr.getInstance().getAllPlayer();
			if(_list == null) {
				_list = new ArrayList<Player>(map.values());
			} else {
				_list.clear();
				_list.addAll(map.values());
			}
			return _list;
		}
		
	}
	
	private static class FSGameOnlinePlayerGather implements IPlayerGatherer {

		@Override
		public List<Player> gatherPlayers() {
			return PlayerMgr.getInstance().getOnlinePlayers();
		}
		
	}
}
