package com.rwbase.common.timer.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.playerdata.Player;
import com.playerdata.PlayerMgr;
import com.rwbase.common.timer.FSGamePlayerGatherer;
import com.rwbase.common.timer.FSGamePlayerOperable;

public class FSGamePlayerOperationTaskMgr {
	
	private Map<Integer, FSGamePlayerOperationTask> _dailyTaskInstanceMap = new HashMap<Integer, FSGamePlayerOperationTask>(); // 通用的玩家日常任务实例，key=小时与分钟的计算结果，value=日常任务实例
	private FSGamePlayerOperationTask _minuteTaskInstance; // 通用分钟时效任务
	private boolean _inited = false;
	
	private static FSGamePlayerOperationTaskMgr _instance = new FSGamePlayerOperationTaskMgr();
	
	private static FSGamePlayerGatherer _allPlayerGatherer = new FSGameAllPlayerGather();
	private static FSGamePlayerGatherer _onlinePlayerGatherer = new FSGameOnlinePlayerGather();
	
	public static FSGamePlayerOperationTaskMgr getInstance() {
		return _instance;
	}
	
	void init(List<int[]> dailyTasks) {
		// 初始化，dailyTasks是一个包含需要预先穿件好的时效任务的集合，[0]表示小时，[1]表示分钟
		if (!_inited) {
			_inited = true;
			for (int i = 0; i < dailyTasks.size(); i++) {
				int[] dailyTaskInfo = dailyTasks.get(i);
				this.createAndSubmitDailyTask(dailyTaskInfo[0], dailyTaskInfo[1]);
			}
			// minute task 默认只对在线角色进行操作
			_minuteTaskInstance = new FSGamePlayerOperationTask(_onlinePlayerGatherer);
			FSGameTimerMgr.getInstance().submitMinuteTask(_minuteTaskInstance, 1);
		}
	}
	
	private int calculateKey(int hourOfDay, int minute) {
		int key = (int) TimeUnit.MINUTES.convert(hourOfDay, TimeUnit.HOURS) + minute;
		return key;
	}
	
	void createAndSubmitDailyTask(int hourOfDay, int minute) {
		int key = calculateKey(hourOfDay, minute);
		FSGamePlayerOperationTask dailyTask = _dailyTaskInstanceMap.get(key);
		if (dailyTask == null) {
			dailyTask = new FSGamePlayerOperationTask(_allPlayerGatherer);
			FSGameTimerMgr.getInstance().submitDayTask(dailyTask, hourOfDay, minute);
			_dailyTaskInstanceMap.put(calculateKey(hourOfDay, minute), dailyTask);
		}
	}
	
	void addOperatorToDailyTask(int hourOfDay, int minute, FSGamePlayerOperable operator) {
		int key = calculateKey(hourOfDay, minute);
		FSGamePlayerOperationTask task = _dailyTaskInstanceMap.get(key);
		if(task == null) {
			throw new NullPointerException("hour:" + hourOfDay + ", minute:" + minute + ", 任务不存在！");
		}
		task.addOperator(operator);
	}
	
	void addOperatorToMinuteTask(FSGamePlayerOperable operator) {
		_minuteTaskInstance.addOperator(operator);
	}
	
	void playerLogin(Player player) {
		for(Iterator<FSGamePlayerOperationTask> itr = _dailyTaskInstanceMap.values().iterator(); itr.hasNext();) {
			itr.next().notifyPlayerLogin(player);
		}
		_minuteTaskInstance.notifyPlayerLogin(player);
	}
	
	private static class FSGameAllPlayerGather implements FSGamePlayerGatherer {

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
	
	private static class FSGameOnlinePlayerGather implements FSGamePlayerGatherer {

		@Override
		public List<Player> gatherPlayers() {
			return PlayerMgr.getInstance().getOnlinePlayers();
		}
		
	}
}
