package com.rwbase.common.timer.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.RandomAccess;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.playerdata.Player;
import com.rwbase.common.timer.FSGamePlayerGatherer;
import com.rwbase.common.timer.FSGamePlayerOperable;
import com.rwbase.common.timer.FSGameTimerTask;
import com.rwbase.common.timer.FSGameTimerTaskSubmitInfo;

/**
 * 
 * 通用时效任务的实现
 * 
 * @author CHEN.P
 *
 */
public class FSGamePlayerOperationTask implements FSGameTimerTask {

	private String _uuid; // 时效任务的uuid，用于作为标识
	private String _name; // 时效任务的名字
	private final List<FSGameDailySubTask> _operationList; // 本时效任务的玩家操作行为
	private final FSGamePlayerGatherer _defaultPlayerGatherer;
	
	FSGamePlayerOperationTask(FSGamePlayerGatherer playerGatherer) {
		this._uuid = java.util.UUID.randomUUID().toString();
		this._name = this.getClass().getSimpleName() + "@" + _uuid;
		this._operationList = new ArrayList<FSGameDailySubTask>();
		this._defaultPlayerGatherer = playerGatherer;
	}
	
	void notifyPlayerLogin(Player player) {
		for (FSGameDailySubTask task : _operationList) {
			task.playerLogin(player);
		}
	}
	
	void addOperator(FSGamePlayerOperable operator) {
		synchronized(_operationList) {
			this._operationList.add(new FSGameDailySubTask(operator));
		}
	}
	
	@Override
	public String getName() {
		return _name;
	}

	@Override
	public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
		synchronized (_operationList) {
			List<Player> allPlayers = Collections.unmodifiableList(this._defaultPlayerGatherer.gatherPlayers());
			FSGameDailySubTask subTask;
			for (int i = _operationList.size(); i-- > 0;) {
				subTask = _operationList.get(i);
				if (subTask._operator instanceof FSGamePlayerGatherer) {
					subTask._players = ((FSGamePlayerGatherer) subTask._operator).gatherPlayers();
				} else {
					subTask._players = allPlayers;
				}
				FSGameTimerMgr.getInstance().getTimer().newTimeSignal(subTask, 0, TimeUnit.MILLISECONDS);
			}
		}
		return "DONE";
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
	public List<FSGameTimerTaskSubmitInfo> getChildTasks() {
		return null;
	}
	
	private static class FSGameDailySubTask implements FSGameTimerTask {
		
		private FSGamePlayerOperable _operator;
		private List<Player> _players;
		private final Queue<Player> _tempPlayers;
		private String _name;
		private long _lastExecuteTime; // 上一次执行的时间
		private final AtomicBoolean _executing = new AtomicBoolean(); // 是否正在执行中
		private final List<String> _lastExecutePlayers; // 上一次被操作的player信息
		
		public FSGameDailySubTask(FSGamePlayerOperable pOperator) {
			this._operator = pOperator;
			this._name = this.getClass().getSimpleName() + " for " + _operator;
			this._lastExecutePlayers = new ArrayList<String>();
			this._tempPlayers = new ConcurrentLinkedQueue<Player>();
		}
		
		private void executeSingle(Player player) {
			if (player.isRobot()) {
				return;
			}
			this._operator.operate(player);
			this._lastExecutePlayers.add(player.getUserId());
		}
		
		void playerLogin(Player player) {
			if (this._operator.isInterestingOn(player)) { 
				if (_executing.get()) {
					this._tempPlayers.add(player);
				} else if (this._lastExecuteTime > 0 && !_lastExecutePlayers.contains(player.getUserId())) {
					this._operator.operate(player);
				}
			}
		}

		@Override
		public String getName() {
			return this._name;
		}

		@Override
		public Object onTimeSignal(FSGameTimeSignal timeSignal) throws Exception {
			this._executing.getAndSet(true);
			this._lastExecutePlayers.clear();
			if(_players instanceof RandomAccess) {
				for (int i = _players.size(); i-- > 0;) {
					this.executeSingle(_players.get(i));
				}
			} else {
				for(Iterator<Player> itr = _players.iterator(); itr.hasNext();) {
					this.executeSingle(itr.next());
				}
			}
			if (_tempPlayers.size() > 0) {
				Player temp;
				while ((temp = _tempPlayers.poll()) != null) {
					this.executeSingle(temp);
				}
			}
			this._lastExecuteTime = System.currentTimeMillis();
			this._executing.getAndSet(false);
			this._players = null;
			return "DONE";
		}

		@Override
		public void afterOneRoundExecuted(FSGameTimeSignal timeSignal) {
			
		}

		@Override
		public void rejected(RejectedExecutionException e) {
			
		}

		@Override
		public boolean isContinue() {
			return false;
		}

		@Override
		public List<FSGameTimerTaskSubmitInfo> getChildTasks() {
			return null;
		}
		
	}

}
